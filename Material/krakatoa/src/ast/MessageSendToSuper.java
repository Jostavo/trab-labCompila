// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

import java.util.Iterator;

public class MessageSendToSuper extends MessageSend {

    private ExprList el;
    private Method m;
    private KraClass k;

    public MessageSendToSuper(Method m, ExprList el, KraClass k) {
        this.m = m;
        this.el = el;
        this.k = k;
    }

    public Type getType() {
        return m.getType();
    }

    @Override
    public void genC(PW pw, boolean putParenthesis) {
    	KraClass owner = this.k.getSuperOwner(this.m.getName());
    	
        if (putParenthesis) {
            pw.print("(");
        }

        pw.print("_" + owner.getName() + "_" + this.m.getName() + "( (" + owner.getCname() + " *) this");

        if (this.el != null) {
            pw.print(", ");
            el.genC(pw);
        }

        pw.print(" )");

        if (putParenthesis) {
            pw.print(")");
        }
    }
    
    @Override
    public void genKra(PW pw, boolean putParenthesis) {
        if (putParenthesis) {
            pw.print("(");
        }

        pw.print("super." + this.m.getName() + "(");

        if (this.el != null) {
            pw.print(", ");
            el.genKra(pw);
        }

        pw.print(" )");

        if (putParenthesis) {
            pw.print(")");
        }
    }

}
