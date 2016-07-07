package org.jfw.apt.orm.core.defaultImpl;

import org.jfw.apt.Util;
import org.jfw.apt.orm.core.ColumnHandler;
import org.jfw.apt.orm.core.ColumnWriterCache;
import org.jfw.apt.out.ClassWriter;

public abstract class AbstractColumnHandler implements ColumnHandler {


	public abstract String getReadMethod();

	public abstract String getWriteMethod();

	@Override
	public void readValue(ClassWriter cw, String beforCode, String afterCode, int colIndex, boolean dbNullable) {
		if ((!dbNullable) || Util.isPrimitive(this.supportsClass())) {
			cw.l((beforCode == null ? "" : beforCode) + "rs." + this.getReadMethod() + "(" + colIndex + ")"
					+ (afterCode == null ? "" : afterCode));
		} else {
			String lv = cw.getMethodTempVarName();

			cw.bL(this.supportsClass()).w(" ").w(lv).w(" = rs.").w(this.getReadMethod()).w("(").w(colIndex).el(");");
			cw.beginIf().w("rs.wasNull()").endIf();
			cw.bL(lv).el(" = null;");
			cw.l("}");
			cw.bL(beforCode == null ? "" : beforCode).w(lv).el(afterCode == null ? "" : afterCode);
		}
	}

	@Override
	public ColumnWriterCache init(String valEl, boolean useTmpVar, boolean nullable, ClassWriter cw) {
		return ColumnWriterCache.build(valEl, useTmpVar, nullable, cw, this);
	}

	public void checkParamIndex(ClassWriter cw) {
		cw.checkJdbcParamIndex();
	}

	
	public String getParamIndexName(ClassWriter cw) {
		return cw.getJdbcParamIndexName();
	}

	public static String genKeyForCheckNullOrCache(String varName) {
		return AbstractColumnHandler.class.getName() + "^^^" + varName + "@@@";
	}

	@Override
	public void prepare(ColumnWriterCache cwc) {
		ClassWriter cw = cwc.getCw();
		this.checkParamIndex(cw);
		String cvv = cwc.getCacheValVar();
		String cnv = cwc.getCheckNullVar();

		if (null != cvv) {
			String cvv_Exists = genKeyForCheckNullOrCache(cvv);
			if (!cw.getMethodScope().containsKey(cvv_Exists)) {
				cw.bL(this.supportsClass()).w(" ").w(cvv).w(" = ").w(cwc.getValEl()).el(";");
				cw.getMethodScope().put(cvv_Exists, Boolean.TRUE);
			}
		}
		if (null != cnv) {
			String cnv_Exists = genKeyForCheckNullOrCache(cnv);
			if (!cw.getMethodScope().containsKey(cnv_Exists)) {
				cw.bL("boolean ").w(cnv).w(" = null == ").w(null == cvv ? cwc.getValEl() : cvv).el(";");
				cw.getMethodScope().put(cnv_Exists, Boolean.TRUE);
			}
		}
	}

	@Override
	public String getCheckNullVal(ColumnWriterCache cwc) {
		if (null == cwc.getCheckNullVar())
			throw new UnsupportedOperationException();
		return cwc.getCheckNullVar();
	}

	@Override
	public void writeValue(ColumnWriterCache cwc, boolean dynamicValue) {
		ClassWriter cw = cwc.getCw();
		this.checkParamIndex(cw);
		if (cwc.isNullable()) {
			if (dynamicValue) {
				cw.bL("if(!").w(cwc.getCheckNullVar()).el("){");
				cw.bL("ps.").w(this.getWriteMethod()).w("(").w(this.getParamIndexName(cw)).w("++,")
						.w(cwc.isUseTmpVar() ? cwc.getCacheValVar() : cwc.getValEl()).el(");");
				cw.l("}");
			} else {
				cw.bL("if(").w(cwc.getCheckNullVar()).el("){");
				cw.bL("ps.setNull(").w(this.getParamIndexName(cw)).w("++,").w(this.getSqlType()).el(");");
				cw.l("}else{");
				cw.bL("ps.").w(this.getWriteMethod()).w("(").w(this.getParamIndexName(cw)).w("++,")
						.w(cwc.isUseTmpVar() ? cwc.getCacheValVar() : cwc.getValEl()).el(");");
				cw.l("}");
			}
		} else {
			cw.bL("ps.").w(this.getWriteMethod()).w("(").w(this.getParamIndexName(cw)).w("++,")
					.w(cwc.isUseTmpVar() ? cwc.getCacheValVar() : cwc.getValEl()).el(");");
		}
	}

	protected abstract int getSqlType();

	@Override
	public boolean isReplaceResource(ColumnWriterCache cwc) {
		return false;
	}

	@Override
	public void replaceResource(ColumnWriterCache cwc) {
	}
}
