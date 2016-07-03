package org.jfw.apt.orm.daoHandler;

import java.util.List;
import java.util.ListIterator;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.MirroredTypeException;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodEntry;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.model.TypeName;
import org.jfw.apt.orm.DataAccessObjectHandler;
import org.jfw.apt.orm.annotation.dao.Batch;
import org.jfw.apt.orm.annotation.dao.DAO;
import org.jfw.apt.orm.annotation.dao.Dynamic;
import org.jfw.apt.annotation.Nullable;
import org.jfw.apt.orm.annotation.dao.method.From;
import org.jfw.apt.orm.annotation.dao.method.Return;
import org.jfw.apt.orm.annotation.dao.method.Select;
import org.jfw.apt.orm.core.model.DataEntryFactory;
import org.jfw.apt.out.ClassWriter;

public abstract class BaseDaoHandler implements DaoHandler {
	public static final String LIST_PREFIX = "java.util.List<";
	public static final int LIST_RCN_INDEX = LIST_PREFIX.length();
	public static final String LIST_SUFFIX = ">";
	public static final int LIST_MIN_INDEX = LIST_RCN_INDEX + 1;
	
	protected ExecutableElement ref;
	protected DataAccessObjectHandler daoh;
	protected MethodEntry me;
	protected ClassWriter cw;
	protected List<MethodParamEntry> params ;

	public String getFromClassName() throws AptException {
		From from = ref.getAnnotation(From.class);
		if (null == from)
			return null;
		String dn;
		try {
			dn = from.value().getName();
		} catch (MirroredTypeException e) {
			dn = TypeName.get(e.getTypeMirror()).toString();
		}
		DataEntryFactory.checkFromable(dn, ref);
		return dn;		
	}

	
	public String getSelectClassName() throws AptException {
		Select select = ref.getAnnotation(Select.class);
		if (null == select)
			return null;
		String dn ;
		try {
			dn = select.value().getName();
		} catch (MirroredTypeException e) {
			dn = TypeName.get(e.getTypeMirror()).toString();
		}
		DataEntryFactory.checkSelectable(dn, ref);
		return dn;
	}


	@Override
	public void proccess(MethodEntry me, DataAccessObjectHandler daoh) throws AptException {
		this.init(me, daoh);
		this.checkJdbc();
		this.beginMethod();
		this.prepare();
		this.writeMethodContent();	
		this.endMethod();
	}
	
	protected abstract void prepare() throws AptException;
	protected abstract void writeMethodContent() throws AptException;

	public String getReturnClassName(){
		Return r = this.ref.getAnnotation(Return.class);
		if (null == r)
			return null;

		try {
			return r.value().getName();
		} catch (MirroredTypeException e) {
			return TypeName.get(e.getTypeMirror()).toString();
		}
	}
	
	public void init(MethodEntry me, DataAccessObjectHandler daoh){
		this.ref = me.getRef();
		this.me = me;
		this.params = me.getParams();
		this.daoh = daoh;
		this.cw = daoh.getOut();
	}

	public void beginMethod(){
		cw.beginMethod();
		cw.l("@Override");
		cw.bL("public ").w(this.me.getReturnType()).w(" ").w(me.getName()).w("(");
		int i ;
		for(i = 0 ; i < me.getParams().size() ; ++i){
			MethodParamEntry mpe = me.getParams().get(i);
			cw.w(i==0?"":",");
			cw.w(mpe.getTypeName()).w(" ").w(mpe.getName());
		}
		cw.w(") ");
		i =0;
		for(ListIterator<String> it = me.getExceptions().listIterator(); it.hasNext();)
		{
			++i;			
			cw.w(i==1?"throws ":",").w(it.next());
		}
		cw.el("{");
	}
	public void endMethod(){
		cw.l("}");
	}
	protected void checkJdbc() throws AptException{
		if(this.params.isEmpty()|| !this.params.get(0).getTypeName().equals("java.sql.Connection")|| !this.params.get(0).getName().equals("con"))
			throw new AptException(ref,"this method must be has param & the first param type is java.sql.Connection name is con");
		if(this.me.getExceptions().isEmpty() || !this.me.getExceptions().contains("java.sql.SQLException")){
			throw new AptException(ref,"this method must be throws java.sql.SQLException");
		}
		if(this.me.getReturnType().equals("void"))throw new AptException(ref,"ref must be have return type");
	}
	
	public static boolean withDynamic(Element ele){
		return null!= ele.getAnnotation(Dynamic.class);		
	}
	public static boolean withBatch(Element ele){
		return null != ele.getAnnotation(Batch.class);
	}
	public static boolean withNullable(Element ele){
		return null!= ele.getAnnotation(Nullable.class);
	}
	public static boolean withDAO(Element ele){
		return null!= ele.getAnnotation(DAO.class);
	}
}
