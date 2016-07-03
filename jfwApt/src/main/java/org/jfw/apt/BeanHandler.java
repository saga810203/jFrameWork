package org.jfw.apt;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.jfw.apt.annotation.Bean;
import org.jfw.apt.annotation.BuildBean;
import org.jfw.apt.annotation.FactoryBean;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.TypeName;
import org.jfw.apt.out.model.ClassBeanDefine;

public class BeanHandler extends AptHandler {
	private Bean bean;
	private FactoryBean factoryBean;
	private TypeElement ref;

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean match(Element ele) {
		if (ele.getKind() == ElementKind.CLASS || ele.getKind() == ElementKind.INTERFACE) {
			this.ref = (TypeElement) ele;
			this.bean = ele.getAnnotation(Bean.class);
			this.factoryBean = ele.getAnnotation(FactoryBean.class);
			return true;
		}
		return false;
	}

	private void handlerBuildBean() throws AptException {
		for (Element el : this.ref.getEnclosedElements()) {
			BuildBean bd = el.getAnnotation(BuildBean.class);
			if (bd == null)
				continue;
			if (el.getKind() != ElementKind.METHOD) {
				throw new AptException(el, "@BuildBean must embellish public static method");
			}
			ExecutableElement ee = (ExecutableElement) el;

			TypeName tn = TypeName.get(ee.getReturnType());
			if (tn.isPrimitive())
				throw new AptException(el, "method with @BuildBean must return Reference Class");
			Set<Modifier> ms = ee.getModifiers();
			if (!ms.contains(Modifier.STATIC) || !ms.contains(Modifier.PUBLIC))
				throw new AptException(el, "@BuildBean must embellish public static method");
			if (!ee.getParameters().isEmpty())
				throw new AptException(el, "@BuildBean must embellish empty paramter method");
			String nm = bd.value();
			if (nm == null || nm.trim().length() == 0) {
				nm = ee.getReturnType().toString() + "@builder";
			}
			nm = nm.trim().replaceAll("\\.", "_");
			this.jfwProccess.getBeanConfig().addServiceBeanByBuilder(this.ref.getQualifiedName().toString(),
					ee.getSimpleName().toString(), nm);

		}
	}

	private void handlerBean() throws AptException {
		String bn = bean.value();
		if (bn == null || bn.trim().length() == 0) {
			bn =this.ref.getQualifiedName().toString();
		}
		bn = bn.trim().replaceAll("\\.", "_");
		ClassBeanDefine cbd = this.jfwProccess.getBeanConfig().addServiceBeanByClass(this.ref.getQualifiedName().toString(), bn);
		Util.buildAtuowrieProperty(cbd, this.ref);
	}

	private void handlerFactoryBean() throws AptException {
		String bn = this.factoryBean.value();
		if (bn == null || bn.trim().length() == 0) {
			bn = this.ref.getQualifiedName().toString().trim() + "@factory";
		}
		bn = bn.trim().replaceAll("\\.", "_");
		ClassBeanDefine cbd = this.jfwProccess.getBeanConfig().addServiceBeanByClass("org.jfw.util.comm.ClassCreateFactory",
				bn);
		cbd.setAttribute("clazz", this.ref.getQualifiedName().toString(), "java.lang.Class");
	}

	@Override
	public void proccess() throws AptException {
		if(null != this.bean && null != this.factoryBean)
			throw new AptException(this.ref, "@Bean @FactoryBean choose one");
		
		this.handlerBuildBean();
		if (null != this.bean)
			this.handlerBean();
		else if (null != this.factoryBean)
			this.handlerFactoryBean();
	}

	@Override
	public void completeRound() throws AptException {
		// TODO Auto-generated method stub

	}

	@Override
	public void complete() throws AptException {
		// TODO Auto-generated method stub

	}

}
