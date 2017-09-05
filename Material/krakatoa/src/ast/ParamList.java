// Enrique Sampaio dos Santos
// Gustavo Rodrigues

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
            if(aux != paramList.get(paramList.size()-1))
                pw.print(", ");
        }
    }
    
    public void genKra(PW pw){
        for(Parameter aux: paramList){
            aux.genKra(pw);
            if(aux != paramList.get(paramList.size()-1))
                pw.print(", ");
        }
    }

    private ArrayList<Parameter> paramList;

}
