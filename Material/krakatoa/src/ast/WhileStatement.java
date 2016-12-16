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
        pw.print("while(");
        e.genC(pw, false);
        pw.println("){");
        pw.add();
        s.genC(pw);
        pw.sub();
        pw.println("}");
    }
}
