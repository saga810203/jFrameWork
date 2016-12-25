package org.jfw.apt.web.handlers.buildParam;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.annotation.param.Cookie;

public final class CookieHandler implements BuildParameter {

	public static final CookieHandler INS = new CookieHandler();
	
	private Cookie cookie;
	@Override
	public void build(ClassWriter sb, MethodParamEntry mpe, AptWebHandler aptWebHandler) throws AptException {
		if(!mpe.getTypeName().equals("java.lang.String"))
			throw new AptException(mpe.getRef(),"param type must be String with @"+Cookie.class.getName());
		String pn = Util.emptyToNull(cookie.value());
		if(pn==null) pn = mpe.getName();
		aptWebHandler.readCookie();
		sb.bL("java.lang.String ").w(mpe.getName()).el(" = null;");
		sb.l("for(javax.servlet.http.Cookie _cookie:_cookies){");
		sb.bL("if(_cookie.getName().equals(\"").w(pn).el("\"){ ");
		sb.bL(mpe.getName()).el(" = _cookie.getValue();");
		sb.l("break;");
		sb.l("}");
		String dv = mpe.getDefaultValue();
		if(dv ==null){
			if(!mpe.isNullable()){
				sb.bL("if(null==").w(mpe.getName()).el(") throw new org.jfw.util.exception.JfwInvalidParamException(\""+aptWebHandler.getMethodUrl()+" cookie domain\",\"" + pn + "\");");	
			}
		}else{
			sb.bL("if(null==").w(mpe.getName()).w(") "+mpe.getName()+"  = ").w(dv).el(";");
		}		
	}

	@Override
	public boolean match(VariableElement ele) {
		this.cookie = ele.getAnnotation(Cookie.class);
		return null != this.cookie;
	}

	private CookieHandler() {
	}
}