package org.jfw.apt.orm.core.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.orm.OrmAnnoCheckUtil;
import org.jfw.apt.orm.annotation.dao.Column;
import org.jfw.apt.orm.annotation.dao.param.Alias;
import org.jfw.apt.orm.core.ColumnHandler;
import org.jfw.apt.orm.core.ColumnHandlerFactory;

public final class ParamColumn {
	private String name;
	private Class<? extends ColumnHandler> handlerClass;

	public String getName() {
		return name;
	}

	public Class<? extends ColumnHandler> getHandlerClass() {
		return handlerClass;
	}

	public static List<ParamColumn> build(MethodParamEntry mpe, DataEntry entry) throws AptException {
		VariableElement ele = mpe.getRef();
		String cn = mpe.getName();
		List<ParamColumn> result = new LinkedList<ParamColumn>();
		org.jfw.apt.orm.annotation.dao.Column col = ele.getAnnotation(org.jfw.apt.orm.annotation.dao.Column.class);
		Class<? extends ColumnHandler> hc = null;
		if (col != null) {
			OrmAnnoCheckUtil.check(ele, col);
			hc = ColumnHandlerFactory.getHandlerClass(col, ele);
			for (String n : col.value()) {
				ParamColumn pc = new ParamColumn();
				pc.name = n.trim();
				pc.handlerClass = hc;
				result.add(pc);
			}
		}else{
			Alias alias = ele.getAnnotation(Alias.class);
			if(null!= alias){
				OrmAnnoCheckUtil.check(ele, alias,entry);
				List<String> columnNames = new ArrayList<String>(alias.value().length);
				for(String n:alias.value()){
					String nr = n.trim();
					if(!columnNames.contains(nr)){
						columnNames.add(nr);
						CalcColumn c = entry.getCalcColumnByJavaName(nr);
						ParamColumn pc = new ParamColumn();
						pc.name =c.getSqlName();
						pc.handlerClass = c.getHandlerClass();
						result.add(pc);
					}
				}	
			}else{
				CalcColumn c = entry.getCalcColumnByJavaName(cn);
				if(c==null)throw new AptException(ele, "not found annotation:@" + Column.class.getName()+ " or invlaid param name with column javaname");
				ParamColumn pc = new ParamColumn();
				pc.name =c.getSqlName();
				pc.handlerClass = c.getHandlerClass();
				result.add(pc);
			}	
		}
		return result;
	}

	private ParamColumn() {
	}

}
