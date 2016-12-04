package org.jfw.util.compiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.jfw.util.ClassUtil;

public class JdkCompiler implements Compiler {
	private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([$_a-zA-Z][$_a-zA-Z0-9\\.]*);");

	private static final Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s+");

	private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

	private final DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();

	private final ClassLoaderImpl classLoader;

	private final JavaFileManagerImpl javaFileManager;

	private volatile List<String> options;

	public Class<?> compile(String code, ClassLoader classLoader) {
		code = code.trim();
		Matcher matcher = PACKAGE_PATTERN.matcher(code);
		String pkg;
		if (matcher.find()) {
			pkg = matcher.group(1);
		} else {
			pkg = "";
		}
		matcher = CLASS_PATTERN.matcher(code);
		String cls;
		if (matcher.find()) {
			cls = matcher.group(1);
		} else {
			throw new IllegalArgumentException("No such class name in " + code);
		}
		String className = pkg != null && pkg.length() > 0 ? pkg + "." + cls : cls;
		try {
			return Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException e) {
			if (!code.endsWith("}")) {
				throw new IllegalStateException("The java code not endsWith \"}\", code: \n" + code + "\n");
			}
			try {
				return doCompile(className, code);
			} catch (RuntimeException t) {
				throw t;
			} catch (Throwable t) {
				throw new IllegalStateException("Failed to compile class, cause: " + t.getMessage() + ", class: "
						+ className + ", code: \n" + code + "\n, stack: " + toString(t));
			}
		}
	}

	public static String toString(Throwable e) {
		StringWriter w = new StringWriter();
		PrintWriter p = new PrintWriter(w);
		p.print(e.getClass().getName() + ": ");
		if (e.getMessage() != null) {
			p.print(e.getMessage() + "\n");
		}
		p.println();
		try {
			e.printStackTrace(p);
			return w.toString();
		} finally {
			p.close();
		}
	}

	@Override
	public void compile(List<String> codes, ClassLoader classLoader) {
	}

	public JdkCompiler() {
		options = new ArrayList<String>();
		options.add("-target");
		options.add("1.6");
		StandardJavaFileManager manager = compiler.getStandardFileManager(diagnosticCollector, null, null);
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader instanceof URLClassLoader
				&& (!loader.getClass().getName().equals("sun.misc.Launcher$AppClassLoader"))) {
			try {
				URLClassLoader urlClassLoader = (URLClassLoader) loader;
				List<File> files = new ArrayList<File>();
				for (URL url : urlClassLoader.getURLs()) {
					files.add(new File(url.getFile()));
				}
				manager.setLocation(StandardLocation.CLASS_PATH, files);
			} catch (IOException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
		classLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoaderImpl>() {
			public ClassLoaderImpl run() {
				return new ClassLoaderImpl(loader);
			}
		});
		javaFileManager = new JavaFileManagerImpl(manager, classLoader);
	}

	public Class<?> doCompile(String name, String sourceCode) throws Throwable {
		int i = name.lastIndexOf('.');
		String packageName = i < 0 ? "" : name.substring(0, i);
		String className = i < 0 ? name : name.substring(i + 1);
		JavaFileObjectImpl javaFileObject = new JavaFileObjectImpl(className, sourceCode);
		javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, packageName, className + ".java", javaFileObject);
		Boolean result = compiler.getTask(null, javaFileManager, diagnosticCollector, options, null,
				Arrays.asList(new JavaFileObject[] { javaFileObject })).call();
		if (result == null || !result.booleanValue()) {
			throw new IllegalStateException(
					"Compilation failed. class: " + name + ", diagnostics: " + diagnosticCollector);
		}
		return classLoader.loadClass(name);
	}

	public void doCompile(Map<String, String> sources) throws Throwable {
		List<JavaFileObjectImpl> jfos = new LinkedList<JavaFileObjectImpl>();
		for (Entry<String, String> entry : sources.entrySet()) {
			String name = entry.getKey();
			int i = name.lastIndexOf('.');
			String packageName = i < 0 ? "" : name.substring(0, i);
			String className = i < 0 ? name : name.substring(i + 1);
			JavaFileObjectImpl javaFileObject = new JavaFileObjectImpl(className, entry.getValue());
			javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, packageName, className + ".java",
					javaFileObject);
			jfos.add(javaFileObject);
		}
		Boolean result = compiler.getTask(null, javaFileManager, diagnosticCollector, options, null, jfos).call();
		if (result == null || !result.booleanValue()) {
			throw new IllegalStateException("Compilation failed. c, diagnostics: " + diagnosticCollector);
		}
	}

	private final class ClassLoaderImpl extends ClassLoader {

		private final Map<String, JavaFileObject> classes = new HashMap<String, JavaFileObject>();

		ClassLoaderImpl(final ClassLoader parentClassLoader) {
			super(parentClassLoader);
		}

		Collection<JavaFileObject> files() {
			return Collections.unmodifiableCollection(classes.values());
		}

		@Override
		protected Class<?> findClass(final String qualifiedClassName) throws ClassNotFoundException {
			JavaFileObject file = classes.get(qualifiedClassName);
			if (file != null) {
				byte[] bytes = ((JavaFileObjectImpl) file).getByteCode();
				return defineClass(qualifiedClassName, bytes, 0, bytes.length);
			}
			try {
				return ClassUtil.forNameWithCallerClassLoader(qualifiedClassName, getClass());
			} catch (ClassNotFoundException nf) {
				return super.findClass(qualifiedClassName);
			}
		}

		void add(final String qualifiedClassName, final JavaFileObject javaFile) {
			classes.put(qualifiedClassName, javaFile);
		}

		@Override
		protected synchronized Class<?> loadClass(final String name, final boolean resolve)
				throws ClassNotFoundException {
			return super.loadClass(name, resolve);
		}

		@Override
		public InputStream getResourceAsStream(final String name) {
			if (name.endsWith(".class")) {
				String qualifiedClasfindClasssName = name.substring(0, name.length() - 6).replace('/', '.');
				JavaFileObjectImpl file = (JavaFileObjectImpl) classes.get(qualifiedClasfindClasssName);
				if (file != null) {
					return new ByteArrayInputStream(file.getByteCode());
				}
			}
			return super.getResourceAsStream(name);
		}
	}

	private static URI toURI(String uri) {
		try {
			return new URI(uri);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static final class JavaFileObjectImpl extends SimpleJavaFileObject {

		private ByteArrayOutputStream bytecode;

		private final CharSequence source;

		public JavaFileObjectImpl(final String baseName, final CharSequence source) {
			super(toURI(baseName + ".java"), Kind.SOURCE);
			this.source = source;
		}

		JavaFileObjectImpl(final String name, final Kind kind) {
			super(toURI(name), kind);
			source = null;
		}

		public JavaFileObjectImpl(URI uri, Kind kind) {
			super(uri, kind);
			source = null;
		}

		@Override
		public CharSequence getCharContent(final boolean ignoreEncodingErrors) throws UnsupportedOperationException {
			if (source == null) {
				throw new UnsupportedOperationException("source == null");
			}
			return source;
		}

		@Override
		public InputStream openInputStream() {
			return new ByteArrayInputStream(getByteCode());
		}

		@Override
		public OutputStream openOutputStream() {
			return bytecode = new ByteArrayOutputStream();
		}

		public byte[] getByteCode() {
			return bytecode.toByteArray();
		}
	}

	private static final class JavaFileManagerImpl extends ForwardingJavaFileManager<JavaFileManager> {

		private final ClassLoaderImpl classLoader;

		private final Map<URI, JavaFileObject> fileObjects = new HashMap<URI, JavaFileObject>();

		public JavaFileManagerImpl(JavaFileManager fileManager, ClassLoaderImpl classLoader) {
			super(fileManager);
			this.classLoader = classLoader;
		}

		@Override
		public FileObject getFileForInput(Location location, String packageName, String relativeName)
				throws IOException {
			FileObject o = fileObjects.get(uri(location, packageName, relativeName));
			if (o != null)
				return o;
			return super.getFileForInput(location, packageName, relativeName);
		}

		public void putFileForInput(StandardLocation location, String packageName, String relativeName,
				JavaFileObject file) {
			fileObjects.put(uri(location, packageName, relativeName), file);
		}

		private URI uri(Location location, String packageName, String relativeName) {
			return toURI(location.getName() + '/' + packageName + '/' + relativeName);
		}

		@Override
		public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName, Kind kind,
				FileObject outputFile) throws IOException {
			JavaFileObject file = new JavaFileObjectImpl(qualifiedName, kind);
			classLoader.add(qualifiedName, file);
			return file;
		}

		@Override
		public ClassLoader getClassLoader(JavaFileManager.Location location) {
			return classLoader;
		}

		@Override
		public String inferBinaryName(Location loc, JavaFileObject file) {
			if (file instanceof JavaFileObjectImpl)
				return file.getName();
			return super.inferBinaryName(loc, file);
		}

		@Override
		public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse)
				throws IOException {
			Iterable<JavaFileObject> result = super.list(location, packageName, kinds, recurse);

			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			List<URL> urlList = new ArrayList<URL>();
			Enumeration<URL> e = contextClassLoader.getResources("com");
			while (e.hasMoreElements()) {
				urlList.add(e.nextElement());
			}

			ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();

			if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
				for (JavaFileObject file : fileObjects.values()) {
					if (file.getKind() == Kind.CLASS && file.getName().startsWith(packageName)) {
						files.add(file);
					}
				}

				files.addAll(classLoader.files());
			} else if (location == StandardLocation.SOURCE_PATH && kinds.contains(JavaFileObject.Kind.SOURCE)) {
				for (JavaFileObject file : fileObjects.values()) {
					if (file.getKind() == Kind.SOURCE && file.getName().startsWith(packageName)) {
						files.add(file);
					}
				}
			}

			for (JavaFileObject file : result) {
				files.add(file);
			}

			return files;
		}
	}
}
