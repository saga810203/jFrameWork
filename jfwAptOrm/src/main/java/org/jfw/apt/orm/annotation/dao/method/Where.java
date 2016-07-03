package org.jfw.apt.orm.annotation.dao.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Where {
	/**
	 * static sql in where sentence
	 * 
	 * @Where("ID='123456'")
	 * @return
	 */
   String value();  
}
