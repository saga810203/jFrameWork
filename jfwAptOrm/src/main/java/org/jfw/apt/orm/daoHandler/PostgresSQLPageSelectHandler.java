package org.jfw.apt.orm.daoHandler;

public class PostgresSQLPageSelectHandler extends BasePageSelectHandler {


	protected String lastPage;
	protected String beginIndex;
		
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
		if(dynamic){
			cw.bL("sql.append(\" LIMIT \").append(").w(this.pageSizeName).w(").append(\" OFFSET \").append(").w(this.beginIndex)
			.el(");");
		}else{
			cw.bL("sql = sql + \" LIMIT \" + ").w(this.pageSizeName).w("+ \" OFFSET \"+").w(this.beginIndex).el(";");
		}

	}

	@Override
	protected void doBeforSelectForNotFirstPage() {
	}

	@Override
	protected void doBeforFromForNotFirstPage() {
	}

	@Override
	protected void doAfterOrderByForNotFirstPage() {
		
		if(dynamic){
			cw.bL("sql.append(\" LIMIT \").append(").w(this.pageSizeName).el(");");
		}else{
			cw.bL("sql = sql + \" LIMIT \" + ").w(this.pageSizeName).el(";");
		}

		
			
	}

	@Override
	protected void initForNotFirstPage() {
		
		this.lastPage = cw.getMethodTempVarName();
		this.beginIndex = cw.getMethodTempVarName();
		cw.bL("int ").w(lastPage).w(" = ").w(this.totalName).w(" / ").w(this.pageSizeName).el(";");
		cw.bL("if(").w(this.totalName).w(" % ").w(this.pageSizeName).el(" != 0){")
		.bL("++").w(this.lastPage).el(";")
		.l("}")
		.bL("if(").w(this.pageNoName).w(" > ").w(this.lastPage).el("){")
		.bL(this.pageNoName).w(" = ").w(this.lastPage).el(";")
		.l("}")
		.bL("_result.setPageNo(").w(this.pageNoName).el(");")
		.bL("--").w(this.pageNoName).el(";");
		
		cw.bL("int ").w(this.beginIndex).w(" = (").w(this.pageNoName).w(" * ").w(this.pageSizeName).el(");");

	}

	@Override
	protected void readRecordCount() {
		cw.bL(this.totalName).el(" = _pageRs.getInt(1);");		
	}

	@Override
	protected void doAfterWhereForFirstPage() {
		
	}

	@Override
	protected void doAfterWhereForNotFirstPage() {
		
	}
}
