package ast;


public class MessageSendToVariable extends MessageSend {
    private Method m;
    private InstanceVariable iv;
    private ExprList el;
    private String classVariableName;
    
    public MessageSendToVariable(InstanceVariable iv, String classVariableName) {
        this.classVariableName = classVariableName;
        this.iv = iv;
    }
    
    public MessageSendToVariable(Method m, String classVariableName, ExprList el) {
        this.classVariableName = classVariableName;
        this.m = m;
        this.el = el;
    }

    public Type getType() { 
        return this.m.getType();
    }
    
    public String getMethodName() {
        return this.m.getName();
    }
    
    public String getClassVariableName() {
        return this.classVariableName;
    }
     
    public void genC( PW pw, boolean putParenthesis ) {
        
    }

    
}    