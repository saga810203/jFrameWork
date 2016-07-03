package org.jfw.apt.orm.daoHandler;

import java.util.ListIterator;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.dao.method.operator.QueryList;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.model.CalcColumn;

public class QueryListHandler extends BaseQueryHandler {


	@Override
	public boolean match(Element ele) {
		return null != this.ref.getAnnotation(QueryList.class);
	}

	@Override
	protected String getDefReturnClassName() throws AptException {
		String rcn = this.me.getReturnType();
		if ((rcn.length() <= LIST_MIN_INDEX) || (!rcn.startsWith(LIST_PREFIX)) || (!rcn.endsWith(LIST_SUFFIX)))
			throw new AptException(ref, "Method[@" + QueryList.class.getName() + "] must return java.util.List<?>");
		return rcn.substring(LIST_RCN_INDEX, rcn.length() - 1);

	}

	@Override
	protected void buildResult() {
		cw.bL(LIST_PREFIX).w(this.returnClassName).w(LIST_SUFFIX).w(" _result = new java.util.ArrayList<")
				.w(this.returnClassName).el(">();");
		cw.l("while(rs.next(){");
		cw.bL(this.returnClassName).w(" _obj =  new ").w(this.returnClassName).el("();");
		cw.bL("_result = new ").w(this.returnClassName).el("();");
		int i=1;
		for(ListIterator<CalcColumn> it = this.fields.listIterator(); it.hasNext();){
			CalcColumn col = it.next();
			ColumnHandlerFactory.get(col.getHandlerClass()).readValue(cw, "_obj."+col.getSetter()+"(", ");", i++, col.isNullable());
		}
		cw.l("_result.add(_obj);");
		cw.l("}");
		cw.l("return _result;");
	}
}
