package org.jfw.apt.web.handlers.buildParam;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;

public interface BuildParameter {
	void build(ClassWriter sb, MethodParamEntry mpe, AptWebHandler aptWebHandler) throws AptException;
	boolean match(VariableElement ele);
}
