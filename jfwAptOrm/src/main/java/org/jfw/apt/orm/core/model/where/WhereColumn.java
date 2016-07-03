package org.jfw.apt.orm.core.model.where;

import org.jfw.apt.orm.core.ColumnHandler;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.ColumnWriterCache;
import org.jfw.apt.out.ClassWriter;

public class WhereColumn {
	private boolean nullable;
	private String whereSql;
	private String name;
	private Class<? extends ColumnHandler> handlerClass;
	private ColumnWriterCache cwc = null;

	private boolean cacheValue = false;

	public boolean isCacheValue() {
		return cacheValue;
	}

	public void setCacheValue(boolean cacheValue) {
		this.cacheValue = cacheValue;
	}

	public ColumnWriterCache getCwc() {
		return cwc;
	}

	public void setCwc(ColumnWriterCache cwc) {
		this.cwc = cwc;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getWhereSql() {
		return whereSql;
	}

	public void setWhereSql(String whereSql) {
		this.whereSql = whereSql;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<? extends ColumnHandler> getHandlerClass() {
		return handlerClass;
	}

	public void setHandlerClass(Class<? extends ColumnHandler> handlerClass) {
		this.handlerClass = handlerClass;
	}

	public void prepare(ClassWriter cw) {
		if (null == this.cwc) {
			this.cwc = ColumnWriterCache.build(this.name, this.cacheValue, this.nullable, cw,
					ColumnHandlerFactory.get(handlerClass));
			this.cwc.setDynamic(this.nullable);
			this.cwc.prepare();
		}

	}

	public void writeValue() {
		this.cwc.writeValue();
	}
}
