package org.jfw.apt.web.annotation.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface SessionVal {
	String value();
	boolean remove() default false;
	String defaultvalue() default "";
//	Class<? extends BuildParameter> buildParamClass() default SessionValHandler.class;
}
