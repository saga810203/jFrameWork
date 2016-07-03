package org.jfw.apt.orm.core.model.update;

import org.jfw.apt.orm.core.ColumnHandler;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.ColumnWriterCache;
import org.jfw.apt.out.ClassWriter;

public class UpdateColumn {
	private boolean nullable;
	private String setSql;
	private String name;
	private Class<? extends ColumnHandler> handlerClass;
	private ColumnWriterCache cwc = null;
	private boolean dynamic;
	private boolean cacheValue;
	
	
	public boolean isCacheValue() {
		return cacheValue;
	}
	public void setCacheValue(boolean cacheValue) {
		this.cacheValue = cacheValue;
	}
	public boolean isNullable() {
		return nullable;
	}
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	public String getSetSql() {
		return setSql;
	}
	public void setSetSql(String setSql) {
		this.setSql = setSql;
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
	public ColumnWriterCache getCwc() {
		return cwc;
	}
	public void setCwc(ColumnWriterCache cwc) {
		this.cwc = cwc;
	}
	public boolean isDynamic() {
		return dynamic;
	}
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	
	public void prepare(ClassWriter cw){
		this.cwc = ColumnWriterCache.build(this.name, this.cacheValue, this.nullable, cw, ColumnHandlerFactory.get(this.handlerClass));
		this.cwc.prepare();
	}
	
	public void writeValue(){
		this.cwc.writeValue();
	}
}
