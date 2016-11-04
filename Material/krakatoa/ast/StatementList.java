package ast;

import java.util.*;

public class StatementList{
	private ArrayList<Statement> listaStmt;
	private boolean temRetorno;
        private ArrayList<ReturnStatement> retornoStmt;

	public StatementList() {
		this.retornoStmt = new ArrayList<ReturnStatement>();
		this.listaStmt = new ArrayList<Statement>();
		this.temRetorno = false;
	}
        
        public boolean verificaRetornos(Type tipo){
            boolean retorno = true;
            
            for(ReturnStatement s: retornoStmt){
                if(s.getType() != tipo)
                    retorno = false;
            }
            
            return retorno;
        }

	public void addStmt(Statement stmt) {
		if(stmt instanceof ReturnStatement){
			this.temRetorno = true;
			this.retornoStmt.add((ReturnStatement) stmt);
		}
		this.listaStmt.add(stmt);
	}

	public void genKra(PW pw) {
		for (Statement s: listaStmt) {
			s.genKra(pw);
		}
	}

	public void setTemRetorno(boolean value){
		this.temRetorno = value;
	}

	public boolean hasReturn(){
		return this.temRetorno;
	}
}
