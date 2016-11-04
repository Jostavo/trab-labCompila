package ast;

public class MessageSendToSuper extends MessageSend { 
    
    public MessageSendToSuper(ExprList list){
        this.list = list;
    }

    public Type getType() { 
        return null;
    }

    public void genC( PW pw, boolean putParenthesis ) {
        
    }
    
    public void genKra( PW pw, boolean putParenthesis ) {
        
    }
    
    ExprList list;
    
}