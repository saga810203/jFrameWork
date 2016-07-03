package org.jfw.apt.orm.core.defaultImpl;

public class WShortHandler extends AbstractColumnHandler {

	@Override
	public String supportsClass() {
		return "java.lang.Short";
	}

	@Override
	public String getReadMethod() {
		return "getShort";
	}

	@Override
	public String getWriteMethod() {
		return "setShort";
	}

	@Override
	protected int getSqlType() {
		return java.sql.Types.SMALLINT;
	}

}
