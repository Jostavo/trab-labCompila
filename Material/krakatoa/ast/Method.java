package ast;

//MethodDec ::= Qualifier Type Id "("[ FormalParamDec ] ")" "{" StatementList "}"
public class Method {
    private String name;
    private Type tipo;
    private ParamList paramList;
    private LocalVariableList localVList;
    private StatementList stmtList;

    public Method(String nome, Type tipo){
        this.nome = nome;
        this.tipo = tipo;
        this.paramList = new ParamList();
        this.localVList = new LocalVariableList();
        this.stmtList = new StatementList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return tipo;
    }

    public void setType(Type tipo) {
        this.tipo = tipo;
    }

    public ParamList getParamList() {
        return paramList;
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
}