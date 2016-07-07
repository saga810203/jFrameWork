package org.jfw.apt.orm.daoHandler;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.ListIterator;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.model.CalcColumn;
import org.jfw.apt.orm.core.model.where.WhereFactory;

public abstract class BaseLimitQueryHandler extends BaseQueryHandler {
	protected String limitName;

	protected abstract Class<? extends Annotation> getAnnotationClass();

	@Override
	protected String getDefReturnClassName() throws AptException {
		this.limitName = null;
		for (ListIterator<MethodParamEntry> it = this.me.getParams().listIterator(); it.hasNext();) {
			MethodParamEntry mpe = it.next();
			String name = mpe.getName();
			if ("int".equals(mpe.getTypeName())) {
				if (name.equals(FIRST_LIMIT_NAME)) {
					this.limitName = name;
				} else if (name.equals(SECOND_LIMIT_NAME) && (null == this.limitName)) {
					this.limitName = name;
				} 
			}
		}
		if (null == this.limitName)
			throw new AptException(ref,
					"Method[@" + this.getAnnotationClass().getName() + "] must has param (int rows) or (int _rows)");
		

		String rcn = this.me.getReturnType();
		if ((rcn.length() <= LIST_PREFIX.length()) || (!rcn.startsWith(LIST_PREFIX)) || (!rcn.endsWith(LIST_SUFFIX)))
			throw new AptException(ref,
					"Method[@" + this.getAnnotationClass().getName() + "] must return java.util.List<?>");
		return rcn.substring(LIST_RCN_INDEX, rcn.length() - 1);

	}


	@Override
	protected void buildResult() {
		cw.bL(LIST_PREFIX).w(this.returnClassName).w(LIST_SUFFIX).w(" _result = new java.util.ArrayList<")
				.w(this.returnClassName).el(">();");
		cw.l("while(rs.next()){");
		cw.bL(this.returnClassName).w(" _obj =  new ").w(this.returnClassName).el("();");
		int i=1;
		for(ListIterator<CalcColumn> it = this.fields.listIterator(); it.hasNext();){
			CalcColumn col = it.next();
			ColumnHandlerFactory.get(col.getHandlerClass()).readValue(cw, "_obj."+col.getSetter()+"(", ");", i++, col.isNullable());
		}
		cw.l("_result.add(_obj);");
		cw.l("}");
		cw.l("return _result;");
	}


	protected void buildWhere() throws AptException {
		this.wherePart = WhereFactory.build(this.me, this.fromDataEntry, false,
				Arrays.asList(this.limitName));
		this.dynamic = (null != this.wherePart) && this.wherePart.isDynamic();
	}

}
