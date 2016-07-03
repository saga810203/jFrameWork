package org.jfw.apt.web.handlers.viewImpl;

import javax.lang.model.element.Element;

import org.jfw.apt.Util;
import org.jfw.apt.annotation.Condition;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.annotation.method.ConditionJSP;
import org.jfw.apt.web.handlers.ViewHandler.ViewHandlerImpl;

public class ConditionJspHandler extends ViewHandlerImpl {
	
	public static final ConditionJspHandler INS = new ConditionJspHandler();
	
	private String prefix = null;
	private Condition[] value = null;
	private String dataName = null;
	private boolean enableJson = false;
	private int jsonViewType = 1;
	private String defaultValue;
	private ConditionJSP jsp;

	@Override
	public boolean match(Element ele) {
		this.jsp = ele.getAnnotation(ConditionJSP.class);
		return null != this.jsp;
	}

	@Override
	public void handlerFail(ClassWriter cw) throws AptException {
		if (this.enableJson) {
			cw.bL("if(").w(this.jsonViewType).el("== viewType){");
			ViewUtil.printJSONException(aptWebHandler, cw);
			cw.l("} else {");
		}
		ViewUtil.printJSPException(this.aptWebHandler, cw);
		if (this.enableJson) {
			cw.l("}");
		}
	}

	@Override
	public void handlerSuccess(ClassWriter cw) throws AptException {
		boolean hasResult = !"void".equals(this.aptWebHandler.getReturnType());
		if (this.enableJson) {
			cw.bL("if(").w(this.jsonViewType).el("== viewType){");
			if (hasResult) {
				ViewUtil.printJSONWithValue(this.aptWebHandler, cw);
			} else {
				ViewUtil.printJSONSuccess(this.aptWebHandler, cw);
			}
			cw.l("return;");
			cw.l("}");
		}
		if (hasResult) {
			cw.bL("req.setAttribute(\"").w(this.dataName).el("\",result);");
		}
		
		
		
		cw.bL("String _jspView = \"").w(this.defaultValue).el("\";");
		for (int i = 0; i < value.length; ++i) {
			if (i != 0)
				cw.bL("}else if");
			else
				cw.bL("if");
			cw.w("(").w(value[i].el().trim()).el("){");
			cw.bL("_jspView = \"").w(value[i].value().trim()).el("\";");
		}
		cw.l("}");
		cw.bL("_jspView =");
		if (this.prefix.length() > 0)
			cw.w("\"").w(this.prefix).w("\" + ").el("_jspView + \".jsp\";");		
		cw.l("req.getRequestDispatcher(_jspView).forward(req,res);");
	}

	@Override
	public void init(ClassWriter cw) throws AptException {
		this.enableJson = jsp.enableJson();
		this.jsonViewType = jsp.jsonViewType();
		this.prefix = Util.emptyToNull(jsp.prefix());
		if (this.prefix == null)
			this.prefix = "";

		this.dataName = jsp.dataName();
		this.defaultValue = Util.emptyToNull(jsp.defaultValue());
		if (this.defaultValue == null)
			throw new AptException(this.aptWebHandler.getMe().getRef(), "invalid @ConditionJSP");
		this.value = jsp.value();
		if (this.value == null || this.value.length == 0)
			throw new AptException(this.aptWebHandler.getMe().getRef(), "invalid @ConditionJSP");
		for (Condition c : value) {
			if (null == Util.emptyToNull(c.el()) || (Util.emptyToNull(c.value()) == null))
				throw new AptException(this.aptWebHandler.getMe().getRef(), "invalid @ConditionJSP");
		}
		if(this.enableJson) this.aptWebHandler.readOut();
	}

}
