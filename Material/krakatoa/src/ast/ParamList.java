package ast;

import java.util.*;

public class ParamList {

    public ParamList() {
       paramList = new ArrayList<Parameter>();
    }

    public void addElement(Parameter p) {
       paramList.add(p);
    }

    public Iterator<Parameter> elements() {
        return paramList.iterator();
    }

    public int getSize() {
        return paramList.size();
    }
    
    public void genC(PW pw){
        for(Parameter aux: paramList){
            aux.genC(pw);
        }
    }

    private ArrayList<Parameter> paramList;

}
