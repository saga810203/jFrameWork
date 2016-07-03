package org.jfw.apt.orm.daoHandler;

import java.util.ListIterator;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.dao.Batch;
import org.jfw.apt.orm.annotation.dao.method.operator.DeleteWith;
import org.jfw.apt.orm.core.model.where.WhereColumn;
import org.jfw.apt.orm.core.model.where.WhereFactory;
import org.jfw.apt.orm.core.model.where.WherePart;

public class DeleteWithHandler extends BaseManipulationHandler{
	protected WherePart wherePart;
	@Override
	public boolean match(Element ele) {
		this.byEntry = false;
		this.batch = false;		
		return (null!= ele.getAnnotation(DeleteWith.class)) && (null == ele.getAnnotation(Batch.class));
	}

	@Override
	protected void prepareSqlParams() throws AptException {
		this.doReplaceSource = false;
		this.wherePart = WhereFactory.build(this.me, this.fromTable, false, null);
		this.dynamic = (null!= this.wherePart) && this.wherePart.isDynamic();
		this.sqlparams.clear();
		if(this.wherePart!= null);
		for (ListIterator<WhereColumn> it = this.wherePart.getColumns().listIterator(); it.hasNext();) {
			this.sqlparams.add(it.next().getCwc());
		}
		
	}

	@Override
	protected void prepareSQL() {
	 if(this.wherePart!= null)
		 this.wherePart.prepare(cw);
	 
	 if(dynamic){
		 cw.l("StringBuilder sql = new StringBuilder();");
		 cw.bL("sql.append(\"DELETE FROM ").w(this.fromTable.getFromSentence()).el("\");");
		 this.wherePart.WriteDynamic(cw);
	 }else{
		 cw.bL("String sql = \"DELETE FROM ").w(this.fromTable.getFromSentence());
		 if(null!= this.wherePart) this.wherePart.WriteStaticTo(cw);
		 cw.el("\";");		 
	 }
	}

}
