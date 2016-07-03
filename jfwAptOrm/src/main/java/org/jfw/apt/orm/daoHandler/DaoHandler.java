package org.jfw.apt.orm.daoHandler;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodEntry;
import org.jfw.apt.orm.DataAccessObjectHandler;

public interface DaoHandler {
	
	
	boolean match(Element ele);
	
	void proccess(MethodEntry me,DataAccessObjectHandler daoh) throws AptException;
}
