package org.jfw.util.codec;

import org.jfw.util.exception.JfwBaseException;

public class JfwCodecException extends JfwBaseException {
    private static final long serialVersionUID = 6032614432891886439L;
    private String codecType;

    public JfwCodecException(String codecType) {
        super();
        this.codecType = codecType;
    }

    public JfwCodecException(String codecType, int code, String message, Throwable cause) {
        super(code, message, cause);
        this.codecType = codecType;
    }

    public JfwCodecException(String codecType, int code, String message) {
        super(code, message);
        this.codecType = codecType;
    }

    public JfwCodecException(String codecType, int code, Throwable cause) {
        super(code, cause);
        this.codecType = codecType;
    }

    public JfwCodecException(String codecType, int code) {
        super(code);
        this.codecType = codecType;
    }

    public JfwCodecException(String codecType, String message, Throwable cause) {
        super(message, cause);
        this.codecType = codecType;
    }

    public JfwCodecException(String codecType, String message) {
        super(message);
        this.codecType = codecType;
    }

    public JfwCodecException(String codecType, Throwable cause) {
        super(cause);
        this.codecType = codecType;
    }

    public String getCodecType() {
        return codecType;
    }

    public void setCodecType(String codecType) {
        this.codecType = codecType;
    }
    
}
