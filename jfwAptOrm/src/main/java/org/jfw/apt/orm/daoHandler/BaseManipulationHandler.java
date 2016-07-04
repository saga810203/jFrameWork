package org.jfw.apt.orm.daoHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.orm.annotation.dao.Batch;
import org.jfw.apt.orm.annotation.dao.method.From;
import org.jfw.apt.orm.core.ColumnWriterCache;
import org.jfw.apt.orm.core.model.DataEntry;
import org.jfw.apt.orm.core.model.DataEntryFactory;
import org.jfw.apt.orm.core.model.Table;

public abstract class BaseManipulationHandler extends BaseDaoHandler {

	protected boolean batch;

	protected boolean byEntry;

	protected boolean byList;

	protected List<ColumnWriterCache> sqlparams = new ArrayList<ColumnWriterCache>();

	protected boolean dynamic = false;

	protected Table fromTable;

	protected boolean doReplaceSource;
	
	protected String batchLengthVariableName ;

	@Override
	protected void prepare() throws AptException {
		this.sqlparams.clear();
	}

	@Override
	protected void writeMethodContent() throws AptException {
		this.prepareTable();
		if(this.batch) this.genBatchLengthVariableName();
		this.prepareSqlParams();

		if (this.batch && this.dynamic)
			throw new AptException(ref, "method[@" + Batch.class + "] must be static sql");
		this.prepareSQL();
		if (!this.batch) {

			if (this.doReplaceSource) {
				cw.l("try{");
			}
		}
		cw.bL("java.sql.PreparedStatement ps = con.prepareStatement(sql");
		if (this.dynamic)
			cw.w(".toString()");
		cw.el(");");
		cw.l("try{");

		this.handleSqlParameters();
		if (this.batch) {
			cw.l("return ps.executeBatch();");
		} else {
			cw.l("return ps.executeUpdate();");
		}
		cw.l("}finally{").writeClosing("ps").l("}");
		if(!this.batch){
		if (this.doReplaceSource) {
			cw.l("}finally{");
			this.replaceResource();
			cw.l("}");
		}
		}

	}

	protected abstract void prepareSqlParams() throws AptException;

	protected void handleSqlParameters() throws AptException {
		for (ListIterator<ColumnWriterCache> it = this.sqlparams.listIterator(); it.hasNext();) {
			it.next().writeValue();
		}
	}

	protected void prepareTable() throws AptException {
		String entryJavaName = null;
		if (this.byEntry) {
			if (this.me.getParams().size() != 2)
				throw new AptException(ref,
						"method paramters size must be 2 and the second paramter with @" + Table.class.getName());
			entryJavaName = this.me.getParams().get(1).getTypeName();
			if (this.batch) {
				if (entryJavaName.startsWith(LIST_PREFIX) && entryJavaName.endsWith(LIST_SUFFIX)) {
					this.byList = true;
					entryJavaName = entryJavaName.substring(LIST_RCN_INDEX);
					entryJavaName = entryJavaName.substring(0, entryJavaName.length() - 1);
				} else if (entryJavaName.endsWith("[]")) {
					this.byList = false;
					entryJavaName = entryJavaName.substring(0, entryJavaName.length() - 2);
				} else {
					throw new AptException(ref, "the second paramter must be java.util.List<?> or Object[] ");
				}
			}
		} else {
			entryJavaName = this.getFromClassName();
			if (null == entryJavaName)
				throw new AptException(ref, "method must with @" + From.class.getName());
		}

		this.fromTable = (Table) DataEntryFactory.get(entryJavaName, DataEntry.TABLE);
		if (null == this.fromTable)
			throw new AptException(ref,
					"class[" + entryJavaName + "] not with @" + org.jfw.apt.orm.annotation.entry.Table.class.getName());

	}

	protected abstract void prepareSQL();

	@Override
	protected void checkJdbc() throws AptException {
		super.checkJdbc();
		this.batch = null != this.ref.getAnnotation(Batch.class);
		String rt = this.me.getReturnType();
		if (batch) {
			if (!"int[]".equals(rt))
				throw new AptException(ref, "mehtod[@" + Batch.class.getName() + "] must return int[]");
		} else {
			if (!"int".equals(rt))
				throw new AptException(ref, "mehtod must return int");
		}
	}

	protected void replaceResource() {
		for (ColumnWriterCache cwc : this.sqlparams) {
			if (cwc.isReplaceResource())
				cwc.replaceResource();
		}

	}
	
	protected void genBatchLengthVariableName() throws AptException{
		if(this.byEntry){
			cw.bL("if(").w(this.params.get(1).getName()).w(this.byList?".isEmpty()":".length ==0").el(") return new int[0];");
			return;			
		}
		boolean first = true;
		for(ListIterator<MethodParamEntry> it = this.params.listIterator();it.hasNext();){
			if(first){
				first = false;
			}else{
				MethodParamEntry mpe = it.next();
				if(null!= mpe.getRef().getAnnotation(Batch.class)){
					this.batchLengthVariableName = cw.getMethodTempVarName();
					cw.bL("int ").w(batchLengthVariableName).w(" = ").w(mpe.getName()).el(".length;");
					cw.bL("if(0==").w(this.batchLengthVariableName).el(") return new int[0];");
					return ;			
				}
			}
		}
		throw new AptException(this.ref,"not found parameter with @"+Batch.class.getName());
	}
}
