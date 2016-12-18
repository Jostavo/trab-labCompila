package ast;

public class VariableExpr extends Expr {
    
    public VariableExpr( Variable v ) {
        this.v = v;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
        if(putParenthesis)
            pw.print("(");
        pw.print(v.getName());
        if(putParenthesis)
            pw.print(")");
    }
    
    public Type getType() {
        return v.getType();
    }
    
    public Variable getVar() {
        return this.v;
    }
    
    private Variable v;
}