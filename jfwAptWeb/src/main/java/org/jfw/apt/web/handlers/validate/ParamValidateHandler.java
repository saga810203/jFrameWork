package org.jfw.apt.web.handlers.validate;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;

public interface ParamValidateHandler {
	
	boolean match(VariableElement ele);
	void handle(ClassWriter cs,MethodParamEntry mpe,AptWebHandler aptWebHandler)throws AptException;

}
