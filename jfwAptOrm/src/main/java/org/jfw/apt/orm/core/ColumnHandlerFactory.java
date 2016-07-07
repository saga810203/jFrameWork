package org.jfw.apt.orm.core;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.TypeName;
import org.jfw.apt.orm.core.defaultImpl.BigDecimalHandler;
import org.jfw.apt.orm.core.defaultImpl.BlobHandler;
import org.jfw.apt.orm.core.defaultImpl.BooleanHandler;
import org.jfw.apt.orm.core.defaultImpl.ByteHandler;
import org.jfw.apt.orm.core.defaultImpl.DoubleHandler;
import org.jfw.apt.orm.core.defaultImpl.FixLenStringHandler;
import org.jfw.apt.orm.core.defaultImpl.FloatHandler;
import org.jfw.apt.orm.core.defaultImpl.IntHandler;
import org.jfw.apt.orm.core.defaultImpl.LongHandler;
import org.jfw.apt.orm.core.defaultImpl.ShortHandler;
import org.jfw.apt.orm.core.defaultImpl.StreamBlobHandler;
import org.jfw.apt.orm.core.defaultImpl.StringHandler;
import org.jfw.apt.orm.core.defaultImpl.WBooleanHandler;
import org.jfw.apt.orm.core.defaultImpl.WByteHandler;
import org.jfw.apt.orm.core.defaultImpl.WDoubleHandler;
import org.jfw.apt.orm.core.defaultImpl.WFloatHandler;
import org.jfw.apt.orm.core.defaultImpl.WIntHandler;
import org.jfw.apt.orm.core.defaultImpl.WLongHandler;
import org.jfw.apt.orm.core.defaultImpl.WShortHandler;
import org.jfw.apt.orm.core.enums.DE;

@SuppressWarnings("unchecked")
public final class ColumnHandlerFactory {

	private final static Class<?>[] handlerClasses = { BigDecimalHandler.class, BlobHandler.class, BooleanHandler.class,
			ByteHandler.class, DoubleHandler.class, FixLenStringHandler.class, FloatHandler.class, IntHandler.class,
			LongHandler.class, ShortHandler.class, StreamBlobHandler.class, StringHandler.class, WBooleanHandler.class,
			WByteHandler.class, WDoubleHandler.class, WFloatHandler.class, WIntHandler.class, WLongHandler.class,
			WShortHandler.class };

	private final static Map<Class<? extends ColumnHandler>, Class<? extends ColumnHandler>> relatedHandlers = new HashMap<Class<? extends ColumnHandler>, Class<? extends ColumnHandler>>();

	private final static Map<Class<? extends ColumnHandler>, ColumnHandler> handlers = new HashMap<Class<? extends ColumnHandler>, ColumnHandler>();

	
	public static void register(Class<? extends ColumnHandler> key, ColumnHandler handler){
		handlers.put(key, handler);
	}
	public static ColumnHandler get(Class<? extends ColumnHandler> cls) {
		ColumnHandler ch = handlers.get(cls);
		if (ch == null)
			throw new RuntimeException("unSupported ColumnHandler:" + cls.getName());
		return ch;
	}

	public static String supportedType(Class<? extends ColumnHandler> cls) {
		return get(cls).supportsClass();
	}
	public static Class<? extends ColumnHandler> getHandlerClass(org.jfw.apt.orm.annotation.entry.CalcColumn annObj,
			Element ref) throws AptException {
		Class<? extends ColumnHandler> cls;
		try {
			DE de = annObj.value();
			if (!de.equals(DE.invalid_de))
				cls = de.getHandlerClass();
			else
				cls = annObj.handlerClass();
		} catch (MirroredTypeException e) {

			String cn = TypeName.get(e.getTypeMirror()).toString();
			try {
				cls = (Class<? extends ColumnHandler>) Class.forName(cn);
			} catch (ClassNotFoundException e1) {
				throw new AptException(ref, "Class[" + cn + "] must complied befor this operation");
			}
		}
		return cls;
	}
	public static Class<? extends ColumnHandler> getHandlerClass(org.jfw.apt.orm.annotation.entry.Column annObj,
			Element ref) throws AptException {
		Class<? extends ColumnHandler> cls;
		try {
			DE de = annObj.value();
			if (!de.equals(DE.invalid_de))
				cls = de.getHandlerClass();
			else
				cls = annObj.handlerClass();
		} catch (MirroredTypeException e) {
			String cn = TypeName.get(e.getTypeMirror()).toString();
			try {
				cls = (Class<? extends ColumnHandler>) Class.forName(cn);
			} catch (ClassNotFoundException e1) {
				throw new AptException(ref, "Class[" + cn + "] must complied befor this operation");
			}
		}
		return cls;
	}

	public static ColumnHandler get(org.jfw.apt.orm.annotation.entry.Column annObj, Element ref) throws AptException {
			return get(getHandlerClass(annObj,ref));
	}

	public static ColumnHandler get(org.jfw.apt.orm.annotation.entry.CalcColumn annObj, Element ref)
			throws AptException {
		return get(getHandlerClass(annObj,ref));
	}
	public static Class<? extends ColumnHandler> getHandlerClass(org.jfw.apt.orm.annotation.dao.Column annObj,
			Element ref) throws AptException {
		Class<? extends ColumnHandler> cls;
		try {
			cls = annObj.handlerClass();
		} catch (MirroredTypeException e) {

			String cn = TypeName.get(e.getTypeMirror()).toString();
			try {
				cls = (Class<? extends ColumnHandler>) Class.forName(cn);
			} catch (ClassNotFoundException e1) {
				throw new AptException(ref, "Class[" + cn + "] must complied befor this operation");
			}
		}
		return cls;
	}
	public static ColumnHandler get(org.jfw.apt.orm.annotation.dao.Column annObj, Element ref) throws AptException {
		return get(getHandlerClass(annObj,ref));
	}

	public static String supportedType(org.jfw.apt.orm.annotation.entry.Column annObj, Element ele)
			throws AptException {
		return get(annObj, ele).supportsClass();
	}

	public static String supportedType(org.jfw.apt.orm.annotation.entry.CalcColumn annObj, Element ele)
			throws AptException {
		return get(annObj, ele).supportsClass();
	}

	public static String supportedType(org.jfw.apt.orm.annotation.dao.Column annObj, Element ele) throws AptException {
		return get(annObj, ele).supportsClass();
	}

	public static Class<? extends ColumnHandler> getRelatedHandler(Class<? extends ColumnHandler> cls) {
		return relatedHandlers.get(cls);

	}

	private ColumnHandlerFactory() {
	}

	static {

		relatedHandlers.put(BooleanHandler.class, WBooleanHandler.class);
		relatedHandlers.put(WBooleanHandler.class, BooleanHandler.class);

		relatedHandlers.put(ByteHandler.class, WByteHandler.class);
		relatedHandlers.put(WByteHandler.class, ByteHandler.class);

		relatedHandlers.put(ShortHandler.class, WShortHandler.class);
		relatedHandlers.put(WShortHandler.class, ShortHandler.class);

		relatedHandlers.put(IntHandler.class, WIntHandler.class);
		relatedHandlers.put(WIntHandler.class, IntHandler.class);

		relatedHandlers.put(LongHandler.class, WLongHandler.class);
		relatedHandlers.put(WLongHandler.class, LongHandler.class);

		relatedHandlers.put(FloatHandler.class, WFloatHandler.class);
		relatedHandlers.put(WFloatHandler.class, FloatHandler.class);

		relatedHandlers.put(DoubleHandler.class, WDoubleHandler.class);
		relatedHandlers.put(WDoubleHandler.class, DoubleHandler.class);

		try {
			for (Class<?> cls : handlerClasses) {
				ColumnHandler ch = (ColumnHandler) (cls.newInstance());
				handlers.put((Class<? extends ColumnHandler>) cls, ch);
			}
		} catch (Exception e) {

		}

	}

}
