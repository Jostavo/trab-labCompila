// Enrique Sampaio dos Santos
// Gustavo Rodrigues

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

    public ArrayList<Variable> getList(){
        return variableList;
    }

    private ArrayList<Variable> variableList;

}
