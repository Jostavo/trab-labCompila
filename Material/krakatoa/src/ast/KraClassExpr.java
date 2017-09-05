// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

/**
 *
 * @author enrique
 */
public class KraClassExpr extends Expr {
    private KraClass k;
    
    public KraClassExpr(KraClass k) {
        this.k = k;
    }
    
    public KraClass getKraClass() {
        return this.k;
    }

    @Override
    public void genC(PW pw, boolean putParenthesis) {
        if (putParenthesis) {
            pw.print("(");
        }
        pw.print("new_"+this.k.getName() + "()");
        if (putParenthesis) {
            pw.print(")");
        }
    }
    
    @Override
    public void genKra(PW pw, boolean putParenthesis) {
        if (putParenthesis) {
            pw.print("(");
        }
        pw.print("new " + this.k.getName() + "()");
        if (putParenthesis) {
            pw.print(")");
        }
    }

    @Override
    public Type getType() {
        return this.k;
    }
}
