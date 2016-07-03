package org.jfw.apt.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface LoginUser {
	/**
	 * false:enable no login
	 * true: must be login
	 */
	boolean value() default true;
	/**
	 * valid authority , 0  don't valid
	 */
	int auth() default 0;
//	Class<? extends BuildParameter> buildParamClass() default NopHandler.class;
}
