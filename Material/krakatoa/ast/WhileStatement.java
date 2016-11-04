package ast;

public class WhileStatement extends Statement {
	private Expr expr;
	private Statement stmt;
	
	public WhileStatement(Expr wlExpr, Statement wlStmt) {
		this.expr = wlExpr;
		this.stmt = wlStmt;
	}
	
	public Expr getExpr() {
		return expr;
	}

	public void setExpr(Expr expr) {
		this.expr = expr;
	}

	public Statement getStmt() {
		return stmt;
	}

	public void setStmt(Statement stmt) {
		this.stmt = stmt;
	}

	public void genC(PW pw){
	}
	
	public void genKra(PW pw){
		pw.printIdent("while (");
		this.expr.genKra(pw, false);
		pw.println(")");
		pw.add();
		this.stmt.genKra(pw);
		pw.sub();
	}
}
