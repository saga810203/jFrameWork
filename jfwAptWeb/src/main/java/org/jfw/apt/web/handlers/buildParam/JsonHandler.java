package org.jfw.apt.web.handlers.buildParam;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.Util;
import org.jfw.apt.annotation.Nullable;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.annotation.param.Json;

public class JsonHandler implements BuildParameter {
	public static final JsonHandler INS = new JsonHandler();

	private Json json;

	private JsonHandler() {
	}

	@Override
	public void build(ClassWriter cw, MethodParamEntry mpe, AptWebHandler aptWebHandler) throws AptException {
		if (Util.isPrimitive(mpe.getTypeName()))
			throw new AptException(mpe.getRef(), "@Json not with on primitive");

		String tn = Util.emptyToNull(json.targetTypeName());
		if (tn == null) {
			tn = mpe.getTypeName();
		}
		boolean parameterized = tn.indexOf("<") >= 0;
		String vn = Util.emptyToNull(json.value());
		if (vn == null) {
			vn = mpe.getName();
		}
		boolean nullable = mpe.getRef().getAnnotation(Nullable.class) != null;
		cw.bL(mpe.getTypeName()).w(" ").w(mpe.getName()).el(" =  null;");
		aptWebHandler.readParameter(vn);
		cw.l("if(null!=param && param.length()>0){");
		cw.l("try{");
		cw.bL(mpe.getName()).w(" = ");
		if (!parameterized) {
			cw.w("org.jfw.util.json.JsonService.fromJson(param,").w(tn).el(".class);");

		} else {
			cw.w("org.jfw.util.json.JsonService.<").w(tn).w(">fromJson(param,(new org.jfw.util.reflect.TypeReference<")
					.w(tn).el(">(){}).getType());");
		}
		cw.bL("}catch(Exception ").w(cw.getClassTempVarName()).el("){");
		cw.l("throw new org.jfw.util.exception.JfwInvalidParamException(\"" + aptWebHandler.getMethodUrl() + "\",\""
				+ vn + "\");");
		cw.l("}");
		if (!nullable) {
			cw.l("}else{");
			cw.l("throw new org.jfw.util.exception.JfwInvalidParamException(\"" + aptWebHandler.getMethodUrl() + "\",\""
					+ vn + "\");");
		}
		cw.l("}");
	}

	public boolean match(VariableElement ele) {
		this.json = ele.getAnnotation(Json.class);
		return null != this.json;
	}
}
