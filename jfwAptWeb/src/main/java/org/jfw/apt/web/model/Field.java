package org.jfw.apt.web.model;

import javax.lang.model.type.MirroredTypeException;

import org.jfw.apt.Util;
import org.jfw.apt.model.TypeName;
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
	private String defaultValue =null;
	private boolean required = false;
	
	
	public static Field build(FieldParam fp){
		Field result = new Field();
		result.name =Util.emptyToNull(fp.value());
		try{
		result.className = TypeName.get(fp.valueClass()).toString();
		}catch(MirroredTypeException e){
			result.className = TypeName.get(e.getTypeMirror()).toString();
		}
		result.paramName =Util.emptyToNull(fp.paramName());
		result.defaultValue = Util.emptyToNull(fp.defaultValue());
		result.required = fp.required();		
		return result;
	}

}