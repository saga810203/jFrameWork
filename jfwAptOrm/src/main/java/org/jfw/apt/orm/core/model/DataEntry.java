package org.jfw.apt.orm.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.lang.model.element.TypeElement;

public class DataEntry {
	/**
	 * javaKind
	 */
	public static final int INTERFACE = 0;

	public static final int ABSTRACT_CLASS = 1;

	public static final int STATIC_CLASS = 2;

	/**
	 * kind
	 */
	public static final int VIRTUAL_TABLE = 1;

	public static final int TABLE = 2;

	public static final int EXTEND_TABLE = 4;

	public static final int VIEW = 8;

	public static final int EXTEND_VIEW = 16;

	protected TypeElement ref;
	protected String javaName;
	protected int javaKind;
	protected List<CalcColumn> columns = new ArrayList<CalcColumn>();
	protected List<String> supportedDataEntrys = new ArrayList<String>();

	protected volatile List<CalcColumn> allCols = null;

	public List<String> getSupportedDataEntrys() {
		return supportedDataEntrys;
	}

	public void setSupportedDataEntrys(List<String> supportedDataEntrys) {
		this.supportedDataEntrys = supportedDataEntrys;
	}

	public String getJavaName() {
		return javaName;
	}

	public void setJavaName(String javaName) {
		this.javaName = javaName;
	}

	public int getKind() {
		return VIRTUAL_TABLE;
	}

	public List<CalcColumn> getColumns() {
		return columns;
	}

	public TypeElement getRef() {
		return ref;
	}

	public void setRef(TypeElement ref) {
		this.ref = ref;
	}

	public void setColumns(List<CalcColumn> columns) {
		this.columns = columns;
	}

	public int getJavaKind() {
		return javaKind;
	}

	public void setJavaKind(int javaKind) {
		this.javaKind = javaKind;
	}

	public static void mergeCalcColumnByJavaName(List<CalcColumn> cols, CalcColumn col) {
		for (CalcColumn c : cols) {
			if (c.getJavaName().equals(col.getJavaName()))
				return;
		}
		cols.add(col);
	}

	public List<CalcColumn> getAllColumn() {
		if (null == this.allCols) {
			allCols = new ArrayList<CalcColumn>();

			allCols.addAll(this.columns);

			for (ListIterator<String> it = supportedDataEntrys.listIterator(); it.hasNext();) {
				DataEntry de = DataEntryFactory.get(it.next());
				if (de != null) {
					List<CalcColumn> sList = de.getAllColumn();
					for (ListIterator<CalcColumn> cIt = sList.listIterator(); cIt.hasNext();) {
						mergeCalcColumnByJavaName(allCols, cIt.next());
					}
				}
			}
		}

		return this.allCols;
	}

	public String getSelectColumnSql() {
		List<CalcColumn> list = this.getAllColumn();
		StringBuilder sb = new StringBuilder();
		boolean first = true;

		for (ListIterator<CalcColumn> it = list.listIterator(); it.hasNext();) {
			CalcColumn c = it.next();
			if (c.isQueryable()) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(it.next().getSqlName());
			}
		}
		return sb.toString();
	}
	
	public String getFromSentence(){
		throw new UnsupportedOperationException();
	}

	public boolean hasColumnByJavaName(String javaName) {
		for (ListIterator<CalcColumn> it = this.getAllColumn().listIterator(); it.hasNext();) {
			if (it.next().getJavaName().equals(javaName))
				return true;
		}
		return false;
	}
	public CalcColumn getCalcColumnByJavaName(String javaName) {
		if(javaName == null) return null;
		for (ListIterator<CalcColumn> it = this.getAllColumn().listIterator(); it.hasNext();) {
			CalcColumn col = it.next();
			if (col.getJavaName().equals(javaName))
				return col;
		}
		return null;
	}
}
