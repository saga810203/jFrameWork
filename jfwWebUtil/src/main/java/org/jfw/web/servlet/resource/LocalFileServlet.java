package org.jfw.web.servlet.resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfw.util.io.IoUtil;
import org.jfw.util.web.WebUtil;

public class LocalFileServlet extends HttpServlet {

	private static final long serialVersionUID = 333550604313917044L;
	public static final String RFC1123_DATE = "EEE, dd MMM yyyy HH:mm:ss zzz";
	public static final TimeZone GMT = TimeZone.getTimeZone("GMT");
	private final Queue<SimpleDateFormat> queue = new ConcurrentLinkedQueue<SimpleDateFormat>();

	private static final ArrayList<Range> FULL = new ArrayList<Range>();

	protected static final String mimeSeparation = "CATALINA_MIME_BOUNDARY";
	private static final Object gzipLock = new Object();

	private ServletContext _servletContext;
	private File rootPath;
	private int prefixLen = 0;

	/* ------------------------------------------------------------ */
	@Override
	public void init() throws ServletException {
		_servletContext = getServletContext();
		String tmp = this.getInitParameter("rootPath");
		if (tmp == null || tmp.trim().length() == 0) {
			this.rootPath = new File("");
		} else {
			rootPath = new File(tmp.trim());
		}
		if ((!this.rootPath.exists()) || this.rootPath.isFile()) {
			throw new ServletException("invalid servlet parameter rootPath in " + this.getServletName());
		}
		tmp = this.getInitParameter("prefixLen");
		if (tmp != null && tmp.trim().length() > 0) {
			try {
				this.prefixLen = Integer.parseInt(tmp);
				if (this.prefixLen < 0)
					this.prefixLen = 0;
			} catch (Exception e) {
				this.prefixLen = 0;
			}
		}

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.serveResource(request, response, true);
	}

	protected void serveResource(HttpServletRequest request, HttpServletResponse response, boolean sendContext)
			throws ServletException, IOException {
		String uri = WebUtil.normalize(request.getRequestURI());
		if (uri.charAt(uri.length() - 1) == '/') {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		try {
			uri = uri.substring(prefixLen);
		} catch (java.lang.StringIndexOutOfBoundsException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		int index = uri.lastIndexOf('/');
		if (index < 1) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String fn = uri.substring(index + 1);
//		if (fn.indexOf('%') >= 0 || fn.indexOf('+') >= 0)
//			fn = java.net.URLDecoder.decode(fn, "UTF-8");
		uri = uri.substring(0, index);

		File file = new File(this.rootPath, uri);
		if ((!file.exists()) || file.isDirectory()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String mType = this._servletContext.getMimeType(fn);
		String aEncoding = this.acceptEncoding(request);
		boolean usingGzip = "gzip".equals(aEncoding);
		if (usingGzip) {
			File gzipFile = new File(file.getParent(), file.getName() + ".gz");
			if (!gzipFile.exists()) {
				synchronized (gzipLock) {
					gzipFile = new File(file.getParent(), file.getName() + ".gz");
					if (!gzipFile.exists()) {
						InputStream in = new FileInputStream(file);
						try {
							FileOutputStream fos = new FileOutputStream(gzipFile);
							try {
								@SuppressWarnings("resource")
								GZIPOutputStream gos = new GZIPOutputStream(fos);
								byte[] buf = new byte[4096];
								int len = 0;
								while ((len = in.read(buf)) >= 0) {
									if (len > 0) {
										gos.write(buf, 0, len);
									}
								}
								gos.flush();
							} catch (Exception e) {
								IoUtil.close(fos);
								gzipFile.delete();
								throw e;
							}
						} finally {
							IoUtil.close(in);
						}
					}

				}
			}
			file = gzipFile;
		}
		SimpleDateFormat sf = queue.poll();
		if (sf == null) {
			sf = new SimpleDateFormat(RFC1123_DATE, Locale.US);
			sf.setTimeZone(GMT);
		}
		long lastModified = file.lastModified();
		String lastModifiedHttp = sf.format(new Date(lastModified));
		queue.add(sf);
		String eTag = null;
		long contentLength = file.length();
		if ((contentLength >= 0) || (lastModified >= 0)) {
			eTag = "W/\"" + contentLength + "-" + lastModified + "\"";
		}
		response.addHeader("Vary", "accept-encoding");

		if (aEncoding != null) {
			response.addHeader("Content-Encoding", aEncoding);
		}

		ArrayList<Range> ranges = null;
		boolean hasRangesReq = false;

		response.setHeader("Accept-Ranges", "bytes");

		String headerValue = request.getHeader("If-Range");
		if (headerValue != null) {
			long headerValueTime = (-1L);
			try {
				headerValueTime = request.getDateHeader("If-Range");
			} catch (IllegalArgumentException e) {
			}
			if (headerValueTime == (-1L)) {
				if (!eTag.equals(headerValue.trim()))
					ranges = FULL;
			} else {
				if (lastModified > (headerValueTime + 1000))
					ranges = FULL;
			}
		}
		if (ranges == null) {
			if (contentLength > 0) {
				String rangeHeader = request.getHeader("Range");
				if (rangeHeader != null) {
					hasRangesReq = true;
					if (!rangeHeader.startsWith("bytes")) {
						response.addHeader("Content-Range", "bytes */" + contentLength);
						response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
						return;
					}
					rangeHeader = rangeHeader.substring(6);
					ranges = new ArrayList<>();
					StringTokenizer commaTokenizer = new StringTokenizer(rangeHeader, ",");
					while (commaTokenizer.hasMoreTokens()) {
						String rangeDefinition = commaTokenizer.nextToken().trim();

						Range currentRange = new Range();
						currentRange.length = contentLength;
						int dashPos = rangeDefinition.indexOf('-');

						if (dashPos == -1) {
							response.addHeader("Content-Range", "bytes */" + contentLength);
							response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
							return;
						}

						if (dashPos == 0) {
							try {
								long offset = Long.parseLong(rangeDefinition);
								currentRange.start = contentLength + offset;
								currentRange.end = contentLength - 1;
							} catch (NumberFormatException e) {
								response.addHeader("Content-Range", "bytes */" + contentLength);
								response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
								return;
							}
						} else {
							try {
								currentRange.start = Long.parseLong(rangeDefinition.substring(0, dashPos));
								if (dashPos < rangeDefinition.length() - 1)
									currentRange.end = Long.parseLong(
											rangeDefinition.substring(dashPos + 1, rangeDefinition.length()));
								else
									currentRange.end = contentLength - 1;
							} catch (NumberFormatException e) {
								response.addHeader("Content-Range", "bytes */" + contentLength);
								response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
								return;
							}
						}
						if (!currentRange.validate()) {
							response.addHeader("Content-Range", "bytes */" + contentLength);
							response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
							return;
						}
						ranges.add(currentRange);
					}
				}
			}
		}

		response.setHeader("ETag", eTag);

		// Last-Modified header
		response.setHeader("Last-Modified", lastModifiedHttp);
		if (contentLength == 0L) {
			sendContext = false;
		}
		ServletOutputStream ostream = null;
		if (sendContext) {
			ostream = response.getOutputStream();
		}

		if ((ranges == null) || (ranges.isEmpty())) {
			if (hasRangesReq || ranges == FULL) {
				if (mType != null) {
					response.setContentType(mType);
				}
				if (contentLength > 0)
					response.addHeader("Content-Length", Long.toString(contentLength));
				if (sendContext) {
					InputStream in = new FileInputStream(file);
					IoUtil.copy(in, ostream, true, true);
				}
			}
			return;
		}

		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

		if (ranges.size() == 1) {
			Range range = ranges.get(0);
			response.addHeader("Content-Range", "bytes " + range.start + "-" + range.end + "/" + range.length);
			long length = range.end - range.start + 1;
			response.addHeader("Content-Length", Long.toString(length));
			if (mType != null) {
				response.setContentType(mType);
			}
			if (sendContext) {
				try {
					response.setBufferSize(8192);
				} catch (IllegalStateException e) {
					// Silent catch
				}
				if (ostream != null) {
					copy(file, ostream, range);
				} else {
					throw new IllegalStateException();
				}
			}
		} else {
			response.setContentType("multipart/byteranges; boundary=CATALINA_MIME_BOUNDARY");
			if (sendContext) {
				try {
					response.setBufferSize(8192);
				} catch (IllegalStateException e) {
				}
				if (ostream != null) {
					copy(file, ostream, ranges.iterator(), mType);
				} else {
					throw new IllegalStateException();
				}
			}
		}
	}

	protected void copy(File file, ServletOutputStream ostream, Iterator<Range> ranges, String contentType)
			throws IOException {

		IOException exception = null;

		while ((exception == null) && (ranges.hasNext())) {

			InputStream resourceInputStream = new FileInputStream(file);
			try (InputStream istream = new BufferedInputStream(resourceInputStream, 4096)) {

				Range currentRange = ranges.next();

				// Writing MIME header.
				ostream.println();
				ostream.println("--" + mimeSeparation);
				if (contentType != null)
					ostream.println("Content-Type: " + contentType);
				ostream.println("Content-Range: bytes " + currentRange.start + "-" + currentRange.end + "/"
						+ currentRange.length);
				ostream.println();

				// Printing content
				exception = copyRange(istream, ostream, currentRange.start, currentRange.end);
			}
		}

		ostream.println();
		ostream.print("--" + mimeSeparation + "--");

		if (exception != null)
			throw exception;

	}

	/* ------------------------------------------------------------ */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		serveResource(request, response, true);
	}

	/* ------------------------------------------------------------ */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doTrace(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

	/* ------------------------------------------------------------ */
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Allow", "GET,HEAD,POST,OPTIONS");
	}

	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		serveResource(request, response, false);
	}

	@SuppressWarnings("unchecked")
	private String acceptEncoding(HttpServletRequest request) {
		Enumeration<String> headers = request.getHeaders("Accept-Encoding");
		String ret = null;
		double bestResourceQuality = 0;
		while (headers.hasMoreElements()) {
			String header = headers.nextElement();
			for (String preference : header.split(",")) {
				double quality = 1;
				int qualityIdx = preference.indexOf(';');
				if (qualityIdx > 0) {
					int equalsIdx = preference.indexOf('=', qualityIdx + 1);
					if (equalsIdx == -1) {
						continue;
					}
					quality = Double.parseDouble(preference.substring(equalsIdx + 1).trim());
				}
				if (quality >= bestResourceQuality) {
					String encoding = preference;
					if (qualityIdx > 0) {
						encoding = encoding.substring(0, qualityIdx);
					}
					encoding = encoding.trim();
					if ("identity".equals(encoding)) {
						ret = null;
						bestResourceQuality = quality;
						continue;
					}
					if ("*".equals(encoding) || "gzip".equals(encoding)) {
						ret = "gzip";
						bestResourceQuality = quality;
						break;
					}
				}
			}
		}
		return ret;
	}

	protected void copy(File file, ServletOutputStream ostream, Range range) throws IOException {

		IOException exception = null;

		InputStream resourceInputStream = new FileInputStream(file);
		InputStream istream = new BufferedInputStream(resourceInputStream, 4096);
		exception = copyRange(istream, ostream, range.start, range.end);
		istream.close();
		if (exception != null)
			throw exception;

	}

	protected IOException copyRange(InputStream istream, ServletOutputStream ostream, long start, long end) {
		long skipped = 0;
		try {
			skipped = istream.skip(start);
		} catch (IOException e) {
			return e;
		}
		if (skipped < start) {
			return new IOException("S\u00F3lo se han saltado [" + skipped + "] cuando se requirieron [" + start + "]");
		}

		IOException exception = null;
		long bytesToRead = end - start + 1;

		byte buffer[] = new byte[4096];
		int len = buffer.length;
		while ((bytesToRead > 0) && (len >= buffer.length)) {
			try {
				len = istream.read(buffer);
				if (bytesToRead >= len) {
					ostream.write(buffer, 0, len);
					bytesToRead -= len;
				} else {
					ostream.write(buffer, 0, (int) bytesToRead);
					bytesToRead = 0;
				}
			} catch (IOException e) {
				exception = e;
				len = -1;
			}
			if (len < buffer.length)
				break;
		}

		return exception;

	}

	protected static class Range {

		public long start;
		public long end;
		public long length;

		/**
		 * Validate range.
		 *
		 * @return true if the range is valid, otherwise false
		 */
		public boolean validate() {
			if (end >= length)
				end = length - 1;
			return (start >= 0) && (end >= 0) && (start <= end) && (length > 0);
		}
	}

}
