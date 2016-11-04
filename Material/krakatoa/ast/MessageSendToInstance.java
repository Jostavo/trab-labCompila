/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

/**
 *
 * @author darkkgreen
 */
public class MessageSendToInstance extends MessageSend {
    
    Variable v1, v2;
    
    public MessageSendToInstance(Variable v1, Variable v2){
        this.v1 = v1;
        this.v2 = v2;
    }
   
    public Type getType(){
        return null;
    }

    @Override
    public void genC(PW pw, boolean putParenthesis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void genKra(PW pw, boolean putParenthesis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
