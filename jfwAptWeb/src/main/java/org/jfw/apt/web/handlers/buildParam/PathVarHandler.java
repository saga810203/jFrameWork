package org.jfw.apt.web.handlers.buildParam;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.annotation.param.PathVar;

public final class PathVarHandler implements BuildParameter {
	public static final PathVarHandler INS = new PathVarHandler();
	
	
	private PathVar pathvar;
	private PathVarHandler(){}
	@Override
	public boolean match(VariableElement ele) {
		this.pathvar = ele.getAnnotation(PathVar.class);
		return null != this.pathvar;
	}

	private int getIndexInPath(String name, String path) {
		String pathL = "{" + name.trim() + "}";
		String[] paths = path.split("/");
		for (int i = 1; i < paths.length; ++i) {
			if (pathL.equals(paths[i])) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void build(ClassWriter cw, MethodParamEntry mpe, AptWebHandler aptWebHandler) throws AptException {
		
		aptWebHandler.readURI();
		String val = mpe.getName().trim();
		String path =aptWebHandler.getUrl()+ aptWebHandler.getMethodUrl();
		int pathIndex = getIndexInPath(val, path) - 1;
		if (pathIndex < 0)
			throw new AptException(mpe.getRef(), "invalid annotation @PathVar ");
		
		

		cw.bL(mpe.getTypeName()).w(" ").w(mpe.getName()).w(" = ");

		String lTypeName = mpe.getTypeName();
		if (lTypeName.equals(int.class.getName())) {
			cw.w("Integer.parseInt(_uriArray[").w(pathIndex).el("]);");
		} else if (lTypeName.equals(Integer.class.getName())) {
			cw.w("Integer.valueOf(_uriArray[").w(pathIndex).el("]);");
		} else if (lTypeName.equals(byte.class.getName())) {
			cw.w("Byte.parseByte(_uriArray[").w(pathIndex).el("]);");
		} else if (lTypeName.equals(Byte.class.getName())) {
			cw.w("Byte.valueOf(_uriArray[").w(pathIndex).el("]);");
		} else if (lTypeName.equals(short.class.getName())) {
			cw.w("Short.parseShort(_uriArray[").w(pathIndex).el("]);");
		} else if (lTypeName.equals(Short.class.getName())) {
			cw.w("Short.valueOf(_uriArray[").w(pathIndex).el("]);");
		} else if (lTypeName.equals(long.class.getName())) {
			cw.w("Long.parseLong(_uriArray[").w(pathIndex).el("]);");
		} else if (lTypeName.equals(Long.class.getName())) {
			cw.w("Long.valueOf(_uriArray[").w(pathIndex).el("]);");
		} else if (lTypeName.equals(double.class.getName())) {
			cw.w("Double.parseDouble(_uriArray[").w(pathIndex).el("]);");
		} else if (lTypeName.equals(Double.class.getName())) {
			cw.w("Double.valueOf(_uriArray[").w(pathIndex).el("]);");
		} else if (lTypeName.equals(float.class.getName())) {
			cw.w("Float.parseFloat(_uriArray[").w(pathIndex).el("]);");
		} else if (lTypeName.equals(Float.class.getName())) {
			cw.w("Float.valueOf(_uriArray[").w(pathIndex).el("]);");
		} else if (lTypeName.equals(boolean.class.getName()) || lTypeName.equals(Boolean.class.getName())) {
			cw.w("\"1\".equals(_uriArray[").w(pathIndex).w("])|| \"true\".equalsIgnoreCase(_uriArray[")
					.w(pathIndex).w("])||\"yes\".equalsIgnoreCase(_uriArray[").w(pathIndex)
					.el("]);");
		} else if (lTypeName.equals(String.class.getName())) {
			if (pathvar.encoding()) {
				cw.w("java.net.URLDecoder.decode(_uriArray[").w(pathIndex).el("],\"UTF-8\");");
			} else {
				cw.w("_uriArray[").w(pathIndex).el("];");
			}
		} else if (lTypeName.equals(java.math.BigInteger.class.getName())) {
			cw.w("java.math.BigInteger.valueOf(_uriArray[").w(pathIndex).el("]);");
		} else if (lTypeName.equals(java.math.BigDecimal.class.getName())) {
			cw.w("java.math.BigDecimal.valueOf(_uriArray[").w(pathIndex).el("]);");
		} else {
			throw new AptException(mpe.getRef(), "UnSupportedType on paramter with @PathVar");
		}
	}

}
