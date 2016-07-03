package org.jfw.apt.orm;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.jfw.apt.CodeGenHandler;
import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodEntry;
import org.jfw.apt.orm.annotation.dao.DAO;
import org.jfw.apt.orm.daoHandler.DaoHandler;
import org.jfw.apt.orm.daoHandler.DeleteHandler;
import org.jfw.apt.orm.daoHandler.DeleteWithHandler;
import org.jfw.apt.orm.daoHandler.InsertHandler;
import org.jfw.apt.orm.daoHandler.OraclePageQueryHandler;
import org.jfw.apt.orm.daoHandler.OraclePageSelectHandler;
import org.jfw.apt.orm.daoHandler.QueryListHandler;
import org.jfw.apt.orm.daoHandler.QueryOneHandler;
import org.jfw.apt.orm.daoHandler.QueryValHandler;
import org.jfw.apt.orm.daoHandler.QueryValListHandler;
import org.jfw.apt.orm.daoHandler.SelectListHandler;
import org.jfw.apt.orm.daoHandler.SelectOneHandler;
import org.jfw.apt.orm.daoHandler.UpdateHandler;
import org.jfw.apt.orm.daoHandler.UpdateWithHandler;
import org.jfw.apt.out.model.ClassBeanDefine;

public class DataAccessObjectHandler extends CodeGenHandler {

	private static List<Class<? extends DaoHandler>> supportedClass = new ArrayList<Class<? extends DaoHandler>>();

	private static List<DaoHandler> daoHandlers;

	synchronized public static List<DaoHandler> getDaoHandlers() {
		if (null == daoHandlers) {
			daoHandlers = new ArrayList<DaoHandler>(supportedClass.size());
			for (ListIterator<Class<? extends DaoHandler>> it = supportedClass.listIterator(); it.hasNext();) {
				try {
					daoHandlers.add(it.next().newInstance());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

		}
		return daoHandlers;
	}

	@Override
	public boolean matchAnnotation() {
		return null != this.ref.getAnnotation(DAO.class);
	}

	@Override
	public void genTargetClassname() {
		this.targetClassname = this.getSourcePackageName();
		if (targetClassname.length() > 0)
			this.targetClassname = this.targetClassname + ".";
		if (this.interfaceType) {
			this.targetClassname = this.targetClassname + "impl." + this.getSourceSimpleClassname() + "Impl";
		} else {
			this.targetClassname = this.targetClassname + "extend." + this.getSourceSimpleClassname() + "Extend";
		}
	}

	@Override
	public void genSupportClassname() {
		if (!this.interfaceType)
			this.supportClassname = this.getSourceClassname();

	}

	@Override
	public void genInterFaces() {
		if (this.interfaceType)
			this.interFaces = new String[] { this.getSourceClassname() };
	}

	@Override
	public void genAnnotations() {
		this.annotationDescps = new String[] { "@org.jfw.apt.annotation.Bean(\"" + this.sourceClassname + "\")" };
	}

	@Override
	public void init() {
	}

	@Override
	public void proccess() throws AptException {
		List<? extends Element> eles = ref.getEnclosedElements();
		for (Element ele : eles) {
			for (ListIterator<DaoHandler> it = getDaoHandlers().listIterator(); it.hasNext();) {
				DaoHandler dao = it.next();
				if (dao.match(ele)) {
					dao.proccess(MethodEntry.build((ExecutableElement) ele), this);
					break;
				}
			}
		}
		out.overClass();
		ClassBeanDefine cbd = this.jfwProccess.getBeanConfig()
				.addServiceBeanByClass(this.ref.getQualifiedName().toString(), null);
		Util.buildAtuowrieProperty(cbd, this.ref);
	}

	@Override
	public void completeRound() {

	}

	@Override
	public void complete() {

	}

	static {
		supportedClass.add(QueryOneHandler.class);
		supportedClass.add(QueryListHandler.class);
		supportedClass.add(QueryValHandler.class);
		supportedClass.add(QueryValListHandler.class);
		supportedClass.add(OraclePageQueryHandler.class);
		// supportedClass.add(PostgreSQLPageQueryHandler.class);
		supportedClass.add(OraclePageSelectHandler.class);
		// supportedClass.add(PostgreSQLPageSelectHandler.class);
		supportedClass.add(SelectOneHandler.class);
		supportedClass.add(SelectListHandler.class);

		supportedClass.add(InsertHandler.class);
		supportedClass.add(UpdateHandler.class);
		supportedClass.add(DeleteHandler.class);
		supportedClass.add(UpdateWithHandler.class);
		supportedClass.add(DeleteWithHandler.class);

	}

}
