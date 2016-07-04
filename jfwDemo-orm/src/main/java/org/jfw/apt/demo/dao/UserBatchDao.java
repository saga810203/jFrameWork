package org.jfw.apt.demo.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.jfw.apt.demo.po.User;
import org.jfw.apt.orm.annotation.dao.Batch;
import org.jfw.apt.orm.annotation.dao.DAO;
import org.jfw.apt.orm.annotation.dao.method.From;
import org.jfw.apt.orm.annotation.dao.method.operator.Delete;
import org.jfw.apt.orm.annotation.dao.method.operator.DeleteWith;
import org.jfw.apt.orm.annotation.dao.method.operator.Insert;
import org.jfw.apt.orm.annotation.dao.method.operator.Update;
import org.jfw.apt.orm.annotation.dao.method.operator.UpdateWith;
import org.jfw.apt.orm.annotation.dao.param.Set;

@DAO
public interface UserBatchDao {
	@Insert
	@Batch
	int[] insert(Connection con, User[] user) throws SQLException,IOException;

	@Insert
	@Batch
	int[] insert(Connection con, List<User> user) throws SQLException,IOException;

	@Batch
	@Update
	int[] update(Connection con, List<User> user) throws SQLException,IOException;

	@Batch
	@Update
	int[] update(Connection con, User[] user) throws SQLException,IOException;

	@Batch
	@Delete
	int[] delete(Connection con, List<User> user) throws SQLException;

	@Batch
	@Delete
	int[] delete(Connection con,User[] user) throws SQLException;

	@Batch
	@UpdateWith
	@From(User.class)
	int[] update(Connection con,@Batch String[] id, @Set String email)throws SQLException;
	
	@Batch
	@DeleteWith
	@From(User.class)
	int[] delete(Connection con,@Batch String[] id)throws SQLException;

}
