package org.jfw.web.servlet.fileupload.cached;

import java.io.File;

public class CachedUploadServletConfig {
	private  boolean cacheByMemory = true;
	
	private File cachePath = null;
	
	private int countLimit = 1;
	private int defaultCountLimit = 1;
	private long sizeLimit = -1;
	private long defaultSizeLimit = -1;
	private CachedItemValidator cachedItemValidator;
	private long cleanInteval = 5* 60 *1000;
	private long holdTime = 30*60*1000;
	
	
	public long getHoldTime() {
		return holdTime;
	}

	public void setHoldTime(long holdTime) {
		this.holdTime = holdTime;
	}

	public long getCleanInteval() {
		return cleanInteval;
	}

	public void setCleanInteval(long cleanInteval) {
		this.cleanInteval = cleanInteval;
	}

	public CachedItemValidator getCachedItemValidator() {
		return cachedItemValidator;
	}

	public void setCachedItemValidator(CachedItemValidator cachedItemValidator) {
		this.cachedItemValidator = cachedItemValidator;
	}

	private String fileSuffix = null;

	public boolean isCacheByMemory() {
		return cacheByMemory;
	}

	public void setCacheByMemory(boolean cacheByMemory) {
		this.cacheByMemory = cacheByMemory;
	}

	public File getCachePath() {
		return cachePath;
	}

	public void setCachePath(File cachePath) {
		this.cachePath = cachePath;
	}

	public int getCountLimit() {
		return countLimit;
	}

	public void setCountLimit(int countLimit) {
		this.countLimit = countLimit;
	}

	public int getDefaultCountLimit() {
		return defaultCountLimit;
	}

	public void setDefaultCountLimit(int defaultCountLimit) {
		this.defaultCountLimit = defaultCountLimit;
	}

	public long getSizeLimit() {
		return sizeLimit;
	}

	public void setSizeLimit(long sizeLimit) {
		this.sizeLimit = sizeLimit;
	}

	public long getDefaultSizeLimit() {
		return defaultSizeLimit;
	}

	public void setDefaultSizeLimit(long defaultSizeLimit) {
		this.defaultSizeLimit = defaultSizeLimit;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}

	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}
	
	
	public long getRealSizeLimit(Long paramLimit){
		if(this.sizeLimit>0) return this.sizeLimit;
		if(paramLimit!=null) return paramLimit.longValue();
		return this.defaultSizeLimit;		
	}
	
	public int getRealCountLimit(Integer paramLimit){
		if(this.countLimit>0) return this.countLimit;
		if(paramLimit!=null) return paramLimit.intValue();
		return this.defaultCountLimit;	
	}
}
