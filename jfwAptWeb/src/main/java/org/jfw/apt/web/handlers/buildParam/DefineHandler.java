package org.jfw.apt.web.handlers.buildParam;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.annotation.param.JdbcConn;
import org.jfw.apt.web.annotation.param.OnlyDefine;

public final class DefineHandler implements BuildParameter{
	
	public static final DefineHandler INS = new DefineHandler();
	private DefineHandler(){}
	public void build(ClassWriter cw, MethodParamEntry mpe, AptWebHandler aptWebHandler) throws AptException{
		String tn = mpe.getTypeName();
		cw.bL(tn).w(" ").w(mpe.getName());
		if(!Util.isPrimitive(tn)) cw.w(" = null");
		cw.el(";");
	}
	public boolean match(VariableElement ele){
		if(null != ele.getAnnotation(JdbcConn.class)){
			return true;
		}
		if(null != ele.getAnnotation(OnlyDefine.class)){
			return true;
		}
		
		return false;
	}
	

}
