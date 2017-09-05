// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

public class Variable {

    public Variable( String name, Type type ) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }

    public Type getType() {
        return type;
    }
    
    public String getCname() { return "_" + name; }
    
    private String name;
    private Type type;
}