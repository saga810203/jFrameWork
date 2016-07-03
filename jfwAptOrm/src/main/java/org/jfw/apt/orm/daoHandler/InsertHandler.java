package org.jfw.apt.orm.daoHandler;

import java.util.LinkedList;
import java.util.ListIterator;

import javax.lang.model.element.Element;

import org.jfw.apt.orm.annotation.dao.Batch;
import org.jfw.apt.orm.annotation.dao.method.operator.Insert;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.ColumnWriterCache;
import org.jfw.apt.orm.core.model.CalcColumn;
import org.jfw.apt.orm.core.model.Column;

public class InsertHandler extends BaseManipulationHandler {
	

	@Override
	public boolean match(Element ele) {
		this.byEntry = true;
		return (null == ele.getAnnotation(Batch.class)) && (null != ele.getAnnotation(Insert.class));
	}



	@Override
	protected void prepareSqlParams() {
		this.dynamic = false;
		this.doReplaceSource = false;
		String pn = this.me.getParams().get(1).getName();
		LinkedList<Column> list = new LinkedList<Column>();
		for (ListIterator<CalcColumn> it = this.fromTable.getAllColumn().listIterator(); it.hasNext();) {
			Column col = (Column) it.next();
			if (col.isInsertable()) {
				if (null == col.getFixSqlValueWithInsert()) {
					list.add(col);
				} else {
					list.add(0, col);
				}
			}
		}

		cw.bL("String sql=\"INSERT INTO ").w(this.fromTable.getFromSentence()).w(" (");

		boolean first = true;
		for (ListIterator<Column> it = list.listIterator(); it.hasNext();) {
			if (first)
				first = false;
			else
				cw.w(",");
			Column col = it.next();
			cw.w(col.getSqlName());
		}
		cw.w(") VALUES (");
		first = true;
		for (ListIterator<Column> it = list.listIterator(); it.hasNext();) {
			if (first)
				first = false;
			else
				cw.w(",");
			Column col = it.next();
			if(null == col.getFixSqlValueWithInsert()){
				cw.w("?");
				ColumnWriterCache cwc =ColumnWriterCache.build(pn+"."+col.getGetter()+"()", true, col.isNullable(), 
						cw, ColumnHandlerFactory.get(col.getHandlerClass()));
				cwc.setDynamic(false);
				if(cwc.isReplaceResource()) this.doReplaceSource = true;
				cwc.setDynamic(false);
				this.sqlparams.add(cwc);				
			}else{
				cw.ws(col.getFixSqlValueWithInsert());
			}
		}
		cw.el(")\";");
		
		for(ColumnWriterCache cwc:this.sqlparams){
			cwc.prepare();
		}

	}
	@Override
	protected void prepareSQL() {	
	}
}
