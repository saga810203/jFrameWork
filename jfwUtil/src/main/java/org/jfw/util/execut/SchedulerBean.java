package org.jfw.util.execut;

import java.lang.reflect.Method;
public class SchedulerBean {

	private Object bean;
	
	private String jobMethodName;
	
	private Method jobMethod;

	private String cron;
	private long fixedDelay = -1;
	private long fixedRate = -1;
	
	
	public Object getBean() {
		return bean;
	}
	public void setBean(Object bean) {
		this.bean = bean;
	}
	public String getJobMethodName() {
		return jobMethodName;
	}
	public void setJobMethodName(String jobMethodName) {
		this.jobMethodName = jobMethodName;
	}
	public Method getJobMethod() {
		return jobMethod;
	}
	public void setJobMethod(Method jobMethod) {
		this.jobMethod = jobMethod;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public long getFixedDelay() {
		return fixedDelay;
	}
	public void setFixedDelay(long fixedDelay) {
		this.fixedDelay = fixedDelay;
	}
	public long getFixedRate() {
		return fixedRate;
	}
	public void setFixedRate(long fixedRate) {
		this.fixedRate = fixedRate;
	}


}
