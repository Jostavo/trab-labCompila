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
public class ReadStatement extends Statement {
    private VariableList vl;
    
    public ReadStatement(VariableList vl) {
        this.vl = vl;
    }

    @Override
    public void genC(PW pw) {
        
    }
}
