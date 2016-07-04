package org.jfw.apt.orm.daoHandler;

import java.util.ListIterator;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.dao.Batch;
import org.jfw.apt.orm.annotation.dao.method.operator.UpdateWith;
import org.jfw.apt.orm.core.ColumnWriterCache;
import org.jfw.apt.orm.core.model.update.BatchUpdateFactory;
import org.jfw.apt.orm.core.model.update.UpdateColumn;
import org.jfw.apt.orm.core.model.update.UpdatePart;
import org.jfw.apt.orm.core.model.where.BatchWhereFactory;
import org.jfw.apt.orm.core.model.where.WhereColumn;
import org.jfw.apt.orm.core.model.where.WherePart;

public class UpdateWithBatchHandler extends BaseManipulationHandler{
	
	protected UpdatePart updatePart;
	protected WherePart wherePart;

	@Override
	public boolean match(Element ele) {
		this.byEntry = false;
		this.batch = true;
		return (null!= ele.getAnnotation(Batch.class))&&(null != ele.getAnnotation(UpdateWith.class));
	}

	@Override
	protected void prepareSqlParams() throws AptException {
		this.dynamic = false;
		this.updatePart = BatchUpdateFactory.build(this.me, this.fromTable);
		this.wherePart = BatchWhereFactory.build(this.me,this.fromTable, true);
		if((null == this.wherePart) || (!this.wherePart.isBatch())){
			throw new AptException(this.ref,"no found batch paramter in where sentence");
		}
		if((this.updatePart.getStaticSentence() == null )&&(this.updatePart.getColumns().isEmpty())){
			throw new AptException(this.ref,"no found set sentence");
		}
	}

	@Override
	protected void prepareSQL() {
		cw.bL("String sql = \"UPDATE ").w(this.fromTable.getFromSentence()).w(" SET ");
		this.updatePart.WriteStatic(cw);
		this.wherePart.WriteStaticTo(cw);
		cw.el("\";");		
	}

	@Override
	protected void handleSqlParameters() throws AptException {
		cw.bL("for(int i = 0 ; i < ").w(this.batchLengthVariableName).el(" ; ++i){");
		
		
		this.updatePart.prepare(cw);
		this.wherePart.prepare(cw);
		this.doReplaceSource = false;
		this.sqlparams.clear();
		for (ListIterator<UpdateColumn> it = this.updatePart.getColumns().listIterator(); it.hasNext();) {
			this.sqlparams.add(it.next().getCwc());
		}
		for (ListIterator<WhereColumn> it = this.wherePart.getColumns().listIterator(); it.hasNext();) {
			this.sqlparams.add(it.next().getCwc());
		}
		
		this.doReplaceSource = false;
		for(ListIterator<ColumnWriterCache> it = this.sqlparams.listIterator();it.hasNext();){
			
			if(it.next().isReplaceResource()) {
				this.doReplaceSource = true;
				break;
			}
		}
		if(this.doReplaceSource){
			cw.l("try{");
		}
		super.handleSqlParameters();
		cw.l("ps.addBatch();");
		if (this.doReplaceSource) {
			cw.l("}finally{");
			this.replaceResource();
			cw.l("}");
		}
		cw.l("}");
	}
}
