package org.jfw.apt.orm.annotation.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jfw.apt.orm.core.ColumnHandler;
@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface Column {
	/**
	 * column in where :  ID   A.ID 
	 * @return
	 */
	String[] value();
	/**
	 * 
	 * @return
	 */
	Class<? extends ColumnHandler> handlerClass();
}
