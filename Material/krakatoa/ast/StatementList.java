package ast;

import java.util.*;

public class StatementList{
	private ArrayList<Statement> listaStmt;
	private boolean temRetorno;

	public StatementList() {
		this.listaStmt = new ArrayList<Statement>();
		this.temRetorno = false;
	}

	public void addStmt(Statement stmt) {
		if(stmt instanceof ReturnStatement)
			this.setTemRetorno(true);
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
