package org.jfw.apt.orm.core.defaultImpl;

import org.jfw.apt.orm.core.ColumnWriterCache;
import org.jfw.apt.out.ClassWriter;

public class ByteHandler extends WByteHandler {
	
	
	@Override
	public String supportsClass() {
		return "byte";
	}

	@Override
	public ColumnWriterCache init(String valEl, boolean useTmpVar, boolean nullable, ClassWriter cw) {
		return super.init(valEl, useTmpVar, false, cw);
	}
}
