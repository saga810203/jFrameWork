package org.jfw.apt.orm.core;

import java.util.HashMap;
import java.util.Map;

import org.jfw.apt.out.ClassWriter;

public class ColumnWriterCache {
	private String valEl;
	private boolean useTmpVar;
	private boolean nullable;
	private ClassWriter cw;
	private String checkNullVar;
	private String cacheValVar;
	private ColumnHandler ch;

	private Map<Object, Object> attributes;

	private boolean dynamic;

	public void put(Object key, Object val) {
		if (null == this.attributes)
			this.attributes = new HashMap<Object, Object>();
		this.attributes.put(key, val);

	}

	public Object get(Object key) {
		return null == this.attributes ? null : this.attributes.get(key);
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public ColumnHandler getHandler() {
		return this.ch;
	}

	private ColumnWriterCache() {
	}
	
	public static String getMethodTempVarName(ClassWriter cw,String key,String secKey){
		String flagKey =ColumnWriterCache.class.getName()+"###"+key+"$$$"+ secKey;
		
		String result =(String) cw.getMethodScope().get(flagKey);
		if(null == result){
			result = cw.getMethodTempVarName();
			cw.getMethodScope().put(flagKey, result);
		}
		return result;
	}


	public static ColumnWriterCache build(String valEl, boolean useTmpVar, boolean nullable, ClassWriter cw,
			ColumnHandler ch) {
		ColumnWriterCache cwc = new ColumnWriterCache();
		cwc.ch = ch;
		cwc.valEl = valEl.trim();
		cwc.useTmpVar = useTmpVar;
		cwc.nullable = nullable;
		cwc.cw = cw;
		if (cwc.nullable ) {
			cwc.checkNullVar = getMethodTempVarName(cw, cwc.valEl,"checkNullVar");
		} else {
			cwc.checkNullVar = null;
		}
		if (cwc.useTmpVar)
			cwc.cacheValVar = getMethodTempVarName(cw, cwc.valEl,"cacheValVar");
		else
			cwc.cacheValVar = null;
		
		return cwc;
	}

	public String supportsClass() {
		return this.ch.supportsClass();
	}

	public void prepare() {
		this.ch.prepare(this);
	}

	public void writeValue() {
		this.ch.writeValue(this, this.dynamic);
	}

	public boolean isReplaceResource() {
		return this.ch.isReplaceResource(this);
	}

	public void replaceResource() {
		this.ch.replaceResource(this);
	}

	public String getCheckNullVar() {
		return checkNullVar;
	}

	public String getCacheValVar() {
		return cacheValVar;
	}

	public String getValEl() {
		return valEl;
	}

	public boolean isUseTmpVar() {
		return useTmpVar;
	}

	public boolean isNullable() {
		return nullable;
	}

	public ClassWriter getCw() {
		return cw;
	}
}
