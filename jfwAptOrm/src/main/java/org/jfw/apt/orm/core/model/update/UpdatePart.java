package org.jfw.apt.orm.core.model.update;

import java.util.LinkedList;
import java.util.ListIterator;

import org.jfw.apt.out.ClassWriter;

public class UpdatePart {

	private boolean dynamic;

	private String staticSentence;

	private LinkedList<UpdateColumn> columns = new LinkedList<UpdateColumn>();

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

	public LinkedList<UpdateColumn> getColumns() {
		return columns;
	}

	public void setColumns(LinkedList<UpdateColumn> columns) {
		this.columns = columns;
	}

	public void prepare(ClassWriter cw) {
		for (ListIterator<UpdateColumn> it = this.columns.listIterator(); it.hasNext();) {
			it.next().prepare(cw);
		}
	}

	public void WriteStatic(ClassWriter cw) {
		boolean first = true;
		if (null != this.staticSentence) {
			cw.ws(this.staticSentence);
			first = false;
		}
		for (ListIterator<UpdateColumn> it = this.columns.listIterator(); it.hasNext();) {
			if (first) {
				first = false;
			} else {
				cw.w(",");
			}
			cw.ws(it.next().getSetSql());
		}
	}

	public boolean hasFixPart() {
		return (null != this.staticSentence) || (!columns.getFirst().isDynamic());
	}

	public void WriteDynamic(ClassWriter cw) {
		boolean first = true;
		if (this.hasFixPart()) {
			if (null != this.staticSentence) {
				cw.ws(this.staticSentence);
				first = false;
			}
			boolean firstDynamic = true;
			for (ListIterator<UpdateColumn> it = this.columns.listIterator(); it.hasNext();) {
				UpdateColumn wc = it.next();
				if (!wc.isDynamic()) {
					if (first) {
						first = false;
					} else {
						cw.w(",");
					}
					cw.ws(wc.getSetSql());
				} else {
					if (firstDynamic) {
						firstDynamic = false;
						cw.el("\");");
					}
					cw.bL("if(!").w(wc.getCwc().getCheckNullVar()).el("){");
					cw.bL("sql.append(\",").ws(wc.getSetSql()).el("\");");
					cw.l("}");
				}
			}
		} else {
			cw.el(" \");");
			if (this.columns.size() > 1) {
				String firstUpdate = cw.getMethodTempVarName();
				cw.bL("boolean ").w(firstUpdate).el(" = false;");
				first = true;
				for (ListIterator<UpdateColumn> it = this.columns.listIterator(); it.hasNext();) {
					UpdateColumn wc = it.next();
					cw.bL("if(").w(wc.getCwc().getCheckNullVar()).el("){");
					if (!first) {
						cw.bL("if(").w(firstUpdate).el("){");
					}
					cw.bL(firstUpdate).el(" =  false;").bL("sql.append(\"").w(wc.getSetSql()).el("\");");
					if (!first) {
						cw.l("}else{").l("sql.append(\",").w(wc.getSetSql()).el("\");");
						cw.l("}");
					}
					if (first) {
						first = false;
					}
					cw.l("}");
				}

				cw.bL("if(!").w(firstUpdate).el("){").l("throw new org.jfw.util.exception.JfwBaseException(202);")
						.l("}");
			} else {
				UpdateColumn uc = this.columns.getFirst();
				cw.bL("if(").w(uc.getCwc().getCheckNullVar()).el("){");
				cw.l("sql.append(\",").w(uc.getSetSql()).el("\");").l("}");
			}
		}
	}

	public void writeValue() {
		for (ListIterator<UpdateColumn> it = this.columns.listIterator(); it.hasNext();) {
			it.next().writeValue();
		}

	}
}
