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
    
    public void genC(PW pw){
        for(Variable aux: this.instanceVariableList){
            aux.genC(pw);
            pw.println(";");
        }
    }

    private ArrayList<InstanceVariable> instanceVariableList;

}
