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
public class WhileStatement extends Statement {
    private Expr e;
    private Statement s;

    public WhileStatement(Expr e, Statement s) {
        this.e = e;
        this.s = s;
    }

    @Override
    public void genC(PW pw) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
