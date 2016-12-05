package ast;

public class MessageSendToSuper extends MessageSend { 
    private ExprList el;
    
    public MessageSendToSuper(ExprList el) {
        this.el = el;
    }
    
    public Type getType() { 
        return null;
    }

    public void genC( PW pw, boolean putParenthesis ) {
        
    }
    
}