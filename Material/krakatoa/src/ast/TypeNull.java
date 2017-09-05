// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

/**
 *
 * @author enrique
 */
public class TypeNull extends Type {
    public TypeNull() {
        super("null");
    }
    
   public String getCname() {
      return "NULL";
   }
}
