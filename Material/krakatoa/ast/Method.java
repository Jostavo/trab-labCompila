package ast;

//MethodDec ::= Qualifier Type Id "("[ FormalParamDec ] ")" "{" StatementList "}"
public class Method extends Variable {

    private ParamList paramList;
    private LocalVariableList localVList;
    private StatementList stmtList;

    public Method(String nome, Type tipo){
    	super(nome, tipo);
        this.paramList = new ParamList();
        this.localVList = new LocalVariableList();
        this.stmtList = new StatementList();
    }

    public void setParamList(ParamList paramList) {
        this.paramList = paramList;
    }

    public ParamList getParamList(){
        return this.paramList;
    }

    public LocalVariableList getLocalVList() {
        return localVList;
    }

    public void setLocalVList(LocalVariableList localVList) {
        this.localVList = localVList;
    }

    public StatementList getStmtList() {
        return stmtList;
    }

    public void setStmtList(StatementList stmtList) {
        this.stmtList = stmtList;
    }

    public void addLocalVariable(Variable v){
        this.localVList.addElement(v);
    }

    public void addParameter(Parameter p){
        this.paramList.addElement(p);
    }
    
    public void genKra(PW pw) {
    	pw.printIdent(this.getType().getCname() + " " + this.getName() + "(");
    	if (this.paramList != null) {
    		this.paramList.genKra(pw);
    	}
    	pw.println(") {");
    	pw.add();
    	this.stmtList.genKra(pw);
    	pw.sub();
    	pw.printlnIdent("}\n");
    }
}