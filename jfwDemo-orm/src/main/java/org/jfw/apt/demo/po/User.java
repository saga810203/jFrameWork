package org.jfw.apt.demo.po;

import java.io.InputStream;

import org.jfw.apt.demo.po.abstracted.ActivedSupported;
import org.jfw.apt.demo.po.abstracted.CreateTimeSupported;
import org.jfw.apt.demo.po.abstracted.IdSupported;
import org.jfw.apt.demo.po.abstracted.ModifyTimeSupported;
import org.jfw.apt.orm.annotation.entry.Column;
import org.jfw.apt.orm.annotation.entry.PrimaryKey;
import org.jfw.apt.orm.annotation.entry.Table;
import org.jfw.apt.orm.core.defaultImpl.StringHandler;
import org.jfw.apt.orm.core.enums.DE;
@PrimaryKey("id")
@Table()
public class User implements IdSupported, CreateTimeSupported, ActivedSupported, ModifyTimeSupported {
	private String id;
	private String createTime;
	private String modifyTime;
	private boolean actived;
	private InputStream in;
	/***************************************/
	
	private String name;
	
	private String password;
	
	private String mobilePhone;
	
	private String email;
	
	private String birth;
	
	private boolean male;
	
	
	@Column(DE.Date_de)
	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}
	@Column(DE.boolean_de)
	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}
	@Column(handlerClass=StringHandler.class,dbType="VARCHAR2(50)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(DE.md5_de)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	@Column(DE.MobilePhone_de)
	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	@Column(DE.Email_de2)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getModifyTime() {
		return this.modifyTime;
	}

	@Override
	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Override
	public boolean isActived() {
		return this.actived;
	}

	@Override
	public void setActived(boolean actived) {
		this.actived = actived;
	}

	@Override
	public String getCreateTime() {
		return this.createTime;
	}

	@Override
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	@Column(DE.streamBlob)
	public InputStream getIn() {
		return in;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}
	
	

}
