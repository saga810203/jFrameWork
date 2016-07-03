package org.jfw.apt.web.handlers.viewImpl;

import javax.lang.model.element.Element;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.annotation.method.JSP;
import org.jfw.apt.web.handlers.ViewHandler.ViewHandlerImpl;

public class JspHandler extends ViewHandlerImpl {
	
	public static final JspHandler INS = new JspHandler();
	
	private String prefix = null;
	private String value = null;
	private String dataName = null;
	private boolean enableJson = false;
	private int jsonViewType = 1;
	private JSP jsp;

	@Override
	public boolean match(Element ele) {
		this.jsp = ele.getAnnotation(JSP.class);
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
		cw.bL("req.getRequestDispatcher(\"").w(this.prefix).w(value).el(".jsp\").forward(req,res);");
	}

	@Override
	public void init(ClassWriter cw) throws AptException {
		this.enableJson = jsp.enableJson();
		this.jsonViewType = jsp.jsonViewType();
		this.prefix = Util.emptyToNull(jsp.prefix());
		if (this.prefix == null)
			this.prefix = "";
		this.value = Util.emptyToNull(jsp.value());
		if (this.value == null)
			throw new AptException(aptWebHandler.getMe().getRef(), "invalid @" + JSP.class.getName());
		this.dataName = jsp.dataName();
		if (this.enableJson)
			aptWebHandler.readOut();

	}

}
