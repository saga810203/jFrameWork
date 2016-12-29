package org.jfw.util.pojo;

public class Entity<K,V> {
    protected K key;
    protected V value;
    
    public Entity(){}
    public Entity(K key,V value){
        this.key = key;
        this.value = value;
    }
    public K getKey() {
        return key;
    }
    public void setKey(K key) {
        this.key = key;
    }
    public V getValue() {
        return value;
    }
    public void setValue(V value) {
        this.value = value;
    }
}
