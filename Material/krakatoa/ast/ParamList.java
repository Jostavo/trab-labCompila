package ast;

import java.util.*;

public class ParamList {

    public ParamList() {
       paramList = new ArrayList<Parameter>();
    }

    public ArrayList<Parameter> getParamList(){
        return paramList;
    }

    public void addElement(Parameter p) {
       paramList.add(p);
    }

    public void addList(ArrayList<Parameter> listaParametros){
        for(Parameter p: listaParametros){
            this.addElement(p);
        }
    }

    public Iterator<Parameter> elements() {
        return paramList.iterator();
    }

    public int getSize() {
        return paramList.size();
    }

    private ArrayList<Parameter> paramList;

}
