package org.jfw.apt.orm.core.defaultImpl;

public class BigDecimalHandler extends AbstractColumnHandler{
	
	@Override
	public String supportsClass() {
		return "java.math.BigDecimal";
	}
	@Override
	public String getReadMethod() {
		// TODO Auto-generated method stub
		return "getBigDecimal";
	}

	@Override
	public String getWriteMethod() {
		return "setBigDecimal";
	}

	@Override
	protected int getSqlType() {
		return java.sql.Types.DECIMAL;
	}

}
