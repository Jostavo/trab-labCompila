package ast;

import java.util.*;

public class ReadStatement extends Statement {
	private ArrayList<String> leftValues;
	
	public ReadStatement(ArrayList<String> stmtValues) {
		this.leftValues = stmtValues;
	}
	
	public ArrayList<String> getLeftValues() {
		return leftValues;
	}

	public void setLeftValues(ArrayList<String> leftValues) {
		this.leftValues = leftValues;
	}

	public void genC(PW pw){
	}
	
	public void genKra(PW pw){
		int size = this.leftValues.size();
		pw.printIdent("read(");
		for (String str: this.leftValues) {
			pw.print(str);
			if ( --size > 0 ) {
    			pw.print(",");
    		}
		}
		pw.println(")");
	}
}
