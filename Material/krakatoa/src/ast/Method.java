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

    public Method(String nome, Type tipo) {
        super(nome, tipo);
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

    public Variable getParam(String paramName) {
        if (this.pl != null) {
            Iterator<Variable> vItr = this.pl.elements();

            while (vItr.hasNext()) {
                Variable v = vItr.next();

                if (v.getName().equals(paramName)) {
                    return v;
                }
            }
        }

        return null;
    }
}
