package org.jfw.apt.web.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface SetCookie {
	String[] value();
	boolean httpOnly() default false;
	boolean secure() default false;
	String comment() default "";
	String domain() default "";
	String path() default "/";
	int maxAge() default -1;
	boolean checkResultNull() default false;	
}
