package org.jfw.apt.web.handlers.viewImpl;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.handlers.ViewHandler;

public class JsonHandler extends ViewHandler.ViewHandlerImpl{
	
	public static final JsonHandler INS= new JsonHandler();

	@Override
	public boolean match(Element ele) {
		return false;
	}
	

	@Override
	public void init(ClassWriter cw) {
		cw.l("res.setContentType(\"application/json\");");
		this.aptWebHandler.readOut();
	}


	@Override
	public void handlerFail(ClassWriter cw) throws AptException {
		ViewUtil.printJSONException(aptWebHandler, cw);
		
	}

	@Override
	public void handlerSuccess(ClassWriter cw) throws AptException {
		boolean hasResult = !"void".equals(this.aptWebHandler.getReturnType());
		if(hasResult){
			ViewUtil.printJSONWithValue(aptWebHandler,cw);
		}else{
			ViewUtil.printJSONSuccess(aptWebHandler,cw);
		}	
		
	}


}
