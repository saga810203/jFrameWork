package org.jfw.apt.demo.web.pojo;

import org.jfw.util.auth.AuthUser;

public class LoginUser implements AuthUser {
	private static final byte[] EMPTY_AUTH = new byte[] { 0 };

	private String id;
	private transient byte[] auths = EMPTY_AUTH;
	private String name;
	private String orgCode;

	public byte[] getAuths() {
		if (null == auths || auths.length == 0) return new byte[]{0};
		return auths;
	}

	public void setAuths(byte[] auths) {
		if (null == auths || auths.length == 0)
			this.auths = EMPTY_AUTH;
		else
			this.auths = auths;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getOrgCode() {
		return this.orgCode;
	}

	@Override
	public boolean hasAuthority(int auth) {
		return false;
	}

}
