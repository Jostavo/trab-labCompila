// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

abstract public class Type {

    public Type( String name ) {
        this.name = name;
    }

    public static Type booleanType = new TypeBoolean();
    public static Type intType = new TypeInt();
    public static Type stringType = new TypeString();
    public static Type voidType = new TypeVoid();
    public static Type undefinedType = new TypeUndefined();
    public static Type nullType = new TypeNull();

    public String getName() {
        return name;
    }

    abstract public String getCname();

    private String name;
}
