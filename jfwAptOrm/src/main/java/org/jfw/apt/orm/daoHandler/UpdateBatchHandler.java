package org.jfw.apt.orm.daoHandler;

import java.util.ListIterator;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.dao.Batch;
import org.jfw.apt.orm.annotation.dao.method.operator.Update;
import org.jfw.apt.orm.core.ColumnWriterCache;
import org.jfw.apt.orm.core.model.update.UpdateColumn;
import org.jfw.apt.orm.core.model.update.UpdateFactory;
import org.jfw.apt.orm.core.model.update.UpdatePart;
import org.jfw.apt.orm.core.model.where.WhereColumn;
import org.jfw.apt.orm.core.model.where.WhereFactory;
import org.jfw.apt.orm.core.model.where.WherePart;

public class UpdateBatchHandler extends BaseManipulationHandler {
	private String eleName;

	protected UpdatePart updatePart;
	protected WherePart wherePart;

	@Override
	public boolean match(Element ele) {
		this.byEntry = true;
		this.batch = true;
		Update u = ele.getAnnotation(Update.class);
		if (null == u)
			return false;
		return (null != ele.getAnnotation(Batch.class));
	}

	@Override
	protected void prepareSqlParams() throws AptException {
		this.dynamic = false;
		this.eleName = cw.getMethodTempVarName();
		this.updatePart = UpdateFactory.build(this.ref, this.fromTable, eleName,false);
		this.wherePart = WhereFactory.build(this.fromTable, this.ref, eleName);
		
		
		
		
		

	}

	@Override
	protected void prepareSQL() {
		cw.bL("String sql =\"UPDATE ").w(this.fromTable.getFromSentence()).w(" SET ");
		updatePart.WriteStatic(cw);
		wherePart.WriteStaticTo(cw);
		cw.el("\";");
	}

	@Override
	protected void handleSqlParameters() throws AptException {
		cw.bL("for(").w(this.fromTable.getJavaName()).w(" ").w(this.eleName).w(":").w(this.me.getParams().get(1).getName()).el("){");
		this.sqlparams.clear();		
		this.updatePart.prepare(cw);
		this.wherePart.prepare(cw);	

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
		if (this.doReplaceSource) {
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
