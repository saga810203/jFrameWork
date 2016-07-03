package org.jfw.apt.web.handlers.buildParam;


public class BaseListRPT extends AbstractRequestParamTransfer {

    public void transferToParams(String classType) {
    	String cn = classType;
        if (cn.equals("java.util.List<java.lang.Integer>")) {
            this.cw.w("Integer.valueOf(params[i])");
        }  else if (cn.equals("java.util.List<java.lang.Byte>")) {
            this.cw.w("Byte.valueOf(params[i])");
        } else if (cn.equals("java.util.List<java.lang.Short>")) {
            this.cw.w("Short.valueOf(params[i])");
        } else if (cn.equals("java.util.List<java.lang.Long>")) {
            this.cw.w("Long.valueOf(params[i])");
        }  else if (cn.equals("java.util.List<java.lang.Double>")) {
            this.cw.w("Double.valueOf(params[i])");
        } else if (cn.equals("java.util.List<java.lang.Float>")) {
            this.cw.w("Float.valueOf(params[i])");
        } else if (cn.equals("java.util.List<java.lang.Boolean>")) {
            this.cw.w("\"1\".equals(params[i])|| \"true\".equalsIgnoreCase(params[i])||\"yes\".equalsIgnoreCase(params[i])");
        } else if (cn.equals("java.util.List<java.lang.String>")) {
            this.cw.w("params[i]");
        } else if (cn.equals("java.util.List<java.math.BigInteger>")) {
            this.cw.w("new java.math.BigInteger(params[i])");
        } else if (cn.equals("java.util.List<java.math.BigDecimal>")) {
            this.cw.w("new java.math.BigDecimal(params[i])");
        } else {
            throw new IllegalArgumentException("noSupported class type" + cn);
        }
    }





    @Override
    public void bulidParam() {
    	String paramName =this.annotation.getParamNameInRequest();
    	if(paramName==null) paramName = this.mpe.getName();
    	this.aptWebHandler.readParameter(paramName);
        String vn = this.mpe.getName();
        String tn = this.mpe.getTypeName();
        String en = tn.substring("java.util.List<".length(), tn.length()-1);
        String dv = this.annotation.getDefaultValue();
        if (this.annotation.isRequired()) {
            cw.bL("if(null==params || params.length==0){");
            this.raiseNoFoundError(paramName);
            cw.l("}");
            cw.bL(tn).w(" ").w(this.mpe.getName()).w(" = new java.util.ArrayList")
            .w("<").w(en).el(">();");
            
            cw.l("for( int i = 0 ; i < params.length ; ++i){").w(vn).w(".add(");
            this.transferToParams( this.mpe.getTypeName());
            cw.el(");").l("}");
        } else {
        	cw.bL(tn).w(" ").w(this.mpe.getName()).el(";");
            cw.l("if(null!=params && params.length!=0){");
            cw.bL(this.mpe.getName()).w(" = new java.util.ArrayList<")
            .w(en).w(">();");
            cw.l("for( int i = 0 ; i < params.length ; ++i){").w(vn)
                    .w(".add(");
            this.transferToParams( this.mpe.getTypeName());
            cw.el(");").l("}");
            cw.l("}else{");
            if(dv == null){
            	cw.bL(this.mpe.getName()).el(" = null;");
            }else{
            	cw.bL(this.mpe.getName()).w(dv).el(";");
            }
            cw.l("}");
        }
    }

    @Override
    public void bulidBeanProterty() {
    	this.checkRequestFieldParamName();
		this.aptWebHandler.readParameters(this.frp.getParamName().trim());
        
        String localName = this.cw.getMethodTempVarName();
        
        String tn = this.frp.getValueClassName();
        String en = tn.substring("java.util.List<".length(), tn.length()-1);
        String dv = this.frp.getDefaultValue();
        if (this.frp.isRequired()) {
            cw.l("if(null==params || params.length==0){");
            this.raiseNoFoundError(this.frp.getParamName().trim());
            cw.l("}");
            cw.bL(tn).w(" ").w(localName).w(" = new java.util.ArrayList<").w(en).el(">();");
            
            cw.l("for( int i = 0 ; i < params.length ; ++i){").bL(localName).w(".add(");
            this.transferToParams(this.frp.getValueClassName());
            cw.el(");");
            cw.l("}");
            cw.writeSetter(this.mpe.getName(), this.frp.getValue(), localName);
        } else {
            cw.l("if(null!=params && params.length!=0){");
            cw.bL(tn).w(" ").w(localName).w(" = new java.util.ArrayList<")
            .w(en).el(">();");
            cw.l("for( int i = 0 ; i < params.length ; ++i){").bL(localName)
                    .w(".add(");
            this.transferToParams(this.frp.getValueClassName());
            cw.el(");").l("}");
            cw.writeSetter(this.mpe.getName(), this.frp.getValue(), localName);
            if(null!= dv){
            	cw.l("}else{");
            	cw.writeSetter(this.mpe.getName(), this.frp.getValue(), dv);
            }
            cw.l("}");
        }
    }
}
