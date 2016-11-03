package ast;

public class LiteralInt extends Expr {
    
    public LiteralInt( int value ) { 
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
        pw.printIdent("" + value);
    }
    
    @Override
    public void genKra( PW pw, boolean putParenthesis ) {
        pw.print(String.valueOf(this.value));
    }
    
    public Type getType() {
        return Type.intType;
    }
    
    private int value;
}
