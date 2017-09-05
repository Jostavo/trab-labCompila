// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

import java.util.Iterator;

public class MessageSendToVariable extends MessageSend {

    private Method m;
    private InstanceVariable iv;
    private ExprList el;
    private KraClass k;
    private String classVariableName;

    public MessageSendToVariable(InstanceVariable iv, String classVariableName, KraClass k) {
        this.classVariableName = classVariableName;
        this.iv = iv;
        this.k = k;
    }

    public MessageSendToVariable(Method m, String classVariableName, ExprList el, KraClass k) {
        this.classVariableName = classVariableName;
        this.m = m;
        this.el = el;
        this.k = k;
    }

    public Type getType() {
        return this.m.getType();
    }

    public String getMethodName() {
        return this.m.getName();
    }

    public String getClassVariableName() {
        return this.classVariableName;
    }

    @Override
    public void genC(PW pw, boolean putParenthesis) {
        if (putParenthesis) {
            pw.print("(");
        }
        if (this.iv != null) {
            pw.print("_" + this.classVariableName + "->_" + this.k.getName() + "_" + this.iv.getName());
        } else if (this.m != null) {
            int methodPosition = this.k.getMethodPosition(this.m.getName());

            pw.print("( (" + this.m.getType().getCname() + " (*) (" + this.k.getCname() + " *");

            if (el != null) {
                Iterator<Expr> eItr = el.elements();

                while (eItr.hasNext()) {
                    Expr aux = eItr.next();

                    pw.print(", " + aux.getType().getCname());
                }
            }

            pw.print(")) _" + this.classVariableName + "->vt[" + methodPosition + "] )(_" + this.classVariableName);

            if (el != null) {
                pw.print(", ");
                el.genC(pw);
            }

            pw.print(")");
        }

        if (putParenthesis) {
            pw.print(")");
        }
    }
    
    @Override
    public void genKra(PW pw, boolean putParenthesis) {
        if (putParenthesis) {
            pw.print("(");
        }
        if (this.iv != null) {
            pw.print(this.classVariableName + "." + this.iv.getName());
        } else if (this.m != null) {
        	pw.print(this.classVariableName + "." + this.m.getName() + "(");

            if (el != null) {
                pw.print(", ");
                el.genC(pw);
            }

            pw.print(")");
        }

        if (putParenthesis) {
            pw.print(")");
        }
    }

}
