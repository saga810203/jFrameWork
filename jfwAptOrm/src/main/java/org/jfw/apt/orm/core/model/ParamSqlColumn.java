package org.jfw.apt.orm.core.model;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.orm.OrmAnnoCheckUtil;
import org.jfw.apt.orm.core.ColumnHandler;
import org.jfw.apt.orm.core.ColumnHandlerFactory;

public class ParamSqlColumn {
	private String sql;
	private Class<? extends ColumnHandler> handlerClass;

	public String getSql() {
		return sql;
	}

	public Class<? extends ColumnHandler> getHandlerClass() {
		return handlerClass;
	}
	public static List<ParamSqlColumn> build(MethodParamEntry mpe, DataEntry entry) throws AptException {
		VariableElement ele = mpe.getRef();
		List<ParamSqlColumn> result = new LinkedList<ParamSqlColumn>();
		org.jfw.apt.orm.annotation.dao.param.SqlColumn col = ele.getAnnotation(org.jfw.apt.orm.annotation.dao.param.SqlColumn.class);
		Class<? extends ColumnHandler> hc = null;
		if (col != null) {
			OrmAnnoCheckUtil.check(ele, col);
			hc = ColumnHandlerFactory.getHandlerClass(col, ele);
			for (String n : col.value()) {
				ParamSqlColumn pc = new ParamSqlColumn();
				pc.sql = n.trim();
				pc.handlerClass = hc;
				result.add(pc);
			}
		}
		return result;
	}
	
	
	private ParamSqlColumn(){}
}
