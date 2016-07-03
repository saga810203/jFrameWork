package org.jfw.apt.orm.daoHandler;

import java.util.ListIterator;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.core.model.CalcColumn;
import org.jfw.apt.orm.core.model.where.WhereFactory;

public abstract class BaseSelectHandler extends BaseQueryHandler{
	
	@Override
	protected void prepareDataEntry() throws AptException {
		this.returnClassName = this.getDefReturnClassName();

	
		this.selectDataEntry =this.fromDataEntry;

		this.fromSentence = this.fromDataEntry.getFromSentence();
        this.fields.clear();
		this.fields.addAll(this.selectDataEntry.getAllColumn());
		for (ListIterator<CalcColumn> it = this.fields.listIterator(); it.hasNext();) {
			if (!it.next().isQueryable())
				it.remove();
		}
	}
	
	@Override
	protected void buildWhere() throws AptException {
		this.wherePart = WhereFactory.build(this.me, this.fromDataEntry,false,null);
		this.dynamic = (null != this.wherePart) && this.wherePart.isDynamic();
	}

}
