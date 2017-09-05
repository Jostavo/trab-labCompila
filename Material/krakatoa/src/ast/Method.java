// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

import java.util.Iterator;
import lexer.Symbol;

/**
 *
 * @author enrique
 */
public class Method extends Variable {

    private StatementList sl;
    private ParamList pl;
    private boolean hasReturn;
    private Symbol qualifier;

    public Method(String nome, Type tipo, Symbol qualifier) {
        super(nome, tipo);
        this.hasReturn = false;
        this.pl = new ParamList();
        this.sl = new StatementList();
        this.qualifier = qualifier;
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
    
    public void genC(PW pw, String className){
        if(this.getType() == Type.stringType){
            pw.print(this.getType().getCname() + "_" + className + "_" + this.getName());
        }else{
            pw.print(this.getType().getCname() + " _" + className + "_" + this.getName());
        }
        
        pw.print("( _class_" + className + " *this");
        if(this.getParamList().getSize() > 0){
            pw.print(", ");
            this.getParamList().genC(pw);
        }
        pw.print(" )");
        pw.println("{");
        pw.add();
        this.getStatementList().genC(pw);
        pw.sub();
        pw.printlnIdent("}");
        pw.println();
    }
    
    public void genKra(PW pw){
    	pw.printlnIdent(qualifier + " " + this.getType().getName() + " " + this.getName() + " (");
    	if(this.getParamList().getSize() > 0) {
            this.getParamList().genKra(pw);
        }
        pw.print(" )");
        pw.println("{");
    	pw.add();
    	this.getStatementList().genKra(pw);
    	pw.sub();
    	pw.printlnIdent("}");
        pw.println();
    }
}
