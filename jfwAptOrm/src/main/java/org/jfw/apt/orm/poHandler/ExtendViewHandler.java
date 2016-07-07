package org.jfw.apt.orm.poHandler;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.entry.Table;
import org.jfw.apt.orm.core.model.DataEntry;
import org.jfw.apt.orm.core.model.DataEntryFactory;
import org.jfw.apt.orm.core.model.ExtendView;

public class ExtendViewHandler extends BaseHandler{
		public static final String ANNO_NAME ="@"+org.jfw.apt.orm.annotation.entry.ExtendView.class.getName(); 
		@Override
		public void proccess() throws AptException {
			ExtendView table = new ExtendView();
			this.entry = table;
			this.annoName =ANNO_NAME;
			this.initAfter();
			if(this.entry.getJavaKind()==DataEntry.INTERFACE) throw new AptException(ref,this.annoName+" not with interface");
			
			String tn = this.getSupperClass();
			if(tn == null)throw new AptException(ref,"must extend a class[@"+Table.class+"] not with "+ANNO_NAME);	
			table.getSupportedDataEntrys().add(tn);
			this.handleCalcColumn();
			
			org.jfw.apt.orm.annotation.entry.ExtendView t = this.ref.getAnnotation(org.jfw.apt.orm.annotation.entry.ExtendView.class);
			String fs =Util.emptyToNull(t.fromSentence());
			if(fs == null) throw new AptException(ref,this.annoName+" must set fromSentence");
			table.setFromSentence(fs);
			fs =Util.emptyToNull(t.tableAlias());
			if(fs == null) throw new AptException(ref,this.annoName+" must set tableAlias");
			table.setTableAlias(fs);
		}
		
		public static void check(ExtendView table) throws AptException{
			DataEntry p = DataEntryFactory.get(table.getSupportedDataEntrys().get(0),DataEntry.TABLE);
			if(p== null) throw new AptException(table.getRef(),"supper class["+table.getSupportedDataEntrys().get(0)+"] not with @"+Table.class.getName());	
		}
}
