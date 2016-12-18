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
public class IfStatement extends Statement {
    private Expr e;
    private Statement sIf, sElse;
    
    public IfStatement(Expr e, Statement sIf, Statement sElse) {
        this.e = e;
        this.sIf = sIf;
        this.sElse = sElse;
    }
    
    @Override
    public void genC(PW pw) {
        pw.printIdent("if");
        e.genC(pw, false);
        pw.println("{");
        pw.add();
        sIf.genC(pw);
        pw.sub();
        if(sElse != null){
            pw.printlnIdent("}else{");
            pw.add();
            sElse.genC(pw);
            pw.sub();
        }
        pw.printlnIdent("}");
    }
    
}
