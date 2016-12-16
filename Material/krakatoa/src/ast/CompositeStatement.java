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
        sl.genC(pw);
    }
    
}
