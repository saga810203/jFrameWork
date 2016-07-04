package org.jfw.apt;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.jfw.apt.annotation.JfwAptConfig;
import org.jfw.apt.exception.AptException;

public class AptConfig extends AptHandler {
	public static final String SYSTEM_PROPERTY_SET_PREFIX="org.jfw.apt.config.define.";
	public static final int SYSTEM_PROPERTY_SET_PREFIX_LEN = SYSTEM_PROPERTY_SET_PREFIX.length();
	
	private static final Map<String,TypeElement> config = new HashMap<String,TypeElement>();

	public static TypeElement get(String key){
		return config.get(key);
	}
	
	@Override
	public void init() {
		

	}

	@Override
	public boolean match(Element ele) {
		JfwAptConfig cf = ele.getAnnotation(JfwAptConfig.class) ;
		if(cf == null) return false;
		String v = Util.emptyToNull(cf.value());
		if(v==null) return false;
		if((ele.getKind()== ElementKind.INTERFACE) && (ele.getEnclosingElement().getKind() == ElementKind.PACKAGE)){
			TypeElement te =(TypeElement)ele;
		    config.put(v, te);
		}
		return false;
	}

	@Override
	public void proccess() throws AptException {
	}

	@Override
	public void completeRound() throws AptException {
	}

	@Override
	public void complete() throws AptException {
		config.clear();
	}

}
