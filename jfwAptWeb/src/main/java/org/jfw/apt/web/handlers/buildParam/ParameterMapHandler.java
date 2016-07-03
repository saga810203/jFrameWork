package org.jfw.apt.web.handlers.buildParam;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.annotation.param.ParameterMap;

public final class ParameterMapHandler implements  BuildParameter{
	
	public static final ParameterMapHandler  INS = new ParameterMapHandler();
	private ParameterMapHandler(){}
	@Override
	public void build(ClassWriter cw, MethodParamEntry mpe, AptWebHandler aptWebHandler) throws AptException {
		if(!mpe.getTypeName().equals("java.util.Map<String,String[]>")) throw new AptException(mpe.getRef(),"@"+ParameterMap.class.getName()+" mast with java.util.Map<String,String[]>");
		cw.bL("java.util.Map<String,String[]> ").w(mpe.getName()).el(" =  req.getParameterMap();");
	}

	@Override
	public boolean match(VariableElement ele) {
		return null != ele.getAnnotation(ParameterMap.class);
	}

}
