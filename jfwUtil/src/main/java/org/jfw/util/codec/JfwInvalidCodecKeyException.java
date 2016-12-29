package org.jfw.util.codec;

import org.jfw.util.StringUtil;

public class JfwInvalidCodecKeyException extends JfwCodecException {
    private static final long serialVersionUID = 2397737655762171304L;
    private byte[] key;

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public JfwInvalidCodecKeyException(String codecType,byte[] key,Throwable cause ) {
        super(codecType,401,"Invalid codec key:"+StringUtil.bytesToStringDesc(key),cause);
        this.key = key;
    }
}
