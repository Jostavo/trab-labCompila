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
public class KraClassExpr extends Expr {
    private KraClass k;
    
    public KraClassExpr(KraClass k) {
        this.k = k;
    }

    @Override
    public void genC(PW pw, boolean putParenthesis) {
        pw.println(this.k.getCname() + "()");
    }

    @Override
    public Type getType() {
        return this.k;
    }
}
