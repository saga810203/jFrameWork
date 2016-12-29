package org.jfw.util.serializer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

import org.jfw.util.ConstData;
import org.jfw.util.exception.JfwBaseException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class DefaultJsonSerializeService implements SerializeService{

    private final static Gson gson = new Gson();;
    @Override
    public byte[] serialize(Object obj) {
       if(obj==null) throw new NullPointerException();
       return gson.toJson(obj).getBytes(ConstData.UTF8);
    }

    @Override
    public SerializeService serialize(Object obj, OutputStream out) throws JfwBaseException {
      try {
        gson.toJson(obj, new OutputStreamWriter(out, ConstData.UTF8));
    } catch (JsonIOException e) {
        //TODO:  define JfwSerializeException
        throw new JfwBaseException(e);    }
      return this;
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> clazz) throws JfwBaseException {
        try {
            return gson.fromJson(new String(bytes,ConstData.UTF8), clazz);
        } catch (JsonSyntaxException e) {
            //TODO:  define JfwSerializeException
            throw new JfwBaseException(e);    
        }
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Type type) throws JfwBaseException {
        try {
            return gson.fromJson(new String(bytes,ConstData.UTF8), type);
        } catch (JsonSyntaxException e) {
            //TODO:  define JfwSerializeException
            throw new JfwBaseException(e);    
        }
    }

    @Override
    public <T> T deSerialize(InputStream in, Class<T> clazz) throws JfwBaseException {
        try {
            return gson.fromJson(new InputStreamReader(in,ConstData.UTF8), clazz);
        } catch (JsonSyntaxException e) {
            //TODO:  define JfwSerializeException
            throw new JfwBaseException(e);    
        }
    }

    @Override
    public <T> T deSerialize(InputStream in, Type type) throws JfwBaseException {
        try {
            return gson.fromJson(new InputStreamReader(in,ConstData.UTF8), type);
        } catch (JsonSyntaxException e) {
            //TODO:  define JfwSerializeException
            throw new JfwBaseException(e);    
        }
    }
}
