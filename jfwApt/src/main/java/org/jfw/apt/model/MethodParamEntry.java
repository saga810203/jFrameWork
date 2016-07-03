package org.jfw.apt.model;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.exception.AptException;

public class MethodParamEntry {
	private String typeName;
	private String name;
	private VariableElement ref;

	private MethodParamEntry(VariableElement ref, String typeName, String name) {
		this.ref = ref;
		this.typeName = typeName;
		this.name = name;
	}

	public static MethodParamEntry build(VariableElement ref) throws AptException {
		String n = ref.getSimpleName().toString();
		try {
			String tn = TypeName.get(ref.asType()).toString();
			return new MethodParamEntry(ref, tn, n);
		} catch (Exception e) {
			throw new AptException(ref, "unSupperted mehtod param type");
		}
	}

	public String getTypeName() {
		return typeName;
	}

	public String getName() {
		return name;
	}

	public VariableElement getRef() {
		return ref;
	}

}
