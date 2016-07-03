package org.jfw.apt.orm.daoHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.dao.method.OrderBy;
import org.jfw.apt.orm.core.model.CalcColumn;
import org.jfw.apt.orm.core.model.DataEntry;
import org.jfw.apt.orm.core.model.DataEntryFactory;
import org.jfw.apt.orm.core.model.where.WhereColumn;
import org.jfw.apt.orm.core.model.where.WhereFactory;
import org.jfw.apt.orm.core.model.where.WherePart;

public abstract class BaseQueryHandler extends BaseDaoHandler {

	public static final String PAGE_PREFIX = "org.jfw.util.PageQueryResult<";
	public static final int PAGE_RCN_INDEX = PAGE_PREFIX.length();
	public static final String PAGE_SUFFIX = ">";
	public static final int PAGE_MIN_INDEX = PAGE_RCN_INDEX + 1;

	public static final String FIRST_PAGE_SIZE_NAME = "_pageSize";
	public static final String FIRST_PAGE_NO_NAME = "_pageNo";

	public static final String SECOND_PAGE_SIZE_NAME = "pageSize";
	public static final String SECOND_PAGE_NO_NAME = "pageNo";

	protected boolean dynamic = false;

	protected DataEntry fromDataEntry;

	protected DataEntry selectDataEntry;

	protected String returnClassName;

	protected List<CalcColumn> fields = new ArrayList<CalcColumn>();

	protected String fromSentence;

	protected String orderBy;

	protected WherePart wherePart;

	public DataEntry getFromDataEntry(String javaName) throws AptException {
		DataEntry result = null;
		String f = this.getFromClassName();

		result = DataEntryFactory.get(f == null ? javaName : f,
				DataEntry.TABLE | DataEntry.EXTEND_TABLE | DataEntry.VIEW | DataEntry.EXTEND_VIEW);

		if (null == result)
			throw new AptException(this.ref, "not found from DataEntry");
		return result;

	}

	public DataEntry getSelectDataEntry(String javaName) throws AptException {
		DataEntry result = null;
		String f = this.getSelectClassName();
		result = DataEntryFactory.get(f == null ? javaName : f);
		if (null == result)
			throw new AptException(this.ref, "not found select DataEntry");
		return result;
	}

	@Override
	protected void writeMethodContent() throws AptException {
		this.orderBy = null;
		OrderBy ob = this.ref.getAnnotation(OrderBy.class);
		if (ob != null)
			this.orderBy = Util.emptyToNull(ob.value());

		this.prepareDataEntry();
		this.buildWhere();
		this.prepareSQL();
		cw.bL("java.sql.PreparedStatement ps = con.prepareStatement(sql");
		if (this.dynamic)
			cw.w(".toString()");
		cw.el(");");
		cw.l("try{");
		this.buildSqlParamters();
		cw.l("java.sql.ResultSet rs = ps.executeQuery();");
		cw.l("try{");
		this.buildResult();
		cw.l("}finally{").writeClosing("rs").l("}");
		cw.l("}finally{").writeClosing("ps").l("}");
	}

	protected void prepareDataEntry() throws AptException {
		String drc = this.getDefReturnClassName();

		String fde = this.getFromClassName();
		if (null == fde) {
			fde = drc;
			DataEntryFactory.checkFromable(fde, ref);
		}
		this.fromDataEntry = DataEntryFactory.get(fde);

		String sde = this.getSelectClassName();
		if (sde == null) {
			sde = drc;
			DataEntryFactory.checkSelectable(sde, ref);
		}
		this.selectDataEntry = DataEntryFactory.get(sde);

		// TODO: check relation for SelectDateEntry with FromDataEntry

		this.returnClassName = this.getReturnClassName();
		if (null == this.returnClassName)
			this.returnClassName = drc;

		// TODO: check relation for returnclass with SelectDataEntry

		this.fromSentence = this.fromDataEntry.getFromSentence();

		this.fields.addAll(this.selectDataEntry.getAllColumn());
		for (ListIterator<CalcColumn> it = this.fields.listIterator(); it.hasNext();) {
			if (!it.next().isQueryable())
				it.remove();
		}
	}

	protected void buildWhere() throws AptException {
		this.wherePart = WhereFactory.build(this.me, this.fromDataEntry,false,null);
		this.dynamic = (null != this.wherePart) && this.wherePart.isDynamic();
	}

	protected abstract String getDefReturnClassName() throws AptException;

	protected void prepareSQL() {
		if (this.wherePart != null)
			this.wherePart.prepare(cw);
		if (this.dynamic) {
			cw.l("StringBuilder sql = new StringBuilder();");
			cw.bL("sql.append(\"SELECT");
		} else {
			cw.bL("String sql = \"SELECT");
		}
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
		cw.w(" FROM ").ws(this.fromSentence);
		if (this.dynamic) {
			cw.el("\");");
			this.wherePart.WriteDynamic(cw);
			if (null != this.orderBy)
				cw.bL("sql.append(\" ").ws(this.orderBy).el("\");");
		} else {
			if (null != this.wherePart) {
				this.wherePart.WriteStaticTo(cw);
			}
			if (this.orderBy != null) {
				cw.w(" ").ws(this.orderBy);
			}
			cw.el("\";");
		}
	}

	protected void buildSqlParamters() {
		if (null == this.wherePart || this.wherePart.getColumns().isEmpty())
			return;
		for (ListIterator<WhereColumn> it = this.wherePart.getColumns().listIterator(); it.hasNext();) {
			it.next().writeValue();
		}
	}

	protected abstract void buildResult();

	@Override
	protected void prepare() throws AptException {
		this.dynamic = false;

		this.fromDataEntry = null;

		this.selectDataEntry = null;

		this.returnClassName = null;

		this.fields.clear();

		this.fromSentence = null;

		this.orderBy = null;

		this.wherePart = null;

	}

}
