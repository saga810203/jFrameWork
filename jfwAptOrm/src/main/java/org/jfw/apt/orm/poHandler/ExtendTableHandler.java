package org.jfw.apt.orm.poHandler;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.entry.Table;
import org.jfw.apt.orm.core.model.DataEntry;
import org.jfw.apt.orm.core.model.DataEntryFactory;
import org.jfw.apt.orm.core.model.ExtendTable;

public class ExtendTableHandler extends BaseHandler{
	public static final String ANNO_NAME ="@"+org.jfw.apt.orm.annotation.entry.ExtendTable.class.getName(); 
	@Override
	public void proccess() throws AptException {
		ExtendTable table = new ExtendTable();
		this.entry = table;
		this.annoName =ANNO_NAME;
		this.initAfter();
		if(this.entry.getJavaKind()==DataEntry.INTERFACE) throw new AptException(ref,this.annoName+" not with interface");
		
		String tn = this.getSupperClass();
		if(tn == null)throw new AptException(ref,"must extend a class[@"+Table.class.getName()+"] not with "+ANNO_NAME);	
		entry.getSupportedDataEntrys().add(tn);
		this.handleCalcColumn();		
	}
	
	public static void check(ExtendTable table) throws AptException{
			DataEntry p = DataEntryFactory.get(table.getSupportedDataEntrys().get(0),DataEntry.TABLE);
			if(p== null) throw new AptException(table.getRef(),"supper class["+table.getSupportedDataEntrys().get(0)+"] not with "+ANNO_NAME);	
	}
}
