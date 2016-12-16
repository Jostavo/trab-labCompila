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
public class PlusPlusStatement extends Statement {
    private Variable v;
    
    public PlusPlusStatement(Variable v) {
        this.v = v;
    }

    @Override
    public void genC(PW pw) {
        pw.print(v.getName() + "++");
    }
}
