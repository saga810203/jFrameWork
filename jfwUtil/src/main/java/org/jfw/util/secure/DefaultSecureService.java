package org.jfw.util.secure;

import java.lang.reflect.Type;

import org.jfw.util.ConstData;
import org.jfw.util.codec.CodecService;
import org.jfw.util.codec.DesCodecService;
import org.jfw.util.codec.UrlSafeBase64CodecService;
import org.jfw.util.exception.JfwBaseException;
import org.jfw.util.serializer.DefaultJsonSerializeService;
import org.jfw.util.serializer.SerializeService;

public class DefaultSecureService implements SecureService {
    private CodecService base64Codec  = new UrlSafeBase64CodecService();
    private CodecService secureCodec = new DesCodecService();
    private SerializeService serializeService = new DefaultJsonSerializeService();
    

    public SerializeService getSerializeService() {
        return serializeService;
    }

    public void setSerializeService(SerializeService serializeService) {
        this.serializeService = serializeService;
    }

    public CodecService getBase64Codec() {
        return base64Codec;
    }

    public void setBase64Codec(CodecService base64Codec) {
        this.base64Codec = base64Codec;
    }

    public CodecService getSecureCodec() {
        return secureCodec;
    }

    public void setSecureCodec(CodecService secureCodec) {
        this.secureCodec = secureCodec;
    }

    @Override
    public byte[] serialize(Object obj) throws JfwBaseException {
       return this.secureCodec.encode(this.serializeService.serialize(obj));
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> clazz) throws JfwBaseException {
        return this.serializeService.deSerialize(this.secureCodec.decode(bytes), clazz);
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Type type) throws JfwBaseException {
        return this.serializeService.deSerialize(this.secureCodec.decode(bytes), type);
    }

    @Override
    public String serializeToString(Object obj) throws JfwBaseException {
       return new String(this.base64Codec.encode(this.serialize(obj)),ConstData.UTF8);
    }

    @Override
    public <T> T deSerialize(String s, Class<T> clazz) throws JfwBaseException {
       return this.deSerialize(s.getBytes(ConstData.UTF8), clazz);
    }

    @Override
    public <T> T deSerialize(String s, Type type) throws JfwBaseException {
        return this.deSerialize(s.getBytes(ConstData.UTF8), type);
    }

}
