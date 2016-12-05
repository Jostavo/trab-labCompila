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
public class Method extends Variable {
    private StatementList sl;
    
    public Method(String nome, Type tipo){
    	super(nome, tipo);
    }
    
    public void setStatementList(StatementList sl) {
        this.sl = sl;
    }
    
    public StatementList getStatementList() {
        return this.sl;
    }
}
