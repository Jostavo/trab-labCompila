package ast;

public class ReturnStatement extends Statement {
	private Expr expr;
	
	public ReturnStatement(Expr rtExpr) {
		this.expr = rtExpr;
	}
	
	public Expr getExpr() {
		return expr;
	}

	public void setExpr(Expr expr) {
		this.expr = expr;
	}

	public void genC(PW pw){
	}
	
	public void genKra(PW pw){
		pw.printIdent("return");
		this.expr.genKra(pw, false);
	}
}
