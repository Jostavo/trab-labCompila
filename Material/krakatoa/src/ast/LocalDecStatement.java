// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

/**
 *
 * @author enrique
 */
public class LocalDecStatement extends Statement {
    private LocalVariableList lvl;
    private Type t;
    
    public LocalDecStatement(Type t, LocalVariableList lvl) {
        this.lvl = lvl;
        this.t = t;
    }

    @Override
    public void genC(PW pw) {
        pw.printIdent(t.getCname() + " ");
        for(Variable aux: lvl.getLista()){
        	if (t != Type.booleanType && t != Type.intType && t != Type.nullType && t != Type.stringType && t != Type.undefinedType && t != Type.voidType) {
        		pw.print("*");
        	}
            pw.print(aux.getCname());
            if(aux != lvl.getLista().get(lvl.getSize()-1)){
                pw.print(", ");
            }
        }
        pw.print(";");
        pw.println();
    }
    
    @Override
    public void genKra(PW pw) {
        pw.printIdent(t.getName() + " ");
        for(Variable aux: lvl.getLista()){
            pw.print(aux.getName());
            if(aux != lvl.getLista().get(lvl.getSize()-1)){
                pw.print(", ");
            }
        }
        pw.print(";");
        pw.println();
    }
    
}
