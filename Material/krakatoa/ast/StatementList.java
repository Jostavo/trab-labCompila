package ast;

import java.util.*;

public class StatementList{
	private ArrayList<Statement> listaStmt;
	private boolean temRetorno;
	private boolean unique;
	
	public StatementList() {
		this.listaStmt = new ArrayList<Statement>();
	}
	
	public void addStmt(Statement stmt) {
		this.listaStmt.add(stmt);
	}
}
