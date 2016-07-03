package org.jfw.apt.orm.daoHandler;

import java.util.ListIterator;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.dao.Batch;
import org.jfw.apt.orm.annotation.dao.method.operator.Delete;
import org.jfw.apt.orm.core.model.where.WhereColumn;
import org.jfw.apt.orm.core.model.where.WhereFactory;
import org.jfw.apt.orm.core.model.where.WherePart;

public class DeleteHandler extends BaseManipulationHandler {
	protected WherePart wherePart;

	@Override
	public boolean match(Element ele) {
		this.byEntry = true;
		this.batch = false;
		
		
		return (null!= ele.getAnnotation(Delete.class)) && (null == ele.getAnnotation(Batch.class));
	}

	@Override
	protected void prepareSqlParams() throws AptException {
		this.doReplaceSource = false;
		String pn = this.me.getParams().get(1).getName();
		this.wherePart = WhereFactory.build(this.fromTable, ref, pn);
		this.wherePart.prepare(cw);
		this.dynamic = false;
		this.sqlparams.clear();
		for (ListIterator<WhereColumn> it = this.wherePart.getColumns().listIterator(); it.hasNext();) {
			this.sqlparams.add(it.next().getCwc());
		}
	}

	@Override
	protected void prepareSQL() {
		this.wherePart.prepare(cw);
		cw.bL("String sql =\"DELETE FROM ").w(this.fromTable.getFromSentence());
		wherePart.WriteStaticTo(cw);
		cw.el("\";");
		
	}

}
