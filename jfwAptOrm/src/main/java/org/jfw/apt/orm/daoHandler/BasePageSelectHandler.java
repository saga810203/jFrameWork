package org.jfw.apt.orm.daoHandler;

import java.util.Arrays;
import java.util.ListIterator;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.orm.annotation.dao.method.operator.PageSelect;
import org.jfw.apt.orm.annotation.entry.Table;
import org.jfw.apt.orm.core.model.CalcColumn;
import org.jfw.apt.orm.core.model.DataEntry;
import org.jfw.apt.orm.core.model.DataEntryFactory;
import org.jfw.apt.orm.core.model.where.WhereFactory;

public abstract class BasePageSelectHandler extends BasePageQueryHandler {

	@Override
	protected String getDefReturnClassName() throws AptException {
		this.pageNoName = null;
		this.pageSizeName = null;
		for (ListIterator<MethodParamEntry> it = this.me.getParams().listIterator(); it.hasNext();) {
			MethodParamEntry mpe = it.next();
			String name = mpe.getName();
			if ("int".equals(mpe.getTypeName())) {
				if (name.equals(FIRST_PAGE_NO_NAME)) {
					this.pageNoName = name;
				} else if (name.equals(FIRST_PAGE_SIZE_NAME)) {
					this.pageSizeName = name;
				} else if (name.equals(SECOND_PAGE_NO_NAME) && (null == this.pageNoName)) {
					this.pageNoName = name;
				} else if (name.equals(SECOND_PAGE_SIZE_NAME) && (null == this.pageSizeName)) {
					this.pageSizeName = name;
				}
			}
		}
		if (null == this.pageNoName)
			throw new AptException(ref,
					"Method[@" + PageSelect.class.getName() + "] must has param (int pageNo) or (int _pageNo)");
		if (null == this.pageSizeName)
			throw new AptException(ref,
					"Method[@" + PageSelect.class.getName() + "] must has param (int pageSize) or (int _pageSize)");

		String rcn = this.me.getReturnType();
		if ((rcn.length() <= PAGE_MIN_INDEX) || (!rcn.startsWith(PAGE_PREFIX)) || (!rcn.endsWith(PAGE_SUFFIX)))
			throw new AptException(ref,
					"Method[@" + PageSelect.class.getName() + "] must return org.jfw.util.PageQueryResult<?>");
		
		String tablename = rcn.substring(PAGE_RCN_INDEX, rcn.length() - 1);
		
		this.fromDataEntry =DataEntryFactory.get(tablename, DataEntry.TABLE);
		if(null == this.fromDataEntry)
			throw new AptException(ref,
					"Method[@" + PageSelect.class.getName() + "] must return org.jfw.util.PageQueryResult<? with @"+Table.class.getName()+">");
		
		return fromDataEntry.getJavaName();

	}

	@Override
	protected void buildWhere() throws AptException {
		this.wherePart = WhereFactory.build(this.me, this.fromDataEntry,false,Arrays.asList(this.pageNoName,this.pageSizeName));
		this.dynamic = (null != this.wherePart) && this.wherePart.isDynamic();
	}
	@Override
	protected void prepareDataEntry() throws AptException {
		this.returnClassName = this.getDefReturnClassName();

	
		this.selectDataEntry =this.fromDataEntry;

		this.fromSentence = this.fromDataEntry.getFromSentence();

		this.fields.addAll(this.selectDataEntry.getAllColumn());
		for (ListIterator<CalcColumn> it = this.fields.listIterator(); it.hasNext();) {
			if (!it.next().isQueryable())
				it.remove();
		}
	}
	
}
