package org.jfw.apt.orm.core.model;

public class ExtendTable extends DataEntry{

	@Override
	public int getKind() {
		return DataEntry.EXTEND_TABLE;
	}

	public String getFromSentence(){
		return ((Table)DataEntryFactory.get(this.getSupportedDataEntrys().get(0))).getFromSentence();
	}
}
