package org.jfw.apt;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.jfw.apt.annotation.Autowrie;
import org.jfw.apt.model.TypeName;
import org.jfw.apt.out.model.ClassBeanDefine;

public final class Util {
	private Util() {
	}

	private static Map<Class<?>, Class<?>> wrapClass = new HashMap<Class<?>, Class<?>>();
	private static Map<String, String> wrapClassName = new HashMap<String, String>();

	public static boolean isPrimitive(String className) {
		return wrapClassName.containsKey(className);
	}

	public static boolean isPrimitive(Class<?> clazz) {
		return wrapClass.containsKey(clazz);
	}

	public static String getWrapClass(String className) {
		return wrapClassName.get(className);
	}

	public static String buildGetter(String fieldName, boolean primitiveBoolean) {
		String tmp = (primitiveBoolean ? "is" : "get") + fieldName.substring(0, 1).toUpperCase(Locale.US);
		if (fieldName.length() > 1)
			tmp = tmp + fieldName.substring(1);
		return tmp;
	}

	public static String buildSetter(String fieldName) {
		String tmp = "set" + fieldName.substring(0, 1).toUpperCase(Locale.US);
		if (fieldName.length() > 1)
			tmp = tmp + fieldName.substring(1);
		return tmp;
	}

	public static String buildFieldName(String methodName,boolean primitiveBoolean,boolean get){
		String result  = null;	
		if(get){
			
			if(primitiveBoolean){
				if((methodName.length()>2) && methodName.startsWith("is"))
					result = methodName.substring(2);
			}else{
				if((methodName.length()>3) && methodName.startsWith("get"))
					result = methodName.substring(3);
			}
		}else{
			if((methodName.length()>3) && methodName.startsWith("set"))
				result = methodName.substring(3);
		}
		if(result !=null){
			char c = result.charAt(0);
			if(c < 'A' || c >'Z') return null;
			if(result.length()>1){
				return result.substring(0,1).toLowerCase(Locale.US)+result.substring(1);
			}
			return result.toLowerCase(Locale.US);
			
		}
		return null;		
	}

	public static String join(String separator, List<String> parts) {
		if (parts.isEmpty())
			return "";
		StringBuilder result = new StringBuilder();
		result.append(parts.get(0));
		for (int i = 1; i < parts.size(); i++) {
			result.append(separator).append(parts.get(i));
		}
		return result.toString();
	}

	public static String emptyToNull(String str) {
		if (str == null || str.trim().length() == 0)
			return null;
		return str.trim();
	}

	public static Set<Modifier> convert(int modifiers) {
		Set<Modifier> result = new LinkedHashSet<Modifier>();
		if (java.lang.reflect.Modifier.isAbstract(modifiers)) {
			result.add(Modifier.ABSTRACT);
		}
		if (java.lang.reflect.Modifier.isFinal(modifiers)) {
			result.add(Modifier.FINAL);
		}
		if (java.lang.reflect.Modifier.isNative(modifiers)) {
			result.add(Modifier.NATIVE);
		}
		if (java.lang.reflect.Modifier.isPrivate(modifiers)) {
			result.add(Modifier.PRIVATE);
		}
		if (java.lang.reflect.Modifier.isProtected(modifiers)) {
			result.add(Modifier.PROTECTED);
		}
		if (java.lang.reflect.Modifier.isPublic(modifiers)) {
			result.add(Modifier.PUBLIC);
		}
		if (java.lang.reflect.Modifier.isStatic(modifiers)) {
			result.add(Modifier.STATIC);
		}
		if (java.lang.reflect.Modifier.isStrict(modifiers)) {
			result.add(Modifier.STRICTFP);
		}
		if (java.lang.reflect.Modifier.isSynchronized(modifiers)) {
			result.add(Modifier.SYNCHRONIZED);
		}
		if (java.lang.reflect.Modifier.isTransient(modifiers)) {
			result.add(Modifier.TRANSIENT);
		}
		if (java.lang.reflect.Modifier.isVolatile(modifiers)) {
			result.add(Modifier.VOLATILE);
		}
		return result;
	}
	public static void fillAutowrieElement(Map<String, String> map, TypeElement typeEle) {
		for (Element ele : typeEle.getEnclosedElements()) {
			if(ele.getModifiers().contains(Modifier.STATIC)) continue;
			if(ele.getKind()==ElementKind.FIELD){
				Autowrie aw = ele.getAnnotation(Autowrie.class);
				if(null == aw) continue;
				String name = ele.getSimpleName().toString();
				if(map.containsKey(name)) continue;
				String refName = aw.value();
				if(refName==null || refName.trim().length()==0){
					TypeMirror tm = ele.asType();
					if(tm.getKind()!=TypeKind.DECLARED) continue;
					refName = TypeName.get(tm).toString().replaceAll("\\.","_");					
				}
				map.put(name, refName.trim());				
			}else if(ele.getKind()==ElementKind.METHOD){
				Autowrie aw = ele.getAnnotation(Autowrie.class);
				if(aw==null) continue;
				
				ExecutableElement ee = (ExecutableElement) ele;
				if(!TypeName.get(ee.getReturnType()).equals(TypeName.VOID)) continue;
				String mn = ee.getSimpleName().toString();
				if((mn.length()<4)||(!mn.startsWith("set"))) continue;
				List<? extends VariableElement> params = ee.getParameters();
				if(params.size()!=1) continue;
				mn = mn.substring(3);
				if(mn.length()==1) mn = mn.toLowerCase(Locale.US);
				else
					mn = mn.substring(0,1).toLowerCase(Locale.US)+mn.substring(1);
				
				if(map.containsKey(mn)) continue;
				String refName = aw.value();
				if(refName==null || refName.trim().length()==0){
					VariableElement param = params.get(0);
					refName =TypeName.get(param.asType()).toString().replaceAll("\\.","_");					
				}
				map.put(mn,refName);
			}
		}
		

		TypeMirror tm = typeEle.getSuperclass();
		if (tm instanceof NoType)
			return;
		if (tm.getKind() != TypeKind.DECLARED)
			return;

		try {
			DeclaredType dt = (DeclaredType) tm;
			fillAutowrieElement(map,(TypeElement) dt.asElement());
		} catch (Throwable th) {
			return;
		}
	}
	
	

	public static void buildAtuowrieProperty(ClassBeanDefine cbd ,TypeElement ele){
		Map<String,String> map = new HashMap<String,String>();
		fillAutowrieElement(map, ele);
		for(Map.Entry<String,String> entry:map.entrySet()){
			cbd.setRefAttribute(entry.getKey(),entry.getValue());
		}
		
	}

	static {
		wrapClass.put(int.class, Integer.class);
		wrapClass.put(byte.class, Byte.class);
		wrapClass.put(short.class, Short.class);
		wrapClass.put(float.class, Float.class);
		wrapClass.put(double.class, Double.class);
		wrapClass.put(boolean.class, Boolean.class);
		wrapClass.put(char.class, Character.class);
		wrapClass.put(long.class, Long.class);
		for (Map.Entry<Class<?>, Class<?>> en : wrapClass.entrySet()) {
			wrapClassName.put(en.getKey().getName(), en.getValue().getName());
		}
	}
}
