package ast;

public class CompositeStatement {
	private StatementList stmtList;
	
	public CompositeStatement(StatementList stList) {
		this.stmtList = stList;
	}
	
	public void genKra(PW pw) {
		pw.println("{");
		pw.add();
		stmtList.genKra(pw);
		pw.sub();
		pw.printlnIdent("}");
	}
}
