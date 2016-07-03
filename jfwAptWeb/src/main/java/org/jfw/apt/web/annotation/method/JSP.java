package org.jfw.apt.web.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface JSP {
	String prefix() default "/WEB-INF/page/";
	String value();
	String dataName() default "JFW_REQUEST_TO_JSP_DATA";
	boolean enableJson() default true;
	int jsonViewType() default 1;
}
