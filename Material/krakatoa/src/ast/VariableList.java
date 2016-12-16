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
public class VariableList {
    public VariableList() {
       this.variableList = new ArrayList<Variable>();
    }

    public void addElement(Variable variable) {
       this.variableList.add( variable );
    }

    public Iterator<Variable> elements() {
    	return this.variableList.iterator();
    }

    public int getSize() {
        return variableList.size();
    }


    private ArrayList<Variable> variableList;

}
