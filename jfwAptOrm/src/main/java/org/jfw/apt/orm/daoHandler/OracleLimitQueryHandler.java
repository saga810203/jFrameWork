package org.jfw.apt.orm.daoHandler;

import java.lang.annotation.Annotation;
import java.util.ListIterator;

import javax.lang.model.element.Element;

import org.jfw.apt.AptConfig;
import org.jfw.apt.Util;
import org.jfw.apt.orm.annotation.dao.method.operator.LimitQuery;
import org.jfw.apt.orm.core.model.CalcColumn;

public class OracleLimitQueryHandler extends BaseLimitQueryHandler {

	@Override
	public boolean match(Element ele) {
		LimitQuery lq = ele.getAnnotation(LimitQuery.class);
		if (lq == null)
			return false;
		String dv = Util.emptyToNull(lq.value());
		if (dv == null) {
			if (AptConfig.get("DB:Oracle") != null)
				return true;
			return false;
		} else {
			if (dv.equals("Oracle"))
				return true;
			return false;
		}
	}

	@Override
	protected Class<? extends Annotation> getAnnotationClass() {
		return LimitQuery.class;
	}

	protected void prepareSQL() {
		if (this.wherePart != null)
			this.wherePart.prepare(cw);
		if (this.dynamic) {
			cw.l("StringBuilder sql = new StringBuilder();");
			cw.bL("sql.append(\"");
		} else {
			cw.l("String sql =\"");
		}
		cw.w("SELECT");
		boolean first = true;
		for (ListIterator<CalcColumn> it = this.fields.listIterator(); it.hasNext();) {
			if (first) {
				first = false;
				cw.w(" ");
			} else {
				cw.w(",");
			}
			cw.ws(it.next().getSqlName());
		}
		cw.w(" FROM ").ws(this.fromSentence);
		if (this.dynamic) {
			cw.el("\");");
			if (!this.wherePart.hasStaticPart()) {
				String tmpVar = cw.getMethodTempVarName();
				cw.bL("int ").w(tmpVar).el(" = sql.length()");
				this.wherePart.WriteDynamic(cw);
				cw.bL("if(").w(tmpVar).w(tmpVar).el(" == sql.length()){");
				cw.l("sql.append(\" WHERE \");");
				cw.l("}else{");
				cw.l("sql.append(\" AND \");");
				cw.l("}");
				cw.bL("sql.append(\"ROWNUM<=\").append(").w(this.limitName).el(");");

			} else {
				this.wherePart.WriteDynamic(cw);
				cw.bL("sql.append(\" AND ROWNUM<=\").append(").w(this.limitName).el(");");
			}
			if (null != this.orderBy)
				cw.bL("sql.append(\" ").ws(this.orderBy).el("\");");
		} else {
			if (null != this.wherePart) {
				cw.w(" AND ROWNUM<=\"+").w(this.limitName);
			} else {
				cw.w(" WHERE ROWNUM<=\"+").w(this.limitName);
			}
			if (this.orderBy != null) {
				cw.w("+\"").ws(this.orderBy).w("\"");

			}
			cw.el(";");
		}
	}
}
