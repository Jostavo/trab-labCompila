// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

/**
 *
 * @author enrique
 */
public class ReturnStatement extends Statement {
    public Expr e;
    
    public ReturnStatement(Expr e) {
        this.e = e;
    }

    @Override
    public void genC(PW pw) {
        pw.printIdent("return ");
        e.genC(pw, false);
        pw.println(";");
    }
    
    @Override
    public void genKra(PW pw) {
        pw.printIdent("return ");
        e.genKra(pw, false);
        pw.println(";");
    }
    
}
