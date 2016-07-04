package org.jfw.apt.orm.daoHandler;

import java.util.ListIterator;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.dao.Batch;
import org.jfw.apt.orm.annotation.dao.method.operator.DeleteWith;
import org.jfw.apt.orm.core.model.where.BatchWhereFactory;
import org.jfw.apt.orm.core.model.where.WhereColumn;
import org.jfw.apt.orm.core.model.where.WherePart;

public class DeleteWithBatchHandler extends BaseManipulationHandler{
	
	protected WherePart wherePart;

	@Override
	public boolean match(Element ele) {
		this.byEntry = false;
		this.batch = true;
		return (null!= ele.getAnnotation(Batch.class))&&(null != ele.getAnnotation(DeleteWith.class));
	}

	@Override
	protected void prepareSqlParams() throws AptException {
		this.dynamic = false;
		this.wherePart = BatchWhereFactory.build(this.me,this.fromTable, true);
		if((null == this.wherePart) || (!this.wherePart.isBatch())){
			throw new AptException(this.ref,"no found batch paramter in where sentence");
		}
	}

	@Override
	protected void prepareSQL() {
		cw.bL("String sql = \"DELETE FROM ").w(this.fromTable.getFromSentence());
		this.wherePart.WriteStaticTo(cw);
		cw.el("\";");		
	}

	@Override
	protected void handleSqlParameters() throws AptException {
		cw.bL("for(int i = 0 ; i < ").w(this.batchLengthVariableName).el(" ; ++i){");

		this.wherePart.prepare(cw);
		this.doReplaceSource = false;
		this.sqlparams.clear();
		for (ListIterator<WhereColumn> it = this.wherePart.getColumns().listIterator(); it.hasNext();) {
			this.sqlparams.add(it.next().getCwc());
		}
		super.handleSqlParameters();
		cw.l("ps.addBatch();");
		cw.l("}");
	}
}
