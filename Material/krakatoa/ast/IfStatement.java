package ast;

public class IfStatement extends Statement {
	private Expr expr;
	private Statement ifStmt, elseStmt;
	
	public IfStatement(Expr ifExpr, Statement ifStmt, Statement elseStmt) {
		this.expr = ifExpr;
		this.ifStmt = ifStmt;
		this.elseStmt = elseStmt;
	}
	
	public Statement getElseStmt() {
		return elseStmt;
	}

	public void setElseStmt(Statement elseStmt) {
		this.elseStmt = elseStmt;
	}

	public Statement getIfStmt() {
		return ifStmt;
	}

	public void setIfStmt(Statement ifStmt) {
		this.ifStmt = ifStmt;
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
	}
}
