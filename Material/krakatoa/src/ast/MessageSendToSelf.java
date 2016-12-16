package ast;

import lexer.Symbol;


public class MessageSendToSelf extends MessageSend {
    private KraClass k;
    private Method m;
    private InstanceVariable iv;
    private ExprList el;
    
    public MessageSendToSelf(KraClass k, InstanceVariable iv) {
        this.k = k;
        this.iv = iv;
    }
    
    public MessageSendToSelf(KraClass k, Method m, ExprList el) {
        this.k = k;
        this.m = m;
        this.el = el;
    }
    
    public MessageSendToSelf(KraClass k) {
        this.k = k;
    }
    
    public Type getType() { 
        if (this.iv != null) {
            return this.iv.getType();
        } else if (this.m != null) {
            return this.m.getType();
        } else {
            return this.k;
        }
    }
    
    @Override
    public void genC(PW pw, boolean putParenthesis) {
       /* if(m.getQual() == Symbol.PUBLIC){
            só jesus salva
        }else{
            pw.print("_"+ nome + "_" + nomeMet + "(this");
            <param>
        }*/
    }
    
}