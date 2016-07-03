package org.jfw.apt.orm.daoHandler;

import java.util.ListIterator;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.dao.Batch;
import org.jfw.apt.orm.annotation.dao.Dynamic;
import org.jfw.apt.orm.annotation.dao.method.operator.Update;
import org.jfw.apt.orm.core.ColumnWriterCache;
import org.jfw.apt.orm.core.model.update.UpdateColumn;
import org.jfw.apt.orm.core.model.update.UpdateFactory;
import org.jfw.apt.orm.core.model.update.UpdatePart;
import org.jfw.apt.orm.core.model.where.WhereColumn;
import org.jfw.apt.orm.core.model.where.WhereFactory;
import org.jfw.apt.orm.core.model.where.WherePart;

public class UpdateHandler extends BaseManipulationHandler {

	protected UpdatePart updatePart;
	protected WherePart wherePart;

	@Override
	public boolean match(Element ele) {
		this.byEntry = true;
		this.batch = false;
		Update u = ele.getAnnotation(Update.class);
		if (null == u)
			return false;
		this.dynamic = (null != ele.getAnnotation(Dynamic.class));
		return (null == ele.getAnnotation(Batch.class));
	}

	@Override
	protected void prepareSqlParams() throws AptException {
		String pn = this.me.getParams().get(1).getName();
		this.updatePart = UpdateFactory.build(this.ref, this.fromTable, pn, this.dynamic);
		this.wherePart = WhereFactory.build(this.fromTable, this.ref, pn);
		this.dynamic = this.updatePart.isDynamic();
		
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
	}

	@Override
	protected void prepareSQL() {
		if (this.dynamic) {
			cw.l("StringBuilder sql = new StringBuilder();").bL("sql.append(\"UPDATE ")
					.w(this.fromTable.getFromSentence()).w(" SET ");
			this.updatePart.WriteDynamic(cw);
			cw.bL("sql.append(\" ");
			this.wherePart.WriteStaticTo(cw);
			cw.el("\");");

		} else {
			cw.bL("String sql =\"UPDATE ").w(this.fromTable.getFromSentence()).w(" SET ");
			updatePart.WriteStatic(cw);
			wherePart.WriteStaticTo(cw);
			cw.el("\";");
		}


	}

}
