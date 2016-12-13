package ast;


public class MessageSendToVariable extends MessageSend {
    private Type t;
    private String methodName;
    private String classVariableName;
    
    public MessageSendToVariable(Type t, String classVariableName, String methodName) {
        this.t = t;
        this.classVariableName = classVariableName;
        this.methodName = methodName;
    }

    public Type getType() { 
        return this.t;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public String getClassVariableName() {
        return this.classVariableName;
    }
     
    public void genC( PW pw, boolean putParenthesis ) {
        
    }

    
}    