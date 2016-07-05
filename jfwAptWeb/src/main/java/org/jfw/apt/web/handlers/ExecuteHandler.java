package org.jfw.apt.web.handlers;

import java.util.List;
import java.util.ListIterator;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.RequestHandler;

public class ExecuteHandler extends RequestHandler {

	@Override
	public void init() {
	}

	
	@Override
	public void appendBeforCode(ClassWriter cw) throws AptException {
		List<MethodParamEntry> mpes = this.aptWebHandler.getMe().getParams();

	
		if(!"void".equals(this.aptWebHandler.getReturnType())){
			cw.bL(" result = ");
		}else{
			cw.bL("");
		}
		cw.w("handler.").w(this.aptWebHandler.getMe().getName()).w("(");
		boolean first = true;
		for(ListIterator<MethodParamEntry> it = mpes.listIterator(); it.hasNext();){
			MethodParamEntry mpe = it.next();
			if(first){
				first = false;
			}else{
				cw.w(",");
			}
			cw.w(mpe.getName());
		}
		cw.el(");");
	}
}
