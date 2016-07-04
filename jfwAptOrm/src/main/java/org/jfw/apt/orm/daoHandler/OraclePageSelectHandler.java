package org.jfw.apt.orm.daoHandler;

import javax.lang.model.element.Element;

import org.jfw.apt.AptConfig;
import org.jfw.apt.Util;
import org.jfw.apt.orm.annotation.dao.method.operator.PageSelect;

public class OraclePageSelectHandler extends BasePageSelectHandler {
	protected String lastPage;
	protected String beginIndex;
	protected String endIndex;

	@Override
	public boolean match(Element ele) {
		PageSelect pq = ele.getAnnotation(PageSelect.class);
		if(pq == null) return false;
		String dv =Util.emptyToNull(pq.value());
		if(dv == null){
			if(AptConfig.get("DB_PAGE:Oracle")!= null) return true;
			return false;
		}else{
			if(dv.equals("Oracle")) return true;
			return false;
		}
	}
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
		if (this.dynamic) {
			cw.bL("sql.append(\") WHERE ROW$NUM >=\").append(").w(this.beginIndex)
					.w(").append(\" AND ROW$RUM < \").append(").w(this.endIndex).el(");");
		} else {
			cw.bL("sql = sql + \") WHERE ROW$NUM >=\"+").w(this.beginIndex).w("+\" AND ROW$NUM <\"+").w(this.endIndex)
					.el(";");
		}
	}

	@Override
	protected void initForNotFirstPage() {
		this.lastPage = cw.getMethodTempVarName();
		this.beginIndex = cw.getMethodTempVarName();
		this.endIndex = cw.getMethodTempVarName();
		cw.bL("int ").w(lastPage).w(" = ").w(this.totalName).w(" / ").w(this.pageSizeName).el(";");
		cw.bL("if(").w(this.totalName).w(" % ").w(this.pageSizeName).el(" != 0){").bL("++").w(this.lastPage).el(";")
				.l("}").bL("if(").w(this.pageNoName).w(" > ").w(this.lastPage).el("){").bL(this.pageNoName).w(" = ")
				.w(this.lastPage).el(";").l("}").bL("_result.setPageNo(").w(this.pageNoName).el(");").bL("--")
				.w(this.pageNoName).el(";");

		cw.bL("int ").w(this.beginIndex).w(" = (").w(this.pageNoName).w(" * ").w(this.pageSizeName).el(") + 1;");
		cw.bL("int ").w(this.endIndex).w(" = ").w(this.beginIndex).w(" + ").w(this.pageSizeName).el(" ;");
	}

	@Override
	protected void readRecordCount() {
		cw.bL(this.totalName).el(" = _pageRs.getInt(1);");
	}

	@Override
	protected void doAfterWhereForFirstPage() {
		if (this.dynamic) {
			if (this.wherePart.hasStaticPart()) {
				cw.l("sql.append(\" AND ROWNUM <=\").append(").w(this.pageSizeName).el(");");
			} else {
				cw.bL("if(").w(this.whereSqlName).el(".length()>0){");
				cw.bL("sql.append(\" AND ROWNUM <=\").append(").w(this.pageSizeName).el(");");
				cw.l("}else{");
				cw.bL("sql.append(\" WHERE ROWNUM <=\").append(").w(this.pageSizeName).el(");");
				cw.l("}");
			}
		} else {
			if (null != this.wherePart) {
				cw.bL("sql = sql +\" AND ROWNUM <=\"+").w(this.pageSizeName).el(";");
			} else {
				cw.bL("sql = sql +\" WHERE ROWNUM <=\"+").w(this.pageSizeName).el(";");
			}
		}

	}

	@Override
	protected void doAfterWhereForNotFirstPage() {
	}

}
