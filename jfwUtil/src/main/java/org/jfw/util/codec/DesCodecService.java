package org.jfw.util.codec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.jfw.util.StringUtil;

public class DesCodecService implements CodecService {

    public static final byte[] DEFAULT_KEY_BYTES = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72 };
    public static final String CODEC_TYPE="DES";
    

    public static DESKeySpec createKey(byte[] key) throws JfwCodecException{
        try {
            return  new DESKeySpec(key);
        } catch (InvalidKeyException e) {
            throw new JfwInvalidCodecKeyException(CODEC_TYPE, key, e);
        }
    }

    @Override
    public byte[] encode(byte[] bytes) throws JfwCodecException {
        return this.encode(bytes,DEFAULT_KEY_BYTES);
    }

    @Override
    public byte[] decode(byte[] bytes) throws JfwCodecException {
       return this.decode(bytes,DEFAULT_KEY_BYTES);
    }

    @Override
    public byte[] encode(byte[] bytes, byte[] key) throws JfwCodecException {
        SecureRandom random = new SecureRandom();
        DESKeySpec desKey = createKey(key);
        SecretKeyFactory keyFactory;
        try {
            keyFactory = SecretKeyFactory.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        SecretKey securekey;
        try {
            securekey = keyFactory.generateSecret(desKey);
        } catch (InvalidKeySpecException e) {
            throw new JfwInvalidCodecKeyException(CODEC_TYPE, key, e);
        }
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("DES");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
        try {
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
        } catch (InvalidKeyException e) {
            throw new JfwInvalidCodecKeyException(CODEC_TYPE, key, e);
        }

        try {
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            throw new JfwCodecException(CODEC_TYPE, 402, "encode error", e);
        } 
    }

    @Override
    public byte[] decode(byte[] bytes, byte[] key) throws JfwCodecException {
        SecureRandom random = new SecureRandom();
        DESKeySpec desKey = createKey(key);
        SecretKeyFactory keyFactory;
        try {
            keyFactory = SecretKeyFactory.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        SecretKey securekey;
        try {
            securekey = keyFactory.generateSecret(desKey);
        } catch (InvalidKeySpecException e) {
            throw new JfwInvalidCodecKeyException(CODEC_TYPE, key, e);
        }
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("DES");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
        try {
            cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        } catch (InvalidKeyException e) {
            throw new JfwInvalidCodecKeyException(CODEC_TYPE, key, e);
        }

        try {
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            throw new JfwCodecException(CODEC_TYPE, 403, "decode error", e);
        } 
    }

    @Override
    public String encode(String s) throws JfwCodecException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String decode(String s) throws JfwCodecException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encode(String s, byte[] key) throws JfwCodecException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String decode(String s, byte[] key) throws JfwCodecException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encode(String s, String enc) throws JfwCodecException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String decode(String s, String enc) throws JfwCodecException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encode(String s, byte[] key, String enc) throws JfwCodecException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String decode(String s, byte[] key, String enc) throws JfwCodecException {
        throw new UnsupportedOperationException();
    }

   
    
    public static void main(String[] args)throws Exception{
        Random ra =new Random();
        CodecService cs = new DesCodecService();
        int len = 256;
        byte[] source = new byte[len];
        for(int i = 0 ; i < len;++i){
            source[i] =(byte)(ra.nextInt(255)-128);
        }
      byte[] key= new byte[]{1,2,3,4,5,6,7,8};
        System.out.println(StringUtil.bytesToStringDesc(source));
        byte[] dest = cs.encode(source,key);
        System.out.println(""+dest.length+StringUtil.bytesToStringDesc(dest));
        
        byte[] es = cs.decode(dest,key);
        
        System.out.println(""+es.length+StringUtil.bytesToStringDesc(es));
    }
}
