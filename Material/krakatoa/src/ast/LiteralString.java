// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

public class LiteralString extends Expr {
    
    public LiteralString( String literalString ) { 
        this.literalString = literalString;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
    	if (putParenthesis) {
			pw.print("(");
		}
        pw.print("\"" + literalString + "\"");
        if (putParenthesis) {
			pw.print(")");
		}
    }
    
    public void genKra( PW pw, boolean putParenthesis ) {
    	if (putParenthesis) {
			pw.print("(");
		}
        pw.print("\"" + literalString + "\"");
        if (putParenthesis) {
			pw.print(")");
		}
    }
    
    public Type getType() {
        return Type.stringType;
    }
    
    private String literalString;
}
