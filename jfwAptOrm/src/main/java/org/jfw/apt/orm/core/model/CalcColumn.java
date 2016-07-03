package org.jfw.apt.orm.core.model;

import org.jfw.apt.Util;
import org.jfw.apt.orm.core.ColumnHandler;

public class CalcColumn {

	private Class<? extends ColumnHandler> handlerClass;
	private boolean nullable;
	private String javaType;
	private String sqlName;
	private String javaName;

	private String getter;
	private String setter;

	// private boolean queryable;

	public boolean isQueryable() {
		return true;
	}

	// public void setQueryable(boolean queryable) {
	// this.queryable = queryable;
	// }
	public Class<? extends ColumnHandler> getHandlerClass() {
		return handlerClass;
	}

	public void setHandlerClass(Class<? extends ColumnHandler> handlerClass) {
		this.handlerClass = handlerClass;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getSqlName() {
		return sqlName;
	}

	public void setSqlName(String sqlName) {
		this.sqlName = sqlName;
	}

	public String getJavaName() {
		return javaName;
	}

	public void setJavaName(String javaName) {
		this.javaName = javaName;
	}

	public String getGetter() {
		if (null == this.getter) {
			this.getter = Util.buildGetter(this.javaName, "boolean".equals(this.javaType));
		}
		return getter;
	}

	public String getSetter() {
		if (null == this.setter) {
			this.setter = Util.buildSetter(this.javaName);
		}
		return setter;
	}

}
