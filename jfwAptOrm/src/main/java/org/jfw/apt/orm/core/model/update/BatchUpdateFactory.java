package org.jfw.apt.orm.core.model.update;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodEntry;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.orm.annotation.dao.Batch;
import org.jfw.apt.orm.annotation.dao.param.Alias;
import org.jfw.apt.orm.annotation.dao.param.Set;
import org.jfw.apt.orm.core.ColumnHandler;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.model.CalcColumn;
import org.jfw.apt.orm.core.model.Table;

public final class BatchUpdateFactory {
	public static void handleSet(LinkedList<UpdateColumn> list, MethodParamEntry mpe, Table table) throws AptException {

		Set set = mpe.getRef().getAnnotation(Set.class);
		if (null == set)
			return;
		String el = Util.emptyToNull(set.value());
		if (el == null)
			el = "=?";

		boolean batchable = null != mpe.getRef().getAnnotation(Batch.class);

		String paramType = mpe.getTypeName();
		
		if (batchable) {
			if (!paramType.endsWith("[]")) {
				throw new AptException(mpe.getRef(), "param type must is Array");
			}
			paramType = paramType.substring(0, paramType.length() - 2);
		}

		Alias alias = mpe.getRef().getAnnotation(Alias.class);
		String fn = alias == null ? null : Util.emptyToNull(alias.value());
		if (fn == null)
			fn = mpe.getName();
		CalcColumn col = table.getCalcColumnByJavaName(fn);
		if (col == null)
			throw new AptException(mpe.getRef(), "no found attr[" + fn + "] in class[" + table.getJavaName() + "}");
		
		Class<? extends ColumnHandler> handlerClass = col.getHandlerClass();
		
		String spc = ColumnHandlerFactory.get(handlerClass).supportsClass();

		if (!paramType.equals(spc)) {	
			if(batchable)
				throw new AptException(mpe.getRef(), "param Type unEquals ColumnHandler.supportClass with Array");
				else throw new AptException(mpe.getRef(), "param Type unEquals ColumnHandler.supportClass");
		}

		
		UpdateColumn uc = new UpdateColumn();
		uc.setName(mpe.getName()+ (batchable?"[i]":""));
		uc.setCacheValue(batchable);
		uc.setSetSql(col.getSqlName() + el);
		uc.setHandlerClass(handlerClass);
		uc.setNullable(col.isNullable());
		uc.setDynamic(false);
		list.add(uc);
		
	}

	public static UpdatePart build(MethodEntry me, Table table) throws AptException {
		UpdatePart updatePart = new UpdatePart();
		String staticSql = UpdateFactory.getStaticUpdateSetSentence(me.getRef());
		List<org.jfw.apt.orm.core.model.Column> ifs =UpdateFactory.getIncludeFixSetValue(me.getRef(), table);
		for (ListIterator<org.jfw.apt.orm.core.model.Column> it = ifs.listIterator(); it.hasNext();) {
			org.jfw.apt.orm.core.model.Column col = it.next();
			if (staticSql.length() > 0)
				staticSql = staticSql + ",";
			staticSql = staticSql + col.getSqlName() + "=" + col.getFixSqlValueWithUpdate();
		}
		updatePart.setStaticSentence(Util.emptyToNull(staticSql));

		LinkedList<UpdateColumn> list = updatePart.getColumns();
		for (ListIterator<MethodParamEntry> it = me.getParams().listIterator(); it.hasNext();) {
			handleSet(list, it.next(), table);
		}
		updatePart.setDynamic(false);
		return updatePart;
	}
}
