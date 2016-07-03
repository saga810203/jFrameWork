package org.jfw.apt.web.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jfw.apt.annotation.Condition;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface ConditionJSP {
	String prefix() default "";
	String defaultValue();
	String dataName() default "JFW_REQUEST_TO_JSP_DATA";
	Condition[] value();
	boolean enableJson() default false;
	int jsonViewType() default 1;
}
