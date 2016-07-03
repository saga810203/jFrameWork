package org.jfw.apt.orm.core;

import java.util.HashMap;
import java.util.Map;

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

@SuppressWarnings("unchecked")
public final class ColumnHandlerFactory {

	private final static Class<?>[] handlerClasses = {
			BigDecimalHandler.class, BlobHandler.class, BooleanHandler.class, ByteHandler.class, DoubleHandler.class,
			FixLenStringHandler.class, FloatHandler.class, IntHandler.class, LongHandler.class, ShortHandler.class,
			StreamBlobHandler.class, StringHandler.class, WBooleanHandler.class, WByteHandler.class,
			WDoubleHandler.class, WFloatHandler.class, WIntHandler.class, WLongHandler.class, WShortHandler.class};
	
	private final static Map<Class<? extends ColumnHandler>,Class<? extends ColumnHandler>> relatedHandlers = new HashMap<Class<? extends ColumnHandler>,Class<? extends ColumnHandler>>();

	private final static Map<Class<? extends ColumnHandler>, ColumnHandler> handlers = new HashMap<Class<? extends ColumnHandler>, ColumnHandler>();

	public static ColumnHandler get(Class<? extends ColumnHandler> cls)
	{
		ColumnHandler ch = handlers.get(cls);
		if(ch == null) throw new RuntimeException("unSupported ColumnHandler:"+cls.getName());
		return ch;
	}
	
	public static String supportedType(Class<? extends ColumnHandler> cls){
		return get(cls).supportsClass();
	}
	
	public static Class<? extends ColumnHandler> getRelatedHandler(Class<? extends ColumnHandler> cls){
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
		
		
		
		try{
		 for(Class<?> cls:handlerClasses){
			 ColumnHandler ch =(ColumnHandler)(cls.newInstance());
			 handlers.put((Class<? extends ColumnHandler>)cls, ch);
		 }
		}catch(Exception e){
			
		}
		
	}

}
