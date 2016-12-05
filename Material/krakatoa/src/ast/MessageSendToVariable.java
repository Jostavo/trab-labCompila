package ast;


public class MessageSendToVariable extends MessageSend {
    private Type t;
    
    public MessageSendToVariable(Type t) {
        this.t = t;
    }

    public Type getType() { 
        return null;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
        
    }

    
}    