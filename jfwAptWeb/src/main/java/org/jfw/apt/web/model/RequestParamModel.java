package org.jfw.apt.web.model;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.MirroredTypeException;

import org.jfw.apt.Util;
import org.jfw.apt.annotation.Nullable;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.model.TypeName;
import org.jfw.apt.web.annotation.param.RequestParam;

public class RequestParamModel {
	
	public String getParamNameInRequest(){
		if(this.paramName.length()>0) return this.paramName;
		else return this.variableName;
	}
	
	/**
	 * 参数名  in req.getParameter or req.getParameterValues
	 * @return
	 */
	public String getParamName() {
		return paramName;
	}
	/**
	 * 参数真事的类名
	 * @return
	 */
	public TypeName getRealClass() {
		return realClass;
	}
	/**
	 * 参数的默认值，java 语法
	 * @return
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * 参数是否是必须的,如果在 request中不存在则报错
	 * @return
	 */
	public boolean isRequired() {
		return required;
	}
	/**
	 * 不是简单的对象时，从request中取值的属性
	 * @return
	 */
	public Field[] getFields() {
		return fields;
	}
	/**
	 * 取类所有的setter方法，不包含的属性
	 * @return
	 */
	public String[] getExcludeFields() {
		return excludeFields;
	}

	private String paramName = "";
	private String variableName = null;
	private TypeName realClass = TypeName.OBJECT;
	private String defaultValue =  "";
	private boolean required = true;
	private Field[] fields = new Field[0];	
	private String[] excludeFields = new String[0];	
	
	
	public static RequestParamModel build(RequestParam rp,String methodVariableName,MethodParamEntry  mpe){
		RequestParamModel result = new RequestParamModel();
		result.variableName = methodVariableName;
		String tmp = Util.emptyToNull(rp.value());
		if(tmp!=null)result.paramName = tmp;
		TypeName tn=TypeName.OBJECT;
		try{
			Class<?> clazz = rp.clazz();
			if(!clazz.equals(Object.class))
				tn = TypeName.get(clazz);
		}catch(MirroredTypeException e){
			tn = TypeName.get(e.getTypeMirror());				
		}
		
		if(!tn.equals(TypeName.OBJECT)) result.realClass = tn;
		result.defaultValue =Util.emptyToNull(rp.defaultValue());
		if((null == result.defaultValue) && (!Util.isPrimitive(mpe.getTypeName()))&& (null != mpe.getRef().getAnnotation(Nullable.class)))
		{
			result.defaultValue="null";
		}
		result.required = result.defaultValue == null;
		if(rp.fields()!=null&& rp.fields().length>0){
			List<Field> list = new ArrayList<Field>();
			for(int i = 0 ; i <  rp.fields().length ; ++i){
				list.add(Field.build(rp.fields()[i]));
			}
			Field[] fs = new Field[list.size()];
			list.toArray(fs);
			result.fields = fs;
		}
		if(rp.excludeFields()!=null&&rp.excludeFields().length>0) result.excludeFields = rp.excludeFields();		
		return result;
	}
}
