package org.jfw.apt.orm.poHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.ClassName;
import org.jfw.apt.model.MethodEntry;
import org.jfw.apt.model.TypeName;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.enums.DE;
import org.jfw.apt.orm.core.model.ColumnFactory;
import org.jfw.apt.orm.core.model.DataEntry;
import org.jfw.apt.orm.core.model.DataEntryFactory;

public abstract class BaseHandler {
	protected String annoName;
	protected TypeElement ref;
	protected DataEntry entry;
	
	public void handle(TypeElement ref) throws AptException{
		this.ref = ref;
		this.proccess();
		DataEntryFactory.add(entry);
	}
	public abstract void proccess() throws AptException;
	
	public void initAfter() throws AptException{
		entry.setRef(ref);
		if(ref.getKind() == ElementKind.INTERFACE){
			entry.setJavaKind(DataEntry.INTERFACE);
		}else{
			Set<Modifier> modifiers = ref.getModifiers();
			if(modifiers.contains(Modifier.ABSTRACT)){
				entry.setJavaKind(DataEntry.ABSTRACT_CLASS);
			}else{
				entry.setJavaKind(DataEntry.STATIC_CLASS);
			}
		}
		
		TypeName tn = TypeName.get(ref.asType());
		if(!tn.getClass().equals(ClassName.class)) throw new AptException(ref,"unsupported class with "+annoName);
		entry.setJavaName(tn.toString());
	}
	
	public String getSupperClass(){		
		TypeMirror tm = this.ref.getSuperclass();
		if(tm.getKind() != TypeKind.DECLARED) return null;		
		TypeName tn = TypeName.get(tm);
		if(tn.equals(ClassName.OBJECT)) return null;
		
		if(!tn.getClass().equals(ClassName.class)) return null;		
		return tn.toString();		
	}
	
	public ArrayList<String> getInterfaces(){
		ArrayList<String> result = new ArrayList<String>();
		List<? extends TypeMirror> list = this.ref.getInterfaces();
		for(TypeMirror tm : list){
			if(tm.getKind() != TypeKind.DECLARED) continue;		
			TypeName tn = TypeName.get(tm);
			if(!tn.getClass().equals(ClassName.class)) continue;		
			result.add(tn.toString());	
		}
		return result;
	}

	public String getJavaType(org.jfw.apt.orm.annotation.entry.Column annObj) {
		DE de = annObj.value();
		if (!de.equals(DE.invalid_de)) {
			return ColumnHandlerFactory.supportedType(de.getHandlerClass());
		}
		return ColumnHandlerFactory.supportedType(annObj.handlerClass());
	}

	public String getJavaType(org.jfw.apt.orm.annotation.entry.CalcColumn annObj) {
		DE de = annObj.value();
		if (!de.equals(DE.invalid_de)) {
			return ColumnHandlerFactory.supportedType(de.getHandlerClass());
		}
		return ColumnHandlerFactory.supportedType(annObj.handlerClass());
	}

	public static String genDbName(String javaName, String name) {
		if (name != null && name.trim().length() > 0)
			return name.trim();
		int index = javaName.lastIndexOf('.');
		if(index>0) javaName = javaName.substring(index+1);				
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < javaName.length(); ++i) {
			char c = javaName.charAt(i);
			if (c >= 'A' && c <= 'Z' && i != 0) {
				sb.append("_");
			}
			sb.append(c);
		}
		return sb.toString().toUpperCase(Locale.US);
	}
	
	private void check(org.jfw.apt.orm.core.model.CalcColumn col) throws AptException{
		for(org.jfw.apt.orm.core.model.CalcColumn c:this.entry.getColumns()){
			if(col.getJavaName().equals(c.getJavaName()))
				throw new AptException(ref,"class["+this.ref.getQualifiedName().toString()+"] exists many attr["+col.getJavaName()+"]");
		}	
	}

	protected void handleColumn() throws AptException {
		List<? extends Element> eles = ref.getEnclosedElements();
		for (Element ele : eles) {
			org.jfw.apt.orm.annotation.entry.Column col = ele.getAnnotation(org.jfw.apt.orm.annotation.entry.Column.class);
			if (col == null)
				continue;

			MethodEntry me = MethodEntry.build((ExecutableElement) ele);
			String javaType = getJavaType(col);
			String javaName = null;

			if (me.isGetter()) {
				if (!javaType.equals(me.getReturnType()))
					throw new AptException(ele, "invalid return type with @"+org.jfw.apt.orm.annotation.entry.Column.class.getName());
				javaName = Util.buildFieldName(me.getName(), "boolean".equals(me.getReturnType()), true);
			} else if (me.isSetter()) {
				if (!javaType.equals(me.getParams().get(0).getTypeName()))
					throw new AptException(ele, "invalid param type with @"+org.jfw.apt.orm.annotation.entry.Column.class.getName());
				javaName = Util.buildFieldName(me.getName(), false, false);
			}

			DE de = col.value();
			if (de.equals(DE.invalid_de)) {
				
				org.jfw.apt.orm.core.model.CalcColumn mc = ColumnFactory.buildColumn(col.handlerClass(), col.nullable(), javaType,
						genDbName(javaName, col.name()), javaName, col.queryable(), col.dbType(),
						col.fixSqlValueWithInsert(), col.fixSqlValueWithUpdate(), col.insertable(), col.renewable());
				this.check(mc);
				
				this.entry.getColumns().add(mc);
			} else {
				
				org.jfw.apt.orm.core.model.CalcColumn mc =ColumnFactory.buildColumn(de.getHandlerClass(), de.isNullable(), javaType,
						genDbName(javaName, col.name()), javaName, de.isQueryable(), de.getDbType(),
						de.getFixSqlValueWithInsert(), de.getFixSqlValueWithUpdate(), de.isInsertable(), de.isRenewable());
				this.check(mc);
				
				this.entry.getColumns().add(mc);
			}

		}
	}

	protected void handleCalcColumn() throws AptException {
		List<? extends Element> eles = ref.getEnclosedElements();
		for (Element ele : eles) {
			org.jfw.apt.orm.annotation.entry.CalcColumn col = ele.getAnnotation(org.jfw.apt.orm.annotation.entry.CalcColumn.class);
			if (col == null)
				continue;

			MethodEntry me = MethodEntry.build((ExecutableElement) ele);
			String javaType = getJavaType(col);
			String javaName = null;

			if (me.isGetter()) {
				if (!javaType.equals(me.getReturnType()))
					throw new AptException(ele, "invalid return type with @"+org.jfw.apt.orm.annotation.entry.Column.class.getName());
				javaName = Util.buildFieldName(me.getName(), "boolean".equals(me.getReturnType()), true);
			} else if (me.isSetter()) {
				if (!javaType.equals(me.getParams().get(0).getTypeName()))
					throw new AptException(ele, "invalid param type with @"+org.jfw.apt.orm.annotation.entry.Column.class.getName());
				javaName = Util.buildFieldName(me.getName(), false, false);
			}

			DE de = col.value();
			if (de.equals(DE.invalid_de)) {
				if(null == ColumnHandlerFactory.get(col.handlerClass()))
					throw new AptException(ele,"unsupported handler class:"+col.handlerClass().getName());
				
				org.jfw.apt.orm.core.model.CalcColumn mc=ColumnFactory.buildCalcColumn(col.handlerClass(), col.nullable(), javaType,
						genDbName(javaName, col.column()), javaName);
				this.check(mc);
				this.entry.getColumns().add(mc);
			} else {
				if(null == ColumnHandlerFactory.get(de.getHandlerClass()))
					throw new AptException(ele,"unsupported handler class:"+de.getHandlerClass().getName());
				org.jfw.apt.orm.core.model.CalcColumn mc = ColumnFactory.buildCalcColumn(de.getHandlerClass(), de.isNullable(), javaType,
						genDbName(javaName, col.column()), javaName);
				this.check(mc);				
				this.entry.getColumns().add(mc);
			}
		}
	}
	
	

}
