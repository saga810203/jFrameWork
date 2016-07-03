package org.jfw.apt.web.handlers.viewImpl;

import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;

public abstract class ViewUtil {
	public static void printJSONException(AptWebHandler aptWebHandler, ClassWriter cw){
		//e:Exception
		//res:HttpServletResponse
		//out: jsp.out
		cw.l("org.jfw.util.json.JsonService.write(e,out);");		
	}
	public static void printJSONWithValue(AptWebHandler aptWebHandler, ClassWriter cw){
		//res:HttpServletResponse
		//out: jsp.out
		//result:Object
		 cw.l("out.write(\"{\\\"success\\\":true,\\\"data\\\":\");");
         cw.l("org.jfw.util.json.JsonService.toJson(result,out);");
         cw.l("out.write(\"}\");");      
	}
	public static void printJSONSuccess(AptWebHandler aptWebHandler, ClassWriter cw){
		//res:HttpServletResponse
		//out: jsp.out
		 cw.l("out.write(\"{\\\"success\\\":true}\");"); 	
	}
	
	public static void printJSPException(AptWebHandler aptWebHandler, ClassWriter cw){
		//e:Exception
		//res:HttpServletResponse
		// FIXME:Handler Error
		cw.l("\r\n//TODO:impl application logic handler in  org.jfw.apt.model.web.handlers.viewHandler.JspHandler  \r\n");
		cw.l("res.sendError(500);\r\n");
	}
}

