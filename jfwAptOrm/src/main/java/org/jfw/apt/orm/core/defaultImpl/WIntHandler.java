package org.jfw.apt.orm.core.defaultImpl;

public class WIntHandler extends AbstractColumnHandler{

	@Override
	public String supportsClass() {
		return "java.lang.Integer";
	}

	@Override
	public String getReadMethod() {
		return "getInt";
	}

	@Override
	public String getWriteMethod() {
		return "setInt";
	}

	@Override
	protected int getSqlType() {
		return java.sql.Types.INTEGER;
	}

}
