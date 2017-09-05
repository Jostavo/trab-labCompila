// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

import java.util.Iterator;
import lexer.Symbol;

public class MessageSendToSelf extends MessageSend {

    private KraClass k;
    private Method m;
    private InstanceVariable iv;
    private ExprList el;
    private KraClass k2;

    public MessageSendToSelf(KraClass k, InstanceVariable iv) {
        this.k = k;
        this.iv = iv;
    }

    public MessageSendToSelf(KraClass k, Method m, ExprList el) {
        this.k = k;
        this.m = m;
        this.el = el;
    }
    
    public MessageSendToSelf(KraClass k, Method m, ExprList el, InstanceVariable iv, KraClass k2) {
        this.k = k;
        this.m = m;
        this.el = el;
        this.iv = iv;
        this.k2 = k2;
    }

    public MessageSendToSelf(KraClass k) {
        this.k = k;
    }

    public Type getType() {
        if (this.iv != null && this.m != null) {
            return this.m.getType();
        } else if (this.iv != null) {
            return this.iv.getType();
        } else if (this.m != null) {
            return this.m.getType();
        } else {
            return this.k;
        }
    }

    @Override
    public void genC(PW pw, boolean putParenthesis) {
        if (putParenthesis) {
            pw.print("(");
        }
        if (this.iv != null && this.m != null) {
            pw.print("( (" + this.m.getType().getCname() + " (*) (" + this.iv.getType().getCname() + " *");
            
            if (el != null) {
                Iterator<Expr> eItr = el.elements();

                while (eItr.hasNext()) {
                    Expr aux = eItr.next();

                    pw.print(", " + aux.getType().getCname());
                }
            }
            
            pw.print(")) this->_" + this.k.getName() + "_" + this.iv.getName() + ".vt[" + this.k2.getMethodPosition(this.m.getName()) + "] )(&this->_" + this.k.getName() + "_" + this.iv.getName());
            
            if (el != null) {
                pw.print(", ");
                el.genC(pw);
            }

            pw.print(")");
        } else if (this.iv != null) {
            pw.print("this->_" + this.k.getName() + "_" + this.iv.getName());
        } else if (this.m != null) {
            pw.print("( (" + this.m.getType().getCname() + " (*) (" + this.k.getCname() + " *");

            if (el != null) {
                Iterator<Expr> eItr = el.elements();

                while (eItr.hasNext()) {
                    Expr aux = eItr.next();

                    pw.print(", " + aux.getType().getCname());
                }
            }

            if (this.k.isPrivate(this.m.getName())) {
            	pw.print(")) _" + this.k.getName() + "_" + this.m.getName() + " )(this");
            } else {
            	pw.print(")) this->vt[" + this.k.getMethodPosition(this.m.getName()) + "] )(this");
            }

            if (el != null) {
                pw.print(", ");
                el.genC(pw);
            }

            pw.print(")");
        } else {
            pw.print("this");
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
        if (this.iv != null && this.m != null) {
        	pw.print("this." + this.k2.getName() + "." + this.m.getName() + "(");
            
        	if (el != null) {
                el.genKra(pw);
            }

            pw.print(")");
        } else if (this.iv != null) {
            pw.print("this." + this.iv.getName());
        } else if (this.m != null) {
        	pw.print("this." + this.m.getName() + "(");
        	
            if (el != null) {
                pw.print(", ");
                el.genKra(pw);
            }

            pw.print(")");
        } else {
            pw.print("this");
        }

        if (putParenthesis) {
            pw.print(")");
        }
    }

}
