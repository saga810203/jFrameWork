package org.jfw.util.compiler;

import java.util.List;

public interface Compiler {
	Class<?> compile(String code, ClassLoader classLoader);
	void compile(List<String> codes, ClassLoader classLoader );
}
