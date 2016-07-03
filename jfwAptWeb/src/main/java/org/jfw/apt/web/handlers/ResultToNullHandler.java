package org.jfw.apt.web.handlers;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.RequestHandler;
import org.jfw.apt.web.annotation.method.ResultToNull;

public class ResultToNullHandler extends RequestHandler {

	@Override
	public void init() {
	}

	@Override
	public void appendBeforCode(ClassWriter cw) throws AptException {
		if (null != this.aptWebHandler.getMe().getRef().getAnnotation(ResultToNull.class)) {

			String returnType = this.aptWebHandler.getReturnType();

			if ((!"void".equals(returnType)) && (!Util.isPrimitive(returnType))) {
				cw.l("result = null;");
			}
		}
	}

}
