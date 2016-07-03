package org.jfw.apt.orm.poHandler;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.core.model.DataEntry;
import org.jfw.apt.orm.core.model.View;

public class ViewHandler extends BaseHandler{
	public static final String ANNO_NAME ="@"+org.jfw.apt.orm.annotation.entry.View.class.getName(); 
	@Override
	public void proccess() throws AptException {
		View view = new View();
		this.entry = view;
		this.annoName =ANNO_NAME;
		this.initAfter();
		if(this.entry.getJavaKind()==DataEntry.INTERFACE) throw new AptException(ref,this.annoName+" not with interface");
		this.handleCalcColumn();
		
		org.jfw.apt.orm.annotation.entry.View t = this.ref.getAnnotation(org.jfw.apt.orm.annotation.entry.View.class);
		String fs =Util.emptyToNull(t.value());
		if(fs == null) throw new AptException(ref,this.annoName+" must set value");
		
		view.setFromSentence(fs);
		
	}
	
	public static void check(View view) throws AptException{
//		if(table.isExtendTable()){
//			String etName = table.getSupportedDataEntrys().get(0);
//			Table et =(Table)DataEntryFactory.get(etName, DataEntry.TABLE);
//			if(et == null) throw new AptException(table.getRef(),"supper class["+etName+"] not with "+ANNO_NAME);	
//		}
//		
//		List<String> ins =new ArrayList<String>(table.getSupportedDataEntrys());
//		if(table.isExtendTable()) ins.remove(0);
//			
//		for(ListIterator<String> it = ins.listIterator(); it.hasNext();){
//			String dn = it.next();
//			DataEntry p = DataEntryFactory.get(dn,DataEntry.VIRTUAL_TABLE);
//			if(p== null) throw new AptException(table.getRef(),"impl interface["+dn+"] not with "+ANNO_NAME);	
//		}	
	}

}
