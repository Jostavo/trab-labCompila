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
public class NullStatement extends Statement {
    public NullStatement() {}
    
    @Override
    public void genC(PW pw) {
        pw.print("NULL");
    }
    
}
