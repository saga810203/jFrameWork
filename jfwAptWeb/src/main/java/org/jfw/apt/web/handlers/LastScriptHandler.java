package org.jfw.apt.web.handlers;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.RequestHandler;
import org.jfw.apt.web.annotation.method.LastScript;

public class LastScriptHandler extends RequestHandler {

	@Override
	public void appendBeforCode(ClassWriter cw) throws AptException {
		LastScript ls = this.aptWebHandler.getMe().getRef().getAnnotation(LastScript.class);
		if (ls != null) {
			String[] ss = ls.value();
			if (ss != null && ss.length > 0) {
				for (int i = 0; i < ss.length; ++i) {
					if (null != ss[i])
						cw.l(ss[i]);
				}
			}
		}
	}

	@Override
	public void init() {
	}
}