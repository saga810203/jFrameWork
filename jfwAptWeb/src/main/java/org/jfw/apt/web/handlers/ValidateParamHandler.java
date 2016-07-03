package org.jfw.apt.web.handlers;

import java.util.ListIterator;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.RequestHandler;
import org.jfw.apt.web.handlers.validate.ParamValidateHandler;

public final class ValidateParamHandler extends RequestHandler {
	public static final ParamValidateHandler[] handlers = new ParamValidateHandler[] {};

	@Override
	public void init() {
	}

	@Override
	public void appendBeforCode(ClassWriter cw) throws AptException {
		for (ListIterator<MethodParamEntry> it = this.aptWebHandler.getMe().getParams().listIterator(); it.hasNext();) {
			MethodParamEntry mpe = it.next();
			for (ParamValidateHandler handler : handlers) {
				if (handler.match(mpe.getRef()))
					handler.handle(cw, mpe, this.aptWebHandler);
			}
		}
	}
}
