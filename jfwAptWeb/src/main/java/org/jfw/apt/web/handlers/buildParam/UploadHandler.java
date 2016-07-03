package org.jfw.apt.web.handlers.buildParam;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.annotation.param.Upload;

public final class UploadHandler implements BuildParameter{
	
	public static final UploadHandler INS = new UploadHandler();
	
	private UploadHandler(){}

	@Override
	public void build(ClassWriter cw, MethodParamEntry mpe, AptWebHandler aptWebHandler) throws AptException {
		cw.l("if(!org.jfw.util.web.fileupload.impl.UploadItemIteratorImpl.isMultipartContent(req)){")
		  .l("throw new RuntimeException(\"invalid request with Multipart\");")
		  .l("}")		
		.bL("org.jfw.util.web.fileupload.UploadItemIterator ").w(mpe.getName())
		.el(" = org.jfw.util.web.fileupload.impl.UploadItemIteratorImpl.build(req);");
	}

	@Override
	public boolean match(VariableElement ele) {
		return null != ele.getAnnotation(Upload.class);
	}
}
