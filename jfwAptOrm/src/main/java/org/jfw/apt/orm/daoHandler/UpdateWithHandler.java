package org.jfw.apt.orm.daoHandler;

import java.util.ListIterator;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.dao.Batch;
import org.jfw.apt.orm.annotation.dao.method.operator.UpdateWith;
import org.jfw.apt.orm.core.ColumnWriterCache;
import org.jfw.apt.orm.core.model.update.UpdateColumn;
import org.jfw.apt.orm.core.model.update.UpdateFactory;
import org.jfw.apt.orm.core.model.update.UpdatePart;
import org.jfw.apt.orm.core.model.where.WhereColumn;
import org.jfw.apt.orm.core.model.where.WhereFactory;
import org.jfw.apt.orm.core.model.where.WherePart;

public class UpdateWithHandler extends BaseManipulationHandler {

	protected WherePart wherePart;

	protected UpdatePart updatePart;

	@Override
	public boolean match(Element ele) {
		this.byEntry = false;
		this.batch = false;
		return (null == ele.getAnnotation(Batch.class)) && (null != ele.getAnnotation(UpdateWith.class));
	}

	@Override
	protected void prepareSqlParams() throws AptException {
		this.updatePart = UpdateFactory.build(this.me, this.fromTable);
		this.wherePart = WhereFactory.build(this.me, this.fromTable, true, null);
		if (this.wherePart == null) {
			this.dynamic = this.updatePart.isDynamic();
		} else {
			this.dynamic = this.updatePart.isDynamic() || this.wherePart.isDynamic();
		}
		
		this.updatePart.prepare(cw);
		if (null != this.wherePart)
			this.wherePart.prepare(cw);
		this.sqlparams.clear();

		for (ListIterator<UpdateColumn> it = this.updatePart.getColumns().listIterator(); it.hasNext();) {
			this.sqlparams.add(it.next().getCwc());
		}
		if (this.wherePart != null) {
			for (ListIterator<WhereColumn> it = this.wherePart.getColumns().listIterator(); it.hasNext();) {
				this.sqlparams.add(it.next().getCwc());
			}
		}

		this.doReplaceSource = false;
		for (ListIterator<ColumnWriterCache> it = this.sqlparams.listIterator(); it.hasNext();) {

			if (it.next().isReplaceResource()) {
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
			if (this.updatePart.isDynamic()) {
				this.updatePart.WriteDynamic(cw);
				if (null != wherePart) {
					if (wherePart.isDynamic()) {
						wherePart.WriteDynamic(cw);
					} else {
						cw.bL("sql.append(\"");
						wherePart.WriteStaticTo(cw);
						cw.el("\");");
					}
				}
			} else {
				this.updatePart.WriteStatic(cw);
				cw.el("\");");
				this.wherePart.WriteDynamic(cw);
			}
		} else {
			cw.bL("String sql =\"UPDATE ").w(this.fromTable.getFromSentence()).w(" SET ");
			updatePart.WriteStatic(cw);
			if (null != this.wherePart)
				wherePart.WriteStaticTo(cw);
			cw.el("\";");
		}
	}
}
