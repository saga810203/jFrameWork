package org.jfw.apt.web.handlers.buildParam;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.Util;
import org.jfw.apt.annotation.Nullable;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.annotation.param.PVar;

public class PVarHandler implements BuildParameter {
	public static final PVarHandler INS = new PVarHandler();
	private PVar pvar ;
	@Override
	public boolean match(VariableElement ele) {
		this.pvar = ele.getAnnotation(org.jfw.apt.web.annotation.param.PVar.class);
		return null != this.pvar;
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
    public static final String TMP_VAR=PVarHandler.class.getName()+"_TMPVAR";
	@Override
	public void build(ClassWriter cw, MethodParamEntry mpe, AptWebHandler aptWebHandler) throws AptException {
		PVar pv = (PVar) mpe.getRef().getAnnotation(PVar.class);
		if(pv==null) return;
		
		aptWebHandler.readURI();
		String val = mpe.getName().trim();
		
		String path = aptWebHandler.getMethodUrl();
		int pathIndex = getIndexInPath(val, path)-1;
		if (pathIndex < 0)
			throw new AptException(mpe.getRef(),"invalid annotation @PVar ");
		String tmpVar = (String)cw.getMethodScope().get(TMP_VAR);
		if(tmpVar==null){
			cw.bL("String ");
			tmpVar=cw.getMethodTempVarName();
			cw.getMethodScope().put(TMP_VAR, tmpVar);
			cw.w(tmpVar).w("=_uriArray[").w(pathIndex).el("].substring(1);");
		}else{
			cw.bL(tmpVar).w("=_uriArray[").w(pathIndex).el("].substring(1);");
		}
		
		
		cw.bL(mpe.getTypeName()).w(" ").w(mpe.getName());
		
		String dv =Util.emptyToNull(pv.defaultValue());
		if(dv!= null){
			cw.w(" = ").w(dv);
		}
		cw.el(";");
		
		String lTypeName = mpe.getTypeName();
		cw.bL("if(").w(tmpVar).el(".length()>0){");		
		cw.bL(mpe.getName()).w("=");		
		if (lTypeName.equals(int.class.getName())) {
			cw.w("Integer.parseInt(").w(tmpVar).el(");");
		} else if (lTypeName.equals(Integer.class.getName())) {
			cw.w("Integer.valueOf(").w(tmpVar).el(");");
		} else if (lTypeName.equals(byte.class.getName())) {
			cw.w("Byte.parseByte(").w(tmpVar).el(");");
		} else if (lTypeName.equals(Byte.class.getName())) {
			cw.w("Byte.valueOf(").w(tmpVar).el(");");
		} else if (lTypeName.equals(short.class.getName())) {
			cw.w("Short.parseShort(").w(tmpVar).el(");");
		} else if (lTypeName.equals(Short.class.getName())) {
			cw.w("Short.valueOf(").w(tmpVar).el(");");
		} else if (lTypeName.equals(long.class.getName())) {
			cw.w("Long.parseLong(").w(tmpVar).el(");");
		} else if (lTypeName.equals(Long.class.getName())) {
			cw.w("Long.valueOf(").w(tmpVar).el(");");
		} else if (lTypeName.equals(double.class.getName())) {
			cw.w("Double.parseDouble(").w(tmpVar).el(");");
		} else if (lTypeName.equals(Double.class.getName())) {
			cw.w("Double.valueOf(").w(tmpVar).el(");");
		} else if (lTypeName.equals(float.class.getName())) {
			cw.w("Float.parseFloat(").w(tmpVar).el(");");
		} else if (lTypeName.equals(Float.class.getName())) {
			cw.w("Float.valueOf(").w(tmpVar).el(");");
		} else if (lTypeName.equals(boolean.class.getName()) || lTypeName.equals(Boolean.class.getName())) {
			cw.w("\"1\".equals(").w(tmpVar).w(")|| \"true\".equalsIgnoreCase(").w(tmpVar).w(")||\"yes\".equalsIgnoreCase(").w(tmpVar).el(");");
		} else if (lTypeName.equals(String.class.getName())) {
			if (pv.encoding()) {
				cw.w("java.net.URLDecoder.decode(").w(tmpVar).w("),\"UTF-8\");");
			} else {
				cw.w(tmpVar).el(";");
			}
		} else if (lTypeName.equals(java.math.BigInteger.class.getName())) {
			cw.w("java.math.BigInteger.valueOf(").w(tmpVar).el(");");
		} else if (lTypeName.equals(java.math.BigDecimal.class.getName())) {
			cw.w("java.math.BigDecimal.valueOf(").w(tmpVar).el(");");
		} else {
			throw new AptException(mpe.getRef(),"UnSupportedType on paramter with @PVar");
		}
		cw.l("}else{");
		if(dv== null && (null != mpe.getRef().getAnnotation(Nullable.class))){
			cw.bL(mpe.getName()).w("= null;");
		}else{
			cw.l("throw new IllegalArgumentException(\"not found parameter:" + mpe.getName() + "\");");
		}
		cw.l("}");
	}

}
