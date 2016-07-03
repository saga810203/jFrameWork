package org.jfw.apt.orm.core.model;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.jfw.apt.Util;
import org.jfw.apt.orm.core.ColumnHandler;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.util.Base64;

public final class ColumnFactory {
	private static ArrayList<CalcColumn> columns = new ArrayList<CalcColumn>();

	private ColumnFactory() {
	}

	private static boolean match(String s1, String s2) {
		if ((null == s1) && (null != s2))
			return false;
		if ((null != s1) && (null == s2))
			return false;
		return s1.equals(s2);
	}

	private static CalcColumn find(Class<? extends ColumnHandler> handlerClass, boolean nullable, String javaType,
			String sqlName, String javaName) {
		for (CalcColumn col : columns) {
			if (col.getClass().equals(CalcColumn.class)) {
				if (col.getHandlerClass().equals(handlerClass) && (col.isNullable() == nullable)
						&& col.getJavaType().equals(javaType) && col.getSqlName().equals(sqlName)
						&& col.getJavaName().equals(javaName)) {
					return col;
				}
			}
		}
		return null;
	}

	private static Column findColumn(Class<? extends ColumnHandler> handlerClass, boolean nullable, String javaType,
			String sqlName, String javaName, boolean queryable, String dbType, String fixSqlValueWithInsert,
			String fixSqlValueWithUpdate, boolean insertable, boolean renewable) {
		for (CalcColumn col : columns) {
			if (col.getClass().equals(Column.class)) {
				Column c = (Column) col;

				if (c.getHandlerClass().equals(handlerClass) && (c.isNullable() == nullable)
						&& c.getJavaType().equals(javaType) && c.getSqlName().equals(sqlName)
						&& c.getJavaName().equals(javaName) && (queryable = c.isQueryable())
						&& match(c.getDbType(), dbType) && match(c.getFixSqlValueWithInsert(), fixSqlValueWithInsert)
						&& match(c.getFixSqlValueWithUpdate(), fixSqlValueWithUpdate)
						&& (c.isInsertable() == insertable) && (c.isRenewable() == renewable)) {
					return c;
				}
			}
		}
		return null;
	}

	public static CalcColumn buildCalcColumn(Class<? extends ColumnHandler> handlerClass, boolean nullable,
			String javaType, String sqlName, String javaName) {
		if(nullable && Util.isPrimitive(ColumnHandlerFactory.get(handlerClass).supportsClass()))
			nullable = false;
		CalcColumn col = find(handlerClass, nullable, javaType, sqlName, javaName);
		if (col == null) {
			col = new CalcColumn();
			col.setHandlerClass(handlerClass);
			col.setJavaName(javaName);
			col.setJavaType(javaType);
			col.setNullable(nullable);
			col.setSqlName(sqlName);
			columns.add(col);
		}
		return col;
	}

	public static Column buildColumn(Class<? extends ColumnHandler> handlerClass, boolean nullable, String javaType,
			String sqlName, String javaName, boolean queryable, String dbType, String fixSqlValueWithInsert,
			String fixSqlValueWithUpdate, boolean insertable, boolean renewable) {
		
		if(nullable && Util.isPrimitive(ColumnHandlerFactory.get(handlerClass).supportsClass()))
			nullable = false;
		
		Column col = findColumn(handlerClass, nullable, javaType, sqlName, javaName, queryable, dbType,
				fixSqlValueWithInsert, fixSqlValueWithUpdate, insertable, renewable);
		if (col == null) {
			col = new Column();
			col.setHandlerClass(handlerClass);
			col.setJavaName(javaName);
			col.setJavaType(javaType);
			col.setNullable(nullable);
			col.setSqlName(sqlName);
			col.setQueryable(queryable);
			col.setDbType(dbType);
			col.setFixSqlValueWithInsert(fixSqlValueWithInsert);
			col.setFixSqlValueWithUpdate(fixSqlValueWithUpdate);
			col.setInsertable(insertable);
			col.setRenewable(renewable);
			columns.add(col);
		}
		return col;
	}


	public static String serialize(CalcColumn col) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append(col.getHandlerClass().getName()).append(",").append(col.getJavaName()).append(",")
				.append(col.getJavaType()).append(",").append(Base64.encode(col.getSqlName())).append(",")
				.append(col.isNullable() ? "1" : "0");
		if (col instanceof Column) {
			Column c = (Column) col;
			sb.append(",").append(Base64.encode(c.getDbType())).append(",").append(Base64.encode(c.getFixSqlValueWithInsert()))
					.append(",").append(Base64.encode(c.getFixSqlValueWithUpdate())).append(",")
					.append(c.isQueryable() ? "1" : "0").append(",").append(c.isInsertable() ? "1" : "0").append(",")
					.append(c.isRenewable() ? "1" : "0");
		}
		return Base64.encode(sb.toString());
	}

	@SuppressWarnings("unchecked")
	public static CalcColumn deSerialize(String str) throws UnsupportedEncodingException {
		if (str == null || (str.length() == 0))
			return  null;

		String[] attrs = Base64.decode(str).split(",");

		if (attrs.length != 5 && (attrs.length != 11))
			throw new RuntimeException();

		Column col = new Column();

		try {
			col.setHandlerClass((Class<? extends ColumnHandler>) Class.forName(attrs[0]));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException();
		}
		col.setJavaName(attrs[1]);
		col.setJavaType(attrs[2]);
		col.setSqlName(Base64.decode(attrs[3]));
		col.setNullable(attrs[4].equals("1"));
		if (attrs.length == 11) {
			col.setDbType(Base64.decode(attrs[5]));
			col.setFixSqlValueWithInsert(Base64.decode(attrs[6]));
			col.setFixSqlValueWithUpdate(Base64.decode(attrs[7]));
			col.setQueryable(attrs[8].equals("1"));
			col.setInsertable(attrs[9].equals("1"));
			col.setRenewable(attrs[10].equals("1"));

			return buildColumn(col.getHandlerClass(), col.isNullable(), col.getJavaType(), col.getSqlName(),
					col.getJavaName(), col.isQueryable(), col.getDbType(), col.getFixSqlValueWithInsert(),
					col.getFixSqlValueWithUpdate(), col.isInsertable(), col.isRenewable());
		} else {
			return buildCalcColumn(col.getHandlerClass(), col.isNullable(), col.getJavaType(), col.getSqlName(),
					col.getJavaName());
		}

	}

}
