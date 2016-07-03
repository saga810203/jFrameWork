package org.jfw.apt.web.handlers.buildParam;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.annotation.LoginUser;
import org.jfw.apt.web.annotation.param.Ignore;

public final class NopHandler implements BuildParameter {
	
	public static final NopHandler INS = new NopHandler();
	private NopHandler(){}
	public void build(ClassWriter cw, MethodParamEntry mpe, AptWebHandler aptWebHandler) throws AptException{
	}
	public boolean match(VariableElement ele){
		if(null != ele.getAnnotation(LoginUser.class)){
			return true;
		}
		if(null != ele.getAnnotation(Ignore.class)){
			return true;
		}
		return false;
	}
	
}
