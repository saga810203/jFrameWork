package org.jfw.util.log;

import java.util.ServiceLoader;

public class LogFactory {

	private static final Object initLock = new Object();
	private static volatile LogFactory defaule = null;
	private static Logger defaultLog = new NoLogger();

	public Logger getLogger(Class<?> clazz) {
		return LogFactory.defaultLog;
	}

	public static Logger getLog(Class<?> clazz) {
		if (defaule == null) {
			synchronized (initLock) {
				if (null == defaule) {
					try {
						ServiceLoader<LogFactory> sl = ServiceLoader.load(LogFactory.class,
								LogFactory.class.getClassLoader());
						for (LogFactory lf : sl) {
							if (lf != null) {
								defaule = lf;
								break;
							}
						}
					} catch (Exception e) {
						defaule = null;
					    e.printStackTrace();
					}
					if(null == defaule) defaule = new LogFactory();
				}
			}
		}
		return LogFactory.defaule.getLogger(clazz);
	}

}
