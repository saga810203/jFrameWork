package org.jfw.util.codec;

public class UrlSafeBase64CodecService extends Base64CodecService {
    public static final String CODEC_TYPE="BASE64_URLSAFE";
    @Override
    public byte[] encode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return bytes;
        }
        return (new Base64(true)).encode(bytes);
    }

    @Override
    public byte[] decode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return bytes;
        }
        return (new Base64(true)).decode(bytes);
    }
    

}
