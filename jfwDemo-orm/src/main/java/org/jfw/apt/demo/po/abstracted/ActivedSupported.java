package org.jfw.apt.demo.po.abstracted;

import org.jfw.apt.annotation.JfwAptConfig;
import org.jfw.apt.orm.annotation.entry.Column;
import org.jfw.apt.orm.annotation.entry.VirtualTable;
import org.jfw.apt.orm.core.enums.DE;

@JfwAptConfig("DB:PostgreSQL")
@VirtualTable
public interface ActivedSupported {
	@Column(DE.boolean_de)
	boolean isActived();
	void setActived(boolean actived);
}
