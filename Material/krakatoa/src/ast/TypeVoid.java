// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

public class TypeVoid extends Type {
    
    public TypeVoid() {
        super("void");
    }
    
   public String getCname() {
      return "void";
   }

}