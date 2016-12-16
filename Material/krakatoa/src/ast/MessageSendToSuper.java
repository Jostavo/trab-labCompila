package ast;

public class MessageSendToSuper extends MessageSend { 
    private ExprList el;
    private Method m;
    
    public MessageSendToSuper(Method m, ExprList el) {
        this.m = m;
        this.el = el;
    }
    
    public Type getType() { 
        return m.getType();
    }

    @Override
    public void genC( PW pw, boolean putParenthesis) {
        
    }
    
}