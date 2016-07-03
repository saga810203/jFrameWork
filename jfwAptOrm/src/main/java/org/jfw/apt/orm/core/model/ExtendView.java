package org.jfw.apt.orm.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ExtendView extends View{
	private String tableAlias;

	public String getTableAlias() {
		return tableAlias;
	}

	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}
	
	
	public List<CalcColumn> getAllColumn() {
		if (null == this.allCols) {
			allCols = new ArrayList<CalcColumn>();

			allCols.addAll(this.columns);
			
			DataEntry de = DataEntryFactory.get(this.supportedDataEntrys.get(0));

			List<CalcColumn> sList = de.getAllColumn();
					for (ListIterator<CalcColumn> cIt = sList.listIterator(); cIt.hasNext();) {
						Column col = (Column) cIt.next();
						mergeCalcColumnByJavaName(allCols, ColumnFactory.buildColumn(col.getHandlerClass(), col.isNullable(),
								col.getJavaType(), this.tableAlias+"."+col.getSqlName(), col.getJavaName(), col.isQueryable(), col.getDbType(), 
								col.getFixSqlValueWithInsert(), col.getFixSqlValueWithUpdate(), col.isInsertable(), col.isRenewable()));
					}
		
		}
		return this.allCols;
	}

	@Override
	public int getKind() {
		return DataEntry.EXTEND_VIEW;
	}
}
