package org.jfw.apt.demo.web.service;

import java.sql.Connection;

import org.jfw.apt.annotation.Autowrie;
import org.jfw.apt.annotation.DefaultValue;
import org.jfw.apt.annotation.Nullable;
import org.jfw.apt.demo.dao.UserDao;
import org.jfw.apt.demo.web.pojo.LoginUser;
import org.jfw.apt.web.annotation.Path;
import org.jfw.apt.web.annotation.operate.Get;
import org.jfw.apt.web.annotation.operate.Post;
import org.jfw.apt.web.annotation.param.JdbcConn;
import org.jfw.apt.web.annotation.param.PVar;
import org.jfw.apt.web.annotation.param.PathVar;
import org.jfw.util.auth.AuthUser;

@Path
public class LoginService {
	public static final String EMPTY="";
	
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
	public AuthUser login(@JdbcConn(false) Connection con,String ln,String pw){
		LoginUser user = new LoginUser();
		user.setId("1234567687");
		user.setName("DEFAULT");
		user.setOrgCode("DEFAULT_ORG_CODE");
		return user;		
	}
	
	@Post
	@Path("/login2")
	public AuthUser login2(@JdbcConn(false) Connection con,@DefaultValue("org.jfw.apt.demo.web.service.LoginService.EMPTY") String ln,@Nullable String pw){
		LoginUser user = new LoginUser();
		user.setId("1234567687");
		user.setName("DEFAULT");
		user.setOrgCode("DEFAULT_ORG_CODE");
		return user;		
	}
	@Get
	@Path("/loginUser")
	public AuthUser loginUser( @org.jfw.apt.web.annotation.LoginUser(false) AuthUser user){
		return user;
	}
	
	@Path("/login/{id}")
	public void login4(@PathVar String id){}
	
	
	
	@Path("/login/pq/{id}/{name}")
	public String ddd(@PVar String id, @PVar String name){
		return null;
	}
	
	
	
}
