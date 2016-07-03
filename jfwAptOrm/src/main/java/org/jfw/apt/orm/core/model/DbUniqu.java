package org.jfw.apt.orm.core.model;

import java.util.ArrayList;
import java.util.List;

public class DbUniqu {
	public static final String PRIMARY_KEY ="primaryKey";
	
	private String name;

	private List<String> columns = new ArrayList<String>();
	
	public String getName() {
		if(null== this.name) return PRIMARY_KEY;
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	
	public void addColumn(String col){
		if(col==null || col.trim().length()==0) return;
		col = col.trim();
		if(!columns.contains(col))columns.add(col);
	}
}
