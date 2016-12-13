/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
