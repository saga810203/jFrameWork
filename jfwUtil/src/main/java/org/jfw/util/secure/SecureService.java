package org.jfw.util.secure;

import java.lang.reflect.Type;

import org.jfw.util.exception.JfwBaseException;

public interface SecureService {
    byte[] serialize(Object obj) throws JfwBaseException;
    <T> T deSerialize(byte[] bytes,Class<T> clazz) throws JfwBaseException;
    <T> T deSerialize(byte[] bytes,Type type)throws JfwBaseException;
    String serializeToString(Object obj) throws JfwBaseException;
    <T> T deSerialize(String s,Class<T> clazz) throws JfwBaseException;
    <T> T deSerialize(String s,Type type)throws JfwBaseException;    
}
