package org.jfw.apt.orm.core.defaultImpl;

public class WFloatHandler extends AbstractColumnHandler{

	@Override
	public String supportsClass() {
		return "java.lang.Float";
	}

	@Override
	public String getReadMethod() {
		return "getFloat";
	}

	@Override
	public String getWriteMethod() {
		return "setFloat";
	}

	@Override
	protected int getSqlType() {
		return java.sql.Types.FLOAT;
	}

}
