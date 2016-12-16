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
public class WriteStatement extends Statement {
    private ExprList el;
    private boolean isLn;
    
    public WriteStatement(ExprList el) {
        this.el = el;
        this.isLn = false;
    }
    
    public WriteStatement(ExprList el, boolean isLn) {
        this.el = el;
        this.isLn = isLn;
    }

    @Override
    public void genC(PW pw) {
                
    }
    
}
