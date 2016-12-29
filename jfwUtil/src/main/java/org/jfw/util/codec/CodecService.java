package org.jfw.util.codec;

public interface CodecService {
    /**
     * Encodes a byte array  using default key
     * @param bytes  byte array to be encoded.
     * @return the encoded byte array
     */
    byte[] encode(byte[] bytes)throws JfwCodecException;
    /**
     * Decodes a byte array  using default key
     * @param bytes  byte array to be Decoded.
     * @return the decoded byte array
     */
    byte[] decode(byte[] bytes)throws JfwCodecException;
    /**
     * Encodes a byte array  using specified key
     * @param bytes  byte array to be encoded.
     * @return the encoded byte array
     * @throws JfwCodecKeyException 
     */
    byte[] encode(byte[] bytes,byte[] key)throws JfwCodecException;
    /**
     * Decodes a byte array  using specified key
     * @param bytes  byte array to be decoded.
     * @return the decoded byte array
     */
    byte[] decode(byte[] bytes,byte[] key)throws JfwCodecException;
    /**
     * Encode a string  by UTF-8  using default key
     * @param s String to be encoded.
     * @return the encoded String
     */
    String encode(String s)throws JfwCodecException;
    /**
     * Decodes a String by UTF-8  using default key
     * @param s  a String to be decoded.
     * @return the decoded String
     */  
    String decode(String s)throws JfwCodecException;
    /**
     * Encodes a String by UTF-8  using specified key
     * @param s  a String  to be encoded.
     * @return the encoded byte array
     */
    String encode(String s,byte[] key)throws JfwCodecException;
    /**
     * Decodes a String by UTF-8  using specified key
     * @param s  a String to be decoded.
     * @return the decoded String
     */ 
    String decode(String s,byte[] key)throws JfwCodecException;
    /**
     * Encode a string  by specified charset  using default key
     * @param s String to be encoded.
     * @return the encoded String
     */
    String encode(String s,String enc)throws JfwCodecException;
    /**
     * Decodes a String by specified  using default key
     * @param s  a String to be decoded.
     * @return the decoded String
     */  
    String decode(String s,String enc)throws JfwCodecException;
    /**
     * Encode a string  by specified charset  using specified key
     * @param s String to be encoded.
     * @return the encoded String
     */
    String encode(String s,byte[] key,String enc)throws JfwCodecException;
    /**
     * Decodes a String by specified  using specified key
     * @param s  a String to be decoded.
     * @return the decoded String
     */  
    String decode(String s,byte[] key,String enc)throws JfwCodecException;   
}
