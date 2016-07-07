package org.jfw.apt.orm;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.dao.param.Alias;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.model.DataEntry;

public final class OrmAnnoCheckUtil {
	
	public static void check(Element ele,org.jfw.apt.orm.annotation.dao.Column col) throws AptException{
		if(null== ColumnHandlerFactory.getHandlerClass(col, ele))
			throw new AptException(ele, "invalid annotation value:@"
					+ org.jfw.apt.orm.annotation.dao.Column.class.getName() + "'handlerClass is null or unsupported");
		
		if (col.value() == null || col.value().length == 0) {
			throw new AptException(ele, "invalid annotation value:@"
					+ org.jfw.apt.orm.annotation.dao.Column.class.getName() + "'value is null or empty");
		}

		for (String n : col.value()) {
			if (null == n || n.trim().length() == 0) {
				throw new AptException(ele, "invalid annotation value:@"
						+ org.jfw.apt.orm.annotation.dao.Column.class.getName() + "'value contains null or empty");
			}
		}
	}

	public static void check(Element ele,Alias alias,DataEntry de) throws AptException{
		if (alias.value() == null || alias.value().length == 0) {
			throw new AptException(ele, "invalid annotation value:@"
					+ Alias.class.getName() + "'value is null or empty");
		}

		for (String n : alias.value()) {
			if (null == n || n.trim().length() == 0) {
				throw new AptException(ele, "invalid annotation value:@"
						+ Alias.class.getName() + "'value contains null or empty");
			}
			if(null == de.getCalcColumnByJavaName(n.trim()))
				throw new AptException(ele, "invalid annotation value:@"
						+ Alias.class.getName() + "'value not found in dataEntry");
		}
	}
	
	
	
	private OrmAnnoCheckUtil(){}
}
