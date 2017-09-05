// Enrique Sampaio dos Santos
// Gustavo Rodrigues
package ast;

/**
 *
 * @author enrique
 */
public class DoWhileStatement extends Statement {
    private CompositeStatement cs;
    private Expr e;
    
    public DoWhileStatement(CompositeStatement cs, Expr e) {
        this.cs = cs;
        this.e = e;
    }

    @Override
    public void genC(PW pw) {
        pw.printIdent("do ");
        cs.genC(pw);
        pw.print(" while ");
        e.genC(pw, true);
        pw.println(";");
    }
    
    @Override
    public void genKra(PW pw) {
        pw.printIdent("do ");
        cs.genKra(pw);
        pw.print(" while ");
        e.genKra(pw, true);
        pw.println(";");
    }
    
}
