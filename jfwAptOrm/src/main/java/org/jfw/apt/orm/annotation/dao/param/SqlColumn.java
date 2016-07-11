package org.jfw.apt.orm.annotation.dao.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jfw.apt.orm.core.ColumnHandler;
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.SOURCE)
public @interface SqlColumn {
	/**
	 * column in where :  ID IN (SELECT OID FROM DDDD WHERE PID = ?)
	 * @return
	 */
	String[] value();
	/**
	 * 
	 * @return
	 */
	Class<? extends ColumnHandler> handlerClass();
}
