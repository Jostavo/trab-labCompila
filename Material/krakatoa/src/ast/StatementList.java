/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author enrique
 */
public class StatementList {
    private ArrayList<Statement> statements;
    private boolean hasReturn;
    
    public StatementList() {
        this.statements = new ArrayList<Statement>();
        this.hasReturn = false;
    }
    
    public void addElement(Statement s) {
        if (s instanceof ReturnStatement) {
            this.hasReturn = true;
        }
        this.statements.add(s);
    }
    
    public boolean getReturnFlag() {
        return this.hasReturn;
    }
    
    public Iterator<Statement> elements() {
        return this.statements.iterator();
    }
}
