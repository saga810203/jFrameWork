package org.jfw.apt.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

import org.jfw.apt.CodeGenHandler;
import org.jfw.apt.CodePartGenerator;
import org.jfw.apt.Util;
import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodEntry;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.model.ClassBeanDefine;
import org.jfw.apt.web.annotation.Path;
import org.jfw.apt.web.annotation.operate.Delete;
import org.jfw.apt.web.annotation.operate.Get;
import org.jfw.apt.web.annotation.operate.Post;
import org.jfw.apt.web.annotation.operate.Put;
import org.jfw.apt.web.handlers.BuildParamHandler;
import org.jfw.apt.web.handlers.ChangeResultHandler;
import org.jfw.apt.web.handlers.ExecuteHandler;
import org.jfw.apt.web.handlers.JdbcConnectionHandler;
import org.jfw.apt.web.handlers.LastScriptHandler;
import org.jfw.apt.web.handlers.LoginUserHandler;
import org.jfw.apt.web.handlers.ResultToNullHandler;
import org.jfw.apt.web.handlers.SetSessionHandler;
import org.jfw.apt.web.handlers.ValidateParamHandler;
import org.jfw.apt.web.handlers.ViewHandler;

public class AptWebHandler extends CodeGenHandler {

	private static List<Class<? extends RequestHandler>> supportedClass = new ArrayList<Class<? extends RequestHandler>>();

	private RequestHandler[] handlers;

	protected int methodIndex = 1;
	protected String url;
	protected Map<Object, CodePartGenerator> codes = new HashMap<Object, CodePartGenerator>();

	protected String methodUrl;
	protected MethodEntry me;
	protected String returnType;
	protected List<MethodParamEntry> params;
	protected String currMethodName;
	protected List<String> httpMethods = new LinkedList<String>();

	public String getReturnType() {
		return returnType;
	}

	public String getUrl() {
		return url;
	}

	public String getMethodUrl() {
		return methodUrl;
	}

	public MethodEntry getMe() {
		return me;
	}

	public List<String> getHttpMethods() {
		return httpMethods;
	}

	public void addCodePart(Object key, CodePartGenerator cpg) {
		this.codes.put(key, cpg);
	}

	@Override
	public boolean matchAnnotation() {
		Path path = this.ref.getAnnotation(Path.class);
		if (path != null) {
			this.url = path.value();
		} else {
			return false;
		}
		return true;
	}

	@Override
	public void genTargetClassname() {
		this.targetClassname = this.sourceClassname + "WebHandler";
	}

	@Override
	public void genSupportClassname() {

	}

	@Override
	public void genInterFaces() {

	}

	@Override
	public void genAnnotations() {
		this.annotationDescps = new String[] { "@org.jfw.apt.annotation.Bean" };

	}

	@Override
	public void init() {
		this.handlers = new RequestHandler[supportedClass.size()];
		for (int i = 0; i < this.handlers.length; ++i) {
			RequestHandler rh;
			try {
				rh = supportedClass.get(i).newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			rh.setAptWebHandler(this);
			this.handlers[i] = rh;
		}
	}

	protected void genCurrentMethodName() {
		this.currMethodName = "ws" + this.methodIndex;
		++this.methodIndex;
	}

	protected void beginMethod() {
		out.beginMethod();

		out.bL("public void ").w(this.currMethodName)
				.w("(javax.servlet.http.HttpServletRequest req,javax.servlet.http.HttpServletResponse res,int _viewType) ")
				.el(" throws javax.servlet.ServletException,java.io.IOException{");
	}

	private void saveRequset() {
		for (String mc : this.httpMethods) {
			ClassBeanDefine wre = this.jfwProccess.getBeanConfig()
					.addEntryBeanByClass("org.jfw.util.web.model.WebRequestEntry", null);
			wre.setRefAttribute("webHandler", this.getTargetClassname().trim().replaceAll("\\.", "_"));
			wre.setString("uri", this.url + this.methodUrl);
			wre.setString("methodName", this.currMethodName);
			wre.setString("methodType", mc);
			wre.joinGroup("jfwmvc");
		}
	}

	public void endMethod() {
		out.l("}");
		this.saveRequset();
	}

	public void writeMethodContent() throws AptException {
		out.l("req.setCharacterEncoding(\"UTF-8\");").l("res.setCharacterEncoding(\"UTF-8\");");
		if (!this.returnType.equals("void")) {
			out.bL(this.returnType).w(" result").el(Util.isPrimitive(this.returnType) ? ";" : " = null ;");
		}
		for (int i = 0; i < this.handlers.length; ++i) {
			this.handlers[i].appendBeforCode(out);
		}
		for (int i = this.handlers.length - 1; i >= 0; --i) {
			this.handlers[i].appendAfterCode(out);
		}
	}

	private void genRequestInfo(Element ele, String mUrl) throws AptException {
		this.methodUrl = genUrl(mUrl, ele);
		this.httpMethods.clear();
		if (null != ele.getAnnotation(Get.class))
			httpMethods.add("GET");
		if (null != ele.getAnnotation(Post.class))
			httpMethods.add("POST");
		if (null != ele.getAnnotation(Put.class))
			httpMethods.add("PUT");
		if (null != ele.getAnnotation(Delete.class))
			httpMethods.add("DELETE");
		if (this.httpMethods.isEmpty())
			this.httpMethods.add("GET");
	}

	private void genInstanceVariable() {

		out.bL("@org.jfw.apt.annotation.Autowrie(\"").w(this.ref.getQualifiedName().toString().replaceAll("\\.", "_"))
				.el("\")");
		out.bL("private ").w(this.sourceClassname).el(" handler = null;");
		out.bL("public void setHandler(").w(this.sourceClassname).el(" value){").l("handler = value;").l("}");

	}

	@Override
	public void proccess() throws AptException {
		this.genInstanceVariable();
		this.url = genUrl(this.url, ref);
		this.codes.clear();
		this.methodIndex = 1;
		this.codes.clear();
		List<? extends Element> eles = ref.getEnclosedElements();
		for (Element ele : eles) {
			if (ele.getKind() == ElementKind.METHOD) {

				Path methodPath = ele.getAnnotation(Path.class);
				if (methodPath != null) {
					this.genRequestInfo(ele, methodPath.value());
					this.me = MethodEntry.build((ExecutableElement) ele);
					this.returnType = this.me.getReturnType();
					this.params = this.me.getParams();
					this.genCurrentMethodName();
					this.cleanConfig();

					this.beginMethod();
					this.writeMethodContent();
					this.endMethod();
				}

			}
		}

		for (CodePartGenerator cpg : this.codes.values()) {
			cpg.generate(this.out);
		}
		out.overClass();

		ClassBeanDefine cbd = this.jfwProccess.getBeanConfig()
				.addServiceBeanByClass(this.ref.getQualifiedName().toString(), null);
		Util.buildAtuowrieProperty(cbd, this.ref);
	}

	@Override
	public void completeRound() {

	}

	@Override
	public void complete() {

	}

	/*************************
	 * begin config in gen method
	 ************************************/
	private void cleanConfig() {
		readedStringArray = false;
		readedString = false;
		readedHeaders = false;
		readedSession = false;
		readedOut = false;
		readedURI = false;
	}

	private boolean readedStringArray = false;
	private boolean readedString = false;
	private boolean readedHeaders = false;
	private boolean readedSession = false;
	private boolean readedOut = false;
	private boolean readedURI = false;

	public void readURI() {
		if (!readedURI) {
			readedURI = true;
			out.l("String[] _uriArray = (String[]) req.getAttribute(\"JFW_REQUEST_URL_ARRAY\");");
		}
	}

	public void readOut() {
		if (!readedOut) {
			readedOut = true;
			out.l("java.io.PrintWriter out = res.getWriter();");
		}
	}

	public void readSession() {
		if (!readedSession) {
			readedSession = true;
			out.l(" javax.servlet.http.HttpSession session = req.getSession();");
		}
	}

	public void readParameters(String paramName) {
		if (!readedStringArray) {
			readedStringArray = true;
			out.bL("String[] ").w("params = req.getParameterValues(\"").ws(paramName).el("\");");
		} else {
			out.bL("params = req.getParameterValues(\"").ws(paramName).el("\");");
		}
	}

	public void readHeaders(String paramName) {
		if (!this.readedHeaders) {
			readedHeaders = true;
			out.l("java.util.List<String> headers = new java.util.LinkedList<String>();");
			out.bL("java.util.Enumeration<String> ");
		} else {
			out.l("headers.clear();");
			out.bL("");
		}
		out.w("enumHeaders = req.getHeaders(\"").ws(paramName).el("\");");
		out.l("while(enumHeaders.hasMoreElements()){").l("  headers.add(enumHeaders.nextElement());").l("}");
		if (!readedStringArray) {
			readedStringArray = true;
			out.bL("String[] ");
		} else {
			out.bL("");
		}
		out.el("params =headers.toArray(new String[headers.size()]);");
	}

	public void readParameter(String paramName) {
		if (!readedString) {
			readedString = true;
			out.bL("String  param = req.getParameter(\"").ws(paramName).el("\");");
		} else {
			out.bL(" param = req.getParameter(\"").ws(paramName).el("\");");
		}
	}

	public void readHeader(String paramName) {
		if (!readedString) {
			readedString = true;
			out.bL("String param = req.getHeader(\"").ws(paramName).el("\");");
		} else {
			out.bL("param = req.getHeader(\"").ws(paramName).el("\");");
		}
	}

	/*************************
	 * end config in gen method
	 ************************************/

	public static String genUrl(String str, Element ele) throws AptException {
		if (str == null || str.trim().length() == 0) {
			return "";
		} else {
			str = str.trim();
			if (!str.startsWith("/"))
				throw new AptException(ele, "@" + Path.class.getName() + "'value must startWith \"/\"");
			return str;
		}
	}

	static {
		supportedClass.add(ViewHandler.class);
		supportedClass.add(LoginUserHandler.class);
		supportedClass.add(BuildParamHandler.class);
		supportedClass.add(ValidateParamHandler.class);
		supportedClass.add(JdbcConnectionHandler.class);
		supportedClass.add(ExecuteHandler.class);
		supportedClass.add(SetSessionHandler.class);
		supportedClass.add(ChangeResultHandler.class);
		supportedClass.add(LastScriptHandler.class);
		supportedClass.add(ResultToNullHandler.class);

	}
}
