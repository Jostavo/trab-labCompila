// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

public class StatementAssert extends Statement {

    public StatementAssert(Expr expr, int lineNumber, String message) {
        this.expr = expr;
        this.lineNumber = lineNumber;
        this.message = message;
    }

    @Override
    public void genC(PW pw) {
        pw.printIdent("if ( !( ");
        expr.genC(pw, false);
        pw.println(" ) ) {");
        pw.add();
        pw.printlnIdent("puts(\"" + message + "\");");
        pw.sub();
        pw.printlnIdent("}");

    }
    
    @Override
    public void genKra(PW pw) {
    	pw.printIdent("assert ");
    	expr.genKra(pw, false);
    	pw.println(", \"" + message + "\"");

    }

    public Expr getExpr() {
        return expr;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getMessage() {
        return message;
    }

    private Expr expr;
    private int lineNumber;
    private String message;
}
