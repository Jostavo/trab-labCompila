package ast;

public class NullExpr extends Expr {
    
   public void genC( PW pw, boolean putParenthesis ) {
      pw.printIdent("NULL");
   }
   
   @Override
   public void genKra(PW pw, boolean putParenthesis) {
	   pw.printIdent("null");
   }
   
   public Type getType() {
      //# corrija
      return null;
   }
}