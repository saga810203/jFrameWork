package org.jfw.apt.web.handlers;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.RequestHandler;
import org.jfw.apt.web.handlers.viewImpl.ConditionJspHandler;
import org.jfw.apt.web.handlers.viewImpl.JsonHandler;
import org.jfw.apt.web.handlers.viewImpl.JspHandler;
import org.jfw.apt.web.handlers.viewImpl.ValueJspHandler;

public class ViewHandler extends RequestHandler {

	private ViewHandlerImpl defaultViewHandlerImpl;

	private ViewHandlerImpl[] handlers;

	private ViewHandlerImpl currViewHandlerImpl;

	@Override
	public void appendBeforCode(ClassWriter cw) throws AptException {
		this.currViewHandlerImpl = null;
		for (int i = 0; i < this.handlers.length; ++i) {
			if (this.handlers[i].match(aptWebHandler.getMe().getRef())) {
				this.currViewHandlerImpl = this.handlers[i];
				break;
			}
		}
		if (null == this.currViewHandlerImpl)
			this.currViewHandlerImpl = defaultViewHandlerImpl;

		this.currViewHandlerImpl.appendBeforCode(cw);

	}

	@Override
	public void appendAfterCode(ClassWriter cw) throws AptException {
		this.currViewHandlerImpl.appendAfterCode(cw);
	}

	public static abstract class ViewHandlerImpl {

		protected AptWebHandler aptWebHandler;

		public AptWebHandler getAptWebHandler() {
			return aptWebHandler;
		}

		public void setAptWebHandler(AptWebHandler aptWebHandler) {
			this.aptWebHandler = aptWebHandler;
		}

		public abstract boolean match(Element ele);

		public abstract void handlerFail(ClassWriter cw) throws AptException;

		public abstract void handlerSuccess(ClassWriter cw) throws AptException;

		public abstract void init(ClassWriter cw) throws AptException;

		public void appendBeforCode(ClassWriter cw) throws AptException {
			this.init(cw);
			cw.l("try{");
		}

		public void appendAfterCode(ClassWriter cw) throws AptException {
			cw.l("}catch(Exception e){");// begin catch
			this.handlerFail(cw);
			cw.l("return;");
			cw.l("}");// end catch
			this.handlerSuccess(cw);
		}
	}

	@Override
	public void init() {
		this.defaultViewHandlerImpl = JsonHandler.INS;
		this.defaultViewHandlerImpl.setAptWebHandler(aptWebHandler);

		this.handlers = new ViewHandlerImpl[] { JspHandler.INS, ValueJspHandler.INS, ConditionJspHandler.INS};
		
		for (ViewHandlerImpl vhi : this.handlers) {
			vhi.setAptWebHandler(this.aptWebHandler);
		}
	}
}
