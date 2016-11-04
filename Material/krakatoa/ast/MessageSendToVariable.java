package ast;


public class MessageSendToVariable extends MessageSend { 
    
    public MessageSendToVariable(Variable v, Method m, ExprList l){
        this.variable = v;
        this.method = m;
        this.list = l;
    }

    public Type getType() { 
        return null;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
        
    }
    
    public void genKra( PW pw, boolean putParenthesis ) {
        
    }

    Variable variable;
    Method method;
    ExprList list;
}    