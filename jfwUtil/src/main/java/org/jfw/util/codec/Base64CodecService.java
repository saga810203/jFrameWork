package org.jfw.util.codec;

import java.nio.charset.Charset;

import org.jfw.util.ConstData;

public class Base64CodecService implements CodecService {
    public static final String CODEC_TYPE="BASE64";
    @Override
    public byte[] encode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return bytes;
        }
        return (new Base64(false)).encode(bytes);
    }

    @Override
    public byte[] decode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return bytes;
        }
        return (new Base64(false)).decode(bytes);
    }

    @Override
    public byte[] encode(byte[] bytes, byte[] key) {
       return this.encode(bytes);
    }

    @Override
    public byte[] decode(byte[] bytes, byte[] key) {
       return this.decode(bytes);
    }

    @Override
    public String encode(String s) {
        return new String(this.encode(s.getBytes(ConstData.UTF8)),ConstData.UTF8);
    }

    @Override
    public String decode(String s) {
        return new String(this.decode(s.getBytes(ConstData.UTF8)),ConstData.UTF8);
    }

    @Override
    public String encode(String s, byte[] key) {
        return this.encode(s);
    }

    @Override
    public String decode(String s, byte[] key) {
      return this.decode(s);
    }

    @Override
    public String encode(String s, String enc) {
        Charset c = Charset.forName(enc);
        return new String(this.encode(s.getBytes(c)),c);
    }

    @Override
    public String decode(String s, String enc) {
        Charset c = Charset.forName(enc);
        return new String(this.decode(s.getBytes(c)),c);
    }

    @Override
    public String encode(String s, byte[] key, String enc) {
      return this.encode(s, enc);
    }

    @Override
    public String decode(String s, byte[] key, String enc) {
        return this.decode(s, enc);
    }

}
