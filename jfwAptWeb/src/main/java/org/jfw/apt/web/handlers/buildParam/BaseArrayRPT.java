package org.jfw.apt.web.handlers.buildParam;

public class BaseArrayRPT extends AbstractRequestParamTransfer {

	public void transferToParams(String classType) {
		String cn = classType;

		if (cn.equals("int[]")) {
			cw.w("Integer.parseInt(params[i])");
		} else if (cn.equals("java.lang.Integer[]")) {
			cw.w("Integer.valueOf(params[i])");
		} else if (cn.equals("byte[]")) {
			cw.w("Byte.parseByte(params[i])");
		} else if (cn.equals("java.lang.Byte[]")) {
			cw.w("Byte.valueOf(params[i])");
		} else if (cn.equals("short[]")) {
			cw.w("Short.parseShort(params[i])");
		} else if (cn.equals("java.lang.Short[]")) {
			cw.w("Short.valueOf(params[i])");
		} else if (cn.equals("long[]")) {
			cw.w("Long.parseLong(params[i])");
		} else if (cn.equals("java.lang.Long[]")) {
			cw.w("Long.valueOf(params[i])");
		} else if (cn.equals("double[]")) {
			cw.w("Double.parseDouble(params[i])");
		} else if (cn.equals("java.lang.Double[]")) {
			cw.w("Double.valueOf(params[i])");
		} else if (cn.equals("float[]")) {
			cw.w("Float.parseFloat(params[i])");
		} else if (cn.equals("java.lang.Float[]")) {
			cw.w("Float.valueOf(params[i])");
		} else if (cn.equals("boolean[]") || cn.equals("java.lang.Boolean[]")) {
			cw.w("\"1\".equals(params[i])|| \"true\".equalsIgnoreCase(params[i])||\"yes\".equalsIgnoreCase(params[i])");
		} else if (cn.equals("java.lang.String[]")) {
			cw.w("params[i]");
		} else if (cn.equals("java.math.BigInteger[]")) {
			cw.w("new java.math.BigInteger(params[i])");
		} else if (cn.equals("java.math.BigDecmal[]")) {
			cw.w("new java.math.BigDecimal(params[i])");
		} else {
			throw new IllegalArgumentException("not supperted class type:" + cn);
		}
	}

	@Override
	public void bulidParam() {
		String vTypeName = this.mpe.getTypeName();

		this.aptWebHandler.readParameter(this.annotation.getParamNameInRequest());
		if (this.annotation.isRequired()) {
			cw.l("if(null==params || params.length==0){");
			this.raiseNoFoundError(this.annotation.getParamNameInRequest());
			cw.l("}");
			cw.bL(vTypeName).w(" ").w(this.mpe.getName()).w(" = new ").w(vTypeName.substring(0, vTypeName.length() - 2))
					.el("params.length];");
			cw.l("for( int i = 0 ; i < params.length ; ++i){");
			cw.bL(this.mpe.getName()).w("[i]=");
			this.transferToParams(this.mpe.getTypeName());
			cw.el(";");
			cw.l("}");
		} else {
			cw.bL(vTypeName).w(" ").w(this.mpe.getName()).el(";");

			cw.l("if(null!=params && params.length!=0){");
			cw.bL(this.mpe.getName()).w(" = new ").w(vTypeName.substring(0, vTypeName.length() - 1))
					.el("params.length];");
			cw.l("for( int i = 0 ; i < params.length ; ++i){");
			cw.bL(this.mpe.getName()).w("[i]=");
			this.transferToParams(this.mpe.getTypeName());
			cw.el(";");
			cw.l("}");
			cw.l("}else{");
			cw.bL(this.mpe.getName()).w(" = ").w(this.annotation.getDefaultValue()).el(";");
			cw.l("}");

		}
	}

	@Override
	public void bulidBeanProterty() {
		String ATypeName = this.frp.getValueClassName();
		String vTypeName = ATypeName.substring(0,ATypeName.length()-2);
		String localName = cw.getMethodTempVarName();
		this.checkRequestFieldParamName();
		this.aptWebHandler.readParameters(this.frp.getParamName().trim());
		if (this.frp.isRequired()) {
			cw.l("if(null==params || params.length==0){");
			this.raiseNoFoundError(this.frp.getParamName().trim());
			cw.l("}");
			cw.bL(vTypeName).w("[] ").w(localName).w(" = new ").w(vTypeName).el("[params.length];");
			cw.l("for( int i = 0 ; i < params.length ; ++i){")
			   .w(localName).w("[i]=");
			this.transferToParams(ATypeName);
			cw.el(";").l("}");
			cw.writeSetter(this.mpe.getName(), this.frp.getValue(), localName);
		} else {
			cw.l("if(null!=params && params.length!=0){");
			cw.bL(vTypeName).w("[] ").w(localName).w(" = new ").w(vTypeName).el("[params.length];");
			cw.l("for( int i = 0 ; i < params.length ; ++i){")
			.bL(localName).w("[i]=");
			this.transferToParams(ATypeName);
			cw.el(";");
			cw.l("}");
			cw.writeSetter(this.mpe.getName(), this.frp.getValue(), localName);
			String dv = this.frp.getDefaultValue();
			if(dv != null){
				cw.l("}else{");
				cw.writeSetter(mpe.getName(), this.frp.getValue(), dv);
			}
			cw.l("}");
		}
	}
}
