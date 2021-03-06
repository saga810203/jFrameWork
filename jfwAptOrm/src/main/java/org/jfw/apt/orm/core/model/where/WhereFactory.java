package org.jfw.apt.orm.core.model.where;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodEntry;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.orm.annotation.dao.method.Or;
import org.jfw.apt.orm.annotation.dao.param.Equals;
import org.jfw.apt.orm.annotation.dao.param.GreaterThan;
import org.jfw.apt.orm.annotation.dao.param.GtEq;
import org.jfw.apt.orm.annotation.dao.param.LessThan;
import org.jfw.apt.orm.annotation.dao.param.Like;
import org.jfw.apt.orm.annotation.dao.param.LtEq;
import org.jfw.apt.orm.annotation.dao.param.Set;
import org.jfw.apt.orm.annotation.dao.param.UnEquals;
import org.jfw.apt.orm.core.ColumnHandler;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.model.CalcColumn;
import org.jfw.apt.orm.core.model.DataEntry;
import org.jfw.apt.orm.core.model.DbUniqu;
import org.jfw.apt.orm.core.model.ParamColumn;
import org.jfw.apt.orm.core.model.ParamSqlColumn;
import org.jfw.apt.orm.core.model.Table;

public final class WhereFactory {
	public static List<Class<? extends Annotation>> supportedClass = new ArrayList<Class<? extends Annotation>>();

	public static Map<Class<? extends Annotation>, String> classEls = new HashMap<Class<? extends Annotation>, String>();

	public static boolean build(MethodParamEntry mpe, DataEntry entry, LinkedList<WhereColumn> result)
			throws AptException {
		boolean ret = false;
		VariableElement ref = mpe.getRef();
		List<ParamColumn> paramColumns = null;
		Class<? extends ColumnHandler> handlerClass = null;
		boolean nullable =mpe.isPrimitive()? false : mpe.isNullable() ;// (null != ref.getAnnotation(Nullable.class));
		for(ParamSqlColumn psc: ParamSqlColumn.build(mpe, entry)){
			ret = true;
			handlerClass = psc.getHandlerClass();
			String tn = mpe.getTypeName();
			String sn = ColumnHandlerFactory.get(handlerClass).supportsClass();
			if (!tn.equals(sn)) {
				Class<? extends ColumnHandler> related = ColumnHandlerFactory.getRelatedHandler(handlerClass);
				if ((null == related) || (!tn.equals(ColumnHandlerFactory.get(related).supportsClass())))
					throw new AptException(ref, "param Type unEquals ColumnHandler.supportClass");
				if (Util.isPrimitive(ColumnHandlerFactory.supportedType(handlerClass))) {
					nullable = true;
				}
				handlerClass = related;
			}

			WhereColumn wc = new WhereColumn();
			wc.setHandlerClass(handlerClass);
			wc.setName(mpe.getName());
			wc.setNullable(nullable);
			wc.setWhereSql(psc.getSql());
			wc.setCacheValue(false);
			if (nullable)
				result.addLast(wc);
			else
				result.addFirst(wc);
			
		}
		
		
		for (Class<? extends Annotation> cls : supportedClass) {
			Annotation anObj = mpe.getRef().getAnnotation(cls);
			if (anObj == null)
				continue;
			ret = true;
			if (null == paramColumns) {
				paramColumns = ParamColumn.build(mpe, entry);
			}

			for (ParamColumn pc : paramColumns) {
				handlerClass = pc.getHandlerClass();
				String tn = mpe.getTypeName();
				String sn = ColumnHandlerFactory.get(handlerClass).supportsClass();
				if (!tn.equals(sn)) {
					Class<? extends ColumnHandler> related = ColumnHandlerFactory.getRelatedHandler(handlerClass);
					if ((null == related) || (!tn.equals(ColumnHandlerFactory.get(related).supportsClass())))
						throw new AptException(ref, "param Type unEquals ColumnHandler.supportClass");
					if (Util.isPrimitive(ColumnHandlerFactory.supportedType(handlerClass))) {
						nullable = true;
					}
					handlerClass = related;
				}

				WhereColumn wc = new WhereColumn();
				wc.setHandlerClass(handlerClass);
				wc.setName(mpe.getName());
				wc.setNullable(nullable);
				wc.setWhereSql(pc.getName() + " " + classEls.get(cls) + " ?");
				wc.setCacheValue(false);
				if (nullable)
					result.addLast(wc);
				else
					result.addFirst(wc);
			}
		}
		return ret;

	}

	public static void buildWithEq(MethodParamEntry mpe, DataEntry entry, LinkedList<WhereColumn> result)
			throws AptException {
		VariableElement ref = mpe.getRef();
		boolean nullable =mpe.isPrimitive()? false : mpe.isNullable();
		List<ParamColumn> pcs = ParamColumn.build(mpe, entry);
		for (ParamColumn pc : pcs) {

			Class<? extends ColumnHandler> handlerClass = pc.getHandlerClass();
			String tn = mpe.getTypeName();
			String sn = ColumnHandlerFactory.get(handlerClass).supportsClass();
			if (!tn.equals(sn)) {
				Class<? extends ColumnHandler> related = ColumnHandlerFactory.getRelatedHandler(handlerClass);
				if ((null == related) || (!tn.equals(ColumnHandlerFactory.get(related).supportsClass())))
					throw new AptException(ref, "param Type unEquals ColumnHandler.supportClass");
				if (Util.isPrimitive(ColumnHandlerFactory.supportedType(handlerClass))) {
					nullable = true;
				}
				handlerClass = related;
			}
			WhereColumn wc = new WhereColumn();
			wc.setHandlerClass(handlerClass);
			wc.setName(mpe.getName());
			wc.setNullable(nullable);
			wc.setWhereSql(pc.getName() + " = ?");
			wc.setCacheValue(false);
			if (nullable)
				result.addLast(wc);
			else
				result.addFirst(wc);
		}
	}

	public static void handlePrimaryKey(Table table, List<WhereColumn> list, Element methodRef, String paramName)
			throws AptException {
		DbUniqu du = table.getPrimaryKey();
		if (du == null)
			throw new AptException(methodRef, "not found primary key in table[" + table.getFromSentence() + "]");
		List<String> keys = du.getColumns();
		for (ListIterator<CalcColumn> it = table.getAllColumn().listIterator(); it.hasNext();) {
			org.jfw.apt.orm.core.model.Column col = (org.jfw.apt.orm.core.model.Column) it.next();

			if (keys.contains(col.getJavaName())) {

				WhereColumn wc = new WhereColumn();
				wc.setHandlerClass(col.getHandlerClass());
				wc.setName(paramName + "." + col.getGetter() + "()");
				wc.setWhereSql(col.getSqlName() + " = ?");
				wc.setNullable(false);
				wc.setCacheValue(true);
				list.add(wc);
			}
		}
	}

	public static WherePart build(Table table, Element methodRef, String paramName) throws AptException {
		WherePart wherePart = new WherePart();
		wherePart.setDynamic(false);
		wherePart.setAnd(true);
		wherePart.setStaticSentence(getStaticWhereSql(methodRef));
		List<WhereColumn> list = wherePart.getColumns();
		handlePrimaryKey(table, list, methodRef, paramName);
		return wherePart;
	}

	public static WherePart build(MethodEntry method, DataEntry entry, boolean excludeSet,
			List<String> excludeParamName) throws AptException {
		WherePart result = new WherePart();
		result.setStaticSentence(getStaticWhereSql(method.getRef()));
		result.setAnd(isAnd(method.getRef()));
		List<MethodParamEntry> list = method.getParams();
		LinkedList<WhereColumn> wcs = result.getColumns();
		for (int i = 1; i < list.size(); ++i) {
			MethodParamEntry mpe = list.get(i);
			if ((null != excludeParamName) && excludeParamName.contains(mpe.getName()))
				continue;
			if (excludeSet && (null != mpe.getRef().getAnnotation(Set.class)))
				continue;
			if (!build(mpe, entry, wcs))
				buildWithEq(mpe, entry, wcs);
		}
		result.setDynamic(wcs.size() > 0 && wcs.getLast().isNullable());
		if (result.getStaticSentence() == null && (result.getColumns().isEmpty()))
			return null;
		return result;
	}

	public static String getStaticWhereSql(Element methodRef) {
		org.jfw.apt.orm.annotation.dao.method.Where ws = methodRef
				.getAnnotation(org.jfw.apt.orm.annotation.dao.method.Where.class);
		if (ws == null)
			return null;
		return Util.emptyToNull(ws.value());
	}

	public static boolean isAnd(Element methodRef) {
		return null == methodRef.getAnnotation(Or.class);
	}

	private WhereFactory() {
	}

	static {
		classEls.put(Equals.class, "=");
		classEls.put(UnEquals.class, "<>");
		classEls.put(GreaterThan.class, ">");
		classEls.put(GtEq.class, ">=");
		classEls.put(LessThan.class, "<");
		classEls.put(LtEq.class, "<=");
		classEls.put(Like.class, "LIKE");
		supportedClass.addAll(classEls.keySet());
	}

}
