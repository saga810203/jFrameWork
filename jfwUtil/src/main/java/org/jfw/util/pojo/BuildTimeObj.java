package org.jfw.util.pojo;

public class BuildTimeObj<T> {
    protected long buildTime;
    protected T value;

    public BuildTimeObj() {
        this.buildTime = System.currentTimeMillis();
    }

    public BuildTimeObj(T obj) {
       
        this.buildTime = System.currentTimeMillis();
        this.value = obj;
    }

    public long getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(long buildTime) {
        if (buildTime >= 0)
            this.buildTime = buildTime;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
