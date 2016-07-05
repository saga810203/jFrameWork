package org.jfw.apt.orm.daoHandler;

import java.util.ListIterator;

import javax.lang.model.element.Element;

import org.jfw.apt.Util;
import org.jfw.apt.annotation.DefaultValue;
import org.jfw.apt.annotation.Nullable;
import org.jfw.apt.orm.annotation.dao.method.operator.QueryOne;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.model.CalcColumn;

public class QueryOneHandler extends BaseQueryHandler {

	private String defaultValue;
	private boolean withNullable;

	@Override
	public boolean match(Element ele) {
		this.withNullable = null != ele.getAnnotation(Nullable.class);
		return null != ele.getAnnotation(QueryOne.class);
	}

	@Override
	protected String getDefReturnClassName() {
		DefaultValue dv = this.ref.getAnnotation(DefaultValue.class);
		this.defaultValue = null == dv ? null : Util.emptyToNull(dv.value());
		return this.me.getReturnType();
	}

	@Override
	protected void buildResult() {

		cw.l("if(rs.next()){");
		cw.bL(this.returnClassName).el("_result = new ").w(this.returnClassName).el("();");
		int i = 1;
		for (ListIterator<CalcColumn> it = this.fields.listIterator(); it.hasNext();) {
			CalcColumn col = it.next();
			ColumnHandlerFactory.get(col.getHandlerClass()).readValue(cw, "_result." + col.getSetter() + "(", ");", i++,
					col.isNullable());
		}
		cw.l("return _result;");
		cw.l("}else{");
		if (this.defaultValue == null) {
			if (this.withNullable)
				cw.l("return null;");
			else
				cw.l("throw new org.jfw.util.exception.JfwBaseException(201);");
		} else {
			cw.bL("return ").ws(this.defaultValue).el(";");
		}
		cw.l("}");
	}

}
