package org.jfw.apt;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;

public abstract class AptHandler {
	protected JfwProccess jfwProccess;
	
	public JfwProccess getJfwProccess() {
		return jfwProccess;
	}
	public void setJfwProccess(JfwProccess jp){
		this.jfwProccess = jp;
		this.init();
	}
	public abstract void init();
	public abstract boolean match(Element ele);
	public void handle(Element ele) throws AptException{
		if(!this.match(ele)) return;
		this.proccess();		
	}
	public abstract void proccess() throws AptException;
	public abstract void completeRound() throws AptException;
	public abstract void complete() throws AptException;
}
