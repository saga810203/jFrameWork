package org.jfw.apt.demo.po.abstracted;

import org.jfw.apt.orm.annotation.entry.Column;
import org.jfw.apt.orm.annotation.entry.VirtualTable;
import org.jfw.apt.orm.core.enums.DE;

@VirtualTable
public interface CreateTimeSupported {
	@Column(DE.createDateTime_de)
	String getCreateTime();
	void setCreateTime(String createTime);
}
