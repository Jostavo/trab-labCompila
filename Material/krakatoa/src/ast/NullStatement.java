// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

/**
 *
 * @author enrique
 */
public class NullStatement extends Statement {
    public NullStatement() {}
    
    @Override
    public void genC(PW pw) {
        pw.println(";");
    }
    
    @Override
    public void genKra(PW pw) {
        pw.println(";");
    }
    
}
