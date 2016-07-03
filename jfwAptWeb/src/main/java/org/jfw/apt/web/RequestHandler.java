package org.jfw.apt.web;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.out.ClassWriter;

public abstract class RequestHandler {
	protected AptWebHandler aptWebHandler;

	public AptWebHandler getAptWebHandler() {
		return aptWebHandler;
	}

	public void setAptWebHandler(AptWebHandler aptWebHandler) {
		this.aptWebHandler = aptWebHandler;
		this.init();
	}
	
	public  void appendBeforCode(ClassWriter cw) throws AptException{}
	public  void appendAfterCode(ClassWriter cw)throws AptException{}
	public  abstract void init();
	

}
