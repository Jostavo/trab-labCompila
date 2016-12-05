package ast;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author enrique
 */
public class MethodList {
    private ArrayList<Method> methodList;
    
    public MethodList() {
        methodList = new ArrayList<Method>();
    }
    
    public void addElement(Method v) {
       methodList.add(v);
    }
    
    public Iterator<Method> elements() {
        return methodList.iterator();
    }

    public int getSize() {
        return methodList.size();
    }
}
