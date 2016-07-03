package org.jfw.apt.orm.annotation.entry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * only handle @Column  in class
 * 
 * Traget must be  interface;
 * 
 * maybe extend many interface with(@VirtualTable)
 * 
 * @author Saga_
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface VirtualTable {

}
