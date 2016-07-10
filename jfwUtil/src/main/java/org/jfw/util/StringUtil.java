package org.jfw.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class StringUtil {
	private StringUtil() {
	}

	public static String fromWithUTF8(InputStream in) throws IOException {
		char[] buf = new char[1024];
		Reader r = new BufferedReader(new InputStreamReader(in, ConstData.UTF8));
		int len = 0;
		StringBuilder sb = new StringBuilder();
		while ((len = r.read(buf)) != -1) {
			if (len > 0)
				sb.append(buf, 0, len);
		}
		return sb.toString();
	}

	public static String fromByUTF8AndClose(InputStream in) throws IOException {
		try {
			char[] buf = new char[1024];
			Reader r = new BufferedReader(new InputStreamReader(in, ConstData.UTF8));
			int len = 0;
			StringBuilder sb = new StringBuilder();
			while ((len = r.read(buf)) != -1) {
				if (len > 0)
					sb.append(buf, 0, len);
			}
			return sb.toString();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
	}

	public static Map<String, String[]> decodeURLQueryString(String queryString) throws UnsupportedEncodingException {
		Map<String, String[]> result = new HashMap<String, String[]>();
		if (queryString != null) {
			List<String> list = ListUtil.splitTrimExcludeEmpty(queryString, '&');
			for (String str : list) {
				int index = str.indexOf('=');
				if (index > 0) {
					String key = str.substring(0, index);
					String value = str.substring(index + 1);
					if (value.length() > 0)
						value = java.net.URLDecoder.decode(str.substring(index + 1), "UTF-8");
					String[] values = result.get(key);
					if (values == null) {
						values = new String[] { value };
					} else {
						String[] nv = new String[values.length + 1];
						System.arraycopy(values, 0, nv, 0, values.length);
						nv[values.length] = value;
						values = nv;
					}
					result.put(key, values);
				}
			}

		}

		return result;
	}

	public static String buildUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase(Locale.US);
	}

}
