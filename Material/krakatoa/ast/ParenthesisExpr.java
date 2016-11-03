package ast;

public class ParenthesisExpr extends Expr {
    
    public ParenthesisExpr( Expr expr ) {
        this.expr = expr;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
        pw.print("(");
        expr.genC(pw, false);
        pw.printIdent(")");
    }
    
    public void genKra( PW pw, boolean putParenthesis ) {
        pw.print("(");
        expr.genKra(pw, false);
        pw.println(")");
    }
    
    public Type getType() {
        return expr.getType();
    }
    
    private Expr expr;
}