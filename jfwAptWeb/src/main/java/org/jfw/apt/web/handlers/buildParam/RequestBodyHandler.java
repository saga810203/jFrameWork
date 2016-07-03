package org.jfw.apt.web.handlers.buildParam;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.annotation.param.RequestBody;

public final class RequestBodyHandler implements BuildParameter {
	public static final RequestBodyHandler INS = new RequestBodyHandler();
	
	private RequestBody body;
	private RequestBodyHandler(){}
	public void build(ClassWriter cw, MethodParamEntry mpe, AptWebHandler aptWebHandler) throws AptException{
		if (Util.isPrimitive(mpe.getTypeName()))
			throw new AptException(mpe.getRef(), "@RequestBody not with on primitive");

		String tn = Util.emptyToNull(body.targetTypeName());
		if (tn == null) {
			tn = mpe.getTypeName();
		}
		
		if(mpe.getTypeName().equals("java.lang.String")){
			cw.bL("String ").w(mpe.getName()).el("= org.jfw.util.StringUtil.fromByUTF8AndClose(req.getInputStream());");
			if(body.encoding()){
				cw.bL(mpe.getName()).w("=java.net.URLDecoder.decode(").w(mpe.getName()).el("\"UTF-8\");");
			}
			return;			
		}
		if(mpe.getTypeName().equals("java.io.InputStream")){
			cw.bL("java.io.InputStream ").w(mpe.getName()).el(" = req.getInputStream();");
			return;
		}
		
		boolean parameterized = tn.indexOf("<") >= 0;
		cw.bL(mpe.getTypeName()).w(" ").w(mpe.getName()).el(" =  null;");
		String localName = cw.getMethodTempVarName();
		cw.bL("java.io.InputStream ").w(localName).el(" = req.getInputStream();").bL("try{").w(mpe.getName())
				.w(" = ");
		if (!parameterized) {
			cw.w("org.jfw.util.json.JsonService.fromJson(new java.io.InputStreamReader(").w(localName)
					.w(", org.jfw.util.ConstData.UTF8),").w(tn).el(".class);");
		} else {
			cw.w("org.jfw.util.json.JsonService.<").w(tn).w(">fromJson(new java.io.InputStreamReader(")
					.w(localName)
					.w(", org.jfw.util.ConstData.UTF8),new new org.jfw.util.reflect.TypeReference<").w(tn)
					.el(">(){}.getType() );");
		}
		cw.l("}finally{").bL(localName).el(".close();").l("}");

	}
	public boolean match(VariableElement ele){
		this.body = ele.getAnnotation(RequestBody.class);
		return null != this.body;
	}
	

}
