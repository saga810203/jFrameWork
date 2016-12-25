package org.jfw.apt.web.handlers.buildParam;

import org.jfw.apt.Util;

public class BaseTypeRPT extends AbstractRequestParamTransfer {

	public void transferToParam(String classType) {
		String cn = classType;
		if (cn.equals("int")) {
			this.cw.w("Integer.parseInt(param)");
		} else if (cn.equals(Integer.class.getName())) {
			this.cw.w("Integer.valueOf(param)");
		} else if (cn.equals(byte.class.getName())) {
			this.cw.w("Byte.parseByte(param)");
		} else if (cn.equals(Byte.class.getName())) {
			this.cw.w("Byte.valueOf(param)");
		} else if (cn.equals(short.class.getName())) {
			this.cw.w("Short.parseShort(param)");
		} else if (cn.equals(Short.class.getName())) {
			this.cw.w("Short.valueOf(param)");
		} else if (cn.equals(long.class.getName())) {
			this.cw.w("Long.parseLong(param)");
		} else if (cn.equals(Long.class.getName())) {
			this.cw.w("Long.valueOf(param)");
		} else if (cn.equals(double.class.getName())) {
			this.cw.w("Double.parseDouble(param)");
		} else if (cn.equals(Double.class.getName())) {
			this.cw.w("Double.valueOf(param)");
		} else if (cn.equals(float.class.getName())) {
			this.cw.w("Float.parseFloat(param)");
		} else if (cn.equals(Float.class.getName())) {
			this.cw.w("Float.valueOf(param)");
		} else if (cn.equals(boolean.class.getName()) || cn.equals(Boolean.class.getName())) {
			this.cw.w("\"1\".equals(param)|| \"true\".equalsIgnoreCase(param)||\"yes\".equalsIgnoreCase(param)");
		} else if (cn.equals(String.class.getName())) {
			this.cw.w("param");
		} else if (cn.equals(java.math.BigInteger.class.getName())) {
			this.cw.w("new  java.math.BigInteger(param)");
		} else if (cn.equals(java.math.BigDecimal.class.getName())) {
			this.cw.w("new java.math.BigDecimal(param)");
		} else {
			throw new IllegalArgumentException("not supported class type:" + cn);
		}

	}

	private boolean isParse(String cn){
		if(cn.equals("java.lang.String")){
			return false;
		}else if(cn.equals("java.lang.Boolean")){
			return false;
		}else if(cn.equals("boolean")){
			return false;
		}
		return true;
	}
	@Override
	public void bulidParam() {
		String paramName = this.annotation.getParamNameInRequest();
		this.aptWebHandler.readParameter(paramName);
		if (this.annotation.isRequired()) {
			this.cw.l("if(null==param || param.length()==0){");
			this.raiseNoFoundError(paramName);
			cw.l("}");
			cw.bL(this.mpe.getTypeName()).w(" ").w(this.mpe.getName()).w(";");
			if(this.isParse(this.mpe.getTypeName()))cw.l("try{");
			
			cw.bL(this.mpe.getName()).w(" = ");
			this.transferToParam(this.mpe.getTypeName());
			cw.el(";");
			if(this.isParse(this.mpe.getTypeName())){
				cw.l("}catch(Exception pee){");
				this.raiseNoFoundError(paramName, "pee");
				cw.l("}");
			}
		} else {
			cw.bL(this.mpe.getTypeName()).w(" ").w(this.mpe.getName()).w(" = ").ws(this.annotation.getDefaultValue()).el(";");
			cw.l("if(null!=param && param.length()!=0){");
			if(this.isParse(this.mpe.getTypeName()))cw.l("try{");
			cw.bL(this.mpe.getName()).w(" = ");
			this.transferToParam(this.mpe.getTypeName());
			cw.l(";");
			if(this.isParse(this.mpe.getTypeName())){
				cw.l("}catch(Exception pee){");
				this.raiseNoFoundError(paramName, "pee");
				cw.l("}");
			}
			cw.l("}");
		}
	}

	@Override
	public void bulidBeanProterty() {
		this.checkRequestFieldParamName();
		this.aptWebHandler.readParameter(this.frp.getParamName().trim());
		if (this.frp.isRequired()) {
			this.cw.l("if(null==param || param.length()==0){");
			this.raiseNoFoundError(this.frp.getParamName().trim());
			cw.l("}");
			if(this.isParse(this.mpe.getTypeName()))cw.l("try{");
			cw.bL(this.mpe.getName()).w(".").w(Util.buildSetter(this.frp.getValue())).w("(");;
			this.transferToParam(this.frp.getValueClassName());
			cw.el(");");
			if(this.isParse(this.mpe.getTypeName())){
				cw.l("}catch(Exception pee){");
				this.raiseNoFoundError(this.frp.getParamName().trim(), "pee");
				cw.l("}");
			}
		} else {
			cw.l("if(null!=param && param.length()!=0){");
			if(this.isParse(this.mpe.getTypeName()))cw.l("try{");
			cw.bL(this.mpe.getName()).w(".").w(Util.buildSetter(this.frp.getValue())).w("(");;
			this.transferToParam(this.frp.getValueClassName());
			cw.el(");");
			if(this.isParse(this.mpe.getTypeName())){
				cw.l("}catch(Exception pee){");
				this.raiseNoFoundError(this.frp.getParamName().trim(), "pee");
				cw.l("}");
			}
			String dv = this.frp.getDefaultValue();
			if(dv!=null){
				cw.l("}else{");
				cw.bL(this.mpe.getName()).w(".").w(Util.buildSetter(this.frp.getValue())).w("(").w(dv).el(");");
			}
			cw.l("}");
		}
	}

}
