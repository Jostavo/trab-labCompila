// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

/**
 *
 * @author enrique
 */
public class WhileStatement extends Statement {
    private Expr e;
    private Statement s;

    public WhileStatement(Expr e, Statement s) {
        this.e = e;
        this.s = s;
    }

    @Override
    public void genC(PW pw) {
        pw.print("while(");
        e.genC(pw, false);
        pw.println("){");
        pw.add();
        s.genC(pw);
        pw.sub();
        pw.println("}");
    }
    
    @Override
    public void genKra(PW pw) {
        pw.print("while(");
        e.genKra(pw, false);
        pw.println("){");
        pw.add();
        s.genKra(pw);
        pw.sub();
        pw.println("}");
    }
}
