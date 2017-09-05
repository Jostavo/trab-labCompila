// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

import java.util.*;

public class LocalVariableList {

    public LocalVariableList() {
       localList = new ArrayList<Variable>();
    }

    public void addElement(Variable v) {
       localList.add(v);
    }

    public Iterator<Variable> elements() {
        return localList.iterator();
    }

    public int getSize() {
        return localList.size();
    }
    
    public ArrayList<Variable> getLista(){
        return localList;
    }

    private ArrayList<Variable> localList;

}
