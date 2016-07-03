package org.jfw.apt.out;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaFileObject;

import org.jfw.apt.JfwProccess;
import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;

public class ClassWriter {
	protected JfwProccess jfwProccess;
	private StringBuilder sb;

	private int indent;

	private int methodVarIndex;
	private int classVarIndex;
	private Map<Object, Object> classScope = new HashMap<Object, Object>();
	private Map<Object, Object> methodScope = new HashMap<Object, Object>();
	private String classname;

	public JfwProccess getJfwProccess() {
		return jfwProccess;
	}

	public void setJfwProccess(JfwProccess jfwProccess) {
		this.jfwProccess = jfwProccess;
	}

	public int getMethodVarIndex() {
		return methodVarIndex;
	}

	public void setMethodVarIndex(int methodVarIndex) {
		this.methodVarIndex = methodVarIndex;
	}

	public Map<Object, Object> getClassScope() {
		return classScope;
	}

	public Map<Object, Object> getMethodScope() {
		return methodScope;
	}

	public String getClassname() {
		return classname;
	}

	public ClassWriter(JfwProccess jfwProccess, String classname, String extend, String[] impls,
			String[] annotationDescps) {
		this.classname = classname;
		this.jfwProccess = jfwProccess;
		this.indent = 0;
		this.methodVarIndex = 1;
		this.classVarIndex = 1;
		this.sb = new StringBuilder();
		int index = classname.lastIndexOf('.');
		if (index > 0) {
			sb.append("package ").append(classname.substring(0, index)).append(";\r\n\r\n");
		}
		if (annotationDescps != null) {
			for (String s : annotationDescps)
				sb.append(s).append("\r\n");
		}
		sb.append("public class ").append(index > 0 ? classname.substring(index + 1) : classname);
		if (extend != null) {
			sb.append(" extends ").append(extend);
		}
		if (impls != null && impls.length > 0) {
			for (int i = 0; i < impls.length; ++i) {
				if (i == 0)
					sb.append(" implements ");
				else
					sb.append(",");
				sb.append(impls[i]);
			}
		}
		sb.append("{\r\n");
		this.indent = 1;
	}

	public String getMethodTempVarName() {
		String result = "_m_" + this.methodVarIndex;
		++this.methodVarIndex;
		return result;
	}

	public String getClassTempVarName() {
		String result = "_c_" + this.classVarIndex;
		++this.classVarIndex;
		return result;
	}

	private ClassWriter writeIndent() {
		for (int i = 0; i < this.indent; ++i) {
			sb.append("    ");
		}
		return this;
	}
/**
 * writeLine
 * @param line
 * @return
 */
	public ClassWriter l(String line) {
		line = line.trim();
		if (line.length() != 0) {
			if (line.startsWith("}"))
				--this.indent;
			this.writeIndent();
			sb.append(line);
			if (line.endsWith("{"))
				++this.indent;
			sb.append("\r\n");
		}
		return this;
	}
/**
 * beginLine
 * @param str
 * @return
 */
	public ClassWriter bL(String str) {
		if (str.startsWith("}"))
			--this.indent;
		this.writeIndent();
		sb.append(str);
		return this;
	}

	/*
	 * writeContent
	 */
	public ClassWriter w(String content) {
		if (null != content)
			sb.append(content);
		return this;
	}

	public ClassWriter w(int content) {

		sb.append(content);
		return this;
	}

	/*
	 * writeForString
	 */
	public ClassWriter ws(String s) {
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if (c == '\\' || c == '"')
				sb.append("\\");
			sb.append(c);
		}
		return this;
	}
/*
 * endLine
 */
	public ClassWriter el(String str) {
		if (str.endsWith("{"))
			++this.indent;
		sb.append(str).append("\r\n");
		return this;
	}

	public ClassWriter writeEndBrace() {
		--this.indent;
		sb.append("}\r\n");
		return this;
	}

	public ClassWriter writeBeginBrace() {
		sb.append("{\r\n");
		++this.indent;
		return this;
	}

	public ClassWriter beginIf() {
		this.writeIndent();
		sb.append("if(");
		return this;
	}

	public ClassWriter endIf() {
		sb.append("){\r\n");
		++this.indent;
		return this;
	}

	public ClassWriter writeElse() {
		this.l("}else{");
		return this;
	}

	public ClassWriter beginElseIf() {
		this.bL("}else if(");
		return this;
	}

	public ClassWriter writeTry() {
		this.writeIndent();
		sb.append("try{\r\n");
		++this.indent;
		return this;
	}

	public ClassWriter writeCatch(String errType, String errVar) {
		--this.indent;
		this.writeIndent();
		sb.append("} catch(").append(errType).append(" ").append(errVar).append("){\r\n");
		++this.indent;
		return this;
	}

	public ClassWriter writeFinally() {
		--this.indent;
		this.writeIndent();
		sb.append("} finally {\r\n");
		++this.indent;
		return this;
	}

	public ClassWriter writeClosing(String errVar) {
		this.writeIndent();
		sb.append("try{").append(errVar).append(".close();}catch(Exception ").append(this.getMethodTempVarName())
				.append("){}\r\n");
		return this;
	}

	public ClassWriter writeField(String fieldDescp) {
		this.indent = 1;
		this.writeIndent();
		sb.append(fieldDescp).append("\r\n");
		return this;
	}

	public ClassWriter beginMethod() {
		this.indent = 1;
		this.methodVarIndex = 1;
		this.methodScope.clear();
		return this;
	}
	
	public void writeSetter(String beanName,String attrName,String paramName){
		this.bL(beanName).w(".").w(Util.buildSetter(attrName)).w("(").w(paramName).el(");");
	}

	public void overClass() throws AptException {
		sb.append("}");
		try {
			JavaFileObject jfo = this.jfwProccess.getFiler().createSourceFile(this.classname,
					this.jfwProccess.getCurrentElement());
			Writer w = jfo.openWriter();
			try {
				w.write(sb.toString());
			} finally {
				w.close();
			}
		} catch (IOException e) {
			throw new AptException(jfwProccess.getCurrentElement(),
					"write java sorce file(" + this.classname + ") error:" + e.getMessage());
		}
	}

}
