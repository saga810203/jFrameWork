package org.jfw.util.exception;

public class JfwInvalidParamException extends JfwBaseException{
	private static final long serialVersionUID = -8528845516609677938L;
private String serviceName;
   private String paramName;
   
   
   public JfwInvalidParamException(String serviceName,String paramName){
	   super(10," invalid parameter["+paramName+"] in service["+serviceName+"]");
	   this.paramName = paramName;
	   this.serviceName = serviceName;
   }
   public JfwInvalidParamException(String serviceName,String paramName,Throwable e){
	   super(10," invalid parameter["+paramName+"] in service["+serviceName+"]",e);
	   this.serviceName = serviceName;
	   this.paramName = paramName;
   }
   public String getParamName(){
	   return this.paramName;
   }
   public String getServiceName(){
	   return this.serviceName;
   }
}
