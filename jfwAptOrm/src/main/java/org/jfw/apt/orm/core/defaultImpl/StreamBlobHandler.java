package org.jfw.apt.orm.core.defaultImpl;

import org.jfw.apt.orm.core.ColumnWriterCache;
import org.jfw.apt.out.ClassWriter;

public class StreamBlobHandler extends AbstractColumnHandler{

	@Override
	public String supportsClass() {
		return "java.io.InputStream";
	}

	@Override
	public ColumnWriterCache init(String valEl, boolean useTmpVar, boolean nullable, ClassWriter cw) {
		return super.init(valEl, useTmpVar, nullable, cw);
	}



	@Override
	public void readValue(ClassWriter cw, String beforCode, String afterCode, int colIndex, boolean dbNullable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void prepare(ColumnWriterCache cwc) {
		// TODO Auto-generated method stub
		super.prepare(cwc);
		ClassWriter cw = cwc.getCw();
		String lv = cw.getMethodTempVarName();
		cwc.put(StreamBlobHandler.class, lv);
		cw.bL("java.sql.Blob ").w(lv).el(" = null;");
	}

	@Override
	public void writeValue(ColumnWriterCache cwc, boolean dynamicValue) {
		
		String blobName = (String)cwc.get(StreamBlobHandler.class);
		
		ClassWriter cw = cwc.getCw();
		this.checkParamIndex(cw);
		if (cwc.isNullable()) {
			if (dynamicValue) {
				cw.bL("if(!").w(cwc.getCheckNullVar()).el("){");				
				cw.bL(blobName).el(" =  con.createBlob();");
				cw.bL("org.jfw.util.io.IoUtil.copy(").w(cwc.getCacheValVar()).w(",").w(blobName).el(".setBinaryStream(1),true,true);");
				cw.bL("ps.setBlob(").w(this.getParamIndexName(cw)).w("++,")	.w(blobName).el(");");
				cw.l("}");
			} else {
				cw.bL("if(").w(cwc.getCheckNullVar()).el("){");
				cw.bL("ps.setNull(").w(this.getParamIndexName(cw)).w("++,").w(java.sql.Types.BLOB).el(");");
				cw.l("}else{");
				cw.bL(blobName).el(" =  con.createBlob();");
				cw.bL("org.jfw.util.io.IoUtil.copy(").w(cwc.getCacheValVar()).w(",").w(blobName).el(".setBinaryStream(1),true,true);");
				cw.bL("ps.Blob(").w(this.getParamIndexName(cw)).w("++,").w(blobName).el(");");
				cw.l("}");
			}
		} else {
			cw.bL(blobName).el(" =  con.createBlob();");
			cw.bL("org.jfw.util.io.IoUtil.copy(").w(cwc.getCacheValVar()==null?cwc.getValEl():cwc.getCheckNullVar()).w(",").w(blobName).el(".setBinaryStream(1),true,true);");
			cw.bL("ps.Blob(").w(this.getParamIndexName(cw)).w("++,").w(blobName).el(");");
		}
	}

	@Override
	public boolean isReplaceResource(ColumnWriterCache cwc) {
		return true;
	}

	@Override
	public void replaceResource(ColumnWriterCache cwc) {
		ClassWriter cw = cwc.getCw();
		String lv = (String)cwc.get(StreamBlobHandler.class);
		cw.l("try{");
		cw.bL("if(null!=").w(lv).el("){");
		cw.bL(lv).el(".free();");
		cw.l("}");
		String llv = cw.getMethodTempVarName();
		cw.writeCatch("Exception", llv);
		cw.writeFinally();
		cw.bL(lv).el(" =  null ;");
		cw.l("}");
	}

	@Override
	public String getReadMethod() {
		throw  new UnsupportedOperationException();
	}

	@Override
	public String getWriteMethod() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected int getSqlType() {
		throw new  UnsupportedOperationException();
	}
	

}
