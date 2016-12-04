package org.jfw.apt.web.handlers.buildParam;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.annotation.param.RequestHeader;

public final class RequestHeaderHandler  implements BuildParameter {
	public static final RequestHeaderHandler INS = new RequestHeaderHandler();
	
	private RequestHeader header;
	private RequestHeaderHandler(){}
	@Override
	public void build(ClassWriter sb, MethodParamEntry mpe, AptWebHandler aptWebHandler) throws AptException {
		if(!mpe.getTypeName().equals("java.lang.String")){
			throw new AptException(mpe.getRef(),"@"+RequestHeader.class.getName()+" must with patamter[class =java.lang.String]");
		}
		String pn =Util.emptyToNull(header.value());
		String dv = Util.emptyToNull(header.defaultValue());
		sb.bL("String ").w(mpe.getName()).w(" = req.getHeader(\"").w(null == pn?mpe.getName():pn).el("\");");
		
		if(dv==null){
			if(!mpe.isNullable()){
				sb.bL("if(null == ").w(mpe.getName()).el("){");
				sb.l("throw new JfwInvalidParamException(\""+aptWebHandler.getMethodUrl()+" header domain\",\"" + (null == pn?mpe.getName():pn) + "\");");
				//sb.l("throw new IllegalArgumentException(\"not found parameter in header:" + (null == pn?mpe.getName():pn) + "\");");
				sb.l("}");
			}
		}else{
			sb.bL("if(null == ").w(mpe.getName()).el("){");
			sb.bL(mpe.getName()).w(" = ").w(dv).el(";");
			sb.l("}");
		}
		
	}

	@Override
	public boolean match(VariableElement ele) {
		this.header = ele.getAnnotation(RequestHeader.class);
		return null != this.header;
	}

}
