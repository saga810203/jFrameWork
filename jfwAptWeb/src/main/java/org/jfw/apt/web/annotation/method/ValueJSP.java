package org.jfw.apt.web.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface ValueJSP {
//	Class<?> viewHandlerClass() default ValueJspHandler.class;
	String prefix() default "";
	String value();// result.getPage()
	String dataName() default "JFW_REQUEST_TO_JSP_DATA";
	boolean enableJson() default false;
	int jsonViewType() default 1;
}
