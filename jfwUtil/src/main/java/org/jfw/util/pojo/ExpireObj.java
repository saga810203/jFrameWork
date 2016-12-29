package org.jfw.util.pojo;

public class ExpireObj<T> extends BuildTimeObj<T> {
    protected long expireTime;
    public ExpireObj(){
        super();
        this.expireTime = Long.MAX_VALUE;
    }
    public ExpireObj(T obj){
        super(obj);
        this.expireTime = Long.MAX_VALUE;
    }
    public long getExpireTime() {
        return expireTime;
    }
    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
    public void setSurvivalTime(long time){
        long max = Long.MAX_VALUE - this.getBuildTime();
        if(max>= time) this.expireTime = this.buildTime+time;
    }
    public boolean isExpired(){
        return this.expireTime < System.currentTimeMillis();
    }
}
