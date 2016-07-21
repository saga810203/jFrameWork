package org.jfw.util.context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.sql.DataSource;

import org.jfw.util.ConstData;
import org.jfw.util.StringUtil;
import org.jfw.util.bean.BeanFactory;
import org.jfw.util.io.MultiInputStreamHandler;
import org.jfw.util.io.ResourceUtil;

public final class JfwAppContext {
	
	private static ConcurrentHashMap<Object,Object> cache = new ConcurrentHashMap<Object,Object>();
	private static boolean actived = false;

	private static BeanFactory beanFactory = null;;

	private static DataSource dataSource = null;

	private static ScheduledExecutorService scheduledExecutorService = null;

	private static LinkedList<Runnable> destoryeds = new LinkedList<Runnable>();

	
	private static void checkActived(){
		if(!actived) throw new RuntimeException("JfwAppContext is unusable");
	}
	
	public static Object removeCachedObject(Object key){
		return cache.remove(key);
	}
	public static Object getCachedObject(Object key){
		return cache.get(key);
	}
	
	public static Object cacheObject(Object key,Object value){
		return cache.put(key, value);
	}
	public static boolean cacheObjectIfAbsent(Object key,Object value){
		return null == cache.putIfAbsent(key, value);
	}
	
	public static String cacheObjectAndGenKey(Object value){
		String key = StringUtil.buildUUID();
		while(true){
			if(null == cache.putIfAbsent(key, value)){
				return key;
			}
			key = StringUtil.buildUUID();
		}
	}
	
	public static void addDestoryed(Runnable runnable) {
		checkActived();
		destoryeds.add(runnable);
	}

	public static ScheduledExecutorService getScheduledExecutorService() {
		checkActived();
		if (null == scheduledExecutorService) {
			createDefaultScheduledExecutorService();
		}
		return scheduledExecutorService;
	}

	synchronized private static void createDefaultScheduledExecutorService() {
		if (null != scheduledExecutorService)
			return;
		scheduledExecutorService = Executors.newScheduledThreadPool(0);
	}

	public static BeanFactory getBeanFactory() {
		checkActived();
		return beanFactory;
	}

	public static DataSource getDataSource() {
		checkActived();
		return dataSource;
	}

	synchronized public static void init(BeanFactory bf) {
		beanFactory = bf;
		dataSource = bf.getBean("dataSource", DataSource.class);
		scheduledExecutorService = bf.getBean("scheduledExecutorService", ScheduledExecutorService.class);
		actived= true;
	}

	synchronized public static void destory() {
		try {
			try {
				if (null != scheduledExecutorService)
					scheduledExecutorService.shutdownNow();
			} finally {
				scheduledExecutorService = null;
			}
			for (Runnable r : destoryeds) {
				try {
					r.run();
				} catch (Throwable th) {
				}
			}
			cache.clear();
		} finally {
			actived = false;
		}
	}

	public static Map<String, String> readConfig(String configFileName) throws Exception {
		return ResourceUtil.<Map<String, String>> readClassResource(configFileName, new ConfigReader(),
				Thread.currentThread().getContextClassLoader());
	}

	private JfwAppContext() {
	}

	static class LineReader {
		public LineReader(Reader reader) {
			this.reader = reader;
			inCharBuf = new char[8192];
		}

		byte[] inByteBuf;
		char[] inCharBuf;
		char[] lineBuf = new char[1024];
		int inLimit = 0;
		int inOff = 0;
		InputStream inStream;
		Reader reader;

		int readLine() throws IOException {
			int len = 0;
			char c = 0;

			boolean skipWhiteSpace = true;
			boolean isCommentLine = false;
			boolean isNewLine = true;
			boolean appendedLineBegin = false;
			boolean precedingBackslash = false;
			boolean skipLF = false;

			while (true) {
				if (inOff >= inLimit) {
					inLimit = (inStream == null) ? reader.read(inCharBuf) : inStream.read(inByteBuf);
					inOff = 0;
					if (inLimit <= 0) {
						if (len == 0 || isCommentLine) {
							return -1;
						}
						return len;
					}
				}
				if (inStream != null) {
					c = (char) (0xff & inByteBuf[inOff++]);
				} else {
					c = inCharBuf[inOff++];
				}
				if (skipLF) {
					skipLF = false;
					if (c == '\n') {
						continue;
					}
				}
				if (skipWhiteSpace) {
					if (c == ' ' || c == '\t' || c == '\f') {
						continue;
					}
					if (!appendedLineBegin && (c == '\r' || c == '\n')) {
						continue;
					}
					skipWhiteSpace = false;
					appendedLineBegin = false;
				}
				if (isNewLine) {
					isNewLine = false;
					if (c == '#' || c == '!') {
						isCommentLine = true;
						continue;
					}
				}

				if (c != '\n' && c != '\r') {
					lineBuf[len++] = c;
					if (len == lineBuf.length) {
						int newLength = lineBuf.length * 2;
						if (newLength < 0) {
							newLength = Integer.MAX_VALUE;
						}
						char[] buf = new char[newLength];
						System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
						lineBuf = buf;
					}
					if (c == '\\') {
						precedingBackslash = !precedingBackslash;
					} else {
						precedingBackslash = false;
					}
				} else {
					// reached EOL
					if (isCommentLine || len == 0) {
						isCommentLine = false;
						isNewLine = true;
						skipWhiteSpace = true;
						len = 0;
						continue;
					}
					if (inOff >= inLimit) {
						inLimit = (inStream == null) ? reader.read(inCharBuf) : inStream.read(inByteBuf);
						inOff = 0;
						if (inLimit <= 0) {
							return len;
						}
					}
					if (precedingBackslash) {
						len -= 1;
						// skip the leading whitespace characters in following
						// line
						skipWhiteSpace = true;
						appendedLineBegin = true;
						precedingBackslash = false;
						if (c == '\r') {
							skipLF = true;
						}
					} else {
						return len;
					}
				}
			}
		}
	}

	public static class ConfigReader implements MultiInputStreamHandler<Map<String, String>> {
		private Map<String, String> pMap = new HashMap<String, String>();

		private void load(LineReader lr) throws IOException {
			char[] convtBuf = new char[1024];
			int limit;
			int keyLen;
			int valueStart;
			char c;
			boolean hasSep;
			boolean precedingBackslash;

			while ((limit = lr.readLine()) >= 0) {
				c = 0;
				keyLen = 0;
				valueStart = limit;
				hasSep = false;
				precedingBackslash = false;
				while (keyLen < limit) {
					c = lr.lineBuf[keyLen];
					if ((c == '=') && !precedingBackslash) {
						valueStart = keyLen + 1;
						hasSep = true;
						break;
					} else if ((c == ' ' || c == '\t' || c == '\f') && !precedingBackslash) {
						valueStart = keyLen + 1;
						break;
					}
					if (c == '\\') {
						precedingBackslash = !precedingBackslash;
					} else {
						precedingBackslash = false;
					}
					keyLen++;
				}
				while (valueStart < limit) {
					c = lr.lineBuf[valueStart];
					if (c != ' ' && c != '\t' && c != '\f') {
						if (!hasSep && (c == '=' || c == ':')) {
							hasSep = true;
						} else {
							break;
						}
					}
					valueStart++;
				}
				String key = loadConvert(lr.lineBuf, 0, keyLen, convtBuf);
				String value = loadConvert(lr.lineBuf, valueStart, limit - valueStart, convtBuf);
				this.pMap.put(key, value);
			}
		}

		private String loadConvert(char[] in, int off, int len, char[] convtBuf) {
			if (convtBuf.length < len) {
				int newLen = len * 2;
				if (newLen < 0) {
					newLen = Integer.MAX_VALUE;
				}
				convtBuf = new char[newLen];
			}
			char aChar;
			char[] out = convtBuf;
			int outLen = 0;
			int end = off + len;

			while (off < end) {
				aChar = in[off++];
				if (aChar == '\\') {
					aChar = in[off++];
					if (aChar == 'u') {
						// Read the xxxx
						int value = 0;
						for (int i = 0; i < 4; i++) {
							aChar = in[off++];
							switch (aChar) {
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + aChar - '0';
								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + 10 + aChar - 'a';
								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + 10 + aChar - 'A';
								break;
							default:
								throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
							}
						}
						out[outLen++] = (char) value;
					} else {
						if (aChar == 't')
							aChar = '\t';
						else if (aChar == 'r')
							aChar = '\r';
						else if (aChar == 'n')
							aChar = '\n';
						else if (aChar == 'f')
							aChar = '\f';
						out[outLen++] = aChar;
					}
				} else {
					out[outLen++] = aChar;
				}
			}
			return new String(out, 0, outLen);
		}

		@Override
		public void handle(InputStream in) throws Exception {
			load(new LineReader(new InputStreamReader(in, ConstData.UTF8)));
		}

		@Override
		public Map<String, String> get() {
			return this.pMap;
		}
	}
}
