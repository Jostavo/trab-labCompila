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
public class LocalDecStatement extends Statement {
    private LocalVariableList lvl;
    private Type t;
    
    public LocalDecStatement(Type t, LocalVariableList lvl) {
        this.lvl = lvl;
        this.t = t;
    }

    @Override
    public void genC(PW pw) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}