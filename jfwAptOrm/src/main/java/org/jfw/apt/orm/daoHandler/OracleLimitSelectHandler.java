package org.jfw.apt.orm.daoHandler;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import org.jfw.apt.AptConfig;
import org.jfw.apt.Util;
import org.jfw.apt.orm.annotation.dao.method.operator.LimitSelect;

public class OracleLimitSelectHandler  extends OracleLimitQueryHandler{
	@Override
	public boolean match(Element ele) {
		LimitSelect lq = ele.getAnnotation(LimitSelect.class);
		if (lq == null)
			return false;
		String dv = Util.emptyToNull(lq.value());
		if (dv == null) {
			if (AptConfig.get("DB:Oracle") != null)
				return true;
			return false;
		} else {
			if (dv.equals("Oracle"))
				return true;
			return false;
		}
	}

	@Override
	protected Class<? extends Annotation> getAnnotationClass() {
		return LimitSelect.class;
	}
}
