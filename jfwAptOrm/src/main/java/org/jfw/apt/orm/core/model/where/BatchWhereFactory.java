package org.jfw.apt.orm.core.model.where;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodEntry;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.orm.annotation.dao.Batch;
import org.jfw.apt.orm.annotation.dao.param.Set;
import org.jfw.apt.orm.core.ColumnHandler;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.model.DataEntry;

public final class BatchWhereFactory {

	@SuppressWarnings("unchecked")
	public static boolean build(MethodParamEntry mpe, DataEntry entry, LinkedList<WhereColumn> result,WherePart part)
			throws AptException {
		boolean ret = false;
		VariableElement ref = mpe.getRef();
		Object[] objs = null;
		String sqlName = null;
		Class<? extends ColumnHandler> handlerClass = null;
		boolean batchable = (null != ref.getAnnotation(Batch.class));
		if(batchable) part.setBatch(true);
		for (Class<? extends Annotation> cls : WhereFactory.supportedClass) {
			Annotation anObj = mpe.getRef().getAnnotation(cls);
			if (anObj == null)
				continue;
			ret = true;
			if (null == objs) {
				objs = WhereFactory.validWhereParameter(mpe, entry);
				sqlName = (String) objs[0];
				handlerClass = (Class<? extends ColumnHandler>) objs[1];
				String tn = mpe.getTypeName();
				if (batchable) {
					if (!tn.endsWith("[]")) {
						throw new AptException(ref, "param type must is Array");
					}
					tn = tn.substring(0, tn.length() - 2);
				}
				
				String sn = ColumnHandlerFactory.get(handlerClass).supportsClass();
				if (!tn.equals(sn)) {
					if(batchable)
						throw new AptException(ref, "param Type unEquals ColumnHandler.supportClass with Array");
						else throw new AptException(ref, "param Type unEquals ColumnHandler.supportClass");
				}
			}

			WhereColumn wc = new WhereColumn();
			wc.setHandlerClass(handlerClass);
			wc.setName(mpe.getName()+ (batchable?"[i]":""));
			wc.setNullable(false);
			wc.setWhereSql(sqlName + " " + WhereFactory.classEls.get(cls) + " ?");
			wc.setCacheValue(false);
			result.addLast(wc);
		}
		return ret;

	}

	@SuppressWarnings("unchecked")
	public static void buildWithEq(MethodParamEntry mpe, DataEntry entry, LinkedList<WhereColumn> result,WherePart part)
			throws AptException {
		VariableElement ref = mpe.getRef();
		boolean batchable = (null != ref.getAnnotation(Batch.class));
		if(batchable)
			part.setBatch(true);		
		Object[] objs = WhereFactory.validWhereParameter(mpe, entry);
		String sqlName = (String) objs[0];
		Class<? extends ColumnHandler> handlerClass = (Class<? extends ColumnHandler>) objs[1];
		String tn = mpe.getTypeName();
		if (batchable) {
			if (!tn.endsWith("[]")) {
				throw new AptException(ref, "param type must is Array");
			}
			tn = tn.substring(0, tn.length() - 2);
		}

		String sn = ColumnHandlerFactory.get(handlerClass).supportsClass();
		if (!tn.equals(sn)) {
			if(batchable)
			throw new AptException(ref, "param Type unEquals ColumnHandler.supportClass with Array");
			else throw new AptException(ref, "param Type unEquals ColumnHandler.supportClass");
		}
		WhereColumn wc = new WhereColumn();
		wc.setHandlerClass(handlerClass);
		wc.setName(mpe.getName()+(batchable?"[i]":""));
		wc.setNullable(false);
		wc.setWhereSql(sqlName + " = ?");
		wc.setCacheValue(batchable);
		result.addLast(wc);

	}

	public static WherePart build(MethodEntry method, DataEntry entry, boolean excludeSet) throws AptException {
		WherePart result = new WherePart();
		result.setBatch(false);
		result.setStaticSentence(WhereFactory.getStaticWhereSql(method.getRef()));
		result.setAnd(WhereFactory.isAnd(method.getRef()));
		List<MethodParamEntry> list = method.getParams();
		LinkedList<WhereColumn> wcs = result.getColumns();
		for (int i = 1; i < list.size(); ++i) {
			MethodParamEntry mpe = list.get(i);
			if (excludeSet && (null != mpe.getRef().getAnnotation(Set.class)))
				continue;
			if (!build(mpe, entry, wcs,result))
				buildWithEq(mpe, entry, wcs,result);
		}
		if (result.getStaticSentence() == null && (result.getColumns().isEmpty()))
			return null;
		return result;
	}


	private BatchWhereFactory() {
	}

}
