// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;


abstract class MessageSend  extends Expr  {
    
     abstract public void genC(PW pw, boolean putParenthesis);
     abstract public void genKra(PW pw, boolean putParenthesis);
}

