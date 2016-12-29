package org.jfw.util.serializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import org.jfw.util.exception.JfwBaseException;
//TODO:  define JfwSerializeException
public interface SerializeService {
    /**
     * serialize a object
     * 
     * @param obj
     *            is not null
     * @return
     */
    byte[] serialize(Object obj);

    SerializeService serialize(Object obj, OutputStream out) throws JfwBaseException;

    <T> T deSerialize(byte[] bytes, Class<T> clazz) throws JfwBaseException;

    <T> T deSerialize(byte[] bytes, Type type) throws JfwBaseException;

    <T> T deSerialize(InputStream in, Class<T> clazz) throws JfwBaseException;

    <T> T deSerialize(InputStream in, Type type) throws JfwBaseException;

}
