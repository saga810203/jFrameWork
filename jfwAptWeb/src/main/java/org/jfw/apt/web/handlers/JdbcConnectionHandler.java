package org.jfw.apt.web.handlers;

import java.util.List;

import org.jfw.apt.CodePartGenerator;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.RequestHandler;
import org.jfw.apt.web.annotation.param.JdbcConn;

public class JdbcConnectionHandler extends RequestHandler implements CodePartGenerator {
	private boolean _commit = false;
	private String _pn = null;

	@Override
	public void init() {
	}

	@Override
	public void generate(ClassWriter cwr) {
		cwr.l("@org.jfw.apt.annotation.Autowrie(\"dataSource\")");
		cwr.l(" private javax.sql.DataSource dataSource = null;");
		cwr.l("public void setDataSource(javax.sql.DataSource dataSource){").l("this.dataSource = dataSource;").l("}");
	}

	@Override
	public void appendBeforCode(ClassWriter cw) throws AptException {
		 _commit = false;
		 _pn = null;
		List<MethodParamEntry> mpes = this.aptWebHandler.getMe().getParams();
		for (MethodParamEntry mpe : mpes) {
			JdbcConn con = mpe.getRef().getAnnotation(JdbcConn.class);
			if (null != con) {
				if (!"java.sql.Connection".equals(mpe.getTypeName()))
					throw new AptException(mpe.getRef(), "@JdbcConn java type must be java.sql.Connection");
				_pn = mpe.getName();
				_commit = con.value();
				break;
			}
		}
		if (_pn != null) {
			this.aptWebHandler.addCodePart(this, this);
			cw.bL(_pn).el("= this.dataSource.getConnection();");
			cw.l("try{");
		}
		
	}

	@Override
	public void appendAfterCode(ClassWriter cw) throws AptException {
		if (this._pn != null) {
			if (this._commit) {
				cw.bL(this._pn).el(".commit();");
				String tmp = cw.getMethodTempVarName();
				String tmp1 =cw.getMethodTempVarName();
				cw.bL("}catch(Throwable ").w(tmp).el("){")
				.bL("try{").w(_pn).w(".rollback();").w("}catch(Throwable ").w(tmp1).el("){}")
				.bL("throw ").w(tmp).el(";");
			}
			String tmp2 =cw.getMethodTempVarName();
			cw.bL("}finally{")
			.bL("try{").w(_pn).w(".close();}catch(Throwable ").w(tmp2).el("){} ").l("}");
		}
	}
	

}
