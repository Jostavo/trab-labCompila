package ast;

public class MessageSendStatement extends Statement { 


   public void genC( PW pw ) {
      pw.printIdent("");
      // messageSend.genC(pw);
      pw.println(";");
   }
   
   public void genKra( PW pw ) {
	   pw.printIdent("");
	   messageSend.genKra(pw, false);
	   pw.println(";");
   }

   private MessageSend  messageSend;

}


