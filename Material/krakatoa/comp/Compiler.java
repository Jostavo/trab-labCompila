
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
		ArrayList<MetaobjectCall> metaobjectCallList = new ArrayList<>();
		ArrayList<KraClass> kraClassList = new ArrayList<>();

		try {
			while ( lexer.token == Symbol.MOCall )
				metaobjectCallList.add(metaobjectCall());

			kraClassList.add(classDec());

			while ( lexer.token == Symbol.CLASS ) {                                
                                kraClassList.add(classDec());
			}

			if ( lexer.token != Symbol.EOF ) {
				signalError.showError("End of file expected");
			}
                        
                        boolean prog = false;
                        
                        for(KraClass aux: kraClassList){
                            if(aux.getCname().equals("Program"))
                                prog = true;
                        }
                        
                        if(prog == false){
                            signalError.showError("No class 'Program' defined");
                        }
		}
		catch( RuntimeException e) {
			
		}
		return new Program(kraClassList, metaobjectCallList, compilationErrorList);
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

                retorno = new KraClass(className);
                classAtual = retorno;

		symbolTable.putInGlobal(className, retorno);
		lexer.nextToken();

		if ( lexer.token == Symbol.EXTENDS ) {
			lexer.nextToken();

			if ( lexer.token != Symbol.IDENT )
				signalError.show(ErrorSignaller.ident_expected);

			String superclassName = lexer.getStringValue();

			if(className.equals(superclassName))
				signalError.showError("Class can't inheritate from itself");

			if(symbolTable.getInGlobal(superclassName) == null)
				signalError.showError("SuperClass not declared : " + superclassName);

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

			if ( lexer.token == Symbol.LEFTPAR ){
                                if(listaPrivateMetodos.getMethod(name) != null || listaPublicMetodos.getMethod(name) != null)
                                    signalError.showError("Method " + name + " was already declared in this scope");
                                
				if(qualifier == Symbol.PRIVATE)
					listaPrivateMetodos.addMethod(methodDec(t, name, qualifier));
				else
					listaPublicMetodos.addMethod(methodDec(t, name, qualifier));
                        }
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
		ParamList listaParametros = null;
		StatementList listaStmt = null;

		if(classAtual.hasPublicMethod(name) || classAtual.hasPrivateMethod(name)){
			signalError.showError("Method " + name + " was already declared");
		}else if(classAtual.hasInstanceVariable(name)){
			signalError.showError("Method " + name + " was already declared as a variable in this class");
		}

		Method metodoDeclarado = new Method(name, type, qualifier.toString());
		metodoAtual = metodoDeclarado;

		lexer.nextToken();
		if ( lexer.token != Symbol.RIGHTPAR )
			listaParametros = formalParamDec(); // --------- PRECISA DO RETORNO ---------
		if ( lexer.token != Symbol.RIGHTPAR )
			signalError.showError("')' expected");

		if(classAtual.getCname().equals("Program") && name.equals("run")) {
			if (listaParametros != null)
				signalError.showError("Program's method 'run' must not have any parameters!");
                        
			if (metodoDeclarado.getType() != Type.voidType)
				signalError.showError("Program's method 'run' must be of type 'Void'");
                        
                        if (qualifier == Symbol.PRIVATE)
                                signalError.showError("Program's method 'run' cannot be 'Private'");
                }

		KraClass sClass = classAtual.getSuper();
                Method scMethod = null;

		while(sClass != null){
			if(sClass.hasPublicMethod(name)){
				scMethod = sClass.getMethod(name);
				break;
			}else
				sClass = sClass.getSuper();
		}

		if (scMethod != null){
			if(type != scMethod.getType())
				signalError.showError("Can't override a method using a different type");
			if(scMethod.getParamList().getSize() != listaParametros.getSize())
				signalError.showError("Can't override a method using different number of parameters");

			ParamList scMethodParam = scMethod.getParamList();

			int i = 0;
			for(Variable aux: scMethodParam.getParamList()){
				if(aux.getType() != scMethodParam.getParamList().get(i).getType())
					signalError.showError("Can't override a parameter using a differente type");

				i++;
			}
		}

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTCURBRACKET )
			signalError.showError("'{' expected");

		lexer.nextToken();
		listaStmt = statementList();

		if(type != Type.voidType && !listaStmt.hasReturn())
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
                Variable v;

		if ( lexer.token != Symbol.IDENT )
			signalError.showError("Identifier expected");
                
                name = lexer.getStringValue();
		v = symbolTable.getInLocal(name);

		if(v == null){
			symbolTable.putInLocal(name, v);
                        v = new Variable(name, type);
			metodoAtual.addLocalVariable(v);
		}else
			signalError.showError("Variable " + name + " was already declared");

		lexer.nextToken();
                
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();

			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");

			name = lexer.getStringValue();
                        v = symbolTable.getInLocal(name);

                        if(v == null){
                                symbolTable.putInLocal(name, v);
                                v = new Variable(name, type);
                                metodoAtual.addLocalVariable(v);
                        }else
                            signalError.showError("Variable " + name + " was already declared");

			lexer.nextToken();
		}

		if (lexer.token != Symbol.SEMICOLON) {
			signalError.showError("Missing ';'");
		}
		lexer.nextToken();

                metodoAtual.addLocalVariableList(lclvList);
		return lclvList;
	}
        
        private LocalVariableList localDecClassType(String tipo) {
		LocalVariableList lclvList = new LocalVariableList();
		String name;
                Variable v;

		if ( lexer.token != Symbol.IDENT )
			signalError.showError("Identifier expected");
                
                Type type = symbolTable.getInGlobal(tipo);
                
                if ( type == null)
                    signalError.showError("Type was not found");
                
                name = lexer.getStringValue();
		v = symbolTable.getInLocal(name);

		if(v == null){
			symbolTable.putInLocal(name, v);
                        v = new Variable(name, type);
			metodoAtual.addLocalVariable(v);
		}else
			signalError.showError("Variable " + name + " was already declared");

		lexer.nextToken();
                
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();

			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");

			name = lexer.getStringValue();
                        v = symbolTable.getInLocal(name);

                        if(v == null){
                                symbolTable.putInLocal(name, v);
                                v = new Variable(name, type);
                                metodoAtual.addLocalVariable(v);
                        }else
                            signalError.showError("Variable " + name + " was already declared");

			lexer.nextToken();
		}

		if (lexer.token != Symbol.SEMICOLON) {
			signalError.showError("Missing ';'");
		}
                
		metodoAtual.addLocalVariableList(lclvList);
                
		return lclvList;
	}

	private ParamList formalParamDec() {
		ParamList listaParametros = new ParamList();
                Parameter p;
                
                p = paramDec();               
		listaParametros.addElement(p);

		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			p = paramDec();               
                        listaParametros.addElement(p);
		}

		return listaParametros;
	}

	private Parameter paramDec() {
		Type tipo = type();
		String name;
		Parameter retorno;		
                
		if ( lexer.token != Symbol.IDENT )
			signalError.showError("Identifier expected");

		name = lexer.getStringValue();
		retorno = new Parameter(name, tipo);
		symbolTable.putInLocal(name, retorno);
		lexer.nextToken();

		return retorno;
	}

	private Type type() {
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
			case IDENT:
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
		switch (lexer.token) {
			case THIS:
			case IDENT:
			case SUPER:
			case INT:
			case BOOLEAN:
			case STRING:
				return assignExprLocalDec();
			case RETURN:
				return returnStatement();
			case READ:
				return readStatement();
			case WRITE:
				return writeStatement();
			case WRITELN:
				return writelnStatement();
			case IF:
				return ifStatement();
			case BREAK:
				return breakStatement();
			case WHILE:
				return whileStatement();
			case SEMICOLON:
				return null;
			case LEFTCURBRACKET:
				return compositeStatement();
			default:
                                if(lexer.token == Symbol.LITERALINT || lexer.token == Symbol.LITERALSTRING ||
                                    lexer.token == Symbol.FALSE || lexer.token == Symbol.TRUE) {
                                    signalError.showError("Operator or variable expected at the left side of assignment");
                                }
				signalError.showError("Statement expected");
				return null;
		}
	}

	private boolean isType(String name) {
		return this.symbolTable.getInGlobal(name) != null;
	}

	/*
	 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec
	 */
	private AssignStatement assignExprLocalDec() {
            Expr left = null;
            Expr right = null;

            if ( lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN
                            || lexer.token == Symbol.STRING ) {
                    localDec();
            } else {
                    left = expr();

                    if ( lexer.token == Symbol.ASSIGN ) {

                            if (left instanceof VariableExpr) {
                                    String varName = ((VariableExpr) left).getVariable().getName();
                                    Variable v = symbolTable.getInLocal(varName);
                                    if (v == null) {
                                            signalError.showError("Variable '" + varName + "' was not declared");
                                    }

                                    left = new VariableExpr(v);
                            }

                            lexer.nextToken();
                            right = expr();
                            
                            if(left.getType() != right.getType()){
                                    if(left.getType()== Type.booleanType && right.getType() == Type.intType){
                                            signalError.showError("\'int\' cannot be assigned to \'boolean\'");
                                    }
                                    if(left.getType()== Type.intType && right.getType() == Type.booleanType){
                                            signalError.showError("Type error: value of the right-hand side is not subtype of the variable of the left-hand side.");
                                    }

                                    if(left.getType()instanceof KraClass && 
                                                    (right.getType() == Type.intType || right.getType() == Type.booleanType || right.getType() == Type.stringType)){
                                            signalError.showError("Type error: the type of the expression of the right-hand side is a basic type and the type of the variable of the left-hand side is a class");
                                    }

                                    if((left.getType() == Type.intType || left.getType() == Type.booleanType || left.getType() == Type.stringType) &&
                                                    right.getType()instanceof KraClass){
                                            signalError.showError("Type error: type of the left-hand side of the assignment is a basic type and the type of the right-hand side is a class");
                                    }

                                    
                                    if((left.getType() == Type.intType || left.getType() == Type.booleanType || left.getType() == Type.stringType) && 
                                                    right.getType()== Type.voidType){
                                            signalError.showError("Type error: 'null' cannot be assigned to a variable of a basic type");
                                    }
                            }
                            
                            if(left.getType() instanceof KraClass && right.getType() instanceof KraClass){
                                    String typeLeftName = left.getType().getName();
                                    String typeRightName = right.getType().getName();

                                    KraClass typeLeft = symbolTable.getInGlobal(typeLeftName);
                                    KraClass typeRight = symbolTable.getInGlobal(typeRightName);


                                    if((typeLeft.getSuper() != null ) && typeLeft.getSuper().getName() == typeRight.getName()){
                                            signalError.showError("Type error: type of the right-hand side of the assignment is not a subclass of the left-hand side");
                                    }

                            }


                            if(right.getType()==Type.voidType){
                                    Variable var = ((VariableExpr)left).getVariable();
                                    symbolTable.removeVarLocalIdent(var.getName(), var);
                                    symbolTable.putInLocal(var.getName(), var);
                            }

                            
                            if (right.getType() == Type.voidType) {
                                    signalError.showError("Expression expected in the right-hand side of assignment");
                            }

                            if ( lexer.token != Symbol.SEMICOLON )
                                    signalError.showError("Missing ';'", true);
                            else
                                    lexer.nextToken();

                    } else {
                            if (left instanceof VariableExpr) {
                                    String typeName = ((VariableExpr) left).getVariable().getName();
                                    localDecClassType(typeName);
                                    left = null;
                            } else if (left instanceof MessageSendToVariable ||
                                            left instanceof MessageSendToSuper ||
                                            left instanceof MessageSendToSelf) {

                                    if (left.getType() != Type.voidType) {
                                            signalError.showError("Message send returns a value that is not used");
                                    }
                            }
                    }
            }
		
            return new AssignStatement(left, right);
	}

	private ExprList realParameters() {
		ExprList anExprList = null;

		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("Missing '('");
		lexer.nextToken();
		if ( startExpr(lexer.token) ) anExprList = exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		return anExprList;
	}

	private WhileStatement whileStatement() {
                whileAberto = true;
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("Missing '('");
		lexer.nextToken();
		Expr wlExpr = expr();
                if(wlExpr.getType() != Type.booleanType)
                    signalError.showError("Non-boolean expression in while statement");
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		Statement wlStmt = statement();
                
                whileAberto = false;
		return new WhileStatement(wlExpr, wlStmt);
	}

	private IfStatement ifStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("Missing '('");
		lexer.nextToken();
		Expr ifExpr = expr();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		Statement ifStmt = statement();
		Statement elseStmt = null;
		if ( lexer.token == Symbol.ELSE ) {
			lexer.nextToken();
			elseStmt = statement();
		}

		return new IfStatement(ifExpr, ifStmt, elseStmt);
	}

	private ReturnStatement returnStatement() {

		lexer.nextToken();
		Expr rtExpr = expr();
                if (metodoAtual.getType() != rtExpr.getType())
                    signalError.showError("Type of the return is not subclass of the method return type");
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();

		return new ReturnStatement(rtExpr);
	}

	private ReadStatement readStatement() {
		ArrayList<String> leftValues = new ArrayList<String>();
		boolean isThis, isSub;

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("Missing '('");
		lexer.nextToken();

		while (true) {
			isThis = false;
			isSub = false;
			String name, subName = null;
			if ( lexer.token == Symbol.THIS ) {
				isThis = true;
				lexer.nextToken();
				if ( lexer.token != Symbol.DOT ) signalError.showError(". expected");
				lexer.nextToken();
			}
			if ( lexer.token != Symbol.IDENT )
				signalError.show(ErrorSignaller.ident_expected);

			name = lexer.getStringValue();
			lexer.nextToken();

			if ( lexer.token == Symbol.DOT ) {
				isSub = true;
				lexer.nextToken();
				if (lexer.token != Symbol.IDENT) {
					signalError.show(ErrorSignaller.ident_expected);
				}
				subName = lexer.getStringValue();
				lexer.nextToken();
			}

			if (isThis) {
				leftValues.add("this." + name);
			} else if (isSub) {
				leftValues.add(name + "." + subName);
			} else {
				leftValues.add(name);
			}

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

		return new ReadStatement(leftValues);
	}

	private WriteStatement writeStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("Missing '('");
		lexer.nextToken();
		ExprList exprListStmt = exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();

		return new WriteStatement(exprListStmt);
	}

	private WritelnStatement writelnStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.showError("Missing '('");
		lexer.nextToken();
		ExprList exprListStmt = exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.showError("')' expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();

		return new WritelnStatement(exprListStmt);
	}

	private BreakStatement breakStatement() {
                if(whileAberto != true)
                    signalError.showError("Break outside while");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(ErrorSignaller.semicolon_expected);
		lexer.nextToken();

		return new BreakStatement();
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
                        
                        if(left.getType() != Type.intType && op != Symbol.OR)
                            signalError.showError("Operator '+' or '-' doesn't support types other than INT");
                        if(right.getType() != Type.intType && op != Symbol.OR)
                            signalError.showError("Operator '+' or '-' doesn't support types other than INT");
                        if(left.getType() != Type.booleanType && op == Symbol.OR)
                            signalError.showError("Operator 'or' doesn't support types other than BOOLEAN");
                        if(right.getType() != Type.booleanType && op == Symbol.OR)
                            signalError.showError("Operator 'or' doesn't support types other than BOOLEAN");
                        
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
                    
                        if(classAtual.getSuper() == null)
                            signalError.showError("The current class does not have a superclass");
                    
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				signalError.showError("'.' expected");
			}

			lexer.nextToken();

			if ( lexer.token != Symbol.IDENT )
				signalError.showError("Identifier expected");

			messageName = lexer.getStringValue();
                        Method m = classAtual.getMethodS(messageName);
                        
                        if (m == null){
                                signalError.showError("Method not found in superclass(es)");
                        }
                        
                        if (m.isPrivate())
                            signalError.showError("Method found is private in the superclass");
                        
			lexer.nextToken();
			exprList = this.realParameters();
			
                        return new MessageSendToSuper(exprList);
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
				if (lexer.token == Symbol.LEFTPAR) {
						signalError.showError("'.' or '=' expected after an identifier OR statement expected");
				}
				
				Variable var;
				if (lexer.token == Symbol.IDENT || lexer.token == Symbol.ASSIGN) {
					var = new Variable(firstId, Type.undefinedType);
					return new VariableExpr(var);
				}
				
				var = symbolTable.getInLocal(firstId);
				if (var == null) {
					signalError.showError("Variable '" + firstId + "' was not declared");
}
                                
				return new VariableExpr(symbolTable.getInLocal(firstId));
			}
			else { // Id "."
				lexer.nextToken(); // coma o "."
				if ( lexer.token != Symbol.IDENT ) {
					signalError.showError("Identifier expected");
				}
				else {
					// Id "." Id
					lexer.nextToken();
                                        id = lexer.getStringValue();
                                        
					if ( lexer.token == Symbol.DOT ) {
						
					}else if ( lexer.token == Symbol.LEFTPAR ) {
						// Id "." Id "(" [ ExpressionList ] ")"
						KraClass firstClass = symbolTable.getInGlobal(firstId);
						Method calledMethod = firstClass.getMethod(id);
						if (calledMethod == null) {
							signalError.showError("Method '" + id + "' was not found in class '" + firstId + "' or its superclasses");
						}
						exprList = this.realParameters();
						
                                                return new MessageSendToVariable(symbolTable.getInLocal(firstId), calledMethod, exprList);                                               
					}
					else {
						KraClass classedoIdent = symbolTable.getInGlobal(symbolTable.getInLocal(firstId).getType().getName());
                                                
                                                return new MessageSendToInstance(symbolTable.getInLocal(firstId), classedoIdent.getVariable(id));
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
				return new MessageSendToSelf(classAtual);
			}
			else {
				lexer.nextToken();
				if ( lexer.token != Symbol.IDENT )
					signalError.showError("Identifier expected");
                                
				id = lexer.getStringValue();
				lexer.nextToken();
				
				if ( lexer.token == Symbol.LEFTPAR ) {
                                        Method mt = classAtual.getMethod(id);
                                    	exprList = this.realParameters();
                                        
                                        return new MessageSendToSelf(classAtual, mt , exprList);
				}
				else if ( lexer.token == Symbol.DOT ) {
					
				}
				else {
					InstanceVariable v = (InstanceVariable) symbolTable.getInLocal(id);
					return new MessageSendToSelf(classAtual, v);
				}
			}
			break;
		default:
			signalError.showError("Expression expected");
		}
		return null;
	}

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
        private boolean whileAberto;

}
