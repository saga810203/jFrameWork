package org.jfw.apt.orm.core.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.lang.model.element.Element;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.poHandler.ExtendTableHandler;
import org.jfw.apt.orm.poHandler.ExtendViewHandler;
import org.jfw.apt.orm.poHandler.TableHandler;
import org.jfw.apt.orm.poHandler.ViewHandler;
import org.jfw.apt.orm.poHandler.VirtualHandler;
import org.jfw.apt.util.Base64;

public final class DataEntryFactory {

	public static final String FILE_NAME = "jfw_orm_dataEntry";

	private static final Map<String, DataEntry> entries = new HashMap<String, DataEntry>();

	private static final List<DataEntry> cached = new LinkedList<DataEntry>();

	public static void add(DataEntry entry) {
		entries.put(entry.getJavaName(), entry);
		cached.add(entry);
	}

	public static DataEntry get(String javaName) {
		return entries.get(javaName);
	}

	public static DataEntry get(String javaName, int kindFlag) {
		DataEntry entry = entries.get(javaName);
		if (entry != null && (0 != (kindFlag & entry.getKind())))
			return entry;
		return null;
	}

	public static String serialize(DbUniqu uniqu) throws UnsupportedEncodingException {
		if (uniqu == null)
			return "";
		StringBuilder sb = new StringBuilder();
		sb.append(Base64.encode(uniqu.getName()));

		for (ListIterator<String> it = uniqu.getColumns().listIterator(); it.hasNext();) {
			sb.append(",").append(Base64.encode(it.next()));
		}
		return Base64.encode(sb.toString());
	}

	public static DbUniqu deSerializeDbUniqu(String str) throws UnsupportedEncodingException {
		DbUniqu dbUniqu = new DbUniqu();
		String[] ss = Base64.decode(str).split(",");
		dbUniqu.setName(Base64.decode(ss[0]));
		for (int i = 1; i < ss.length; ++i) {
			dbUniqu.getColumns().add(Base64.decode(ss[i]));
		}
		return dbUniqu;
	}

	public static String serializeStrings(List<String> strs, boolean ser) throws UnsupportedEncodingException {
		if (strs == null || strs.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder();
		boolean first = true;

		for (ListIterator<String> it = strs.listIterator(); it.hasNext();) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			String s = it.next();
			sb.append(ser ? Base64.encode(s) : s);

		}
		return Base64.encode(sb.toString());
	}

	public static List<String> deSerializeStrings(String str, boolean deser) throws UnsupportedEncodingException {
		List<String> result = new ArrayList<String>();
		if (str == null || str.length() == 0)
			return result;
		String[] ss = Base64.decode(str).split(",");
		for (int i = 0; i < ss.length; ++i) {
			result.add(deser ? Base64.decode(ss[i]) : ss[i]);
		}
		return result;
	}

	public static String serializeColumns(List<CalcColumn> cols) throws UnsupportedEncodingException {
		if (cols == null || cols.isEmpty())
			return "";
		List<String> list = new ArrayList<String>();
		for (ListIterator<CalcColumn> it = cols.listIterator(); it.hasNext();) {
			list.add(ColumnFactory.serialize(it.next()));
		}
		return serializeStrings(list, false);
	}

	public static List<CalcColumn> deSerializeColumns(String s) throws UnsupportedEncodingException {
		List<CalcColumn> list = new ArrayList<CalcColumn>();
		if (s == null || s.length() == 0)
			return list;

		List<String> strs = deSerializeStrings(s, false);
		for (ListIterator<String> it = strs.listIterator(); it.hasNext();) {
			list.add(ColumnFactory.deSerialize(it.next()));
		}

		return list;
	}

	public static String serializeDbUniqus(List<DbUniqu> objs) throws UnsupportedEncodingException {
		if (objs == null || objs.isEmpty())
			return "";
		List<String> list = new ArrayList<String>();
		for (ListIterator<DbUniqu> it = objs.listIterator(); it.hasNext();) {
			list.add(serialize(it.next()));
		}
		return serializeStrings(list, false);
	}

	public static List<DbUniqu> deSerializeDbUniquss(String s) throws UnsupportedEncodingException {
		List<DbUniqu> list = new ArrayList<DbUniqu>();
		if (s == null || s.length() == 0)
			return list;

		List<String> strs = deSerializeStrings(s, false);
		for (ListIterator<String> it = strs.listIterator(); it.hasNext();) {
			list.add(deSerializeDbUniqu(it.next()));
		}
		return list;
	}

	public static String serialize(DataEntry entry) throws UnsupportedEncodingException {
		if (null == entry)
			return "";
		StringBuilder sb = new StringBuilder();

		int kind = entry.getKind();
		sb.append(kind) // 0
				.append(",").append(entry.getJavaKind()) // 1
				.append(",").append(entry.getJavaName()) // 2
				.append(",").append(serializeColumns(entry.getColumns())); // 3

		if (kind == DataEntry.VIEW || (kind == DataEntry.TABLE) || (kind == DataEntry.EXTEND_VIEW)) {
			sb.append(",").append(Base64.encode(((View) entry).getFromSentence())); // 4
			if (kind == DataEntry.EXTEND_VIEW)
				sb.append(",").append(Base64.encode(((ExtendView) entry).getTableAlias())); // 5
			if (kind == DataEntry.TABLE) {
				Table t = (Table) entry;
				sb.append(",").append(serialize(t.getPrimaryKey())) // 5
						.append(",").append(serializeDbUniqus(t.getUniqus())); // 6
			}
		}
		return sb.toString();
	}

	public static DataEntry deSerializeDataEntry(String str) throws UnsupportedEncodingException {
		if (str == null || str.length() == 0)
			return null;
		String[] ss = str.split(",");
		int kind = Integer.parseInt(ss[0]);
		DataEntry entry = null;
		if (kind == DataEntry.VIRTUAL_TABLE) {
			entry = new DataEntry();

		} else if (kind == DataEntry.TABLE) {
			entry = new Table();
			((Table) entry).setFromSentence(Base64.decode(ss[4]));
			((Table) entry).setPrimaryKey(deSerializeDbUniqu(ss[5]));
			((Table) entry).setUniqus(deSerializeDbUniquss(ss.length > 6 ? ss[6] : null));
		} else if (kind == DataEntry.VIEW) {
			entry = new View();
			((View) entry).setFromSentence(Base64.decode(ss[4]));

		} else if (kind == DataEntry.EXTEND_VIEW) {
			entry = new ExtendView();
			((ExtendView) entry).setFromSentence(Base64.decode(ss[4]));
			((ExtendView) entry).setTableAlias(Base64.decode(ss[5]));
		} else if (kind == DataEntry.EXTEND_TABLE) {
			entry = new ExtendTable();

		} else
			throw new IllegalArgumentException("read orm define error: invalid dataEntry'kind");
		entry.setJavaKind(Integer.parseInt(ss[1]));
		entry.setJavaName(ss[2]);
		entry.setColumns(deSerializeColumns(ss[3]));
		return entry;

	}

	public static void load(InputStream in) throws UnsupportedEncodingException, IOException {
		BufferedReader read = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line;
		while ((line = read.readLine()) != null) {
			DataEntry entry = deSerializeDataEntry(line);
			if (entry != null)
				entries.put(entry.getJavaName(), entry);
		}
	}

	public static void load() {
		if (entries.isEmpty()) {
			try {
				Enumeration<URL> en = DataEntryFactory.class.getClassLoader().getResources(FILE_NAME);
				while (en.hasMoreElements()) {
					URL url = en.nextElement();
					InputStream in = url.openStream();
					try {
						load(in);
					} finally {
						try {
							in.close();
						} catch (Exception e) {
						}
					}

				}
			} catch (Exception e) {
				throw new RuntimeException("load dataEntry error", e);
			}
		}
	}

	public static String getEntriesAsString() throws UnsupportedEncodingException {
		if (cached.isEmpty())
			return null;
		StringWriter sw = new StringWriter();
		for (DataEntry entry : cached) {
			sw.write(serialize(entry));
			sw.write("\n");
		}
		sw.flush();
		return sw.toString();
	}

	public static void checkCachedEntries() throws AptException {
		for (DataEntry en : cached) {
			int kind = en.getKind();
			if (kind == DataEntry.VIRTUAL_TABLE) {
				VirtualHandler.check(en);
			} else if (kind == DataEntry.TABLE) {
				TableHandler.check((Table) en);
			} else if (kind == DataEntry.EXTEND_TABLE) {
				ExtendTableHandler.check((ExtendTable) en);
			} else if (kind == DataEntry.VIEW) {
				ViewHandler.check((View) en);
			} else if (kind == DataEntry.EXTEND_VIEW) {
				ExtendViewHandler.check((ExtendView) en);
			}
		}
	}

	public static void checkCachedTable() throws AptException {
		for (DataEntry en : cached) {
			if (en.getKind() == DataEntry.TABLE) {
				Table tt = (Table) en;

				if (!checkDbUniqu(tt.getPrimaryKey(), tt))
					throw new AptException(tt.getRef(), "invalid primary key:not exists column");
				for (DbUniqu q : tt.getUniqus())
					if (!checkDbUniqu(q, tt))
						throw new AptException(tt.getRef(), "invalid unique :not exists column");
			}
		}
	}

	public static boolean checkDbUniqu(DbUniqu q, Table t) {
		if (q != null) {
			for (String s : q.getColumns()) {
				if (!t.hasColumnByJavaName(s))
					return false;
			}
		}
		return true;
	}

	public static void checkFromable(String javaName, Element ele) throws AptException {
		if (null == get(javaName, DataEntry.TABLE | DataEntry.EXTEND_TABLE | DataEntry.VIEW | DataEntry.EXTEND_VIEW))
			throw new AptException(ele,
					"Class[" + javaName
							+ "] not with @org.jfw.apt.orm.annotation.Table or @org.jfw.apt.orm.annotation.ExtendTable or"
							+ " @org.jfw.apt.orm.annotation.View or @org.jfw.apt.orm.annotation.ExtendView");
	}

	public static void checkSelectable(String javaName, Element ele) throws AptException {
		if (null == get(javaName))
			throw new AptException(ele,
					"Class[" + javaName
							+ "] not with @org.jfw.apt.orm.annotation.VirtualTable or @org.jfw.apt.orm.annotation.Table or @org.jfw.apt.orm.annotation.ExtendTable or"
							+ " @org.jfw.apt.orm.annotation.View or @org.jfw.apt.orm.annotation.ExtendView");
	}

	public static void cleanCached() {
		cached.clear();
	}

	public static void genTableDDL(Table table, StringBuilder sb) {
		if (table.isCreate()) {
			sb.append("CREATE TABLE ").append(table.getFromSentence()).append(" (");
			boolean first = true;
			for (ListIterator<CalcColumn> it = table.getAllColumn().listIterator(); it.hasNext();) {
				Column col = (Column) it.next();
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(col.getSqlName()).append(" ").append(col.getDbType());
				if (!col.isNullable())
					sb.append(" ").append("NOT NULL");
			}
			sb.append(");\r\n");
			DbUniqu qu = table.getPrimaryKey();
			if (qu != null) {
				sb.append("ALTER TABLE ").append(table.getFromSentence()).append(" ADD PRIMARY KEY (");
				first = true;
				for (String key : qu.getColumns()) {
					Column col = (Column) table.getCalcColumnByJavaName(key);
					if (first) {
						first = false;
					} else {
						sb.append(",");
					}
					sb.append(col.getSqlName());

				}

				sb.append(");\r\n");
			}

			for (DbUniqu qnu : table.getUniqus()) {
				sb.append("ALTER TABLE ").append(table.getFromSentence()).append(" ADD UNIQUE (");
				first = true;
				for (String key : qnu.getColumns()) {
					Column col = (Column) table.getCalcColumnByJavaName(key);
					if (first) {
						first = false;
					} else {
						sb.append(",");
					}
					sb.append(col.getSqlName());
				}
				sb.append(");\r\n");
			}
		}
	}

	public static String getDDL() {
		StringBuilder sb = new StringBuilder();
		for (DataEntry entry : cached) {
			if (entry.getKind() == DataEntry.TABLE) {
				genTableDDL((Table) entry, sb);
			}
		}
		return sb.toString();
	}

	private DataEntryFactory() {
	}
}
