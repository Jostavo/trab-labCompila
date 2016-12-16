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
    
    public void genC(PW pw){
        if(this.type.getName().equals("String")){
            pw.printIdent(this.getType().getCname() + "_" + this.getName());
        }else{
            pw.printIdent(this.getType().getCname() + " _" + this.getName());
        }
    }

    private String name;
    private Type type;
}