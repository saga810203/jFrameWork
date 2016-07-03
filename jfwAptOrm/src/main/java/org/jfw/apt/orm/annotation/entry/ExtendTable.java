package org.jfw.apt.orm.annotation.entry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * only handle @CalcColumn  in class
 * 
 * must extend a class with(@Table)
 * 
 * @author Saga_
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ExtendTable {
}
