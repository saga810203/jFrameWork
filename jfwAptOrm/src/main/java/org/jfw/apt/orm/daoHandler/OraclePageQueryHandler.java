package org.jfw.apt.orm.daoHandler;

public class OraclePageQueryHandler extends BasePageQueryHandler {
	
	protected String lastPage;
	protected String beginIndex;
	protected String endIndex;

	@Override
	protected String getSelectFieldWithRecordCount() {
		return "COUNT(1)";
	}

	@Override
	protected void doBeforSelectForFirstPage() {
	}

	@Override
	protected void doBeforFromForFirstPage() {
	}

	@Override
	protected void doAfterOrderByForFirstPage() {
	}

	@Override
	protected void doBeforSelectForNotFirstPage() {
		cw.w("SELECT * FROM (");
	}

	@Override
	protected void doBeforFromForNotFirstPage() {
		cw.w(",ROWNUM ROW$NUM");
	}

	@Override
	protected void doAfterOrderByForNotFirstPage() {
		if(this.dynamic){
			cw.bL("sql.append(\") WHERE ROW$NUM >=\").append(").w(this.beginIndex)
			  .w(").append(\" AND ROW$RUM < \").append(").w(this.endIndex).el(");");
		}else{
			cw.bL("sql = sql + \") WHERE ROW$NUM >=\"+").w(this.beginIndex).w("+\" AND ROW$NUM <\"+").w(this.endIndex).el(";");
		}
	}

	@Override
	protected void initForNotFirstPage() {
		this.lastPage = cw.getMethodTempVarName();
		this.beginIndex = cw.getMethodTempVarName();
		this.endIndex = cw.getMethodTempVarName();
		cw.bL("int ").w(lastPage).w(" = ").w(this.totalName).w(" / ").w(this.pageSizeName).el(";");
		cw.bL("if(").w(this.totalName).w(" % ").w(this.pageSizeName).el(" != 0){")
		.bL("++").w(this.lastPage).el(";")
		.l("}")
		.bL("if(").w(this.pageNoName).w(" > ").w(this.lastPage).el("){")
		.bL(this.pageNoName).w(" = ").w(this.lastPage).el(";")
		.l("}")
		.bL("_result.setPageNo(").w(this.pageNoName).el(");")
		.bL("--").w(this.pageNoName).el(";");
		
		cw.bL("int ").w(this.beginIndex).w(" = (").w(this.pageNoName).w(" * ").w(this.pageSizeName).el(") + 1;");
		cw.bL("int ").w(this.endIndex).w(" = ").w(this.beginIndex).w(" + ").w(this.pageSizeName).el(" ;");
	}

	@Override
	protected void readRecordCount() {
		cw.bL(this.totalName).el(" = _pageRs.getInt(1);");
	}

	@Override
	protected void doAfterWhereForFirstPage() {
		if(this.dynamic){
			cw.bL("if(").w(this.whereSqlName).el(".length()>0){")
			  .l("sql.append(\" AND \");")
			.l("}else{")  
			.l("sql.append(\" WHERE \");")
			.l("}")
			.bL("sql.append(\"ROWNUM <=").w(this.pageSizeName).el("\");");
		}else{
			if (null != this.wherePart) {
				cw.bL("sql = sql +\" AND ROWNUM <=").w(this.pageSizeName).el("\";");
			}else{
				cw.bL("sql = sql +\" WHERE ROWNUM <=").w(this.pageSizeName).el("\";");
			}
		}
		
	}

	@Override
	protected void doAfterWhereForNotFirstPage() {
	}

}
