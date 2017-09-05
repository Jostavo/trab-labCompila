// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

/**
 *
 * @author enrique
 */
public class MinusMinusStatement extends Statement {
    private Expr e;
    
    public MinusMinusStatement(Expr e) {
        this.e = e;
    }

    @Override
    public void genC(PW pw) {
        pw.printIdent("--");
        e.genC(pw, true);
        pw.println(";");
    }
    
    @Override
    public void genKra(PW pw) {
        pw.printIdent("--");
        e.genKra(pw, false);
        pw.println(";");
    }
}
