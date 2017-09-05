// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

import java.util.*;
import comp.CompilationError;

public class Program {

	public Program(ArrayList<KraClass> classList, ArrayList<MetaobjectCall> metaobjectCallList,
			ArrayList<CompilationError> compilationErrorList) {
		this.classList = classList;
		this.metaobjectCallList = metaobjectCallList;
		this.compilationErrorList = compilationErrorList;
	}

	public void genC(PW pw) {
		int runPos = -1;
		
		pw.println("#include <malloc.h>");
		pw.println("#include <stdlib.h>");
		pw.println("#include <stdio.h>");
		pw.println();
		pw.println("typedef int boolean;");
		pw.println("#define true 1");
		pw.println("#define false 0");
		pw.println();
		pw.println("typedef void (*Func)();");
		pw.println();
		for (KraClass aux : classList) {
			aux.genC(pw);
			pw.println();
			
			if (aux.getName().equals("Program")) {
				runPos = aux.getMethodPosition("run");
			}
		}
		pw.printlnIdent("int main() {");
		pw.add();
		pw.printlnIdent("_class_Program *program;");
		pw.printlnIdent("program = new_Program();");
		pw.printlnIdent("( ( void (*)(_class_Program *) ) program->vt[" + runPos + "] )(program);");
		pw.printlnIdent("return 0;");
		pw.sub();
		pw.printlnIdent("}");
	}

	public void genKra(PW pw) {
		for (KraClass aux : classList) {
			aux.genKra(pw);
			pw.println();
		}
	}

	public ArrayList<KraClass> getClassList() {
		return classList;
	}

	public ArrayList<MetaobjectCall> getMetaobjectCallList() {
		return metaobjectCallList;
	}

	public boolean hasCompilationErrors() {
		return compilationErrorList != null && compilationErrorList.size() > 0;
	}

	public ArrayList<CompilationError> getCompilationErrorList() {
		return compilationErrorList;
	}

	private ArrayList<KraClass> classList;
	private ArrayList<MetaobjectCall> metaobjectCallList;

	ArrayList<CompilationError> compilationErrorList;

}