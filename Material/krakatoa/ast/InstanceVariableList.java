package ast;

import java.util.*;

public class InstanceVariableList {

    public InstanceVariableList() {
       instanceVariableList = new ArrayList<InstanceVariable>();
    }

    public void addElement(InstanceVariable instanceVariable) {
       instanceVariableList.add( instanceVariable );
    }

    public void addElement(String name, Type type){
        InstanceVariable v = new InstanceVariable(name, type);
        this.addElement(v);
    }

    public Iterator<InstanceVariable> elements() {
    	return this.instanceVariableList.iterator();
    }

    public int getSize() {
        return instanceVariableList.size();
    }

    public void addList(InstanceVariableList listaVariaveis){
    	Iterator<InstanceVariable> instanceVariableIterator = listaVariaveis.elements();
    	while (instanceVariableIterator.hasNext()) {
    		this.addElement(instanceVariableIterator.next());
    	}
    }

    public InstanceVariable getVariable(String m){
        for(InstanceVariable aux: instanceVariableList){
            if(aux.getName().equals(m))
                return aux;
        }

        return null;
    }

    public boolean contains(String m){
        if(this.getVariable(m) != null){
            return true;
        }

        return false;
    }
    
    public void genKra(PW pw) {
    	for(InstanceVariable v: instanceVariableList) {
    		v.genKra(pw);
    	}
    }

    private ArrayList<InstanceVariable> instanceVariableList;
}
