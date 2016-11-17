package org.jfw.apt.web.handlers.buildParam;

import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.model.RequestParamModel;

public abstract class AbstractRequestParamTransfer implements RequestParamTransfer {
	protected ClassWriter cw;
	protected MethodParamEntry mpe;
	protected AptWebHandler aptWebHandler;
	protected RequestParamModel annotation;
	protected RequestParamTransfer.FieldRequestParam frp;

	public abstract void bulidParam();

	public abstract void bulidBeanProterty();

	public void checkRequestFieldParamName() {
		if (this.frp.getValue() == null || this.frp.getValue().trim().length() == 0) {
			throw new RuntimeException("@RequestParam.fields no set value");
		}
	}

	public void raiseNoFoundError(String paramName) {
		this.cw.l("throw new JfwInvalidParamException(\""+this.aptWebHandler.getMethodUrl()+"\",\"" + paramName + "\");");
	}
	public void raiseNoFoundError(String paramName,String exParam) {
		this.cw.l("throw new JfwInvalidParamException(\""+this.aptWebHandler.getMethodUrl()+"\",\"" + paramName + "\","+exParam+");");
	}
	

	@Override
	public void transfer(ClassWriter cw,MethodParamEntry mpe,AptWebHandler aptWebHandler,
			RequestParamModel annotation) {
		this.cw = cw;
		this.mpe = mpe;
		this.annotation = annotation;
		this.frp = null;
		this.aptWebHandler = aptWebHandler;
		this.bulidParam();
	}

	@Override
	public void transferBeanProperty(ClassWriter cw,MethodParamEntry mpe,AptWebHandler aptWebHandler,
			RequestParamTransfer.FieldRequestParam frp) {
		this.cw = cw;
		this.mpe = mpe;
		this.aptWebHandler = aptWebHandler;
		this.frp = frp;
		this.bulidBeanProterty();
	}

}
