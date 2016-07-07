package org.jfw.apt.orm.daoHandler;

import java.lang.annotation.Annotation;
import java.util.ListIterator;

import javax.lang.model.element.Element;

import org.jfw.apt.AptConfig;
import org.jfw.apt.Util;
import org.jfw.apt.orm.annotation.dao.method.operator.LimitQuery;
import org.jfw.apt.orm.core.model.CalcColumn;

public class PostgreSQLLimitQueryHandler extends BaseLimitQueryHandler {

	@Override
	public boolean match(Element ele) {
		LimitQuery lq = ele.getAnnotation(LimitQuery.class);
		if (lq == null)
			return false;
		String dv = Util.emptyToNull(lq.value());
		if (dv == null) {
			if (AptConfig.get("DB:PostgreSQL") != null)
				return true;
			return false;
		} else {
			if (dv.equals("PostgreSQL"))
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
			cw.bL("sql.append(\"SELECT");
		} else {
			cw.bL("String sql = \"SELECT");
		}
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
			this.wherePart.WriteDynamic(cw);
			if (null != this.orderBy)
				cw.bL("sql.append(\" ").ws(this.orderBy).el("\");");
			cw.bL("sql.append(\" LIMIT \").append(").w(this.limitName).el(");");
		} else {
			if (null != this.wherePart) {
				this.wherePart.WriteStaticTo(cw);
			}
			if (this.orderBy != null) {
				cw.w(" ").ws(this.orderBy);
			}
			cw.w(" LIMIT \"+").w(this.limitName).el(";");
		}
	}
}
