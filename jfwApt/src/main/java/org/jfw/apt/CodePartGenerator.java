package org.jfw.apt;
import org.jfw.apt.out.ClassWriter;

public interface CodePartGenerator {
	void generate(ClassWriter cw);

}
