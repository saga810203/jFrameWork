package org.jfw.apt.orm;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.jfw.apt.AptHandler;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.orm.annotation.entry.ExtendTable;
import org.jfw.apt.orm.annotation.entry.ExtendView;
import org.jfw.apt.orm.annotation.entry.Table;
import org.jfw.apt.orm.annotation.entry.View;
import org.jfw.apt.orm.annotation.entry.VirtualTable;
import org.jfw.apt.orm.core.model.DataEntryFactory;
import org.jfw.apt.orm.poHandler.ExtendTableHandler;
import org.jfw.apt.orm.poHandler.ExtendViewHandler;
import org.jfw.apt.orm.poHandler.TableHandler;
import org.jfw.apt.orm.poHandler.ViewHandler;
import org.jfw.apt.orm.poHandler.VirtualHandler;

public class PersistentObjectHandler extends AptHandler {

	public static List<Class<? extends Annotation>> supportedClass = Arrays.asList(VirtualTable.class, Table.class,
			ExtendTable.class, View.class, ExtendView.class);

	protected TypeElement ref;

	protected Object annotationObj;
	
	protected Class<? extends Annotation> currentAnnotationClass;

	protected int kind;

	@Override
	public void init() {
		DataEntryFactory.load();
	}

	@Override
	public boolean match(Element ele) {
		if (ele.getEnclosingElement().getKind() != ElementKind.PACKAGE)
			return false;
		ElementKind kind = ele.getKind();
		if ((ElementKind.INTERFACE != kind) && (ElementKind.CLASS != kind))
			return false;
		this.ref = (TypeElement) ele;
		this.annotationObj = null;

		for (Class<? extends Annotation> cls : supportedClass) {
			this.annotationObj = this.ref.getAnnotation(cls);
			if (null != this.annotationObj){
				this.currentAnnotationClass = cls;
				break;}
		}
		return null != this.annotationObj;
	}

	@Override
	public void proccess() throws AptException {
		if (this.currentAnnotationClass.equals(Table.class)) {
			new TableHandler().handle(this.ref);

		} else if (this.currentAnnotationClass.equals(VirtualTable.class)) {
			new VirtualHandler().handle(this.ref);

		} else if (this.currentAnnotationClass.equals(ExtendTable.class)) {
			new ExtendTableHandler().handle(this.ref);

		} else if (this.currentAnnotationClass.equals(View.class)) {
			new ViewHandler().handle(this.ref);
		} else if (this.currentAnnotationClass.equals(ExtendView.class)) {
			new ExtendViewHandler().handle(this.ref);
		}
	}

	@Override
	public void completeRound() throws AptException {
		DataEntryFactory.checkCachedEntries();
		DataEntryFactory.checkCachedTable();
	}

	@Override
	public void complete() throws AptException {
		String val;
		try {
			val = DataEntryFactory.getEntriesAsString();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("save dataEntry error", e);
		}
		if(val!=null && val.length()>0) this.jfwProccess.saveResourceFile(DataEntryFactory.FILE_NAME, val);
		val = DataEntryFactory.getDDL();
		if(val!=null && val.length()>0) this.jfwProccess.saveResourceFile("jfw_table.sql", val);
		
		
		DataEntryFactory.cleanCached();
	}

}
