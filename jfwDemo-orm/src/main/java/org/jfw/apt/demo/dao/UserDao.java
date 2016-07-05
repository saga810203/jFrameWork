package org.jfw.apt.demo.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.jfw.apt.annotation.DefaultValue;
import org.jfw.apt.annotation.Nullable;
import org.jfw.apt.demo.po.User;
import org.jfw.apt.orm.annotation.dao.Column;
import org.jfw.apt.orm.annotation.dao.DAO;
import org.jfw.apt.orm.annotation.dao.Dynamic;
import org.jfw.apt.orm.annotation.dao.method.From;
import org.jfw.apt.orm.annotation.dao.method.OrderBy;
import org.jfw.apt.orm.annotation.dao.method.operator.Delete;
import org.jfw.apt.orm.annotation.dao.method.operator.DeleteWith;
import org.jfw.apt.orm.annotation.dao.method.operator.Insert;
import org.jfw.apt.orm.annotation.dao.method.operator.PageQuery;
import org.jfw.apt.orm.annotation.dao.method.operator.PageSelect;
import org.jfw.apt.orm.annotation.dao.method.operator.SelectList;
import org.jfw.apt.orm.annotation.dao.method.operator.SelectOne;
import org.jfw.apt.orm.annotation.dao.method.operator.Update;
import org.jfw.apt.orm.annotation.dao.method.operator.UpdateWith;
import org.jfw.apt.orm.annotation.dao.param.Alias;
import org.jfw.apt.orm.annotation.dao.param.Equals;
import org.jfw.apt.orm.annotation.dao.param.GtEq;
import org.jfw.apt.orm.annotation.dao.param.Like;
import org.jfw.apt.orm.annotation.dao.param.LtEq;
import org.jfw.apt.orm.annotation.dao.param.Set;
import org.jfw.apt.orm.core.defaultImpl.StringHandler;
import org.jfw.util.PageQueryResult;
import org.jfw.util.exception.JfwBaseException;

@DAO
public interface UserDao {
	
	
	@Insert
	int insert(Connection con, User user) throws SQLException,IOException;

	@Update
	int update(Connection con, User user) throws SQLException,IOException;

	@Dynamic
	@Update
	int updateDynamic(Connection con, User user) throws SQLException,IOException;

	@SelectList
	@OrderBy("ORDER BY ID")
	List<User> query(Connection con) throws SQLException;

	@SelectOne
	User query(Connection con, String id) throws SQLException, JfwBaseException;

	@DefaultValue("null")
	@SelectOne
	User queryByName(Connection con, @Like String name) throws SQLException;
	
	@SelectList
	List<User> query(Connection con, @Like @Nullable String name,
			@GtEq @Column(handlerClass = StringHandler.class, value = "BIRTH") @Nullable String gBirth,
			@LtEq @Column(handlerClass = StringHandler.class, value="BIRTH") @Nullable String lBirth,
			Boolean actived,boolean male ) throws SQLException;
	
	@PageQuery
	@OrderBy("ORDER BY NAME")
	PageQueryResult<User> pageQuery(Connection con, @Like @Nullable String name,
			@GtEq @Column(handlerClass = StringHandler.class, value = "BIRTH") @Nullable String gBirth,
			@LtEq @Column(handlerClass = StringHandler.class, value="BIRTH") @Nullable String lBirth,
			Boolean actived,boolean male,int pageSize,int pageNo) throws SQLException;
	
	@PageSelect
	PageQueryResult<User> pageQuery(Connection con, @Like @Nullable String name,
			@GtEq @Alias("birth") @Nullable String gBirth,
			@LtEq @Alias("birth") @Nullable String lBirth,
			Boolean actived,int pageSize,int pageNo) throws SQLException;
	
	

	@Nullable
	@SelectOne
	User queryByName2(Connection con, @Like @Nullable String name) throws SQLException;

	@DefaultValue("null")
	@SelectOne
	User query3(Connection con, Boolean actived) throws SQLException;

	@Nullable
	@SelectOne
	User query3(Connection con, @Like @Equals @Nullable String name) throws SQLException;

	@Delete
	int delete(Connection con, User user) throws SQLException;
	@From(User.class)
	@DeleteWith
	int delete(Connection con, @Nullable String id,@Nullable String name) throws SQLException;
	
	@From(User.class)
	@UpdateWith
	int update(Connection con,@Set Boolean actived, @Set @Nullable String name,String id,String mobilePhone)throws SQLException,JfwBaseException;
	

}
