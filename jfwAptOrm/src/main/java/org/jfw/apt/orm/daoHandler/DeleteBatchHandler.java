package org.jfw.apt.orm.daoHandler;

import java.util.ListIterator;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.dao.Batch;
import org.jfw.apt.orm.annotation.dao.method.operator.Delete;
import org.jfw.apt.orm.core.model.where.WhereColumn;
import org.jfw.apt.orm.core.model.where.WhereFactory;
import org.jfw.apt.orm.core.model.where.WherePart;

public class DeleteBatchHandler extends BaseManipulationHandler {
	protected WherePart wherePart;
	protected String eleName;
	

	@Override
	public boolean match(Element ele) {
		this.byEntry = true;
		this.batch = true;		
		return (null!= ele.getAnnotation(Delete.class)) && (null != ele.getAnnotation(Batch.class));
	}

	@Override
	protected void prepareSqlParams() throws AptException {
		this.eleName = cw.getMethodTempVarName();
		this.doReplaceSource = false;
		this.wherePart = WhereFactory.build(this.fromTable, ref, this.eleName);
		this.dynamic = false;
	}

	@Override
	protected void prepareSQL() {
		cw.bL("String sql =\"DELETE FROM ").w(this.fromTable.getFromSentence());
		wherePart.WriteStaticTo(cw);
		cw.el("\";");
		
	}

	@Override
	protected void handleSqlParameters() throws AptException {
		cw.bL("for(").w(this.fromTable.getJavaName()).w(" ").w(this.eleName).w(":").w(this.me.getParams().get(1).getName()).el("){");
		this.wherePart.prepare(cw);
		this.sqlparams.clear();
		for (ListIterator<WhereColumn> it = this.wherePart.getColumns().listIterator(); it.hasNext();) {
			this.sqlparams.add(it.next().getCwc());
		}
		super.handleSqlParameters();
		cw.l("ps.addBatch();");
		cw.l("}");			
	}
	
	

}
