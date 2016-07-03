package org.jfw.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.jfw.apt.out.ClassWriter;

public abstract class CodeGenHandler extends AptHandler {

	protected TypeElement ref;

	protected ElementKind kind;
	protected boolean interfaceType = false;
	protected boolean abstractType = false;

	protected String sourceClassname;
	protected String targetClassname;

	protected String supportClassname = null;;
	protected String[] interFaces = null;
	protected String[] annotationDescps = null;

	protected ClassWriter out;

	public TypeElement getRef() {
		return ref;
	}

	public ClassWriter getOut() {
		return out;
	}

	public String getSourcePackageName() {
		int index = this.sourceClassname.lastIndexOf(".");
		if (index == -1)
			return "";
		return this.sourceClassname.substring(0, index);
	}

	public String getSourceSimpleClassname() {
		int index = this.sourceClassname.lastIndexOf(".");
		if (index == -1)
			return this.sourceClassname;
		++index;
		return this.sourceClassname.substring(index);
	}

	public String getTargetPackageName() {
		int index = this.targetClassname.lastIndexOf(".");
		if (index == -1)
			return "";
		return this.targetClassname.substring(0, index);
	}

	public String getTargetSimpleClassname() {
		int index = this.targetClassname.lastIndexOf(".");
		if (index == -1)
			return this.targetClassname;
		++index;
		return this.targetClassname.substring(index);
	}

	public String getSourceClassname() {
		return this.sourceClassname;
	}

	public String getTargetClassname() {
		return this.targetClassname;
	}

	public abstract boolean matchAnnotation();

	public abstract void genTargetClassname();

	public abstract void genSupportClassname();

	public abstract void genInterFaces();

	public abstract void genAnnotations();

	@Override
	public boolean match(Element ele) {
		this.interfaceType = false;
		this.abstractType = false;
		this.kind = ele.getKind();
		if (kind == ElementKind.INTERFACE) {
			this.interfaceType = true;
			this.abstractType = true;
		} else if (ElementKind.CLASS == kind) {
			this.abstractType = ele.getModifiers().contains(Modifier.ABSTRACT);
		} else {
			return false;
		}
		this.ref = (TypeElement) ele;
		if(this.ref.getEnclosingElement().getKind()!=ElementKind.PACKAGE) return false;

		if (!this.matchAnnotation())
			return false;
		this.sourceClassname = this.ref.getQualifiedName().toString();
		this.targetClassname = null;
		this.supportClassname = null;
		this.interFaces = null;
		this.annotationDescps = null;
		this.genTargetClassname();
		this.genSupportClassname();
		this.genInterFaces();
		this.genAnnotations();
		this.out = new ClassWriter(this.jfwProccess, this.targetClassname, this.supportClassname, this.interFaces,
				this.annotationDescps);
		return true;
	}

}
