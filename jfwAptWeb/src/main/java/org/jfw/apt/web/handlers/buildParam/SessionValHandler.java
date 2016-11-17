package org.jfw.apt.web.handlers.buildParam;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.Util;
import org.jfw.apt.annotation.Nullable;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.annotation.param.SessionVal;

public final class SessionValHandler implements BuildParameter {

	public static final SessionValHandler INS = new SessionValHandler();

	private SessionVal sv;

	private SessionValHandler() {
	}

	@Override
	public void build(ClassWriter cw, MethodParamEntry mpe, AptWebHandler aptWebHandler) throws AptException {
		aptWebHandler.readSession();
		String val = Util.emptyToNull(sv.value());
		if (null == val)
			val = mpe.getName();
		String dv = mpe.getDefaultValue();
		if (mpe.isPrimitive()) {
			String wn = Util.getWrapClass(mpe.getTypeName());
			cw.bL(mpe.getTypeName()).w(" ").w(mpe.getName()).el(";");
			String ln = cw.getMethodTempVarName();
			cw.bL(wn).w(" ").w(ln).w(" = (").w(wn).w(")session.getAttribute(\"").w(val).el("\");");

			cw.bL("if(null==").w(ln).el("){");
			if (dv == null) {
				cw.l("throw new JfwInvalidParamException(\"" + aptWebHandler.getMethodUrl() + " session domain\",\""
						+ val + "\");");
			} else {
				cw.bL(mpe.getName()).w("=").w(dv).el(";");
			}
			cw.l("}else{");
			cw.bL(mpe.getName()).w(" = ").w(ln).el(";");
			cw.l("}");
		} else {
			cw.bL(mpe.getTypeName()).w(" ").w(mpe.getName()).w(" = (").w(mpe.getTypeName())
					.w(")session.getAttribute(\"").w(val).el("\");");

			if (dv == null) {
				if (!mpe.isNullable()) {
					cw.bL("if(null==").w(mpe.getName()).el("){");
					cw.l("throw new JfwInvalidParamException(\"" + aptWebHandler.getMethodUrl() + " session domain\",\""
							+ val + "\");");
					cw.l("}");
				} // cw.l("throw new IllegalArgumentException(\"not found
				// session value:" + val + "\");");
			} else {
				cw.bL("if(null==").w(mpe.getName()).el("){");
				cw.bL(mpe.getName()).w(" = ").w(dv).el(";");
				cw.l("}");
			}

		}

		if (sv.remove()) {
			cw.bL("session.removeAttribute(\"").w(val).el("\");");
		}

	}

	@Override
	public boolean match(VariableElement ele) {
		this.sv = ele.getAnnotation(SessionVal.class);
		return null != sv;

	}

}
