package ast;

import java.util.*;

public class MethodList {
    private ArrayList<Method> listaMetodos;

    public MethodList(){
        this.listaMetodos = new ArrayList<Method>();
    }

    public Method getMethod(String m){
        for(Method aux: listaMetodos){
            if(aux.getName().equals(m))
                return aux;
        }

        return null;
    }

    public boolean addMethod(Method m){
        return listaMetodos.addElement(m);
    }

    public boolean contains(String m){
        if(this.getMethod(m) != null){
            return true;
        }

        return false;
    }
}