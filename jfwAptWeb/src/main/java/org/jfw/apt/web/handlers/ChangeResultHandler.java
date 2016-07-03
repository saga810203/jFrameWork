package org.jfw.apt.web.handlers;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.RequestHandler;
import org.jfw.apt.web.annotation.method.ChangeResult;

public class ChangeResultHandler  extends RequestHandler {

	@Override
	public void init() {
	}

	@Override
	public void appendBeforCode(ClassWriter cw) throws AptException {
		ChangeResult cr = this.aptWebHandler.getMe().getRef().getAnnotation(ChangeResult.class);
		if(cr!=null){
		  if(!"void".equals(this.aptWebHandler.getReturnType())){
			  String ss = Util.emptyToNull(cr.value());
			  if(ss!=null){
				  cw.bL("result = ").w(this.aptWebHandler.getSourceClassname()).w(".").w(ss).el("(result);");
			  }
		  }
		}
	}



}
