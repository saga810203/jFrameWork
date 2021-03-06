package org.jfw.apt.orm.annotation.entry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jfw.apt.orm.core.ColumnHandler;
import org.jfw.apt.orm.core.defaultImpl.AbstractColumnHandler;
import org.jfw.apt.orm.core.enums.DE;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Column {
	DE value() default DE.invalid_de;
	Class<? extends ColumnHandler> handlerClass() default AbstractColumnHandler.class;
	String dbType() default "";
	boolean nullable() default false;
	String fixSqlValueWithInsert() default "";
	String fixSqlValueWithUpdate() default "";
	boolean insertable() default true;
	boolean renewable() default true;
	boolean queryable() default true;	
	String name() default "";
}
