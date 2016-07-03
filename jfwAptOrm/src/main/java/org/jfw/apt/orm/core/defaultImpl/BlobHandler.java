package org.jfw.apt.orm.core.defaultImpl;

public class BlobHandler extends AbstractColumnHandler{

	@Override
	public String supportsClass() {
		return "byte[]";
	}
	
	@Override
	public String getReadMethod() {
		return "getBytes";
	}

	@Override
	public String getWriteMethod() {
		return "setBytes";
	}

	@Override
	protected int getSqlType() {
		return java.sql.Types.BLOB;
	}

}
