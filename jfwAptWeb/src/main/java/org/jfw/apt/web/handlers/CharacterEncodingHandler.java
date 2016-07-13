package org.jfw.apt.web.handlers;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.RequestHandler;
import org.jfw.apt.web.annotation.method.CharacterEnocding;

public class CharacterEncodingHandler extends RequestHandler {

	@Override
	public void init() {
	}

	@Override
	public void appendBeforCode(ClassWriter cw) throws AptException {
		CharacterEnocding ce = this.aptWebHandler.getMe().getRef().getAnnotation(CharacterEnocding.class);
		String encoding = ce == null ? "UTF-8" : Util.emptyToNull(ce.encoding());
		if (encoding == null)
			encoding = "UTF-8";
		boolean force = ce == null ? true : ce.force();
		if (force) {
			cw.l("if(null == req.getCharacterEncoding()){");
			cw.bL("req.setCharacterEncoding(\"").w(encoding).el("\");");
			if (force) {
				cw.bL("res.setCharacterEncoding(\"").w(encoding).el("\");");
			}
			cw.l("}");
		}
	}

}
