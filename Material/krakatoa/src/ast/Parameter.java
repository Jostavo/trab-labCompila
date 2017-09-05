// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;


public class Parameter extends Variable {

    public Parameter( String name, Type type ) {
        super(name, type);
    }
    
    public void genC(PW pw) {
        if(this.getType().getName().equals("String")){
            pw.printIdent(this.getType().getCname() + " " + this.getCname());
        }else if(this.getType().getName().equals("boolean")){
            pw.printIdent(this.getType().getName() + " " + this.getCname());
        }else{
            pw.printIdent(this.getType().getCname() + " " + this.getCname());
        }
    }
    
    public void genKra(PW pw) {
        pw.printIdent(this.getType().getName() + " " + this.getName());
    }

}