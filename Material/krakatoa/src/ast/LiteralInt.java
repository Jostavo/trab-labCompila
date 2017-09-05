// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

public class LiteralInt extends Expr {
    
    public LiteralInt( int value ) { 
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    public void genC( PW pw, boolean putParenthesis ) {
    	if (putParenthesis) {
			pw.print("(");
		}
        pw.print(""+value);
        if (putParenthesis) {
			pw.print(")");
		}
    }
    
    public void genKra( PW pw, boolean putParenthesis ) {
    	if (putParenthesis) {
			pw.print("(");
		}
        pw.print(""+value);
        if (putParenthesis) {
			pw.print(")");
		}
    }
    
    public Type getType() {
        return Type.intType;
    }
    
    private int value;
}
