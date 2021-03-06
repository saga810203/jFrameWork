package org.jfw.apt.model;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.jfw.apt.exception.AptException;

public class MethodEntry {
	private List<String> modifiers = new ArrayList<String>();
	private String returnType;
	private String name;
	private List<MethodParamEntry> params = new ArrayList<MethodParamEntry>();
	private List<String> exceptions = new ArrayList<String>();
	private ExecutableElement ref;

	public static MethodEntry build(ExecutableElement ref) throws AptException {
		MethodEntry me = new MethodEntry();
		me.ref = ref;
		for (Modifier m : ref.getModifiers()) {
			me.modifiers.add(m.toString());
		}
		try {
			me.returnType = TypeName.get(ref.getReturnType()).toString();
		} catch (Exception e) {
			throw new AptException(ref, "unSupported Method returnType");
		}
		me.name = ref.getSimpleName().toString();

		List<? extends VariableElement> eles = ref.getParameters();
		for(ListIterator<? extends VariableElement> it = eles.listIterator();it.hasNext();){
			VariableElement vEle = it.next();
			MethodParamEntry mpe = MethodParamEntry.build(vEle);
			me.params.add(mpe);
		}


		List<? extends TypeMirror> ths = ref.getThrownTypes();
		for(ListIterator<? extends TypeMirror> it = ths.listIterator();it.hasNext();){
			TypeMirror tm = it.next();
			String en;
			if(tm.getKind()==TypeKind.ERROR){
				en = tm.toString();
			}else{
				en = TypeName.get(tm).toString();
			}
			me.exceptions.add(en);
		}

		return me;
	}

	public List<String> getModifiers() {
		return modifiers;
	}

	public void setModifiers(List<String> modifiers) {
		this.modifiers = modifiers;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MethodParamEntry> getParams() {
		return params;
	}

	public void setParams(List<MethodParamEntry> params) {
		this.params = params;
	}

	public List<String> getExceptions() {
		return exceptions;
	}

	public void setExceptions(List<String> exceptions) {
		this.exceptions = exceptions;
	}

	public ExecutableElement getRef() {
		return ref;
	}

	public void setRef(ExecutableElement ref) {
		this.ref = ref;
	}

	public boolean isGetter() {
		return this.params.isEmpty() && 
				(!"void".equals(returnType))
				&& ((returnType.equals("boolean") && name.startsWith("is") && name.length() > 2 && name.charAt(2) >= 'A' && name.charAt(2) <= 'Z')
						|| ((!returnType.equals("boolean")) && name.startsWith("get") && name.length() > 3 && name.charAt(3) >= 'A' && name.charAt(3) <= 'Z'));

	}

	public boolean isSetter() {
		return this.params.size() == 1 && returnType.equals("void") && name.startsWith("set") && name.length() > 3
				&& name.charAt(3) >= 'A' && name.charAt(3) <= 'Z';

	}

}
