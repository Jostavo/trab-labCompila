package ast;

import java.util.*;

public class ExprList {

    public ExprList() {
        exprList = new ArrayList<Expr>();
    }

    public void addElement( Expr expr ) {
        exprList.add(expr);
    }

    public void genC( PW pw ) {

        int size = exprList.size();
        for ( Expr e : exprList ) {
        	e.genC(pw, false);
            if ( --size > 0 )
                pw.print(", ");
        }
    }
    
    public void genKra( PW pw ) {

        int size = exprList.size();
        for ( Expr e : exprList ) {
        	e.genKra(pw, false);
            if ( --size > 0 )
                pw.print(", ");
        }
    }

    public ArrayList<Expr> getExprList(){
        return exprList;
    }

    private ArrayList<Expr> exprList;

}
