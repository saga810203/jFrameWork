package org.jfw.apt.orm.core.defaultImpl;

public class WByteHandler extends AbstractColumnHandler{

	@Override
	public String supportsClass() {
		return "java.lang.Byte";
	}

	@Override
	public String getReadMethod() {
		return "getByte";
	}

	@Override
	public String getWriteMethod() {
		return "setByte";
	}
	
	@Override
	protected int getSqlType() {
		return java.sql.Types.TINYINT;
	}

}
