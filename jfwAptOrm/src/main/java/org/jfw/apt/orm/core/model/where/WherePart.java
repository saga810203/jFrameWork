package org.jfw.apt.orm.core.model.where;

import java.util.LinkedList;
import java.util.ListIterator;

import org.jfw.apt.out.ClassWriter;

public class WherePart {
	private boolean and;

	private boolean dynamic;

	private String staticSentence;

	private LinkedList<WhereColumn> columns = new LinkedList<WhereColumn>();

	public boolean isAnd() {
		return and;
	}

	public void setAnd(boolean and) {
		this.and = and;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public String getStaticSentence() {
		return staticSentence;
	}

	public void setStaticSentence(String staticSentence) {
		this.staticSentence = staticSentence;
	}

	public LinkedList<WhereColumn> getColumns() {
		return columns;
	}

	public void WriteStaticTo(ClassWriter cw) {
		boolean first = true;
		cw.w(" WHERE ");
		if (null != this.staticSentence) {
			cw.ws(this.staticSentence);
			first = false;
		}
		for (ListIterator<WhereColumn> it = this.getColumns().listIterator(); it.hasNext();) {
			if (first) {
				first = false;
			} else {
				cw.w(and ? " AND " : " OR");
			}
			cw.ws(it.next().getWhereSql());
		}
	}

	public void prepare(ClassWriter cw) {
		for (ListIterator<WhereColumn> it = this.columns.listIterator(); it.hasNext();) {
			it.next().prepare(cw);
		}
	}
	public boolean hasStaticPart(){
		return ( null != this.staticSentence) || (!columns.getFirst().isNullable());
	}
	public void WriteDynamic(ClassWriter cw) {
		boolean first = true;
		if (hasStaticPart()) {
			cw.bL("sql.append(\" WHERE ");
			if (null != this.staticSentence) {
				cw.ws(this.staticSentence);
				first = false;
			}
			boolean firstNull = true;
			for (ListIterator<WhereColumn> it = this.columns.listIterator(); it.hasNext();) {
				WhereColumn wc = it.next();
				if (!wc.isNullable()) {
					if (first) {
						first = false;
					} else {
						cw.w(this.and ? " AND " : " OR ");
					}
					cw.ws(wc.getWhereSql());
				} else {
					if (firstNull) {
						firstNull = false;
						cw.el("\");");
					}
					cw.bL("if(!").w(wc.getCwc().getCheckNullVar()).el("){");
					cw.bL("sql.append(\" ").w(and ? "AND " : "OR ").ws(wc.getWhereSql()).el("\");");
					cw.l("}");
				}
			}
		} else {

			if (this.columns.size() > 1) {
				String tmpVar = cw.getMethodTempVarName();
				cw.bL("boolean ").w(tmpVar).el(" = true;");
				for (ListIterator<WhereColumn> it = this.columns.listIterator(); it.hasNext();) {
					WhereColumn wc = it.next();
					cw.bL("if(!").w(wc.getCwc().getCheckNullVar()).el("){");
					cw.bL("if(").w(tmpVar).el("){").bL(tmpVar).el(" =  false;").l("sql.append(\" WHERE \");")
							.l("}else{").bL("sql.append(\" ").w(and ? "AND " : "OR ").el("\");").l("}");

					cw.bL("sql.append(\"").ws(wc.getWhereSql()).el("\");");
					cw.l("}");
				}
			} else {
				WhereColumn wc = this.columns.getFirst();
				cw.bL("if(!").w(wc.getCwc().getCheckNullVar()).el("){");
				cw.bL("sql.append(\" WHERE ").ws(wc.getWhereSql()).el("\");");
				cw.l("}");

			}
		}
	}

	public void writeValue() {
		for (ListIterator<WhereColumn> it = this.columns.listIterator(); it.hasNext();) {
			it.next().writeValue();
		}
	}

}
