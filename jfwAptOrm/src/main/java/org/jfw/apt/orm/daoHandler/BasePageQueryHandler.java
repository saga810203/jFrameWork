package org.jfw.apt.orm.daoHandler;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.ListIterator;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.orm.core.ColumnHandlerFactory;
import org.jfw.apt.orm.core.model.CalcColumn;
import org.jfw.apt.orm.core.model.where.WhereColumn;
import org.jfw.apt.orm.core.model.where.WhereFactory;

public abstract class BasePageQueryHandler extends BaseQueryHandler {
	protected String pageSizeName;
	protected String pageNoName;

	protected String totalName;

	protected String isFirstPageName;

	protected String whereSqlName;


	protected abstract Class<? extends Annotation> getAnnotationClass();

	@Override
	protected String getDefReturnClassName() throws AptException {
		this.pageNoName = null;
		this.pageSizeName = null;
		for (ListIterator<MethodParamEntry> it = this.me.getParams().listIterator(); it.hasNext();) {
			MethodParamEntry mpe = it.next();
			String name = mpe.getName();
			if ("int".equals(mpe.getTypeName())) {
				if (name.equals(FIRST_PAGE_NO_NAME)) {
					this.pageNoName = name;
				} else if (name.equals(FIRST_PAGE_SIZE_NAME)) {
					this.pageSizeName = name;
				} else if (name.equals(SECOND_PAGE_NO_NAME) && (null == this.pageNoName)) {
					this.pageNoName = name;
				} else if (name.equals(SECOND_PAGE_SIZE_NAME) && (null == this.pageSizeName)) {
					this.pageSizeName = name;
				}
			}
		}
		if (null == this.pageNoName)
			throw new AptException(ref,
					"Method[@" + this.getAnnotationClass().getName() + "] must has param (int pageNo) or (int _pageNo)");
		if (null == this.pageSizeName)
			throw new AptException(ref,
					"Method[@" + this.getAnnotationClass().getName() + "] must has param (int pageSize) or (int _pageSize)");

		String rcn = this.me.getReturnType();
		if ((rcn.length() <= PAGE_MIN_INDEX) || (!rcn.startsWith(PAGE_PREFIX)) || (!rcn.endsWith(PAGE_SUFFIX)))
			throw new AptException(ref,
					"Method[@" + this.getAnnotationClass().getName() + "] must return org.jfw.util.PageQueryResult<?>");
		return rcn.substring(PAGE_RCN_INDEX, rcn.length() - 1);

	}

	/**
	 * COUNT(1)
	 * 
	 * @return
	 */
	protected abstract String getSelectFieldWithRecordCount();

	protected void prepareSQL() {
		this.totalName = cw.getMethodTempVarName();
		cw.bL("int ").w(this.totalName).el(" = 0;");
		cw.bL(PAGE_PREFIX).w(this.returnClassName).w(PAGE_SUFFIX).w(" _result = new ").w(PAGE_PREFIX)
				.w(this.returnClassName).el(">();");
		if (this.wherePart != null)
			this.wherePart.prepare(cw);
		this.whereSqlName = cw.getMethodTempVarName();

		if (this.dynamic) {
			cw.l("StringBuilder sql = new StringBuilder();");
			// cw.bL("sql.append(\"SELECT ");
			this.wherePart.WriteDynamic(cw);

			cw.bL("StringBuilder ").w(this.whereSqlName).el(" = sql;");
		} else {
			cw.l("String sql = null;");
			if (null != this.wherePart) {
				cw.bL("sql = \" ");
				this.wherePart.WriteStaticTo(cw);
				cw.l("\";");
				cw.bL("String ").w(this.whereSqlName).w(" = sql;");

			}

		}

		if (this.dynamic) {
			cw.l("sql = new StringBuilder();");
			cw.bL("sql.append(\"SELECT ");
		} else {
			cw.bL("sql = \"SELECT ");
		}

		cw.ws(this.getSelectFieldWithRecordCount()).w(" FROM ").ws(this.fromSentence);
		cw.el("\");");
		if (this.dynamic) {
			if (this.wherePart.hasStaticPart()) {
				cw.bL("sql.append(").w(this.whereSqlName).el(");");
			} else {
				cw.bL("if(").w(this.whereSqlName).el(".length()>0){").bL("sql.append(").w(this.whereSqlName).el(");")
						.l("}");
			}
		} else {
			if (null != this.wherePart) {
				cw.bL("sql = sql + ").w(this.whereSqlName).el(";");
			}
		}

	}

	protected abstract void readRecordCount();

	protected void buildSqlParamters() {
		this.buildSqlParamters2();

		cw.bL("_result.setPageSize(").w(this.pageSizeName).el(");");
		cw.l("java.sql.ResultSet _pageRs = ps.executeQuery();");
		cw.l("try{");
		cw.l("_pageRs.next();");
		this.readRecordCount();
		cw.l("}finally{").writeClosing("_pageRs").l("}");

		cw.l("}finally{").writeClosing("ps").l("}");

		cw.bL("_result.setTotal(").w(this.totalName).el(");");
		cw.bL("if(0== ").w(this.totalName).el("){").l("_result.setPageNo(1);")
				.bL("_result.setData(java.util.Collections.<").w(this.returnClassName).el(">emptyList());")
				.l("return _result;").l("}");
		cw.resetJdbcParamIndex();
		this.isFirstPageName = cw.getMethodTempVarName();
		cw.bL("boolean ").w(this.isFirstPageName).w(" = (1 == ").w(this.pageNoName).el(");");
		cw.bL("if(").w(this.isFirstPageName).el("){");
		this.doFirstPage();
		cw.l("}else{");
		this.doNotFirstPage();
		cw.l("}");

		cw.bL("ps = con.prepareStatement(sql");
		if (this.dynamic)
			cw.w(".toString()");
		cw.el(");");
		cw.l("try{");
		this.buildSqlParamters2();
	}

	protected abstract void doBeforSelectForFirstPage();

	protected abstract void doBeforFromForFirstPage();

	protected abstract void doAfterWhereForFirstPage();

	protected abstract void doAfterOrderByForFirstPage();

	protected abstract void doBeforSelectForNotFirstPage();

	protected abstract void doBeforFromForNotFirstPage();

	protected abstract void doAfterWhereForNotFirstPage();

	protected abstract void doAfterOrderByForNotFirstPage();

	protected abstract void initForNotFirstPage();

	protected void doFirstPage() {

		cw.l("_result.setPageNo(1);");
		if (this.dynamic) {
			cw.l("sql = new StringBuilder();");
			cw.bL("sql.append(\"");
		} else {
			cw.bL("sql = \"");

		}
		this.doBeforSelectForFirstPage();
		cw.w("SELECT");
		boolean first = true;
		for (ListIterator<CalcColumn> it = this.fields.listIterator(); it.hasNext();) {
			if (first) {
				first = false;
				cw.w(" ");
			} else {
				cw.w(",");
			}
			cw.ws(it.next().getSqlName());
		}
		this.doBeforFromForFirstPage();

		cw.w(" FROM ").ws(this.fromSentence);
		if (this.dynamic) {
			cw.el("\");");
			if (this.wherePart.hasStaticPart()) {
				cw.bL("sql.append(").w(this.whereSqlName).el(");");
			} else {
				cw.bL("if(").w(this.whereSqlName).el(".length()>0){").bL("sql.append(").w(this.whereSqlName).el(");")
						.l("}");
			}
			doAfterWhereForFirstPage();
			if (null != this.orderBy)
				cw.bL("sql.append(\" ").ws(this.orderBy).el("\");");
		} else {
			cw.el("\";");
			if (null != this.wherePart) {
				cw.bL("sql = sql + ").w(this.whereSqlName).el(";");
			}
			doAfterWhereForFirstPage();
			if (this.orderBy != null) {
				cw.bL("sql = sql + ").w("\"").ws(this.orderBy).el("\";");
				;
			}
		}

		this.doAfterOrderByForFirstPage();
	}

	protected void doNotFirstPage() {
		this.initForNotFirstPage();

		if (this.dynamic) {
			cw.l("sql = new StringBuilder();");
			cw.bL("sql.append(\"");
		} else {
			cw.bL("sql = \"");

		}
		this.doBeforSelectForNotFirstPage();
		cw.w("SELECT");
		boolean first = true;
		for (ListIterator<CalcColumn> it = this.fields.listIterator(); it.hasNext();) {
			if (first) {
				first = false;
				cw.w(" ");
			} else {
				cw.w(",");
			}
			cw.ws(it.next().getSqlName());
		}
		this.doBeforFromForNotFirstPage();

		cw.w(" FROM ").ws(this.fromSentence);
		if (this.dynamic) {
			cw.el("\");");
			if(this.wherePart.hasStaticPart()){
				cw.bL("sql.append(").w(this.whereSqlName).el(");");
			}else{
			cw.bL("if(").w(this.whereSqlName).el(".length()>0){").bL("sql.append(").w(this.whereSqlName).el(");")
					.l("}");}
			doAfterWhereForNotFirstPage();
			if (null != this.orderBy)
				cw.bL("sql.append(\" ").ws(this.orderBy).el("\");");
		} else {
			cw.el("\";");
			if (null != this.wherePart) {
				cw.bL("sql = sql + ").w(this.whereSqlName).el(";");
			}
			doAfterWhereForNotFirstPage();
			if (this.orderBy != null) {
				cw.bL("sql = sql + ").w("\"").ws(this.orderBy).el("\";");
				;
			}
		}
		this.doAfterOrderByForNotFirstPage();
	}

	protected void buildSqlParamters2() {
		if (null == this.wherePart || this.wherePart.getColumns().isEmpty())
			return;
		for (ListIterator<WhereColumn> it = this.wherePart.getColumns().listIterator(); it.hasNext();) {
			it.next().writeValue();
		}
	}

	@Override
	protected void buildResult() {
		String listName = cw.getMethodTempVarName();
		String limitName = cw.getMethodTempVarName();

		cw.bL(LIST_PREFIX).w(this.returnClassName).w(LIST_SUFFIX).w(" ").w(listName).w(" = new java.util.ArrayList<")
				.w(this.returnClassName).el(">();");
		cw.bL("_result.setData(").w(listName).el(");");
		cw.bL("int ").w(limitName).el(" = 0;");

		cw.bL("while((").w(limitName).w("<").w(this.pageSizeName).el(") && rs.next()){");
		cw.bL("++").w(limitName).el(";");
		cw.bL(this.returnClassName).w(" _obj =  new ").w(this.returnClassName).el("();");
		int i = 1;
		for (ListIterator<CalcColumn> it = this.fields.listIterator(); it.hasNext();) {
			CalcColumn col = it.next();
			ColumnHandlerFactory.get(col.getHandlerClass()).readValue(cw, "_obj." + col.getSetter() + "(", ");", i++,
					col.isNullable());
		}
		cw.bL(listName).el(".add(_obj);");
		cw.l("}");
		cw.l("return _result;");
	}

	protected void buildWhere() throws AptException {
		this.wherePart = WhereFactory.build(this.me, this.fromDataEntry, false,
				Arrays.asList(this.pageNoName, this.pageSizeName));
		this.dynamic = (null != this.wherePart) && this.wherePart.isDynamic();
	}

}
