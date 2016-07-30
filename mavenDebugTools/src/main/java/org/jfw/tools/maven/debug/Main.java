package org.jfw.tools.maven.debug;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {
	 public static final String MULTIMODULE_PROJECT_DIRECTORY = "maven.multiModuleProjectDirectory";
	
	private final static String mavenHome = "d:/Tools/JAVA/apache-maven-3.3.1";
	private final static String mainJar = mavenHome+"/boot/plexus-classworlds-2.5.2.jar";
	private final static String mainConf =mavenHome+"/bin/m2.conf";
	private final static String mainClass="org.codehaus.plexus.classworlds.launcher.Launcher";
	private final  static String workDir="E:/EclipseProject/framework/portal"; 
	private final static String[] goals =new String[]{"clean","jetty:run"};
	

	
	
	public static void main(String[] args) throws Exception{
		System.setProperty("classworlds.conf",mainConf);
		System.setProperty("maven.home",mavenHome);
		//System.setProperty("$M2_HOME",mavenHome);
		System.setProperty(MULTIMODULE_PROJECT_DIRECTORY,workDir);
		 System.out.println(System.getProperty(MULTIMODULE_PROJECT_DIRECTORY));
		
		
		 File file = new File(mainJar);
		 URL url = file.toURI().toURL();
		 
		 URLClassLoader  urlClassLoader = new URLClassLoader(new URL[]{url},Thread.currentThread().getContextClassLoader());
		 Thread.currentThread().currentThread().setContextClassLoader(urlClassLoader);
		 Class<?> clazz = urlClassLoader.loadClass(mainClass);
	
		 Method method = clazz.getMethod("main",new Class[]{String[].class});
		 method.invoke(null, new Object []{goals});
	}

}
