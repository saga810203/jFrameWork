package org.jfw.apt.orm.annotation.entry;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
/**
 * only handle @Column  in class
 * 
 * target must be class
 *  
 * maybe extend a class with(@Table)
 * maybe impl many interface with (@VirtualTable)
 * 
 * @author Saga_
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Table {
	/**
	 * table name 
	 * maybe empty auto gen by class simplename
	 * 
	 * @return
	 */
	String value() default "";
}
