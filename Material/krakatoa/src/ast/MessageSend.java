package ast;


abstract class MessageSend  extends Expr  {
    
     abstract public void genC(PW pw, boolean putParenthesis);
}

