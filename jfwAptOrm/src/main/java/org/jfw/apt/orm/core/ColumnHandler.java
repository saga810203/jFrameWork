package org.jfw.apt.orm.core;

import org.jfw.apt.out.ClassWriter;

public interface ColumnHandler {
	//对应的Bean属性类
		String supportsClass();
		//read ResultSet rs;
		void readValue(ClassWriter cw,String beforCode,String afterCode,int colIndex,boolean dbNullable);

	    //write   PreparedStatement ps;
		//        int _index = 1;
		ColumnWriterCache init(String valueEl,boolean userTempalteVar,boolean vlaueNullable,ClassWriter cw);
		void prepare(ColumnWriterCache cache);	
		/*
		 * sb.append("if(");
		 * OrmHandler.checkNull(sb);
		 * sb.append("){");
		 * OrmHandler.writeNullValue(sb);
		 * sb.append("}else{");
		 * OrmHandler.writeValue(sb);
		 * sb.append("}");	 * 
		 */	
		String getCheckNullVal(ColumnWriterCache cache);	
		//使用 "_index++" 局部变量 
		void writeValue(ColumnWriterCache cache,boolean dynamicValue);
		boolean isReplaceResource(ColumnWriterCache cache);
		void replaceResource(ColumnWriterCache cache);	
}
