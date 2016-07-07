package org.jfw.apt.orm.poHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.entry.PrimaryKey;
import org.jfw.apt.orm.annotation.entry.Unique;
import org.jfw.apt.orm.annotation.entry.Uniques;
import org.jfw.apt.orm.core.model.DataEntry;
import org.jfw.apt.orm.core.model.DataEntryFactory;
import org.jfw.apt.orm.core.model.DbUniqu;
import org.jfw.apt.orm.core.model.Table;

public class TableHandler extends BaseHandler {
	public static final String ANNO_NAME = "@" + org.jfw.apt.orm.annotation.entry.Table.class.getName();

	@Override
	public void proccess() throws AptException {
		Table table = new Table();
		this.entry = table;
		this.annoName = ANNO_NAME;
		this.initAfter();
		if (this.entry.getJavaKind() == DataEntry.INTERFACE)
			throw new AptException(ref, this.annoName + " not with interface");

		String tn = this.getSupperClass();
		List<String> interfaces = this.getInterfaces();
		if (tn != null) {
			table.setExtendTable(true);
			interfaces.add(0, tn);
		} else {
			table.setExtendTable(false);
		}
		this.entry.setSupportedDataEntrys(interfaces);

		PrimaryKey pk = this.ref.getAnnotation(PrimaryKey.class);
		if (pk != null && pk.value() != null && pk.value().length > 0) {
			DbUniqu p = new DbUniqu();
			for (String s : pk.value()) {
				p.addColumn(s);
			}
			if (p.getColumns().isEmpty())
				throw new AptException(ref, " invalid primany key:empty column");
			table.setPrimaryKey(p);
		}

		this.buildUniques(table);

		this.handleColumn();

		org.jfw.apt.orm.annotation.entry.Table t = this.ref.getAnnotation(org.jfw.apt.orm.annotation.entry.Table.class);
		table.setCreate(t.create());
		table.setFromSentence(genDbName(this.entry.getJavaName(), Util.emptyToNull(t.value())));

	}

	protected void buildUniques(Table table) throws AptException{
		Uniques  qs = this.ref.getAnnotation(Uniques.class);
		if(qs!=null && qs.value()!=null && qs.value().length>0){
			for(Unique q:qs.value()){
				String name =Util.emptyToNull(q.name());
				if(name == null || table.hasUniqueByName(name)) throw new AptException(ref,"invalid unique name:null or empty or exist");
				
				ArrayList<String> list = new ArrayList<String>();
				if(q.clolumns() ==null || q.clolumns().length==0) throw new AptException(ref,"invalid unique column:zero column");
				
				for(String s:q.clolumns()){
					String str = Util.emptyToNull(s);
					if(str==null) throw new AptException(ref,"invalid unique column:null column");
					list.add(str);
				}
				if(table.hasUniqueByCol(list.toArray(new String[list.size()]))) throw new AptException(ref,"invalid unique column:exist");
				DbUniqu du = new DbUniqu();
				du.setName(name);
				du.setColumns(list);
				table.getUniqus().add(du);
			}
		}
		
		
	}

	public static void check(Table table) throws AptException {
		boolean first = true;
		for (ListIterator<String> it = table.getSupportedDataEntrys().listIterator(); it.hasNext();) {
			String dn = it.next();
			if(first){
				first = false;
				if(table.isExtendTable()){
					Table et = (Table) DataEntryFactory.get(dn, DataEntry.TABLE);
					if (et == null){
						throw new AptException(table.getRef(), "supper class[" + dn + "] not with " + ANNO_NAME);
					}
					continue;
				}
			}
			DataEntry p = DataEntryFactory.get(dn, DataEntry.VIRTUAL_TABLE);
			if (p == null) it.remove();
		}
	}
}
