// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

/**
 *
 * @author enrique
 */
public class BreakStatement extends Statement {
    public BreakStatement() {}
    
    @Override
    public void genC(PW pw) {
        pw.printlnIdent("break;");
    }
    
    @Override
    public void genKra(PW pw) {
    	pw.printlnIdent("break;");
    }
    
}
