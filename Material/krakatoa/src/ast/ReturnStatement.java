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
public class ReturnStatement extends Statement {
    public Expr e;
    
    public ReturnStatement(Expr e) {
        this.e = e;
    }

    @Override
    public void genC(PW pw) {
        pw.printIdent("return ");
        e.genC(pw, false);
        pw.println(";");
    }
    
}
