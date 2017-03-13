package org.jfw.apt.web.handlers.buildParam;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.Util;
import org.jfw.apt.annotation.Nullable;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.annotation.param.ArrayJson;

public class ArrayJsonHandler implements BuildParameter {
	public static final ArrayJsonHandler INS = new ArrayJsonHandler();

	private ArrayJson arrayJson;

	private ArrayJsonHandler() {
	}

	@Override
	public void build(ClassWriter cw, MethodParamEntry mpe, AptWebHandler aptWebHandler) throws AptException {
		String mpn = mpe.getTypeName();
		if (!mpn.endsWith("[]"))
			throw new AptException(mpe.getRef(), "@ArrayJson must with on Array");
		mpn = mpn.substring(0, mpn.length() - 2);
		if (Util.isPrimitive(mpn))
			throw new AptException(mpe.getRef(), "@ArrayJson not with on primitive array");

		String tn = Util.emptyToNull(arrayJson.targetTypeName());
		if (tn == null) {
			tn = mpn;
		}
		boolean parameterized = tn.indexOf("<") >= 0;
		String vn = Util.emptyToNull(arrayJson.value());
		if (vn == null) {
			vn = mpe.getName();
		}
		boolean nullable = mpe.getRef().getAnnotation(Nullable.class) != null;
		aptWebHandler.readParameters(vn);
		cw.bL(mpn).w("[] ").w(mpe.getName()).el(" =  null;");
		cw.l("if(null!=params && params.length>0){");
		if (parameterized) {
			cw.bL(mpe.getName()).w(" =(").w(tn).el("[]) new Object[params.length];");
		} else {
			cw.bL(mpe.getName()).w(" = new ").w(tn).el("[params.length];");
		}
		cw.l("try{");
		cw.l("for(int i = 0 ; i < params.length; ++i){");
		cw.l("if(null!= params[i] && params[i].length()>0){");
		cw.bL(mpe.getName()).w("[i] = ");
		if (!parameterized) {
			cw.w("org.jfw.util.json.JsonService.fromJson(params[i],").w(tn).el(".class);");
		} else {
			cw.w("org.jfw.util.json.JsonService.<").w(tn)
					.w(">fromJson(params[i],(new org.jfw.util.reflect.TypeReference<").w(tn).el(">(){}).getType());");
		}
		cw.l("}").l("}");
		cw.bL("}catch(Exception ").w(cw.getClassTempVarName()).el("){");
		cw.l("throw new org.jfw.util.exception.JfwInvalidParamException(\"" + aptWebHandler.getMethodUrl() + "\",\""
				+ vn + "\");");
		if (!nullable) {
			cw.l("}else{");
			cw.l("throw new org.jfw.util.exception.JfwInvalidParamException(\"" + aptWebHandler.getMethodUrl() + "\",\""
					+ vn + "\");");
		}
		cw.l("}");
	}

	public boolean match(VariableElement ele) {
		this.arrayJson = ele.getAnnotation(ArrayJson.class);
		return null != this.arrayJson;
	}
}
