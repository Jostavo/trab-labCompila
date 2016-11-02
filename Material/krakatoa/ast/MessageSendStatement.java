package ast;

public class MessageSendStatement extends Statement { 


   public void genC( PW pw ) {
      pw.printIdent("");
      // messageSend.genC(pw);
      pw.println(";");
   }

   private MessageSend  messageSend;

}


