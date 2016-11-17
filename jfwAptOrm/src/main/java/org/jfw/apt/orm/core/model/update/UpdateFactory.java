package org.jfw.apt.orm.core.model.update;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.lang.model.element.Element;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodEntry;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.orm.OrmAnnoCheckUtil;
import org.jfw.apt.orm.annotation.dao.Dynamic;
import org.jfw.apt.annotation.Nullable;
import org.jfw.apt.orm.annotation.dao.method.IncludeFixSet;
import org.jfw.apt.orm.annotation.dao.method.SetSentence;
import org.jfw.apt.orm.annotation.dao.param.Alias;
import org.jfw.apt.orm.annotation.dao.param.Set;
import org.jfw.apt.orm.core.ColumnHandler;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.model.CalcColumn;
import org.jfw.apt.orm.core.model.DbUniqu;
import org.jfw.apt.orm.core.model.Table;

public final class UpdateFactory {

	public static String getStaticUpdateSetSentence(Element method) {
		SetSentence ss = method.getAnnotation(SetSentence.class);
		if (null == ss)
			return "";
		String ret = Util.emptyToNull(ss.value());
		return ret == null ? "" : ret;
	}

	public static List<org.jfw.apt.orm.core.model.Column> getIncludeFixSetValue(Element ele, Table table) {
		List<org.jfw.apt.orm.core.model.Column> ret = new ArrayList<org.jfw.apt.orm.core.model.Column>();
		IncludeFixSet ifs = ele.getAnnotation(IncludeFixSet.class);
		if (null != ifs) {
			String[] ifsStrs = ifs.value();
			if (ifsStrs != null && ifsStrs.length > 0) {
				for (String str : ifsStrs) {
					org.jfw.apt.orm.core.model.Column col = (org.jfw.apt.orm.core.model.Column) table
							.getCalcColumnByJavaName(Util.emptyToNull(str));
					if (col != null)
						ret.add(col);
				}
			}else{
				for(CalcColumn col:table.getAllColumn()){
					org.jfw.apt.orm.core.model.Column c = (org.jfw.apt.orm.core.model.Column)col;
					if(null!= c.getFixSqlValueWithUpdate()){
						ret.add(c);
					}	
				}
			}
		}
		return ret;
	}

	public static UpdatePart build(Element methodEle, Table table, String paramName, boolean enableDyanmic) {

		UpdatePart updatePart = new UpdatePart();
		List<String> excludeColumnJavaNames = new ArrayList<String>();
		DbUniqu qu = table.getPrimaryKey();
		if (qu != null) {
			excludeColumnJavaNames.addAll(qu.getColumns());
		}
		LinkedList<UpdateColumn> list = updatePart.getColumns();
		String sus = "";
		boolean ud = false;
		for (ListIterator<org.jfw.apt.orm.core.model.CalcColumn> it = table.getAllColumn().listIterator(); it
				.hasNext();) {
			org.jfw.apt.orm.core.model.Column col = (org.jfw.apt.orm.core.model.Column) it.next();
			if (excludeColumnJavaNames.contains(col.getJavaName()))
				continue;
			if (!col.isRenewable())
				continue;
			String fv = col.getFixSqlValueWithUpdate();
			if (fv != null) {
				if (sus.length() > 0) {
					sus = sus + ",";
				}
				sus = sus + col.getSqlName() + "=" + fv;
				continue;
			}
			UpdateColumn uc = new UpdateColumn();
			uc.setHandlerClass(col.getHandlerClass());
			uc.setName(paramName + "." + col.getGetter() + "()");
			uc.setCacheValue(true);
			uc.setSetSql(col.getSqlName() + " = ?");
			if (Util.isPrimitive(col.getJavaType())) {
				uc.setNullable(false);
				uc.setDynamic(false);
				list.addFirst(uc);
			} else {
				if (enableDyanmic) {
					uc.setNullable(true);
					uc.setDynamic(true);
					list.addLast(uc);
					ud = true;
				} else {
					uc.setNullable(col.isNullable());
					uc.setDynamic(false);
					list.addFirst(uc);
				}
			}

		}
		updatePart.setStaticSentence(Util.emptyToNull(sus));
		updatePart.setDynamic(ud);
		return updatePart;
	}

	public static void handleSet(LinkedList<UpdateColumn> list, MethodParamEntry mpe, Table table) throws AptException {
		Element ele = mpe.getRef();
		Set set = mpe.getRef().getAnnotation(Set.class);
		if (null == set)
			return;
		String el = Util.emptyToNull(set.value());
		if (el == null)
			el = "=?";

		boolean withDynamic = null != mpe.getRef().getAnnotation(Dynamic.class);

		String paramType = mpe.getTypeName();

		Alias alias = mpe.getRef().getAnnotation(Alias.class);
		if (alias != null)
			OrmAnnoCheckUtil.check(ele, alias, table);

		String[] cns = alias == null ? new String[] { mpe.getName() } : alias.value();

		for (String cn : cns) {
			String fn = cn.trim();
			CalcColumn col = table.getCalcColumnByJavaName(fn);
			if (col == null)
				throw new AptException(mpe.getRef(), "no found attr[" + fn + "] in class[" + table.getJavaName() + "}");

			Class<? extends ColumnHandler> handlerClass = col.getHandlerClass();

			String spc = ColumnHandlerFactory.get(handlerClass).supportsClass();

			if (!paramType.equals(spc)) {

				Class<? extends ColumnHandler> related = ColumnHandlerFactory.getRelatedHandler(handlerClass);
				if ((null == related) || (!paramType.equals(ColumnHandlerFactory.get(related).supportsClass())))
					throw new AptException(mpe.getRef(), "param Type unEquals ColumnHandler.supportClass");

				handlerClass = related;

				if (!Util.isPrimitive(paramType)) {
					withDynamic = true;
				}
			}
			if ((!col.isNullable()) && (mpe.isNullable())) {
				withDynamic = true;
			}

			UpdateColumn uc = new UpdateColumn();
			uc.setName(mpe.getName());
			uc.setCacheValue(false);
			uc.setSetSql(col.getSqlName() + el);
			uc.setHandlerClass(handlerClass);
			if (Util.isPrimitive(paramType)) {
				uc.setNullable(false);
				uc.setDynamic(false);
				list.addFirst(uc);
				return;
			} else if (withDynamic) {
				uc.setDynamic(true);
				uc.setNullable(true);
				list.addLast(uc);

			} else {
				uc.setDynamic(false);
				uc.setNullable(col.isNullable());
				list.addFirst(uc);
			}
		}
	}

	public static UpdatePart build(MethodEntry me, Table table) throws AptException {
		UpdatePart updatePart = new UpdatePart();
		String staticSql = getStaticUpdateSetSentence(me.getRef());
		List<org.jfw.apt.orm.core.model.Column> ifs = getIncludeFixSetValue(me.getRef(), table);
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
		updatePart.setDynamic(list.size() > 0 && list.getLast().isDynamic());

		return updatePart;
	}

	private UpdateFactory() {
	}
}
