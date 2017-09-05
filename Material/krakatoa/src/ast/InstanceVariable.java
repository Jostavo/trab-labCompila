// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

public class InstanceVariable extends Variable {

    public InstanceVariable( String name, Type type ) {
        super(name, type);
    }

    public void genC(PW pw, String className){
        if(this.getType().getName().equals("String")){
            pw.printIdent(this.getType().getCname() + " _" + className + "_" + this.getName());
        }else if(this.getType().getName().equals("boolean")){
            pw.printIdent(this.getType().getName() + " _" + className + "_" + this.getName());
        }else{
            pw.printIdent(this.getType().getCname() + " _" + className + "_" + this.getName());
        }
    }
    
    public void genKra(PW pw) {
    	pw.print(this.getType() + " " + this.getName());
    }
}