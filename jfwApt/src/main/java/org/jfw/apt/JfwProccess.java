package org.jfw.apt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.Diagnostic.Kind;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.out.model.BeanConfig;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class JfwProccess extends AbstractProcessor {

	private BeanConfig beanConfig = new BeanConfig();
	private Messager messager;
	private Filer filer;
	private Map<Object, Object> attributes = new HashMap<Object, Object>();
	private Map<Object, Object> roundAttributes = new HashMap<Object, Object>();
	private Set<? extends TypeElement> annotations;
	private RoundEnvironment roundEnv;
	private Set<? extends Element> rootElements;
	private Element currentElement = null;

	private List<AptHandler> handlers;

	public List<AptHandler> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<AptHandler> handlers) {
		this.handlers = handlers;
	}

	public Map<Object, Object> getRoundAttributes() {
		return roundAttributes;
	}

	public void setRoundAttributes(Map<Object, Object> roundAttributes) {
		this.roundAttributes = roundAttributes;
	}

	public Element getCurrentElement() {
		return currentElement;
	}

	public void setCurrentElement(Element currentElement) {
		this.currentElement = currentElement;
	}

	public BeanConfig getBeanConfig() {
		return beanConfig;
	}

	public void setBeanConfig(BeanConfig beanConfig) {
		this.beanConfig = beanConfig;
	}

	public Messager getMessager() {
		return messager;
	}

	public void setMessager(Messager messager) {
		this.messager = messager;
	}

	public Filer getFiler() {
		return filer;
	}

	public void setFiler(Filer filer) {
		this.filer = filer;
	}

	public Map<Object, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<Object, Object> attributes) {
		this.attributes = attributes;
	}

	public Set<? extends TypeElement> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<? extends TypeElement> annotations) {
		this.annotations = annotations;
	}

	public RoundEnvironment getRoundEnv() {
		return roundEnv;
	}

	public void setRoundEnv(RoundEnvironment roundEnv) {
		this.roundEnv = roundEnv;
	}

	public Set<? extends Element> getRootElements() {
		return rootElements;
	}

	public void setRootElements(Set<? extends Element> rootElements) {
		this.rootElements = rootElements;
	}

	private synchronized void initHandles() {
		this.handlers = new LinkedList<AptHandler>();

		InputStream in = JfwProccess.class.getClassLoader().getResourceAsStream("aptHandlers.conf");
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = null;
			while ((line = r.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("#"))
					continue;
				AptHandler h = (AptHandler) Class.forName(line).newInstance();
				h.setJfwProccess(this);
				this.handlers.add(h);
			}

		} catch (Exception e) {
			this.handlers.clear();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}

	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.messager = processingEnv.getMessager();
		this.filer = processingEnv.getFiler();
		this.initHandles();
		this.attributes.put(JfwProccess.class, this);
	}

	public void saveResourceFile(String fileName, String fileContent) {
		try {
			FileObject fo = this.filer.createResource(javax.tools.StandardLocation.SOURCE_OUTPUT, "", fileName,
					(Element[]) null);
			OutputStream os = fo.openOutputStream();
			try {
				os.write(fileContent.getBytes("UTF-8"));
				os.flush();
			} finally {
				os.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("save resource file[" + fileName + "] error:" + e.getMessage());
		}
		try {
			FileObject fo = this.filer.createResource(javax.tools.StandardLocation.CLASS_OUTPUT, "", fileName,
					(Element[]) null);
			OutputStream os = fo.openOutputStream();
			try {
				os.write(fileContent.getBytes("UTF-8"));
				os.flush();
			} finally {
				os.close();
			}
		} catch (Exception e) {
			throw new RuntimeException( "save resource file[" + fileName + "] error:" + e.getMessage());
		}

	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (this.handlers.isEmpty())
			return true;
		this.roundAttributes.clear();
		this.annotations = annotations;
		this.roundEnv = roundEnv;
		this.rootElements = roundEnv.getRootElements();
		try {
			for (ListIterator<AptHandler> it = this.handlers.listIterator(); it.hasNext();) {
				AptHandler h = it.next();
				for (Iterator<? extends Element> itel = this.rootElements.iterator(); itel.hasNext();) {
					this.currentElement = itel.next();
					h.handle(this.currentElement);
				}
				h.completeRound();
				if (rootElements.isEmpty())
					h.complete();
			}
			if (this.rootElements.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				this.beanConfig.appendTo(sb);
				this.saveResourceFile("beanConfig.properties", sb.toString());
			}
		} catch (AptException e) {
			this.messager.printMessage(Kind.ERROR, e.getMessage(), e.getEle());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			String m = e.getMessage();
			if (m == null)
				m = "nullException";
			this.messager.printMessage(Kind.ERROR, m, this.currentElement);
		}
		return true;
	}

}
