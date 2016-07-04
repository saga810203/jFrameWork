package org.jfw.apt.demo.web.service;

import org.jfw.apt.annotation.Autowrie;
import org.jfw.apt.demo.dao.UserDao;
import org.jfw.apt.demo.web.pojo.LoginUser;
import org.jfw.apt.web.annotation.Path;
import org.jfw.apt.web.annotation.operate.Post;
import org.jfw.util.auth.AuthUser;

@Path
public class LoginService {
	@Autowrie
	private UserDao userDao;

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	@Post
	@Path("/login")
	public AuthUser login(String ln,String pw){
		LoginUser user = new LoginUser();
		user.setId("1234567687");
		user.setName("DEFAULT");
		user.setOrgCode("DEFAULT_ORG_CODE");
		return user;		
	}
	
	
}
