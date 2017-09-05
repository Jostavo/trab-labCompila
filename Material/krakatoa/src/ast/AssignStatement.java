// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

/**
 *
 * @author enrique
 */
public class AssignStatement extends Statement {

    private Expr left;
    private Expr right;

    public AssignStatement(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void genC(PW pw) {
        pw.printIdent("");
        left.genC(pw, false);
        if (right != null) {
            pw.print(" = ");
            right.genC(pw, false);
            pw.println(";");
        }

    }
    
    @Override
    public void genKra(PW pw) {
    	pw.printIdent("");
        left.genKra(pw, false);
        if (right != null) {
            pw.print(" = ");
            right.genKra(pw, false);
            pw.println(";");
        }
    }

}
