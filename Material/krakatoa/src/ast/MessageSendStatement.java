// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

public class MessageSendStatement extends Statement { 


   @Override
   public void genC( PW pw ) {
      pw.printIdent("");
      messageSend.genC(pw, false);
      pw.println(";");
   }
   
   @Override
   public void genKra( PW pw ) {
      pw.printIdent("");
      messageSend.genKra(pw, false);
      pw.println(";");
   }

   private MessageSend messageSend;

}


