package ast;


public class MessageSendToSelf extends MessageSend {
    private KraClass k;
    private Method m;
    private InstanceVariable iv;
    private ExprList el;
    
    public MessageSendToSelf(KraClass k, InstanceVariable iv) {
        this.k = k;
        this.iv = iv;
    }
    
    public Type getType() { 
        return this.iv.getType();
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
    }
    
    
}