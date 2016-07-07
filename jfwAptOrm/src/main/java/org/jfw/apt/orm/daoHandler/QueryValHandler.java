package org.jfw.apt.orm.daoHandler;

import javax.lang.model.element.Element;

import org.jfw.apt.Util;
import org.jfw.apt.annotation.DefaultValue;
import org.jfw.apt.annotation.Nullable;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.dao.Column;
import org.jfw.apt.orm.annotation.dao.method.From;
import org.jfw.apt.orm.annotation.dao.method.operator.QueryVal;
import org.jfw.apt.orm.core.ColumnHandler;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.model.CalcColumn;
import org.jfw.apt.orm.core.model.DataEntryFactory;

public class QueryValHandler extends BaseQueryHandler {

	private CalcColumn valColumn = new CalcColumn();
	private boolean withNullable;

	private String defaultValue;

	@Override
	public boolean match(Element ele) {
		this.withNullable = null != ele.getAnnotation(Nullable.class);
		return null != ele.getAnnotation(QueryVal.class);
	}

	@Override
	protected String getDefReturnClassName() throws AptException {
		DefaultValue dv = this.ref.getAnnotation(DefaultValue.class);
		this.defaultValue = null == dv ? null : Util.emptyToNull(dv.value());

		this.returnClassName = this.me.getReturnType();
		Column col = this.ref.getAnnotation(Column.class);
		if (col == null)
			throw new AptException(ref,
					"Method[@" + QueryVal.class.getName() + "] must has @" + Column.class.getName());

		if(col.value() == null || col.value().length!= 1 || Util.emptyToNull(col.value()[0]) == null)
			throw new AptException(ref,
					"Method[@" + QueryVal.class.getName() + "] must set @" + Column.class.getName() + "'value  only one unEmtyp String");
		
		String fieldname = col.value()[0].trim();
		Class<? extends ColumnHandler> hc = ColumnHandlerFactory.getHandlerClass(col, ref);
		if (hc == null)
			throw new AptException(ref,
					"Method[@" + QueryVal.class.getName() + "] must set @" + Column.class.getName() + "'handlerClass");
		ColumnHandler ch = ColumnHandlerFactory.get(hc);
		if (!ch.supportsClass().equals(this.returnClassName))
			throw new AptException(ref, "Method[@" + QueryVal.class.getName() + "]  @" + Column.class.getName()
					+ "'handlerClass not equals return type");
		valColumn.setHandlerClass(hc);
		valColumn.setSqlName(fieldname);
		if (Util.isPrimitive(this.returnClassName))
			valColumn.setNullable(false);
		else
			valColumn.setNullable(this.me.getRef().getAnnotation(Nullable.class) != null);
		return null;
	}

	@Override
	protected void buildResult() {
		cw.l("if(rs.next()){");
		ColumnHandlerFactory.get(valColumn.getHandlerClass()).readValue(cw, "return ", ";", 1, false);
		cw.l("}else{");
		if (this.defaultValue == null) {
			if (this.withNullable
					&& (!Util.isPrimitive(ColumnHandlerFactory.supportedType(this.valColumn.getHandlerClass()))))
				cw.l("return null;");
			else
				cw.l("throw new org.jfw.util.exception.JfwBaseException(201);");
		} else {
			cw.bL("return ").ws(this.defaultValue).el(";");
		}
		cw.l("}");

	}

	@Override
	protected void prepareDataEntry() throws AptException {
		this.getDefReturnClassName();

		String fde = this.getFromClassName();
		if (null == fde) {
			new AptException(ref, "Method[@" + QueryVal.class.getName() + "] must has @" + From.class.getName());
		}
		this.fromDataEntry = DataEntryFactory.get(fde);

		this.fromSentence = this.fromDataEntry.getFromSentence();

		this.fields.clear();
		this.fields.add(valColumn);
	}

}
