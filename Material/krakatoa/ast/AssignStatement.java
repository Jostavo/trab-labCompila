/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

/**
 *
 * @author darkkgreen
 */
public class AssignStatement extends Statement{
    Expr dir;
    Expr esq;
    
    public AssignStatement(Expr dir, Expr esq){
        this.dir = dir;
        this.esq = esq;
    }
    
    public void genC(PW pw){
    }

    public void genKra(PW pw){
            pw.printlnIdent("break");
    }
    
}
