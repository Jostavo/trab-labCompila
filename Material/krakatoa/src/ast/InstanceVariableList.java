// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

import java.util.*;

public class InstanceVariableList {

    public InstanceVariableList() {
       instanceVariableList = new ArrayList<InstanceVariable>();
    }

    public void addElement(InstanceVariable instanceVariable) {
       instanceVariableList.add( instanceVariable );
    }

    public Iterator<InstanceVariable> elements() {
    	return this.instanceVariableList.iterator();
    }

    public int getSize() {
        return instanceVariableList.size();
    }
    
    public void genC(PW pw, String className){
        for(InstanceVariable aux: this.instanceVariableList){
            aux.genC(pw, className);
            pw.println(";");
        }
    }
    
    public void genKra(PW pw){
        for(InstanceVariable aux: this.instanceVariableList){
        	pw.printIdent("private ");
            aux.genKra(pw);
            pw.println(";");
        }
    }

    private ArrayList<InstanceVariable> instanceVariableList;

}
