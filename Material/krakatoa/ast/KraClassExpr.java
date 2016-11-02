package ast;

import lexer.*;

public class KraClassExpr extends Expr {
	public KraClassExpr(KraClass aClass) {
		this.kraClass = aClass;
	}
	
	@Override
	public void genC( PW pw, boolean putParenthesis ) {
		pw.println(this.kraClass.getCname() + "()");
	}
	
	@Override
	public Type getType() {
		return kraClass;
	}
	
	private KraClass kraClass;
}
