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
        /*pw.printIdent(t.getName() + " ");
        for(Variable aux: lvl.getLista()){
            pw.print(aux.getName());
            if(aux != lvl.getLista().get(lvl.getSize()-1)){
                pw.print(", ");
            }
        }
        pw.print(";");
        pw.println();*/
    }
    
}
