package org.jfw.apt.web.handlers;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.RequestHandler;
import org.jfw.apt.web.annotation.method.SetSession;

public class SetSessionHandler  extends RequestHandler {

	@Override
	public void init() {
	}

	@Override
	public void appendBeforCode(ClassWriter cw) throws AptException {
		SetSession ss = this.aptWebHandler.getMe().getRef().getAnnotation(SetSession.class);
		if(ss!=null){
			this.aptWebHandler.readSession();
			String[] vals = ss.value();
			for(String val:vals){
				if(val==null || val.trim().length()==0 )throw new AptException(this.aptWebHandler.getMe().getRef(),"invalid @SetSession");
				val = val.trim();
				int index = val.indexOf("=");
				if(index<1 || (index>=(val.length()-1)))throw new AptException(this.aptWebHandler.getMe().getRef(),"invalid @SetSession");
				String n = val.substring(0,index).trim();
				String v = val.substring(index+1).trim();
				if(v.length()==0)throw new AptException(this.aptWebHandler.getMe().getRef(),"invalid @SetSession");
				cw.bL("session.setAttribute(\"").w(n).w("\",").w(v).el(");");			
			}
		}
	
	}



}
