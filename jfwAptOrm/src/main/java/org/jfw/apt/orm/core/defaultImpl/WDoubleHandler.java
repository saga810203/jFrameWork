package org.jfw.apt.orm.core.defaultImpl;

public class WDoubleHandler extends AbstractColumnHandler{

	@Override
	public String supportsClass() {
		return "java.lang.Double";
	}

	@Override
	public String getReadMethod() {
		return "getDouble";
	}

	@Override
	public String getWriteMethod() {
		return "setDouble";
	}

	@Override
	protected int getSqlType() {
		return java.sql.Types.DOUBLE;
	}
	

}
