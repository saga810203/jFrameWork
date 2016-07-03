package org.jfw.apt.orm.core.model;

public class View extends DataEntry{
	
	private String fromSentence;
	
	@Override
	public String getFromSentence() {
		return fromSentence;
	}
	public void setFromSentence(String fromSentence) {
		this.fromSentence = fromSentence;
	}
	@Override
	public int getKind() {
		return DataEntry.VIEW;
	}
	
}
