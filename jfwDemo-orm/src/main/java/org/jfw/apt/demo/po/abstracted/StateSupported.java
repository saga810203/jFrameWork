package org.jfw.apt.demo.po.abstracted;

import org.jfw.apt.orm.annotation.entry.Column;
import org.jfw.apt.orm.annotation.entry.VirtualTable;
import org.jfw.apt.orm.core.defaultImpl.FixLenStringHandler;

@VirtualTable
public interface StateSupported {
	@Column(dbType="CHAR(1)",handlerClass=FixLenStringHandler.class)
	String getState();
	void setState(String state);
}
