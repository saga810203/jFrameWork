package org.jfw.apt.orm.daoHandler;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import org.jfw.apt.AptConfig;
import org.jfw.apt.Util;
import org.jfw.apt.orm.annotation.dao.method.operator.LimitSelect;

public class PostgreSQLLimitSelectHandler extends PostgreSQLLimitQueryHandler {
	@Override
	public boolean match(Element ele) {
		LimitSelect lq = ele.getAnnotation(LimitSelect.class);
		if (lq == null)
			return false;
		String dv = Util.emptyToNull(lq.value());
		if (dv == null) {
			if (AptConfig.get("DB:PostgreSQL") != null)
				return true;
			return false;
		} else {
			if (dv.equals("PostgreSQL"))
				return true;
			return false;
		}
	}

	@Override
	protected Class<? extends Annotation> getAnnotationClass() {
		return LimitSelect.class;
	}
}
