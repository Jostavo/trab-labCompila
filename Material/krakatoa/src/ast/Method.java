/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

import java.util.Iterator;

/**
 *
 * @author enrique
 */
public class Method extends Variable {

    private StatementList sl;
    private ParamList pl;
    private boolean hasReturn;

    public Method(String nome, Type tipo) {
        super(nome, tipo);
        this.hasReturn = false;
    }
    
    public void setHasReturn(boolean hasReturn) {
        this.hasReturn = hasReturn;
    }
    
    public boolean getHasReturn() {
        return this.hasReturn;
    }

    public void setStatementList(StatementList sl) {
        this.sl = sl;
    }

    public StatementList getStatementList() {
        return this.sl;
    }

    public void setParamList(ParamList pl) {
        this.pl = pl;
    }

    public ParamList getParamList() {
        return this.pl;
    }

    public Parameter getParam(String paramName) {
        if (this.pl != null) {
            Iterator<Parameter> pItr = this.pl.elements();

            while (pItr.hasNext()) {
                Parameter p = pItr.next();

                if (p.getName().equals(paramName)) {
                    return p;
                }
            }
        }

        return null;
    }
    
    public void genC(PW pw, String mother){
        if(this.getType().getName().equals("String")){
            pw.println(this.getType().getCname() + "_" + mother + "_" + this.getName());
        }else{
            pw.println(this.getType().getCname() + " _" + mother + "_" + this.getName());
        }
        
        pw.print("( _class_" + mother + " *this");
        if(this.getParamList().getSize() != 0){
            pw.print(", ");
            //this.getParamList().genC(pw);
        }
        pw.print(" )");
        pw.println("{");
        pw.add();
        //
        pw.sub();
        pw.println("}");
    }
}
