package org.jfw.web.servlet.fileupload.cached;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfw.util.ListUtil;
import org.jfw.util.StringUtil;
import org.jfw.util.context.JfwAppContext;
import org.jfw.util.exception.JfwBaseException;
import org.jfw.util.json.JsonService;
import org.jfw.util.web.fileupload.Item;
import org.jfw.util.web.fileupload.UploadItemIterator;
import org.jfw.util.web.fileupload.impl.UploadItemIteratorImpl;

public class CachedUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 6471271800146211600L;

	private Map<String, Long> cacheMap = new ConcurrentHashMap<String, Long>();

	private LinkedList<String> cleanKeys = new LinkedList<String>();

	private List<String> suffixs;

	private boolean serviced = false;
	private CachedUploadServletConfig config;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (serviced) {
			this.handleRequest(req, resp);
		} else {
			resp.sendError(500);
		}
	}

	private static Integer parseInt(String[] strs) {
		if (strs != null) {
			String str = strs[0];
			if (str != null && str.length() > 0) {
				return Integer.parseInt(str);
			}
		}
		return null;
	}

	private static Long parseLong(String[] strs) {
		if (strs != null) {
			String str = strs[0];
			if (str != null && str.length() > 0) {
				return Long.parseLong(str);
			}
		}
		return null;
	}

	private void initResponse(HttpServletResponse res, boolean byJsonp) {
		res.setDateHeader("Expires", 0);
		res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		res.addHeader("Cache-Control", "post-check=0, pre-check=0");
		res.setHeader("Pragma", "no-cache");
		res.setContentType(byJsonp ? "text/html" : "application/json");
	}

	private void outJsonpPrefix(PrintWriter out) {
		out.print(
				"<!DOCTYPE html><html lang=\"zh-cn\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head><body><script type=\"text/javascript\"> var jsonpData =");
	}

	private void outJsonpSuffix(PrintWriter out, String jsonpContent) {
		out.print(";" + jsonpContent + "</script></body></html>");

	}

	private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String queryString = req.getQueryString();
		Map<String, String[]> map = StringUtil.decodeURLQueryString(queryString);
		Integer cLimit = CachedUploadServlet.parseInt(map.get("countLimit"));
		Long sLimit = CachedUploadServlet.parseLong(map.get("sizeLimit"));
		String jsonpContent = null;

		String[] jces = map.get("function");
		if (jces != null) {
			jsonpContent = jces[0];
		}
		int countLimit = this.config.getRealCountLimit(cLimit);
		long sizeLimit = this.config.getRealSizeLimit(sLimit);

		List<CachedItem> items = null;
		boolean byJsonp = null != jsonpContent;
		this.initResponse(resp, byJsonp);
		PrintWriter out = resp.getWriter();
		try {
			items = this.upload(req, countLimit, sizeLimit);
			if (!items.isEmpty() && this.config.getCachedItemValidator() != null)
				this.config.getCachedItemValidator().validdate(this.config, items);
			if (!items.isEmpty())
				this.scheduleCachedItem(items);
		} catch (JfwBaseException e) {
			if (byJsonp)
				this.outJsonpPrefix(out);
			JsonService.write(e, out);
			if (byJsonp)
				this.outJsonpSuffix(out, jsonpContent);
			return;
		}
		if (byJsonp)
			this.outJsonpPrefix(out);
		out.write("{\"success\":true,\"data\":");
		JsonService.toJson(items, out);
		out.write("}");
		if (byJsonp)
			this.outJsonpSuffix(out, jsonpContent);

	}

	private void scheduleCachedItem(List<CachedItem> items) {
		long expried = System.currentTimeMillis() + this.config.getHoldTime();
		for (CachedItem item : items) {
			this.cacheMap.put(item.getCacheKey(), expried);
		}
	}

	private CachedItem cache(InputStream in, long sizeLimt, byte[] buf) throws JfwBaseException {
		long size = 0;
		int len = 0;
		boolean byMemory = this.config.isCacheByMemory();
		String key = StringUtil.buildUUID();
		ByteArrayOutputStream byteObj = null;
		FileOutputStream fileObj = null;
		File file = null;
		try {
			if (byMemory) {
				byteObj = new ByteArrayOutputStream();
			} else {
				while (true) {
					file = new File(this.config.getCachePath(), key);
					if (!file.exists()) {
						fileObj = new FileOutputStream(file);
						break;
					}
					key = StringUtil.buildUUID();
				}
			}
			OutputStream outObj = byMemory ? byteObj : fileObj;

			try {
				while ((len = in.read(buf)) != -1) {
					if (len > 0) {
						size += len;
						if (size > sizeLimt)
							throw new JfwBaseException(301, "upload file size is larger than setting");
						outObj.write(buf, 0, len);
					}
				}
				outObj.flush();
			} finally {
				outObj.close();
			}
		} catch (IOException e) {
			if (!byMemory)
				file.delete();
			throw new JfwBaseException(92, "save resource error");
		}
		if (byMemory) {
			byte[] bs = byteObj.toByteArray();
			while (!JfwAppContext.cacheObjectIfAbsent(key, bs)) {
				key = StringUtil.buildUUID();
			}
		}
		CachedItem result = new CachedItem();
		result.setFileSize(size);
		result.setCacheKey(key);
		return result;
	}

	private boolean matchExt(String fn) {
		if (!this.suffixs.isEmpty()) {
			int index = fn.lastIndexOf('.');
			if (index < 0)
				return false;
			return this.suffixs.contains(fn.substring(index).toLowerCase(Locale.US));
		}
		return true;
	}

	private List<CachedItem> upload(HttpServletRequest req, int countLimit, long sizeLimit) throws JfwBaseException {
		List<CachedItem> items = new LinkedList<CachedItem>();
		if (!UploadItemIteratorImpl.isMultipartContent(req))
			return items;
		String fieldName = null;
		String fileName = null;
		try {
			UploadItemIterator it = UploadItemIteratorImpl.build(req);
			int count = 0;
			try {
				byte[] buf = new byte[8192];
				while (it.hasNext()) {
					Item item = it.next();
					if (!item.isFormField()) {
						++count;
						if (count > countLimit)
							throw new JfwBaseException(303, "upload file count is larger than setting");
						fileName = item.getName();
						fieldName = item.getFieldName();

						if (!this.matchExt(fileName)) {
							throw new JfwBaseException(302, "upload file type unsupported with setting");
						}
						// TODO: valid file sizeLimt user item.getSize();

						CachedItem citem = cache(item.getInputStream(), sizeLimit, buf);
						citem.setFieldName(fieldName);
						citem.setFileName(fileName);
						items.add(citem);
					}
				}
				return items;
			} finally {
				it.clean();
			}
		} catch (IOException e) {
			if (!items.isEmpty())
				this.removeCachedObj(items.toArray(new CachedItem[items.size()]));
			throw new JfwBaseException(94, "transfer file error", e);
		} catch (JfwBaseException e) {
			if (!items.isEmpty())
				this.removeCachedObj(items.toArray(new CachedItem[items.size()]));
			throw e;
		}
	}

	private void removeCachedObj(CachedItem... cachedItems) {
		for (CachedItem item : cachedItems) {
			removeCachedObj(item.getCacheKey());
		}
	}

	private void removeCachedObj(String key) {
		if (this.config.isCacheByMemory())
			JfwAppContext.removeCachedObject(key);
		else
			new File(this.config.getCachePath(), key).delete();
	}

	@Override
	public void init() throws ServletException {
		this.config = JfwAppContext.getBeanFactory().getBean(this.getServletName(), CachedUploadServletConfig.class);
		if (null != config) {
			try {
				this.doConfig();
				this.serviced = true;
			} catch (Throwable th) {
			}
			JfwAppContext.getScheduledExecutorService().scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {
					long ct = System.currentTimeMillis();
					for (String key : cleanKeys) {
						cacheMap.remove(key);
					}
					cleanKeys.clear();
					for (Map.Entry<String, Long> entry : cacheMap.entrySet()) {
						String key = entry.getKey();
						if (entry.getValue() > ct) {
							removeCachedObj(key);
							cleanKeys.add(key);
						}
					}
				}

			}, this.config.getCleanInteval(), this.config.getCleanInteval(), TimeUnit.MILLISECONDS);

		}
	}

	private void doConfig() throws Exception {
		if (!this.config.isCacheByMemory()) {
			File path = this.config.getCachePath();
			if (path == null)
				throw new Exception("servlet[" + this.getServletName() + "] no set cache path");
			if (path.exists()) {
				if (path.isFile())
					throw new Exception("servlet[" + this.getServletName() + "] invalid cache path");
			} else {
				if (!path.mkdirs())
					throw new Exception("servlet[" + this.getServletName() + "] mkdir cache path error");
			}
		}

		String sTmp = this.config.getFileSuffix();
		if (sTmp != null) {
			this.suffixs = ListUtil.splitTrimExcludeEmpty(sTmp, ',');
		} else {
			this.suffixs = Collections.<String> emptyList();
		}
	}

}
