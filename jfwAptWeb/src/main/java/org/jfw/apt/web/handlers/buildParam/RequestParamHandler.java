package org.jfw.apt.web.handlers.buildParam;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.VariableElement;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.ClassName;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.model.TypeName;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.AptWebHandler;
import org.jfw.apt.web.annotation.param.RequestParam;
import org.jfw.apt.web.model.Field;
import org.jfw.apt.web.model.RequestParamModel;

public final class RequestParamHandler implements BuildParameter {
	public static final RequestParamHandler INS = new RequestParamHandler();

	private RequestParamHandler() {
	}

	@Override
	public void build(ClassWriter cw, MethodParamEntry mpe, AptWebHandler aptWebHandler) throws AptException {
		if (mpe.getTypeName().equals("java.io.InputStream")) {
			cw.bL("java.io.InputStream ").w(mpe.getName()).el(" = req.getInputStream();");
			return;
		}

		RequestParamModel rpm = RequestParamModel.build(this.requestParam, mpe.getName(), mpe);
		RequestParamTransfer rpt = null;
		Class<RequestParamTransfer> cls = null;
		try {
			cls = TransferFactory.getRequestParamTransfer(mpe.getTypeName());
			if (cls != null) {
				rpt = cls.newInstance();
			}
		} catch (Exception e) {
			throw new AptException(mpe.getRef(), "create object instance error [" + cls.toString() + "]");
		}
		if (null == rpt) {
			rpt = new DefaultTransfer();
		}
		rpt.transfer(cw, mpe, aptWebHandler, rpm);
	}

	private static class DefaultTransfer implements RequestParamTransfer {
		private RequestParamTransfer.FieldRequestParam frp[];
		private RequestParamModel annotation;
		private MethodParamEntry mpe;
		private ClassName res;
		private Map<String, List<TypeName>> properties;

		private void initTargetType(MethodParamEntry mpe) throws AptException {
			this.mpe = mpe;
			TypeName tn = annotation.getRealClass();
			if (TypeName.OBJECT.equals(tn))
				tn = TypeName.get(this.mpe.getRef().asType());

			if (!tn.getClass().equals(ClassName.class)) {
				throw new AptException(this.mpe.getRef(), "can't create instance or invalid Class:" + tn.toString());
			}
			res = (ClassName) tn;
			if (!res.canInstance())
				throw new AptException(this.mpe.getRef(), "can't create instance or invalid Class:" + tn.toString());
			this.properties = res.getAllSetter();
		}

		private List<RequestParamTransfer.FieldRequestParam> getFieldRequstParamWithDeclared() throws AptException {
			List<RequestParamTransfer.FieldRequestParam> list = new LinkedList<RequestParamTransfer.FieldRequestParam>();
			for (Field field : this.annotation.getFields()) {
				RequestParamTransfer.FieldRequestParam rfp = new RequestParamTransfer.FieldRequestParam();
				rfp.setDefaultValue(field.getDefaultValue());
				rfp.setValue(field.getName());
				if (rfp.getValue() == null)
					throw new AptException(this.mpe.getRef(), "@FieldParam'value not null or emtry string");

				rfp.setParamName(field.getParamName());
				if (rfp.getParamName() == null)
					rfp.setParamName(rfp.getValue());

				rfp.setRequired(field.isRequired());
				rfp.setValueClassName(field.getClassName());
				List<TypeName> plist = this.properties.get(rfp.getValue());
				if (plist == null)
					throw new AptException(this.mpe.getRef(),
							"@FieldParam[value=" + rfp.getValue() + "] not in Bean property");
				if (plist.size() > 1) {
					rfp.setTransferClass(TransferFactory.getRequestParamTransfer(rfp.getValueClassName()));
					if (rfp.getTransferClass() == null)
						throw new AptException(mpe.getRef(), "@FieldParam'valueClass not found RequestParamTransfer");
					boolean found = false;
					for (TypeName tn : plist) {
						if (tn.toString().equals(rfp.getValueClassName())) {
							found = true;
							break;
						}
					}
					if (!found) {
						throw new AptException(mpe.getRef(),
								"@FieldParam[valueClass=" + rfp.getValueClassName() + "] not found in Bean property");
					}
				} else {
					rfp.setTransferClass(TransferFactory.getRequestParamTransfer(plist.get(0).toString()));
					if (rfp.getTransferClass() == null)
						throw new AptException(mpe.getRef(),
								"@FieldParam[value=" + rfp.getValue() + "] not found RequestParamTransfer");

				}
				if (this.annotation.getParamName().length() > 0) {
					rfp.setParamName(this.annotation.getParamName() + "_" + rfp.getParamName());
				}
				list.add(rfp);
			}
			return list;
		}

		private List<RequestParamTransfer.FieldRequestParam> getFieldRequstParamWithDefault() throws AptException {
			List<RequestParamTransfer.FieldRequestParam> list = new LinkedList<RequestParamTransfer.FieldRequestParam>();
			String[] exincludes = annotation.getExcludeFields();
			if (exincludes != null && exincludes.length > 0) {
				for (String s : exincludes) {
					if (s != null && s.trim().length() > 0) {
						this.properties.remove(s.trim());
					}
				}
			}
			for (Map.Entry<String, List<TypeName>> en : this.properties.entrySet()) {
				List<TypeName> li = en.getValue();
				Class<RequestParamTransfer> clRpt = null;
				String valueClassName = null;
				if (li.size() == 1) {
					clRpt = TransferFactory.getRequestParamTransfer(li.get(0).toString());
					valueClassName = li.get(0).toString();
				} else {
					for (TypeName ttn : li) {
						if (clRpt == null) {
							clRpt = TransferFactory.getRequestParamTransfer(ttn.toString());
							valueClassName = ttn.toString();
						} else {
							if (null != TransferFactory.getRequestParamTransfer(ttn.toString())) {
								throw new AptException(mpe.getRef(), "parameter Bean property can set mulit Value");
							}
						}
					}
				}
				if (clRpt == null)
					continue;
				RequestParamTransfer.FieldRequestParam rfp = new RequestParamTransfer.FieldRequestParam();
				rfp.setValue(en.getKey());
				if (this.annotation.getParamName().length() > 0) {
					rfp.setParamName(annotation.getParamName() + "_" + en.getKey());
				} else {
					rfp.setParamName(en.getKey());
				}
				rfp.setDefaultValue(null);
				rfp.setRequired(false);
				rfp.setTransferClass(clRpt);
				rfp.setValueClassName(valueClassName);
				list.add(rfp);
			}
			return list;
		}

		private void buildField() throws AptException {
			List<RequestParamTransfer.FieldRequestParam> list;
			Field[] fields = this.annotation.getFields();
			if (fields.length > 0) {
				list = this.getFieldRequstParamWithDeclared();
			} else {
				list = this.getFieldRequstParamWithDefault();
			}
			this.frp = list.toArray(new RequestParamTransfer.FieldRequestParam[list.size()]);
		}

		@Override
		public void transfer(ClassWriter cw, MethodParamEntry mpe, AptWebHandler aptWebHandler,
				RequestParamModel annotation) throws AptException {
			this.annotation = annotation;
			this.initTargetType(mpe);
			this.buildField();

			cw.bL(res.toString()).w(" ").w(mpe.getName()).w(" = new ").w(res.toString()).el("();");
			for (RequestParamTransfer.FieldRequestParam rptFrp : this.frp) {
				RequestParamTransfer rptf;
				try {
					rptf = rptFrp.getTransferClass().newInstance();
				} catch (Exception e) {
					throw new AptException(mpe.getRef(),
							"create TransferClass instance error:" + (null == e.getMessage() ? "" : e.getMessage()));
				}
				rptf.transferBeanProperty(cw, mpe, aptWebHandler, rptFrp);
			}
		}

		@Override
		public void transferBeanProperty(ClassWriter cw, MethodParamEntry mpe, AptWebHandler aptWebHandler,
				RequestParamTransfer.FieldRequestParam frp) {
			throw new UnsupportedOperationException();
		}

	}

	@Override
	public boolean match(VariableElement ele) {
		this.requestParam = ele.getAnnotation(RequestParam.class);
		return null != this.requestParam;
	}

	private RequestParam requestParam;

	public void setRequestParam(RequestParam requestParam) {
		this.requestParam = requestParam;
	}

}
