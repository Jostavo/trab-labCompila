package ast;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author enrique
 */
public class CompositeStatement extends Statement {
    private StatementList sl;
    
    public CompositeStatement(StatementList sl) {
        this.sl = sl;
    }

    @Override
    public void genC(PW pw) {
<<<<<<< HEAD
        pw.println("{");
        pw.add();
        sl.genC(pw);
        pw.sub();
        pw.printIdent("}");
=======
        sl.genC(pw);
>>>>>>> ae9783e2a2684d436f90ed1a45a924638e1d3376
    }
    
}
