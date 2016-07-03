package org.jfw.apt.orm.core.defaultImpl;

import org.jfw.apt.orm.core.ColumnWriterCache;
import org.jfw.apt.out.ClassWriter;

public class BooleanHandler extends AbstractColumnHandler{

	@Override
	public String supportsClass() {
		return "boolean";
	}
	
	
	

	@Override
	public void readValue(ClassWriter cw, String beforCode, String afterCode, int colIndex, boolean dbNullable) {
		cw.l((beforCode == null ? "" : beforCode) + "\"1\".equals(rs.getString(" + colIndex + "))"
				+ (afterCode == null ? "" : afterCode));
	}




	@Override
	public void prepare(ColumnWriterCache cwc) {
		this.checkParamIndex(cwc.getCw());
	}




	@Override
	public void writeValue(ColumnWriterCache cwc, boolean dynamicValue) {
		ClassWriter cw = cwc.getCw();
		cw.bL("ps.setString(").w(this.getParamIndexName(cw)).w("++,(").w(cwc.getValEl()).el(")?\"1\":\"0\");");
	}




	@Override
	public String getReadMethod() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getWriteMethod() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCheckNullVal(ColumnWriterCache cwc) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected int getSqlType() {
		throw new UnsupportedOperationException();
	}

}
