package ast;

import java.util.*;

public class ParamList {

    public ParamList() {
       paramList = new ArrayList<Variable>();
    }

    public ArrayList<Variable> getParamList(){
        return paramList;
    }

    public void addElement(Variable v) {
       paramList.add(v);
    }

    public void addList(ArrayList<Variable> listaParametros){
        for(Variable v: listaParametros){
            this.addElement(v);
        }
    }

    public Iterator<Variable> elements() {
        return paramList.iterator();
    }

    public int getSize() {
        return paramList.size();
    }

    private ArrayList<Variable> paramList;

}
