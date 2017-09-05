// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

public class NullExpr extends Expr {

    public void genC(PW pw, boolean putParenthesis) {
        if (putParenthesis) {
            pw.print("(");
        }
        pw.print("NULL");
        if (putParenthesis) {
            pw.print(")");
        }
    }
    
    public void genKra(PW pw, boolean putParenthesis) {
        if (putParenthesis) {
            pw.print("(");
        }
        pw.print("null");
        if (putParenthesis) {
            pw.print(")");
        }
    }

    public Type getType() {
        //# corrija
        return Type.nullType;
    }
}
