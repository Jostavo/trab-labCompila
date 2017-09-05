// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author enrique
 */
public class CompositeStatement extends Statement {
    private StatementList sl;
    
    public CompositeStatement(StatementList sl) {
        this.sl = sl;
    }

    @Override
    public void genC(PW pw) {
        pw.println("{");
        pw.add();
        sl.genC(pw);
        pw.sub();
        pw.printIdent("}");
    }
    
    @Override
    public void genKra(PW pw) {
        pw.println("{");
        pw.add();
        sl.genKra(pw);
        pw.sub();
        pw.printIdent("}");
    }
    
}
