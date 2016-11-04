package ast;

public class BreakStatement extends Statement {
	public BreakStatement() {
		
	}
	
	public void genC(PW pw){
	}
	
	public void genKra(PW pw){
		pw.printlnIdent("break");
	}
}
