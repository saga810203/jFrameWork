package org.jfw.apt.orm.core.defaultImpl;

public class FixLenStringHandler extends StringHandler{

	@Override
	protected int getSqlType() {
		return java.sql.Types.CHAR;
	}

}
