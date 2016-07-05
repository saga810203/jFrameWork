package org.jfw.apt.web.model;

import org.jfw.apt.Util;
import org.jfw.apt.web.annotation.param.FieldParam;

public class Field{
	public String getName() {
		return name;
	}
	public String getClassName() {
		return className;
	}
	public String getParamName() {
		return paramName;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public boolean isRequired() {
		return required;
	}
	private String name;
	private String className;
	private String paramName;
	private String defaultValue ="";
	private boolean required = false;
	
	
	public static Field build(FieldParam fp){
		Field result = new Field();
		result.name = fp.value();
		result.className = fp.valueClassName();
		result.paramName = fp.paramName();
		result.defaultValue = Util.emptyToNull(fp.defaultValue());
		result.required = null == result.defaultValue;			
		return result;
	}

}