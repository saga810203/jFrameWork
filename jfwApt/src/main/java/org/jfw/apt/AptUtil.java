package org.jfw.apt;

import java.lang.annotation.Annotation;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public final class AptUtil {
	private AptUtil(){}
	
	public static Object getAnnotationObj(Element ele, AnnotationMirror am) {
		try {
			TypeElement type = (TypeElement) am.getAnnotationType().asElement();
			String cn = type.getQualifiedName().toString();
			@SuppressWarnings("unchecked")
			Class<? extends Annotation> cls = (Class<? extends Annotation>) Class.forName(cn);
			return ele.getAnnotation(cls);
		} catch (Exception e) {
			return null;
		}
	}
}
