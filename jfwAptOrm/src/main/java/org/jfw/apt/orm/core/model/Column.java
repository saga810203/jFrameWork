package org.jfw.apt.orm.core.model;

public class Column extends CalcColumn{

	private String dbType;
	
	private String fixSqlValueWithInsert;
	private String fixSqlValueWithUpdate;
	private boolean insertable;
	private boolean renewable;
	private String comment;
	private boolean queryable;
	
	@Override
	public boolean isQueryable() {
		return queryable;
	}
	public void setQueryable(boolean queryable) {
		this.queryable = queryable;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	public String getFixSqlValueWithInsert() {
		return fixSqlValueWithInsert;
	}
	public void setFixSqlValueWithInsert(String fixSqlValueWithInsert) {
		this.fixSqlValueWithInsert = fixSqlValueWithInsert;
	}
	public String getFixSqlValueWithUpdate() {
		return fixSqlValueWithUpdate;
	}
	public void setFixSqlValueWithUpdate(String fixSqlValueWithUpdate) {
		this.fixSqlValueWithUpdate = fixSqlValueWithUpdate;
	}
	public boolean isInsertable() {
		return insertable;
	}
	public void setInsertable(boolean insertable) {
		this.insertable = insertable;
	}
	public boolean isRenewable() {
		return renewable;
	}
	public void setRenewable(boolean renewable) {
		this.renewable = renewable;
	}
	
	
}
