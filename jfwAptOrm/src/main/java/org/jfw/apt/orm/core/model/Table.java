package org.jfw.apt.orm.core.model;

import java.util.ArrayList;
import java.util.List;

public class Table extends View{
	
	private boolean create;
	
	private boolean extendTable ;
	
	private DbUniqu primaryKey;
	
	private List<DbUniqu> uniqus = new ArrayList<DbUniqu>();

	
	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public DbUniqu getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(DbUniqu primaryKey) {
		this.primaryKey = primaryKey;
	}

	public List<DbUniqu> getUniqus() {
		return uniqus;
	}

	public void setUniqus(List<DbUniqu> uniqus) {
		this.uniqus = uniqus;
	}

	public boolean isExtendTable() {
		return extendTable;
	}

	public void setExtendTable(boolean extendTable) {
		this.extendTable = extendTable;
	}
	
	public boolean hasUniqueByName(String name){
		for(DbUniqu q:this.uniqus){
			if(q.getName().equals(name)) return true;
		}
		return false;
	}
	public boolean hasUniqueByCol(String[] col){
		for(DbUniqu q:this.uniqus){
			if(q.getColumns().size()==col.length){
				for(String c:col){
					if(!q.getColumns().contains(c)) return false;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int getKind() {
		return DataEntry.TABLE;
	}
	
	
}
