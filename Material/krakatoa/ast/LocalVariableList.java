package ast;

import java.util.*;

public class LocalVariableList {

    public LocalVariableList() {
       localList = new ArrayList<Variable>();
    }
    
    public ArrayList<Variable> getLocalVariableList(){
        return localList;
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
    
    public void genKra(PW pw) {
    	for (Variable v: localList) {
    		pw.printlnIdent(v.getType().getName() + " " + v.getName() + ";");
    	}
    }

    private ArrayList<Variable> localList;

}
