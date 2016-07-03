package org.jfw.apt.web.handlers.buildParam;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.model.RequestParamModel;

public interface RequestParamTransfer {
    void transfer(ClassWriter cw,MethodParamEntry mpe,AptWebHandler aptWebHandler,RequestParamModel annotation) throws AptException;
    void transferBeanProperty(ClassWriter cw,MethodParamEntry mpe,AptWebHandler aptWebHandler,RequestParamTransfer.FieldRequestParam frp);
    public static class FieldRequestParam{
        private String value;
        private String paramName;
        private String valueClassName;
        private String defaultValue;
        private boolean required;
        private Class<RequestParamTransfer> transferClass;
		public Class<RequestParamTransfer> getTransferClass() {
			return transferClass;
		}
		public void setTransferClass(Class<RequestParamTransfer> transferClass) {
			this.transferClass = transferClass;
		}
		public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        public String getParamName() {
            return paramName;
        }
        public void setParamName(String paramName) {
            this.paramName = paramName;
        }
        public String getValueClassName() {
            return this.valueClassName;
        }
        public void setValueClassName(String valueClassName) {
            this.valueClassName = valueClassName;
        }
        public String getDefaultValue() {
            return defaultValue;
        }
        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
        public boolean isRequired() {
            return required;
        }
        public void setRequired(boolean required) {
            this.required = required;
        }
    }
}
