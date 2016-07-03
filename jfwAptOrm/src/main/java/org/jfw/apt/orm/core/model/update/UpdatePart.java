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
	public void prepare(ClassWriter cw){
		for(ListIterator<UpdateColumn> it = this.columns.listIterator();it.hasNext();){
			it.next().prepare(cw);
		}
	}
	public void WriteStatic(ClassWriter cw){
		boolean first = true;
			if(null!= this.staticSentence) {
			cw.ws(this.staticSentence);
			first = false;
		}
		for(ListIterator<UpdateColumn> it = this.columns.listIterator() ; it.hasNext();){
			if(first){
				first = false;
			}else{
				cw.w(",");
			}
			cw.ws(it.next().getSetSql());
		}
	}
	
	public void WriteDynamic(ClassWriter cw){
		boolean first = true;
		boolean hasStaticSQL = null!= this.staticSentence;
		if(hasStaticSQL || (!columns.getFirst().isDynamic())){
		if(hasStaticSQL){
				cw.ws(this.staticSentence);
				first = false;				
			}
			boolean firstDynamic = true;
			for(ListIterator<UpdateColumn> it = this.columns.listIterator(); it.hasNext();){
				UpdateColumn wc = it.next();
				if(!wc.isDynamic()){
					if(first){
						first = false;
					}else{
						cw.w(",");
					}
					cw.ws(wc.getSetSql());
				}else{
					if(firstDynamic){
						firstDynamic = false;
						cw.el("\");");
					}
					cw.bL("if(").w(wc.getCwc().getCheckNullVar()).el("){");
					cw.bL("sql.append(\",").ws(wc.getSetSql()).el("\");");
					cw.l("}");	
				}	
			}
		}else{
			cw.el(" \");");
			String firstUpdate = cw.getMethodTempVarName();
			cw.bL("boolean ").w(firstUpdate).el(" = false;");
			for(ListIterator<UpdateColumn> it = this.columns.listIterator(); it.hasNext();){
				UpdateColumn wc = it.next();
					cw.bL("if(").w(wc.getCwc().getCheckNullVar()).el("){");
						cw.bL("if(").w(firstUpdate).el("){")
						  .bL(firstUpdate).el(" =  false;")
						  .l("}else{")
						  .l("sql.append(\",\");")
						  .l("}")
						  .bL("sql.append(\"").w(wc.getSetSql()).el("\");")
					.l("}");			
			}
			
			cw.bL("if(!").w(firstUpdate).el("){")
			  .l("throw new org.jfw.util.exception.JfwBaseException(202);")
			  .l("}");
		}
	}

	public void writeValue(){
		for(ListIterator<UpdateColumn> it = this.columns.listIterator();it.hasNext();){
			it.next().writeValue();
		}
		
		
	}
}
