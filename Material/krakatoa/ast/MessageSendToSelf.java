package ast;


public class MessageSendToSelf extends MessageSend {
    
    KraClass classe;
    Method method;
    ExprList list;
    InstanceVariable variable;
    
    public MessageSendToSelf(KraClass c, Method m, ExprList e){
        this.classe = c;
        this.list = e;
        this.method = m;
        this.variable = null;
    }
    
    public MessageSendToSelf(KraClass c, InstanceVariable v){
        this.classe = c;
        this.list = null;
        this.method = null;
        this.variable = v;
    }
    
    public MessageSendToSelf(KraClass c){
        this.classe = c;
        this.list = null;
        this.method = null;
        this.variable = null;
    }
    
    public Type getType() { 
        return null;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
    }
    
    public void genKra( PW pw, boolean putParenthesis ) {
    }
    
    
}