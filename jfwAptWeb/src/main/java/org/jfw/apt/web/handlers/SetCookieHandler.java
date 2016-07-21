package org.jfw.apt.web.handlers;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.RequestHandler;
import org.jfw.apt.web.annotation.method.SetCookie;

public class SetCookieHandler  extends RequestHandler {

	@Override
	public void init() {
	}

	@Override
	public void appendBeforCode(ClassWriter cw) throws AptException {
		SetCookie ss = this.aptWebHandler.getMe().getRef().getAnnotation(SetCookie.class);
		if(ss!=null){
           if(ss.checkResultNull()){
        	   cw.l("if(null != result){");
           }
			String[] vals = ss.value();
			for(String val:vals){
				if(val==null || val.trim().length()==0 )throw new AptException(this.aptWebHandler.getMe().getRef(),"invalid @"+SetCookie.class.getName());
				val = val.trim();
				int index = val.indexOf("=");
				if(index<1)throw new AptException(this.aptWebHandler.getMe().getRef(),"invalid @"+SetCookie.class.getName());
				String n = val.substring(0,index).trim();
				String v = val.substring(index+1).trim();
				String tn = cw.getMethodTempVarName();
				cw.bL("javax.servlet.http.Cookie ").w(tn).w(" =  new javax.servlet.http.Cookie(\"").w(n).w("\",").w(v).el(");");
				if(ss.httpOnly()){
					cw.bL(tn).el(".setHttpOnly(true);");
				}
				String path = Util.emptyToNull(ss.path());
				if(null!=path){
					cw.bL(tn).w(".setPath(\"").w(path).el("\");");
				}
				if(ss.secure()){
					cw.bL(tn).el(".setSecure(true);");
				}
				String comment = Util.emptyToNull(ss.comment());
				if(comment!=null){
					cw.bL(tn).w(".setComment(\"").w(comment).el("\");");
				}
				String domain = Util.emptyToNull(ss.domain());
				if(null!= domain){
					cw.bL(tn).w(".setDomain(\"").w(domain).el("\");");
				}
				if(ss.maxAge()>-1){
					cw.bL(tn).w(".setMaxAge(").w(ss.maxAge()).el(");");
				}
				cw.bL("res.addCookie(").w(tn).el(");");			
			}
	           if(ss.checkResultNull()){
	        	   cw.l("}");
	           }
		}
	
	}
}
