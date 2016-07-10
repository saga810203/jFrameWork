package org.jfw.util.exception;

import java.util.HashMap;
import java.util.Map;

public class JfwBaseException extends Exception {
	
	/**
	 * 0: un define execption
	 * 
	 * 1: no user login
	 * 
	 * 
	 * 100 Insufficient authority
	 * 
	 * 200 database error
	 * 201 select single column no found : select count(1) from dual  not found,
	 * 
	 */

	private static final Map<Integer,String> reasons = new HashMap<Integer,String>();
	
	private static String convert(int code){
		String result = reasons .get(code);
		if(result == null) return "UnDescription  Exception";
		return result;
	}
	
	private static final long serialVersionUID = 6512669010239097709L;
	
	private int code;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public JfwBaseException() {
		super();
		this.code = 0;
	}
	public JfwBaseException(int code) {
		super(convert(code));
		this.code = code;
	}

	public JfwBaseException(String message, Throwable cause) {
		super(message, cause);
		this.code = 0;
	}
	public JfwBaseException(int code,String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}
	public JfwBaseException(String message) {
		super(message);
		this.code = 0;
	}
	public JfwBaseException(int code,String message) {
		super(message);
		this.code = code;
	}
	public JfwBaseException(int code ,Throwable cause) {
		super(cause);
		this.code = code;
	}
	public JfwBaseException(Throwable cause) {
		super(cause);
		this.code = 0;
	}
	
	static{
		reasons.put(0,"UnDefine Exception");
		reasons.put(1, "No Logined");		
		reasons.put(10,"paramter null or empty");
		reasons.put(11,"paramter format error");
		reasons.put(90,"resource not exists");
		reasons.put(91,"load resource error");
		reasons.put(92,"save resource error");
		reasons.put(93,"delete resource error");
		reasons.put(94,"transfer resource error");

		reasons.put(100, "Insufficient authority");
		reasons.put(201, "NO FOUND DATA WITH JDBC"); //Select One 
		reasons.put(202,"EMPTY SET SENTENCE IN UPDATE WITH JDBC");
		
		reasons.put(301, "upload file size is larger than setting");
		reasons.put(302, "upload file type unsupported with setting");
		reasons.put(303, "upload file count is larger than setting");
		
	}

}
