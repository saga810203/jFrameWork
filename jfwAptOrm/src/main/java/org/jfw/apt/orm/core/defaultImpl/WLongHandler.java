package org.jfw.apt.orm.core.defaultImpl;

public class WLongHandler extends AbstractColumnHandler{

	@Override
	public String supportsClass() {
		return "java.lang.Long";
	}

	@Override
	public String getReadMethod() {
		return "getLong";
	}

	@Override
	public String getWriteMethod() {
		return "setLong";
	}

	@Override
	protected int getSqlType() {
		return java.sql.Types.BIGINT;
	}

}
