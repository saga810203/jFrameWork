package org.jfw.apt.orm.core.defaultImpl;

public class StringHandler extends AbstractColumnHandler {
	@Override
	public String supportsClass() {
		return "java.lang.String";
	}
	@Override
	public String getReadMethod() {
		return "getString";
	}
	@Override
	public String getWriteMethod() {
		return "setString";
	}
	@Override
	protected int getSqlType() {
		return java.sql.Types.VARCHAR;
	}
}
