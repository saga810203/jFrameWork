package org.jfw.apt.orm.core.defaultImpl;

import org.jfw.apt.orm.core.ColumnWriterCache;
import org.jfw.apt.out.ClassWriter;

public class WBooleanHandler extends AbstractColumnHandler {

	@Override
	public ColumnWriterCache init(String valEl, boolean useTmpVar, boolean nullable, ClassWriter cw) {
		return super.init(valEl, useTmpVar, true, cw);
	}

	@Override
	public void readValue(ClassWriter cw, String beforCode, String afterCode, int colIndex, boolean dbNullable) {
		String lv = cw.getMethodTempVarName();
		cw.bL(this.supportsClass()).w(" ").w(lv).w(" = \"1\".equals(rs.getString(").w(colIndex).el("));");
		cw.beginIf().w("rs.wasNull()").endIf();
		cw.bL(lv).el(" = null;");
		cw.writeEndBrace();
		cw.bL(beforCode == null ? "" : beforCode).w(lv).el(afterCode == null ? "" : afterCode);
	}


	@Override
	public void writeValue(ColumnWriterCache cwc, boolean dynamicValue) {
		ClassWriter cw = cwc.getCw();
		this.checkParamIndex(cw);
		if (dynamicValue) {
			cw.bL("if(!").w(cwc.getCheckNullVar()).el("){");
			cw.bL("ps.setString(").w(this.getParamIndexName(cw)).w("++,(")
					.w(cwc.isUseTmpVar() ? cwc.getCacheValVar() : cwc.getValEl()).el(")?\"1\":\"0\");");
			cw.l("}");
		} else {
			cw.bL("if(").w(cwc.getCheckNullVar()).el("){");
			cw.bL("ps.setNull(").w(this.getParamIndexName(cw)).w("++,").w(java.sql.Types.CHAR).el(");");
			cw.l("}else{");
			cw.bL("ps.setString(").w(this.getParamIndexName(cw)).w("++,(")
					.w(cwc.isUseTmpVar() ? cwc.getCacheValVar() : cwc.getValEl()).el(")?\"1\":\"0\");");
			cw.l("}");
		}
	}

	@Override
	public String supportsClass() {
		return "java.lang.Boolean";
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
	protected int getSqlType() {
		throw new UnsupportedOperationException();
	}

}
