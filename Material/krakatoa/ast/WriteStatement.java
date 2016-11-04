package ast;

public class WriteStatement extends Statement {
	private ExprList exprList;
	
	public WriteStatement(ExprList exprListStmt) {
		this.exprList = exprListStmt;
	}
	
	public ExprList getExprList() {
		return exprList;
	}

	public void setExprList(ExprList exprList) {
		this.exprList = exprList;
	}

	public void genC(PW pw){
	}
	
	public void genKra(PW pw){
		pw.printIdent("write(");
		this.exprList.genKra(pw);
		pw.println(")");
	}
}
