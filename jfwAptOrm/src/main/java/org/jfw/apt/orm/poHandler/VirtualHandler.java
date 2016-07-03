package org.jfw.apt.orm.poHandler;

import java.util.List;
import java.util.ListIterator;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.core.model.DataEntry;
import org.jfw.apt.orm.core.model.DataEntryFactory;

public class VirtualHandler extends BaseHandler{
	
	public static final String ANNO_NAME ="@"+org.jfw.apt.orm.annotation.entry.VirtualTable.class.getName(); 

	@Override
	public void proccess() throws AptException {
		this.entry = new DataEntry();
		this.annoName =ANNO_NAME;
		this.initAfter();
		if(this.entry.getJavaKind()!=DataEntry.INTERFACE) throw new AptException(ref,this.annoName+" must with interface");
		List<String> interfaces = this.getInterfaces();
		this.entry.setSupportedDataEntrys(interfaces);
		this.handleColumn();	
	}
	
	public static void check(DataEntry de) throws AptException{
		for(ListIterator<String> it = de.getSupportedDataEntrys().listIterator(); it.hasNext();){
			String dn = it.next();
			DataEntry p = DataEntryFactory.get(dn,DataEntry.VIRTUAL_TABLE);
			if(p== null) throw new AptException(de.getRef(),"supper interface["+dn+"] not with "+ANNO_NAME);	
		}
		
		
	}

}
