package org.jfw.apt.orm.daoHandler;

import javax.lang.model.element.Element;

import org.jfw.apt.Util;
import org.jfw.apt.annotation.Nullable;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.dao.Column;
import org.jfw.apt.orm.annotation.dao.method.From;
import org.jfw.apt.orm.annotation.dao.method.operator.QueryValList;
import org.jfw.apt.orm.core.ColumnHandler;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.model.CalcColumn;
import org.jfw.apt.orm.core.model.DataEntryFactory;

public class QueryValListHandler extends BaseQueryHandler {

	private CalcColumn valColumn = new CalcColumn();

	@Override
	public boolean match(Element ele) {
		return null != ele.getAnnotation(QueryValList.class);
	}

	@Override
	protected String getDefReturnClassName() throws AptException {
		String rcn = this.me.getReturnType();
		if ((rcn.length() <= LIST_MIN_INDEX) || (!rcn.startsWith(LIST_PREFIX)) || (!rcn.endsWith(LIST_SUFFIX)))
			throw new AptException(ref, "Method[@" + QueryValList.class.getName() + "] must return java.util.List<?>");
		this.returnClassName = rcn.substring(LIST_RCN_INDEX, rcn.length() - 1);

		Column col = this.ref.getAnnotation(Column.class);
		if (col == null)
			throw new AptException(ref,
					"Method[@" + QueryValList.class.getName() + "] must has @" + Column.class.getName());

		if(col.value() == null || col.value().length!= 1 || Util.emptyToNull(col.value()[0]) == null)
			throw new AptException(ref,
					"Method[@" + QueryValList.class.getName() + "] must set @" + Column.class.getName() + "'value  only one unEmtyp String");
		
		String fieldname = col.value()[0].trim();
		Class<? extends ColumnHandler> hc =  ColumnHandlerFactory.getHandlerClass(col,ref);
		if (hc == null)
			throw new AptException(ref, "Method[@" + QueryValList.class.getName() + "] must set @"
					+ Column.class.getName() + "'handlerClass");
		ColumnHandler ch = ColumnHandlerFactory.get(hc);
		String tmp = ch.supportsClass();
		if (Util.isPrimitive(tmp)) {
			tmp = Util.getWrapClass(tmp);
		}

		if (!tmp.equals(this.returnClassName))
			throw new AptException(ref, "Method[@" + QueryValList.class.getName() + "]  @" + Column.class.getName()
					+ "'handlerClass not equals return type");
		valColumn.setHandlerClass(hc);
		valColumn.setSqlName(fieldname);
		valColumn.setNullable(Util.isPrimitive(ch.supportsClass()) ? false : (null != this.ref.getAnnotation(Nullable.class)));
		return null;

	}
	@Override
	protected void prepareDataEntry() throws AptException {
		this.getDefReturnClassName();

		String fde = this.getFromClassName();
		if (null == fde) {
			new AptException(ref,"Method[@" + QueryValList.class.getName() + "] must has @"+From.class.getName());
		}
		this.fromDataEntry = DataEntryFactory.get(fde);
		
		this.fromSentence = this.fromDataEntry.getFromSentence();

		this.fields.clear();
		this.fields.add(valColumn);
	}

	@Override
	protected void buildResult() {
		cw.bL(LIST_PREFIX).w(this.returnClassName).w(LIST_SUFFIX).w(" _result = new java.util.ArrayList<")
				.w(this.returnClassName).el(">();");
		cw.l("while(rs.next(){");

			ColumnHandlerFactory.get(valColumn.getHandlerClass()).readValue(cw, "_result.add(", ");", 1,
					valColumn.isNullable());
		cw.l("}");
		cw.l("return _result;");

	}

}
