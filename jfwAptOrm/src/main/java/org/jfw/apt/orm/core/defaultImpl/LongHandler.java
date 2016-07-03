package org.jfw.apt.orm.core.defaultImpl;

import org.jfw.apt.orm.core.ColumnWriterCache;
import org.jfw.apt.out.ClassWriter;

public class LongHandler extends WLongHandler{

	@Override
	public String supportsClass() {
		return "long";
	}

	@Override
	public ColumnWriterCache init(String valEl, boolean useTmpVar, boolean nullable, ClassWriter cw) {
		return super.init(valEl, useTmpVar, false, cw);
	}
	
}
