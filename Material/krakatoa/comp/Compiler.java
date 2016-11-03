
package comp;

import ast.*;
import lexer.*;
import java.io.*;
import java.util.*;

public class Compiler {

	// compile must receive an input with an character less than
	// p_input.lenght
	private KraClass classAtual;
	private Method metodoAtual;
	private boolean hasProgram;

	public Program compile(char[] input, PrintWriter outError) {

		ArrayList<CompilationError> compilationErrorList = new ArrayList<>();
		signalError = new ErrorSignaller(outError, compilationErrorList);
		symbolTable = new SymbolTable();
		lexer = new Lexer(input, signalError);
		signalError.setLexer(lexer);

		Program program = null;
		lexer.nextToken();
		program = program(compilationErrorList);
		return program;
	}

	private Program program(ArrayList<CompilationError> compilationErrorList) {
		// Program ::= KraClass { KraClass }
		KraClass auxiliar;
		ArrayList<MetaobjectCall> metaobjectCallList = new ArrayList<>();
		ArrayList<KraClass> kraClassList = new ArrayList<>();
		Program program = new Program(kraClassList, metaobjectCallList, compilationErrorList);
		try {
			while ( lexer.token == Symbol.MOCall )
				metaobjectCallList.add(metaobjectCall());

			auxiliar = classDec();

			if(auxiliar.getName().equals("Program")) {
				hasProgram = true;
				if(!auxiliar.hasPublicMethod("run")){
					signalError.showError("Method 'run' was not found in class 'Program'");
				}
			}

			kraClassList.add(auxiliar);

			while ( lexer.token == Symbol.CLASS ) {
				auxiliar = classDec();
				kraClassList.add(auxiliar);

				if (auxiliar.getName().equals("Program")) {
					hasProgram = true;
					if (!auxiliar.hasPublicMethod("run")) {
						signalError.showError("Method 'run' was not found in class 'Program'");
					}
				} else if (hasProgram) {
					signalError.showError("New class after Program");
				}
			}
			
			if (!hasProgram) {
				signalError.showError("Source code without a class 'Program'");
			}

			if ( lexer.token != Symbol.EOF ) {
				signalError.showError("End of file expected");
			}

			//PROGRAM deve ser a última classe declarada. Devemos verificar isso aqui?
			//Fiz uma flag pra quando a classe Program já foi declarada
		}
		catch( RuntimeException e) {
			signalError.showError("Problem compiling the classes");
		}
		return program;
	}

	@SuppressWarnings("incomplete-switch")
	private MetaobjectCall metaobjectCall() {
		String name = lexer.getMetaobjectName();
		lexer.nextToken();
		ArrayList<Object> metaobjectParamList = new ArrayList<>();
		if ( lexer.token == Symbol.LEFTPAR ) {
			// metaobject call with parameters
			lexer.nextToken();
			while ( lexer.token == Symbol.LITERALINT || lexer.token == Symbol.LITERALSTRING ||
					lexer.token == Symbol.IDENT ) {
				switch ( lexer.token ) {
				case LITERALINT:
					metaobjectParamList.add(lexer.getNumberValue());
					break;
				case LITERALSTRING:
					metaobjectParamList.add(lexer.getLiteralStringValue());
					break;
				case IDENT:
					metaobjectParamList.add(lexer.getStringValue());
				}
				lexer.nextToken();
				if ( lexer.token == Symbol.COMMA ) 
					lexer.nextToken();
				else
					break;
			}
			if ( lexer.token != Symbol.RIGHTPAR ) 
				signalError.showError("')' expected after metaobject call with parameters");
			else
				lexer.nextToken();
		}
		if ( name.equals("nce") ) {
			if ( metaobjectParamList.size() != 0 )
				signalError.showError("Metaobject 'nce' does not take parameters");
		}
		else if ( name.equals("ce") ) {
			if ( metaobjectParamList.size() != 3 && metaobjectParamList.size() != 4 )
				signalError.showError("Metaobject 'ce' take three or four parameters");
			if ( !( metaobjectParamList.get(0) instanceof Integer)  )
				signalError.showError("The first parameter of metaobject 'ce' should be an integer number");
			if ( !( metaobjectParamList.get(1) instanceof String) ||  !( metaobjectParamList.get(2) instanceof String) )
				signalError.showError("The second and third parameters of metaobject 'ce' should be literal strings");
			if ( metaobjectParamList.size() >= 4 && !( metaobjectParamList.get(3) instanceof String) )  
				signalError.showError("The fourth parameter of metaobject 'ce' should be a literal string");
			
		}
			
		return new MetaobjectCall(name, metaobjectParamList);
	}

	/*
		 * KraClass ::= ``class'' Id [ ``extends'' Id ] "{" MemberList "}"
		 * MemberList ::= { Qualifier Member }
		 * Member ::= InstVarDec | MethodDec
		 * InstVarDec ::= Type IdList ";"
		 * MethodDec ::= Qualifier Type Id "("[ FormalParamDec ] ")" "{" StatementList "}"
		 * Qualifier ::= [ "static" ]  ( "private" | "public" )
	 */
	private KraClass classDec() {

		KraClass retorno = null;
		InstanceVariableList listaVariaveis = new InstanceVariableList();
		MethodList listaPublicMetodos = new MethodList();
		MethodList listaPrivateMetodos = new MethodList();

		if ( lexer.token != Symbol.CLASS )
			signalError.showError("'class' expected");

		lexer.nextToken();

		if ( lexer.token != Symbol.IDENT )
			signalError.show(ErrorSignaller.ident_expected);

		String className = lexer.getStringValue();

		if(symbolTable.getInGlobal(className) != null)
			signalError.showError("Class already declared: " + className);

		symbolTable.putInGlobal(className, retorno = new KraClass(className));
		lexer.nextToken();

		classAtual = retorno;

		if ( lexer.token == Symbol.EXTENDS ) {
			lexer.nextToken();

			if ( lexer.token != Symbol.IDENT )
				signalError.show(ErrorSignaller.ident_expected);

			String superclassName = lexer.getStringValue();

			if(className.equals(superclassName))
				signalError.showError("Class can't inheritate from itself");

			if(symbolTable.getInGlobal(superclassName) == null)
				signalError.showError("SuperClass not declared : " + superclassName);

			if(superclassName == "int" || superclassName == "boolean" || superclassName == "String" || superclassName == "void")
				signalError.showError("Basic type inheritance not accepted");

			retorno.setSuper(symbolTable.getInGlobal(superclassName));
			lexer.nextToken();
		}

		if ( lexer.token != Symbol.LEFTCURBRACKET )
			signalError.showError("'{' expected", true);
		lexer.nextToken();

		while (lexer.token == Symbol.PRIVATE || lexer.token == Symbol.PUBLIC) {

			Symbol qualifier;

			switch (lexer.token) {
				case PRIVATE:
					lexer.nextToken();
					qualifier = Symbol.PRIVATE;
					break;
				case PUBLIC:
					lexer.nextToken();
					qualifier = Symbol.PUBLIC;
					break;
				default:
					signalError.showError("private, or public expected");
					qualifier = Symbol.PUBLIC;
			}

			Type t = type();

			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");

			String name = lexer.getStringValue(); // --------- AQUI A GENTE PRECISA ADICIONAR AO LOCAL DA VARIAVEL ESSAS COISAS ---------
			lexer.nextToken();

			if ( lexer.token == Symbol.LEFTPAR )
				if(qualifier == Symbol.PRIVATE)
					listaPrivateMetodos.addMethod(methodDec(t, name, qualifier));
				else
					listaPublicMetodos.addMethod(methodDec(t, name, qualifier));
			else if ( qualifier != Symbol.PRIVATE )
				signalError.showError("Attempt to declare public instance variable '" + name + "'");
			else
				listaVariaveis.addList(instanceVarDec(t, name));
		}

		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.showError("'public', 'private', or '}' expected");

		lexer.nextToken();

		retorno.setVariableList(listaVariaveis);
		retorno.setPublicMethodList(listaPublicMetodos);
		retorno.setPrivateMethodList(listaPrivateMetodos);

		return retorno;
	}

	private InstanceVariableList instanceVarDec(Type type, String name) {
		// InstVarDec ::= [ "static" ] "private" Type IdList ";"
		InstanceVariableList listaVariaveis = new InstanceVariableList();

		if(classAtual.hasInstanceVariable(name)){
			signalError.showError("Variable " + name + " was already declared in this class");
		}else if(classAtual.hasPrivateMethod(name) || classAtual.hasPublicMethod(name)){
			signalError.showError("Variable " + name + " was already declared as a method in this class");
		}

		listaVariaveis.addElement(name, type);

		while (lexer.token == Symbol.COMMA) { // --------- AQUI VERIFICA SE POSSUI MAIS VARIÁVEIS ---------
			lexer.nextToken();

			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");

			String variableName = lexer.getStringValue(); // --------- NECESSÁRIO VÁRIOS RETORNOS ------------------ VERIFICAR ------------------
			lexer.nextToken();

			if(classAtual.hasInstanceVariable(variableName)){
				signalError.showError("Variable " + variableName + " was already declared in this class");
			}else if(classAtual.hasPrivateMethod(variableName) || classAtual.hasPublicMethod(variableName)){
				signalError.showError("Variable " + variableName + " was already declared as a method in this class");
			}

			listaVariaveis.addElement(variableName, type);
		}

		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);

		lexer.nextToken();

		return listaVariaveis;
	}

	private Method methodDec(Type type, String name, Symbol qualifier) {
		/*
		 * MethodDec ::= Qualifier Return Id "("[ FormalParamDec ] ")" "{"
		 *                StatementList "}"
		 */
		ParamList listaParametros;
		StatementList listaStmt;

		if(classAtual.hasPublicMethod(name) || classAtual.hasPrivateMethod(name)){
			signalError.showError("Method " + name + " was already declared");
		}else if(classAtual.hasInstanceVariable(name)){
			signalError.showError("Method " + name + " was already declared as a variable in this class");
		}

		Method metodoDeclarado = new Method(name, type);
		metodoAtual = metodoDeclarado;

		lexer.nextToken();
		if ( lexer.token != Symbol.RIGHTPAR )
			listaParametros = formalParamDec(); // --------- PRECISA DO RETORNO ---------
		if ( lexer.token != Symbol.RIGHTPAR )
			signalError.showError("')' expected");

		if(classAtual.getName().equals("Program") && metodoDeclarado.getName().equals("run")) {
			if (metodoDeclarado.getParamList().getSize() > 0)
				signalError.showError("Program's method 'run' must not have any parameters!");
			if (metodoDeclarado.getType() != Type.voidType)
				signalError.showError("Program's method 'run' must be of type 'Void'");
		}

		KraClass sClass = classAtual.getSuperClass();

		while(sClass != null){
			if(sClass.hasPublicMethod(name)){
				Method scMethod = sClass.getMethod(m);
				break;
			}else
				sClass = sClass.getSuper();
		}

		if (scMethod != null){
			if(type != scMethod.getType())
				signalError.showError("Can't override a method using a different type");
			if(scMethod.getParamList().size() != listaParametros.size())
				signalError.showError("Can't override a method using different number of parameters");

			ParamList scMethodParam = scMethod.getParamList();

			int i = 0;
			for(Variable aux: scMethodParam){
				if(aux.getType() != scMethodParam.getParamList().get(i).getType())
					signalError.showError("Can't override a parameter using a differente type");

				i++;
			}
		}

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTCURBRACKET )
			signalError.showError("'{' expected");

		lexer.nextToken();
		listaStmt = statementList(); // --------- PRECISA DO RETORNO --------- PRECISA VERIFICAR SE O STATEMENT TEM RETURN
		metodoDeclarado.setStmtList(listaStmt);

		if(type != Type.voidType && !listaStmt.hasReturn()) // DONE
			signalError.showError("Method must have a return statement");
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.showError("} expected");

		lexer.nextToken();

		metodoDeclarado.setParamList(listaParametros);
		metodoDeclarado.setStmtList(listaStmt);

		symbolTable.removeLocalIdent();

		return metodoDeclarado;
	}

	private LocalVariableList localDec() {
		// LocalDec ::= Type IdList ";"
		LocalVariableList lclvList = new LocalVariableList();
		Type type = type();
		String name;

		if ( lexer.token != Symbol.IDENT )
			signalError.showError("Identifier expected");

		Variable v = new Variable(name = lexer.getStringValue(), type);

		if(symbolTable.getInLocal(name) == null && classAtual.hasInstanceVariable(name)){
			symbolTable.putInLocal(name, v);
			metodoAtual.addLocalVariable(v);
		}else
			signalError.showError("Variable " + name + " was already declared");

		lexer.nextToken();
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();

			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");

			v = new Variable(name = lexer.getStringValue(), type);

			if(symbolTable.getInLocal(name) == null && classAtual.hasInstanceVariable(name)){
				symbolTable.putInLocal(name, v);
				metodoAtual.addLocalVariable(v);
				lclvList.addElement(v);
			}else
				signalError.showError("Variable " + name + " was already declared");

			lexer.nextToken();
		}
			
		if (lexer.token != Symbol.SEMICOLON) {
			signalError.showError("Missing ';'");
		}
		lexer.nextToken(); // PAREI AQUI

		return lclvList;
	} //INCOMPLETO - NECESSITA DA CRIAÇÃO DO RETURN DA DECLARAÇÃO

	private ParamList formalParamDec() {
		// FormalParamDec ::= ParamDec { "," ParamDec }
		ParamList listaParametros = new ParamList();

		listaParametros.addElement(paramDec());

		while (lexer.token == Symbol.COMMA) { // --------- PRECISA TRATAR COM LISTAS ---------
			lexer.nextToken();
			listaParametros.addElement(paramDec());
		}

		return listaParametros;
	}

	private Parameter paramDec() {
		// ParamDec ::= Type Id
		Type tipo;
		String name;
		Parameter retorno;

		tipo = type();
		if ( lexer.token != Symbol.IDENT )
			signalError.showError("Identifier expected");

		name = lexer.getStringValue();
		retorno = new Parameter(name, tipo);
		symbolTable.putInLocal(name, retorno);
		lexer.nextToken();

		return retorno;
	}

	private Type type() {
		// Type ::= BasicType | Id
		Type result;

		switch (lexer.token) {
			case VOID:
				result = Type.voidType;
				break;
			case INT:
				result = Type.intType;
				break;
			case BOOLEAN:
				result = Type.booleanType;
				break;
			case STRING:
				result = Type.stringType;
				break;
			case IDENT: //VERIFICAR ISSO DAQUI
				String className = lexer.getStringValue();
				KraClass typedClass = symbolTable.getInGlobal(className);
				if (typedClass == null) {
					signalError.showError("Type '" + className + "' was not found");
				}
				result = typedClass;
				break;
			default:
				signalError.showError("Type expected");
				result = Type.undefinedType;
		}
		lexer.nextToken();

		return result;
	}

	private CompositeStatement compositeStatement() {
		StatementList listaStmt;
		CompositeStatement compStmt;

		lexer.nextToken();
		listaStmt = statementList();

		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.showError("} expected");
		else
			lexer.nextToken();

		compStmt = new CompositeStatement(listaStmt);

		return compStmt;
	}

	private StatementList statementList() {
		// CompStatement ::= "{" { Statement } "}"
		Symbol tk;
		StatementList stmtList = new StatementList();
		Statement auxiliar;

		while ((tk = lexer.token) != Symbol.RIGHTCURBRACKET && tk != Symbol.ELSE) { //Aqui a gente vai salvando os statements
			//STATEMENT LIST NÃO ESTÁ PRONTA
			auxiliar = statement();
			stmtList.addStmt(auxiliar);
		}

		return stmtList;
	} //INCOMPLETO

	private Statement statement() {
		/*
		 * Statement ::= Assignment ";" | IfStat |WhileStat | MessageSend
		 *                ";" | ReturnStat ";" | ReadStat ";" | WriteStat ";" |
		 *               "break" ";" | ";" | CompStatement | LocalDec
		 */

		switch (lexer.token) {
			case THIS:
			case IDENT:
			case SUPER:
			case INT:
			case BOOLEAN:
			case STRING:
				return assignExprLocalDec();
				break;
			case ASSERT: //NÃO FAÇO IDEIA DO QUE FAZER AQUI
				assertStatement();
				break;
			case RETURN:
				return returnStatement();
				break;
			case READ:
				return readStatement();
				break;
			case WRITE:
				return writeStatement();
				break;
			case WRITELN:
				return writelnStatement();
				break;
			case IF:
				return ifStatement();
				break;
			case BREAK:
				return breakStatement();
				break;
			case WHILE:
				return whileStatement();
				break;
			case SEMICOLON:
				return nullStatement();
				break;
			case LEFTCURBRACKET:
				return compositeStatement();
				break;
			default:
				signalError.showError("Statement expected");
		}
	}

	private Statement assertStatement() {
		lexer.nextToken();
		int lineNumber = lexer.getLineNumber();
		Expr e = expr();
		if ( e.getType() != Type.booleanType )
			signalError.showError("boolean expression expected");
		if ( lexer.token != Symbol.COMMA ) {
			this.signalError.showError("',' expected after the expression of the 'assert' statement");
		}
		lexer.nextToken();
		if ( lexer.token != Symbol.LITERALSTRING ) {
			this.signalError.showError("A literal string expected after the ',' of the 'assert' statement");
		}
		String message = lexer.getLiteralStringValue();
		lexer.nextToken();
		if ( lexer.token == Symbol.SEMICOLON )
			lexer.nextToken();
		
		return new StatementAssert(e, lineNumber, message);
	}

	private boolean isType(String name) {
		return this.symbolTable.getInGlobal(name) != null;
	}

	/*
	 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec
	 */
	private Statement assignExprLocalDec() {
		LocalVariableList lvList;

		if ( lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN || lexer.token == Symbol.STRING ||
				(lexer.token == Symbol.IDENT && isType(lexer.getStringValue())) ) {
			/*
			 * LocalDec ::= Type IdList ``;''
			 */
			lvList = localDec();
			lexer.nextToken();

			return null; //Não tenho certeza se volta null
		}
		else {
			/*
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ]
			 */
			Expr esq = null;
			Expr dir = null;

			esq = expr();

			if ( lexer.token == Symbol.ASSIGN ) { //PAREI AQUI
				lexer.nextToken();
				dir = expr();
				if ( lexer.token != Symbol.SEMICOLON )
					signalError.showError("';' expected", true);
				else
					lexer.nextToken();
			}else if(){

			}else{
				signalError.showError("Expected '.' or '='");
			}
		}
		return null;
	} //PAREI NESSA FUNÇÃO

	private ExprList realParameters() {
		ExprList anExprList = null;

		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("Missing '('");
		lexer.nextToken();
		if ( startExpr(lexer.token) ) anExprList = exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		return anExprList;
	}

	private void whileStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("Missing '('");
		lexer.nextToken();
		expr();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		statement();
	}

	private void ifStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("Missing '('");
		lexer.nextToken();
		expr();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		statement();
		if ( lexer.token == Symbol.ELSE ) {
			lexer.nextToken();
			statement();
		}
	}

	private void returnStatement() {

		lexer.nextToken();
		expr();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
	}

	private void readStatement() {
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("Missing '('");
		lexer.nextToken();
		while (true) {
			if ( lexer.token == Symbol.THIS ) {
				lexer.nextToken();
				if ( lexer.token != Symbol.DOT ) signalError.showError(". expected");
				lexer.nextToken();
			}
			if ( lexer.token != Symbol.IDENT )
				signalError.show(ErrorSignaller.ident_expected);

			String name = lexer.getStringValue();
			lexer.nextToken();
			if ( lexer.token == Symbol.COMMA )
				lexer.nextToken();
			else
				break;
		}

		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
	}

	private void writeStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("Missing '('");
		lexer.nextToken();
		exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
	}

	private void writelnStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("Missing '('");
		lexer.nextToken();
		exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
	}

	private void breakStatement() {
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();
	}

	private void nullStatement() {
		lexer.nextToken();
	}

	private ExprList exprList() {
		// ExpressionList ::= Expression { "," Expression }

		ExprList anExprList = new ExprList();
		anExprList.addElement(expr());
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			anExprList.addElement(expr());
		}
		return anExprList;
	}

	private Expr expr() {

		Expr left = simpleExpr();
		Symbol op = lexer.token;
		if ( op == Symbol.EQ || op == Symbol.NEQ || op == Symbol.LE
				|| op == Symbol.LT || op == Symbol.GE || op == Symbol.GT ) {
			lexer.nextToken();
			Expr right = simpleExpr();
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	private Expr simpleExpr() {
		Symbol op;

		Expr left = term();
		while ((op = lexer.token) == Symbol.MINUS || op == Symbol.PLUS
				|| op == Symbol.OR) {
			lexer.nextToken();
			Expr right = term();
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	private Expr term() {
		Symbol op;

		Expr left = signalFactor();
		while ((op = lexer.token) == Symbol.DIV || op == Symbol.MULT
				|| op == Symbol.AND) {
			lexer.nextToken();
			Expr right = signalFactor();
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	private Expr signalFactor() {
		Symbol op;
		if ( (op = lexer.token) == Symbol.PLUS || op == Symbol.MINUS ) {
			lexer.nextToken();
			return new SignalExpr(op, factor());
		}
		else
			return factor();
	}

	/*
	 * Factor ::= BasicValue | "(" Expression ")" | "!" Factor | "null" |
	 *      ObjectCreation | PrimaryExpr
	 * 
	 * BasicValue ::= IntValue | BooleanValue | StringValue 
	 * BooleanValue ::=  "true" | "false" 
	 * ObjectCreation ::= "new" Id "(" ")" 
	 * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"  | 
	 *                 Id  |
	 *                 Id "." Id | 
	 *                 Id "." Id "(" [ ExpressionList ] ")" |
	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
	 *                 "this" | 
	 *                 "this" "." Id | 
	 *                 "this" "." Id "(" [ ExpressionList ] ")"  | 
	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
	 */
	private Expr factor() {

		Expr anExpr;
		ExprList exprList;
		String messageName, id;

		switch (lexer.token) {
		// IntValue
		case LITERALINT:
			return literalInt();
			// BooleanValue
		case FALSE:
			lexer.nextToken();
			return LiteralBoolean.False;
			// BooleanValue
		case TRUE:
			lexer.nextToken();
			return LiteralBoolean.True;
			// StringValue
		case LITERALSTRING:
			String literalString = lexer.getLiteralStringValue();
			lexer.nextToken();
			return new LiteralString(literalString);
			// "(" Expression ")" |
		case LEFTPAR:
			lexer.nextToken();
			anExpr = expr();
			if ( lexer.token != Symbol.RIGHTPAR )
				signalError.showError("')' expected");
			lexer.nextToken();
			return new ParenthesisExpr(anExpr);

			// "null"
		case NULL:
			lexer.nextToken();
			return new NullExpr();
			// "!" Factor
		case NOT:
			lexer.nextToken();
			anExpr = expr();
			if(Type.booleanType != anExpr.getType())
				signalError.showError("Boolean expression expected");
			return new UnaryExpr(anExpr, Symbol.NOT);
			// ObjectCreation ::= "new" Id "(" ")"
		case NEW:
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");

			String className = lexer.getStringValue();

			KraClass aClass = symbolTable.getInGlobal(className);
			if (aClass == null) {
				signalError.showError("Class '" + className + "' was not found");
			}

			lexer.nextToken();
			if ( lexer.token != Symbol.LEFTPAR )
				signalError.showError("'(' expected");
			lexer.nextToken();
			if ( lexer.token != Symbol.RIGHTPAR )
				signalError.showError("')' expected");
			lexer.nextToken();

			return new KraClassExpr(aClass);
			/*
          	 * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"  | 
          	 *                 Id  |
          	 *                 Id "." Id | 
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 "this" | 
          	 *                 "this" "." Id | 
          	 *                 "this" "." Id "(" [ ExpressionList ] ")"  | 
          	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
			 */
		case SUPER:
			// "super" "." Id "(" [ ExpressionList ] ")"
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				signalError.showError("'.' expected");
			}

			lexer.nextToken();

			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");

			messageName = lexer.getStringValue();
			KraClass superclasse = classAtual.getSuper();

			if(superclasse == null)
				signalError.showError("Class " + classAtual.getCname() + " does not have a superclass");

			boolean encontrada = false;

			while(superclasse != null){
				encontrada = superclasse.hasPublicMethod(messageName);

				if(encontrada)
					break;
				else
					superclasse = superclasse.getSuper();
			}

			if(!encontrada)
				signalError.showError("Class " + classAtual.getCname() + " does not have a superclass with the assigned method");

			lexer.nextToken();
			exprList = realParameters();
			Method metodoAnalisado = superclasse.getMethod(messageName);
			ParamList plMethod = metodoAnalisado.getParamList();

			if(exprList.getSize() == plMethod.getSize()){
				int i = 0;
				int erros = 0;
				for(Parameter aux: plMethod){
					if(!comparaTipos(aux.getType(), exprList.getExprList().get(i).getType())){ // CRIAR ESSA FUNÇÃO DE COMPARAÇÃO DE TIPOS --------------------
						erros++;
					}
					i++;
				}

				if(erros > 0){
					signalError.showError(erros + " types are not convertible nor equal");
				}
			}else
				signalError.showError("Different number of parameters!");

			return new MessageSendToSuper(metodoAnalisado, exprList, classAtual); //VERIFICAR ----------------- NÃO ESTÁ PRONTO
		case IDENT:
			/*
          	 * PrimaryExpr ::=  
          	 *                 Id  |
          	 *                 Id "." Id | 
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
			 */

			String firstId = lexer.getStringValue();
			lexer.nextToken();

			if ( lexer.token != Symbol.DOT ) {
				if(classAtual.hasPublicMethod(firstId) || classAtual.hasPrivateMethod(firstId))
					signalError.showError("Use 'this.' to call instance methods");

				if(symbolTable.getInLocal(firstId) && classAtual.hasInstanceVariable(firstId))
					signalError.showError("Use 'this.' to call instance variables");
				else if(symbolTable.getInLocal(firstId) == null)
					signalError.showError("Id " + firstId + " was not previously declared");

				if(symbolTable.getInLocal(firstId) instanceof InstanceVariable && classAtual.hasInstanceVariable(firstId))
					signalError.showError("Use 'this.' to call instance variables");

				return new VariableExpr(symbolTable.getInLocal(firstId));
			}
			else { // Id "." PAREI NESSA VERIFICAÇÃO ----------------------------
				lexer.nextToken(); // coma o "."
				if ( lexer.token != Symbol.IDENT ) {
					signalError.showError("Identifier expected");
				}
				else {
					// Id "." Id
					lexer.nextToken();
					id = lexer.getStringValue();
					if ( lexer.token == Symbol.DOT ) {
						// Id "." Id "." Id "(" [ ExpressionList ] ")"
						/*
						 * se o compilador permite vari�veis est�ticas, � poss�vel
						 * ter esta op��o, como
						 *     Clock.currentDay.setDay(12);
						 * Contudo, se vari�veis est�ticas n�o estiver nas especifica��es,
						 * sinalize um erro neste ponto.
						 */
						lexer.nextToken();
						if ( lexer.token != Symbol.IDENT )
							signalError.showError("Identifier expected");
						messageName = lexer.getStringValue();
						lexer.nextToken();
						exprList = this.realParameters();

					}
					else if ( lexer.token == Symbol.LEFTPAR ) {
						// Id "." Id "(" [ ExpressionList ] ")"
						KraClass firstClass = symbolTable.getInGlobal(firstId);
						Method calledMethod = firstClass.getMethod(id);
						if (calledMethod == null) {
							signalError.showError("Method '" + id + "' was not found in class '" + firstId + "' or its superclasses");
						}
						exprList = this.realParameters();
						//PRECISA CHECAR SE OS PARAMETROS ESTAO CORRETOS
					}
					else {
						// retorne o objeto da ASA que representa Id "." Id
					}
				}
			}
			break;
		case THIS:
			/*
			 * Este 'case THIS:' trata os seguintes casos: 
          	 * PrimaryExpr ::= 
          	 *                 "this" | 
          	 *                 "this" "." Id | 
          	 *                 "this" "." Id "(" [ ExpressionList ] ")"  | 
          	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
			 */
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				// only 'this'
				// retorne um objeto da ASA que representa 'this'
				// confira se n�o estamos em um m�todo est�tico
				return null;
			}
			else {
				lexer.nextToken();
				if ( lexer.token != Symbol.IDENT )
					signalError.showError("Identifier expected");
				id = lexer.getStringValue();
				lexer.nextToken();
				// j� analisou "this" "." Id
				if ( lexer.token == Symbol.LEFTPAR ) {
					// "this" "." Id "(" [ ExpressionList ] ")"
					/*
					 * Confira se a classe corrente possui um m�todo cujo nome �
					 * 'ident' e que pode tomar os par�metros de ExpressionList
					 */
					exprList = this.realParameters();
				}
				else if ( lexer.token == Symbol.DOT ) {
					// "this" "." Id "." Id "(" [ ExpressionList ] ")"
					lexer.nextToken();
					if ( lexer.token != Symbol.IDENT )
						signalError.showError("Identifier expected");
					lexer.nextToken();
					exprList = this.realParameters();
				}
				else {
					// retorne o objeto da ASA que representa "this" "." Id
					/*
					 * confira se a classe corrente realmente possui uma
					 * vari�vel de inst�ncia 'ident'
					 */
					return null;
				}
			}
			break;
		default:
			signalError.showError("Expression expected");
		}
		return null;
	} // VERIFICAR O FACTOR -------- FIZ ATÉ O COMEÇO DO IDENT, MAS É NECESSÁRIO FINALIZAR UMAS COISAS

	private LiteralInt literalInt() {
		int value = lexer.getNumberValue();
		lexer.nextToken();
		return new LiteralInt(value);
	}

	private static boolean startExpr(Symbol token) {

		return token == Symbol.FALSE || token == Symbol.TRUE
				|| token == Symbol.NOT || token == Symbol.THIS
				|| token == Symbol.LITERALINT || token == Symbol.SUPER
				|| token == Symbol.LEFTPAR || token == Symbol.NULL
				|| token == Symbol.IDENT || token == Symbol.LITERALSTRING;

	}

	private SymbolTable		symbolTable;
	private Lexer			lexer;
	private ErrorSignaller	signalError;

}
