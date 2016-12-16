//Enrique Sampaio dos Santos e Gustavo Rodrigues
package comp;

import ast.*;
import lexer.*;
import java.io.*;
import java.util.*;

public class Compiler {

    private static KraClass classeAtual;
    private static Method metodoAtual;
    private static int whileCounter = 0;
    private static boolean hasProgram = false;

    // compile must receive an input with an character less than
    // p_input.lenght
    public Program compile(char[] input, PrintWriter outError) {

        ArrayList<CompilationError> compilationErrorList = new ArrayList<>();
        signalError = new ErrorSignaller(outError, compilationErrorList);
        symbolTable = new SymbolTable();
        lexer = new Lexer(input, signalError);
        whileCounter = 0;
        hasProgram = false;
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
        Program program = new Program(kraClassList, metaobjectCallList, compilationErrorList);
        try {
            while (lexer.token == Symbol.MOCall) {
                metaobjectCallList.add(metaobjectCall());
            }
            program.getClassList().add(classDec());
            while (lexer.token == Symbol.CLASS) {
                program.getClassList().add(classDec());
            }

            if (!hasProgram) {
                signalError.showError("Source code without a class 'Program'");
            }

            if (lexer.token != Symbol.EOF) {
                signalError.showError("End of file expected");
            }
        } catch (RuntimeException e) {
            // if there was an exception, there is a compilation signalError
//            e.printStackTrace();
        }
        return program;
    }

    @SuppressWarnings("incomplete-switch")
    private MetaobjectCall metaobjectCall() {
        String name = lexer.getMetaobjectName();
        lexer.nextToken();
        ArrayList<Object> metaobjectParamList = new ArrayList<>();
        if (lexer.token == Symbol.LEFTPAR) {
            // metaobject call with parameters
            lexer.nextToken();
            while (lexer.token == Symbol.LITERALINT || lexer.token == Symbol.LITERALSTRING
                    || lexer.token == Symbol.IDENT) {
                switch (lexer.token) {
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
                if (lexer.token == Symbol.COMMA) {
                    lexer.nextToken();
                } else {
                    break;
                }
            }
            if (lexer.token != Symbol.RIGHTPAR) {
                signalError.showError("')' expected after metaobject call with parameters");
            } else {
                lexer.nextToken();
            }
        }
        if (name.equals("nce")) {
            if (metaobjectParamList.size() != 0) {
                signalError.showError("Metaobject 'nce' does not take parameters");
            }
        } else if (name.equals("ce")) {
            if (metaobjectParamList.size() != 3 && metaobjectParamList.size() != 4) {
                signalError.showError("Metaobject 'ce' take three or four parameters");
            }
            if (!(metaobjectParamList.get(0) instanceof Integer)) {
                signalError.showError("The first parameter of metaobject 'ce' should be an integer number");
            }
            if (!(metaobjectParamList.get(1) instanceof String) || !(metaobjectParamList.get(2) instanceof String)) {
                signalError.showError("The second and third parameters of metaobject 'ce' should be literal strings");
            }
            if (metaobjectParamList.size() >= 4 && !(metaobjectParamList.get(3) instanceof String)) {
                signalError.showError("The fourth parameter of metaobject 'ce' should be a literal string");
            }

        }

        return new MetaobjectCall(name, metaobjectParamList);
    }

    private KraClass classDec() {
        // Note que os m�todos desta classe n�o correspondem exatamente �s
        // regras
        // da gram�tica. Este m�todo classDec, por exemplo, implementa
        // a produ��o KraClass (veja abaixo) e partes de outras produ��es.

        /*
		 * KraClass ::= ``class'' Id [ ``extends'' Id ] "{" MemberList "}"
		 * MemberList ::= { Qualifier Member } 
		 * Member ::= InstVarDec | MethodDec
		 * InstVarDec ::= Type IdList ";" 
		 * MethodDec ::= Qualifier Type Id "("[ FormalParamDec ] ")" "{" StatementList "}" 
		 * Qualifier ::= [ "static" ]  ( "private" | "public" )
         */
        if (lexer.token != Symbol.CLASS) {
            signalError.showError("'class' expected");
        }
        lexer.nextToken();
        if (lexer.token != Symbol.IDENT) {
            signalError.show(ErrorSignaller.ident_expected);
        }
        String className = lexer.getStringValue();

        if (className.equals("Program")) {
            hasProgram = true;
        }

        KraClass k = new KraClass(className);
        symbolTable.putInGlobal(className, k);
        classeAtual = k;

        lexer.nextToken();
        if (lexer.token == Symbol.EXTENDS) {
            lexer.nextToken();
            if (lexer.token != Symbol.IDENT) {
                signalError.show(ErrorSignaller.ident_expected);
            }
            String superclassName = lexer.getStringValue();

            if (className.equals(superclassName)) {
                signalError.showError("Class '" + className + "' is inheriting from itself");
            }

            KraClass superK = symbolTable.getInGlobal(superclassName);

            classeAtual.setSuperClass(superK);

            lexer.nextToken();
        }
        if (lexer.token != Symbol.LEFTCURBRACKET) {
            signalError.showError("'{' expected", true);
        }
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

            if (lexer.token != Symbol.IDENT) {
                signalError.showError("Identifier expected");
            }

            String name = lexer.getStringValue();
            lexer.nextToken();

            if (lexer.token == Symbol.LEFTPAR) {
                if (classeAtual.getPublicMethod(name) != null || classeAtual.getPrivateMethod(name) != null) {
                    signalError.showError("Method '" + name + "' is being redeclared");
                }

                Method m = methodDec(t, name, qualifier);

                symbolTable.removeLocalIdent();

                if (qualifier == Symbol.PRIVATE) {
                    classeAtual.setPrivateMethod(m);
                } else {
                    classeAtual.setPublicMethod(m);
                }
            } else if (qualifier != Symbol.PRIVATE) {
                signalError.showError("Attempt to declare a public instance variable");
            } else {
                instanceVarDec(t, name);
            }
        }

        if (classeAtual.getName().equals("Program") && classeAtual.getPublicMethod("run") == null) {
            signalError.showError("Method 'run' was not found in class 'Program'");
        }

        if (lexer.token != Symbol.RIGHTCURBRACKET) {
            signalError.showError("public/private or \"}\" expected");
        }
        lexer.nextToken();
        
        return classeAtual;
    }

    private InstanceVariableList instanceVarDec(Type type, String name) {
        // InstVarDec ::= [ "static" ] "private" Type IdList ";"

        InstanceVariableList ivl = new InstanceVariableList();

        if (classeAtual.getInstanceVariable(name) != null) {
            signalError.showError("Variable '" + name + "' is being redeclared");
        }

        InstanceVariable i = new InstanceVariable(name, type);
        classeAtual.setInstanceVariable(i);
        ivl.addElement(i);

        while (lexer.token == Symbol.COMMA) {
            lexer.nextToken();
            if (lexer.token != Symbol.IDENT) {
                signalError.showError("Identifier expected");
            }
            String variableName = lexer.getStringValue();
            lexer.nextToken();

            if (classeAtual.getInstanceVariable(variableName) != null) {
                signalError.showError("Variable '" + variableName + "' is being redeclared");
            }

            i = new InstanceVariable(variableName, type);
            classeAtual.setInstanceVariable(i);
            ivl.addElement(i);
        }
        if (lexer.token != Symbol.SEMICOLON) {
            signalError.show(ErrorSignaller.semicolon_expected);
        }
        lexer.nextToken();

        return ivl;
    }

    private Method methodDec(Type type, String name, Symbol qualifier) {
        /*
		 * MethodDec ::= Qualifier Return Id "("[ FormalParamDec ] ")" "{"
		 *                StatementList "}"
         */
        ParamList paramlist;

        if (classeAtual.getName().equals("Program") && name.equals("run") && qualifier == Symbol.PRIVATE) {
            signalError.showError("Method 'run' of class 'Program' cannot be private");
        }

        if (classeAtual.getName().equals("Program") && name.equals("run") && type != Type.voidType) {
            signalError.showError("Method 'run' of class 'Program' with a return value type different from 'void'");
        }

        if (classeAtual.getInstanceVariable(name) != null) {
            signalError.showError("Method '" + name + "' has name equal to an instance variable");
        }

        if (classeAtual.getSuperMethod(name) != null && classeAtual.getSuperMethod(name).getType() != type) {
            signalError.showError("Method '" + name + "' of subclass '" + classeAtual.getName() + "' has a signature different from method inherited from superclass '" + classeAtual.getSuperClass().getName() + "'");
        }

        metodoAtual = new Method(name, type, qualifier);

        lexer.nextToken();
        if (lexer.token != Symbol.RIGHTPAR) {
            paramlist = formalParamDec();

            KraClass sk = classeAtual.getSuperClass();

            if (sk != null) {
                Method sm = sk.getMethod(name);

                if (sm != null) {
                    if (sm.getParamList().getSize() != paramlist.getSize()) {
                        signalError.showError("Method '" + metodoAtual.getName() + "' is being redefined in subclass '" + classeAtual.getName() + "' with a signature different from the method of superclass '" + sk.getName() + "'");
                    }

                    Iterator<Parameter> pItr = sm.getParamList().elements();
                    Iterator<Parameter> pItr2 = paramlist.elements();

                    while (pItr.hasNext()) {
                        Parameter p = pItr.next();
                        Parameter p2 = pItr2.next();

                        if (p.getType() != p2.getType()) {
                            signalError.showError("Method '" + metodoAtual.getName() + "' is being redefined in subclass '" + classeAtual.getName() + "' with a signature different from the method of superclass '" + sk.getName() + "'");
                        }
                    }
                }
            }

            if (classeAtual.getName().equals("Program") && metodoAtual.getName().equals("run") && paramlist.getSize() != 0) {
                signalError.showError("Method 'run' of class 'Program' cannot take parameters");
            }

            metodoAtual.setParamList(paramlist);
        }
        if (lexer.token != Symbol.RIGHTPAR) {
            signalError.showError(") expected");
        }

        lexer.nextToken();
        if (lexer.token != Symbol.LEFTCURBRACKET) {
            signalError.showError("'{' expected");
        }

        lexer.nextToken();

        metodoAtual.setStatementList(statementList());

        if (!metodoAtual.getHasReturn() && metodoAtual.getType() != Type.voidType) {
            signalError.showError("Missing 'return' statement in method '" + metodoAtual.getName() + "'");
        }

        if (lexer.token != Symbol.RIGHTCURBRACKET) {
            signalError.showError("} expected");
        }

        lexer.nextToken();

        return metodoAtual;
    }

    private LocalDecStatement localDec() {
        // LocalDec ::= Type IdList ";"

        LocalVariableList lvl = new LocalVariableList();
        Type type = type();
        if (lexer.token != Symbol.IDENT) {
            System.out.println("show" + lexer.token);
            signalError.showError("Identifier expected");
        }
        Variable v = new Variable(lexer.getStringValue(), type);

        if (symbolTable.getInLocal(v.getName()) != null) {
            signalError.showError("Variable '" + v.getName() + "' is being redeclared");
        }

        symbolTable.putInLocal(v.getName(), v);
        lvl.addElement(v);
        lexer.nextToken();
        while (lexer.token == Symbol.COMMA) {
            lexer.nextToken();
            if (lexer.token != Symbol.IDENT) {
                signalError.showError("Identifier expected");
            }
            v = new Variable(lexer.getStringValue(), type);
            symbolTable.putInLocal(v.getName(), v);
            lvl.addElement(v);
            lexer.nextToken();

            if (lexer.token == Symbol.ASSIGN) {
                lexer.nextToken();

                Expr e = expr();

                if (v.getType() != e.getType()) {
                    signalError.showError("Incompatible types");
                }
            }
        }

        if (lexer.token == Symbol.ASSIGN) {
            lexer.nextToken();

            Expr e = expr();

            if (v.getType() != e.getType()) {
                signalError.showError("Incompatible types");
            }
        }

        if (lexer.token != Symbol.SEMICOLON) {
            signalError.showError("Missing ';'", true);
        }
        lexer.nextToken();

        return new LocalDecStatement(type, lvl);
    }

    private ParamList formalParamDec() {
        // FormalParamDec ::= ParamDec { "," ParamDec }

        ParamList pl = new ParamList();

        pl.addElement(paramDec());
        while (lexer.token == Symbol.COMMA) {
            lexer.nextToken();
            pl.addElement(paramDec());
        }

        return pl;
    }

    private Parameter paramDec() {
        // ParamDec ::= Type Id
        Parameter param;
        Type aux;

        aux = type();
        if (lexer.token != Symbol.IDENT) {
            signalError.showError("Identifier expected");
        }
        param = new Parameter(lexer.getStringValue(), aux);
        lexer.nextToken();

        return param;
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
            case IDENT:
                // # corrija: fa�a uma busca na TS para buscar a classe
                // IDENT deve ser uma classe.
                KraClass k = symbolTable.getInGlobal(lexer.getStringValue());

                if (k == null) {
                    signalError.showError("Type '" + lexer.getStringValue() + "' was not found");
                }

                result = k;
                break;
            default:
                signalError.showError("Type expected");
                result = Type.undefinedType;
        }
        lexer.nextToken();
        return result;
    }

    private CompositeStatement compositeStatement() {
        if (lexer.token != Symbol.LEFTCURBRACKET) {
            signalError.showError("'{' expected");
        }
        lexer.nextToken();
        StatementList sl = statementList();
        if (lexer.token != Symbol.RIGHTCURBRACKET) {
            signalError.showError("} expected");
        } else {
            lexer.nextToken();
        }

        return new CompositeStatement(sl);
    }

    private StatementList statementList() {
        // CompStatement ::= "{" { Statement } "}"
        Symbol tk;
        StatementList sl = new StatementList();
        // statements always begin with an identifier, if, read, write, ...
        while ((tk = lexer.token) != Symbol.RIGHTCURBRACKET
                && tk != Symbol.ELSE) {
            sl.addElement(statement());
        }

        return sl;
    }

    private Statement statement() {
        /*
		 * Statement ::= Assignment ``;'' | IfStat |WhileStat | MessageSend
		 *                ``;'' | ReturnStat ``;'' | ReadStat ``;'' | WriteStat ``;'' |
		 *               ``break'' ``;'' | ``;'' | CompStatement | LocalDec
         */

        switch (lexer.token) {
            case THIS:
            case IDENT:
            case SUPER:
            case INT:
            case BOOLEAN:
            case STRING:
                return assignExprLocalDec();
            case ASSERT:
                return assertStatement();
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
            case DO:
                return doWhileStatement();
            case SEMICOLON:
                return nullStatement();
            case LEFTCURBRACKET:
                return compositeStatement();
            case PLUS:
                return plusPlusStatement();
            case MINUS:
                return minusMinusStatement();
            default:
                signalError.showError("Statement expected");
        }

        return null;
    }

    private PlusPlusStatement plusPlusStatement() {
        lexer.nextToken();

        if (lexer.token != Symbol.PLUS) {
            signalError.showError("Statement expected");
        }

        lexer.nextToken();

        Variable v = symbolTable.getInLocal(lexer.getStringValue());

        if (v == null) {
            signalError.showError("Variable '" + lexer.getStringValue() + "' not declared");
        } else if (v.getType() != Type.intType) {
            signalError.showError("Incompatible types");
        }

        lexer.nextToken();

        if (lexer.token != Symbol.SEMICOLON) {
            signalError.showError("Missing ;");
        }

        lexer.nextToken();

        return new PlusPlusStatement(v);
    }

    private MinusMinusStatement minusMinusStatement() {
        lexer.nextToken();

        if (lexer.token != Symbol.MINUS) {
            signalError.showError("Statement expected");
        }

        lexer.nextToken();

        Variable v = symbolTable.getInLocal(lexer.getStringValue());

        if (v == null) {
            signalError.showError("Variable '" + lexer.getStringValue() + "' not declared");
        } else if (v.getType() != Type.intType) {
            signalError.showError("Incompatible types");
        }

        lexer.nextToken();

        if (lexer.token != Symbol.SEMICOLON) {
            signalError.showError("Missing ;");
        }

        lexer.nextToken();

        return new MinusMinusStatement(v);
    }

    private Statement assertStatement() {
        lexer.nextToken();
        int lineNumber = lexer.getLineNumber();
        Expr e = expr();
        if (e.getType() != Type.booleanType) {
            signalError.showError("boolean expression expected");
        }
        if (lexer.token != Symbol.COMMA) {
            this.signalError.showError("',' expected after the expression of the 'assert' statement");
        }
        lexer.nextToken();
        if (lexer.token != Symbol.LITERALSTRING) {
            this.signalError.showError("A literal string expected after the ',' of the 'assert' statement");
        }
        String message = lexer.getLiteralStringValue();
        lexer.nextToken();
        if (lexer.token == Symbol.SEMICOLON) {
            lexer.nextToken();
        }

        return new StatementAssert(e, lineNumber, message);
    }

    /*
	 * retorne true se 'name' � uma classe declarada anteriormente. � necess�rio
	 * fazer uma busca na tabela de s�mbolos para isto.
     */
    private boolean isType(String name) {
        return this.symbolTable.getInGlobal(name) != null;
    }

    /*
	 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec
     */
    private Statement assignExprLocalDec() {
        Expr left = null;
        Expr right = null;

        if (lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN
                || lexer.token == Symbol.STRING
                || // token � uma classe declarada textualmente antes desta
                // instru��o
                (lexer.token == Symbol.IDENT && isType(lexer.getStringValue()) && symbolTable.getInLocal(lexer.getStringValue()) == null)) {
            /*
			 * uma declara��o de vari�vel. 'lexer.token' � o tipo da vari�vel
			 * 
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec 
			 * LocalDec ::= Type IdList ``;''
             */
            return localDec();
        } else {
            /*
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ]
             */
            left = expr();

            if (lexer.token == Symbol.ASSIGN) {
                lexer.nextToken();
                right = expr();

                if (left.getType() != right.getType() && !isSubType(left, right)) {
                    if (right.getType() == null) {
                        if (left.getType() == Type.booleanType || left.getType() == Type.intType || left.getType() == Type.stringType || left.getType() == Type.undefinedType || left.getType() == Type.voidType) {
                            signalError.showError("Type error: 'null' cannot be assigned to a variable of a basic type");
                        }
                    } else if (left.getType() instanceof KraClass && right.getType() instanceof KraClass) {
                        KraClass kc = (KraClass) right.getType();

                        if (!kc.hasSuperClass(left.getType().getName())) {
                            signalError.showError("Type error: value of the right-hand side is not subtype of the variable of the left-hand side.");
                        }
                    } else {
                        signalError.showError("Type error: value of the right-hand side is not subtype of the variable of the left-hand side.");
                    }
                }

                if (lexer.token != Symbol.SEMICOLON) {
                    signalError.showError("';' expected", true);
                } else {
                    lexer.nextToken();
                }

            } else if (left instanceof MessageSendToVariable && left.getType() != Type.voidType) {
                MessageSendToVariable mstv = (MessageSendToVariable) left;
                signalError.showError("Message send '" + mstv.getClassVariableName() + "." + mstv.getMethodName() + "()' returns a value that is not used");
            }
            return new AssignStatement(left, right);
        }
    }

    private boolean isSubType(Expr left, Expr right) {
        if (!(left instanceof VariableExpr) || !(right instanceof VariableExpr || right instanceof KraClassExpr)) {
            return false;
        }

        VariableExpr leftVe = (VariableExpr) left;

        KraClass rightKc;
        if (right instanceof VariableExpr) {
            VariableExpr rightVe = (VariableExpr) right;
            rightKc = symbolTable.getInGlobal(rightVe.getType().getName());
        } else {
            KraClassExpr rightKe = (KraClassExpr) right;
            rightKc = rightKe.getKraClass();
        }

        KraClass leftKc = symbolTable.getInGlobal(leftVe.getType().getName());

        if (leftKc == null || rightKc == null) {
            return false;
        }

        return rightKc.hasSuperClass(leftKc.getName());
    }

    private ExprList realParameters() {
        ExprList anExprList = null;

        if (lexer.token != Symbol.LEFTPAR) {
            signalError.showError("( expected");
        }
        lexer.nextToken();
        if (startExpr(lexer.token)) {
            anExprList = exprList();
        }
        if (lexer.token != Symbol.RIGHTPAR) {
            signalError.showError(") expected");
        }
        lexer.nextToken();
        return anExprList;
    }

    private WhileStatement whileStatement() {
        whileCounter++;
        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) {
            signalError.showError("( expected");
        }
        lexer.nextToken();
        Expr e = expr();

        if (e.getType() != Type.booleanType) {
            signalError.showError("non-boolean expression in 'while' command");
        }

        if (lexer.token != Symbol.RIGHTPAR) {
            signalError.showError(") expected");
        }
        lexer.nextToken();
        Statement s = statement();

        whileCounter--;
        return new WhileStatement(e, s);
    }

    private DoWhileStatement doWhileStatement() {
        lexer.nextToken();
        CompositeStatement cs = compositeStatement();

        if (lexer.token != Symbol.WHILE) {
            signalError.showError("'while' expected");
        }

        lexer.nextToken();

        if (lexer.token != Symbol.LEFTPAR) {
            signalError.showError("'(' expected");
        }

        lexer.nextToken();

        Expr e = expr();

        if (e.getType() != Type.booleanType) {
            signalError.showError("boolean expression expected in a do-while statement");
        }

        if (lexer.token != Symbol.RIGHTPAR) {
            signalError.showError(") expected");
        }

        lexer.nextToken();

        if (lexer.token != Symbol.SEMICOLON) {
            signalError.show(ErrorSignaller.semicolon_expected);
        }
        lexer.nextToken();

        return new DoWhileStatement(cs, e);
    }

    private IfStatement ifStatement() {

        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) {
            signalError.showError("( expected");
        }
        lexer.nextToken();
        Expr e = expr();
        if (lexer.token != Symbol.RIGHTPAR) {
            signalError.showError(") expected");
        }
        lexer.nextToken();
        Statement sIf = statement();

        Statement sElse = null;
        if (lexer.token == Symbol.ELSE) {
            lexer.nextToken();
            sElse = statement();
        }

        return new IfStatement(e, sIf, sElse);
    }

    private ReturnStatement returnStatement() {

        if (metodoAtual.getType() == Type.voidType) {
            signalError.showError("Illegal 'return' statement. Method returns 'void'");
        }

        lexer.nextToken();

        Expr e = expr();

        Type et = e.getType();

        if (et != Type.intType && et != Type.booleanType && et != Type.stringType && et != Type.undefinedType && et != Type.voidType) {
            KraClassExpr kce = (KraClassExpr) e;

            if (kce.getType() != metodoAtual.getType()) {
                if (!kce.getKraClass().hasSuperClass(metodoAtual.getType().getName())) {
                    signalError.showError("Type error: type of the expression returned is not subclass of the method return type");
                }
            }
        }

        if (lexer.token != Symbol.SEMICOLON) {
            signalError.show(ErrorSignaller.semicolon_expected);
        }
        lexer.nextToken();

        metodoAtual.setHasReturn(true);

        return new ReturnStatement(e);
    }

    private ReadStatement readStatement() {
        VariableList vl = new VariableList();
        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) {
            signalError.showError("( expected");
        }
        lexer.nextToken();
        while (true) {
            boolean isThis = false;
            if (lexer.token == Symbol.THIS) {
                isThis = true;
                lexer.nextToken();
                if (lexer.token != Symbol.DOT) {
                    signalError.showError(". expected");
                }
                lexer.nextToken();
            }
            if (lexer.token != Symbol.IDENT) {
                signalError.show(ErrorSignaller.ident_expected);
            }

            String name = lexer.getStringValue();

            InstanceVariable iv;
            Variable v;

            if (isThis) {
                iv = classeAtual.getInstanceVariable(name);

                vl.addElement(iv);

                if (iv.getType() == Type.booleanType) {
                    signalError.showError("Command 'read' does not accept 'boolean' variables");
                }
            } else {
                v = symbolTable.getInLocal(name);

                vl.addElement(v);

                if (v.getType() == Type.booleanType) {
                    signalError.showError("Command 'read' does not accept 'boolean' variables");
                }
            }

            lexer.nextToken();
            if (lexer.token == Symbol.COMMA) {
                lexer.nextToken();
            } else {
                break;
            }
        }

        if (lexer.token != Symbol.RIGHTPAR) {
            signalError.showError(") expected");
        }
        lexer.nextToken();
        if (lexer.token != Symbol.SEMICOLON) {
            signalError.show(ErrorSignaller.semicolon_expected);
        }
        lexer.nextToken();

        return new ReadStatement(vl);
    }

    private WriteStatement writeStatement() {
        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) {
            signalError.showError("( expected");
        }
        lexer.nextToken();
        ExprList el = exprList();
        Iterator<Expr> elItr = el.elements();

        while (elItr.hasNext()) {
            Expr e = elItr.next();

            if (e.getType() == Type.booleanType) {
                signalError.showError("Command 'write' does not accept 'boolean' expressions");
            } else if (e.getType() != Type.booleanType && e.getType() != Type.intType && e.getType() != Type.stringType && e.getType() != Type.undefinedType && e.getType() != Type.voidType) {
                signalError.showError("Command 'write' does not accept objects");
            }
        }

        if (lexer.token != Symbol.RIGHTPAR) {
            signalError.showError(") expected");
        }
        lexer.nextToken();
        if (lexer.token != Symbol.SEMICOLON) {
            signalError.show(ErrorSignaller.semicolon_expected);
        }
        lexer.nextToken();

        return new WriteStatement(el);
    }

    private WriteStatement writelnStatement() {

        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) {
            signalError.showError("( expected");
        }
        lexer.nextToken();
        ExprList el = exprList();
        if (lexer.token != Symbol.RIGHTPAR) {
            signalError.showError(") expected");
        }
        lexer.nextToken();
        if (lexer.token != Symbol.SEMICOLON) {
            signalError.show(ErrorSignaller.semicolon_expected);
        }
        lexer.nextToken();

        return new WriteStatement(el, true);
    }

    private BreakStatement breakStatement() {
        if (whileCounter == 0) {
            signalError.showError("Command 'break' outside a command 'while'");
        }

        lexer.nextToken();
        if (lexer.token != Symbol.SEMICOLON) {
            signalError.show(ErrorSignaller.semicolon_expected);
        }
        lexer.nextToken();

        return new BreakStatement();
    }

    private NullStatement nullStatement() {
        lexer.nextToken();

        return new NullStatement();
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
        if (op == Symbol.EQ || op == Symbol.NEQ || op == Symbol.LE
                || op == Symbol.LT || op == Symbol.GE || op == Symbol.GT) {
            lexer.nextToken();
            Expr right = simpleExpr();

            if (left.getType() != right.getType() && left.getType() != null && right.getType() != null && !isSubType(left, right) && !isSubType(right, left)) {
                switch (op) {
                    case EQ:
                        signalError.showError("Incompatible types cannot be compared with '==' because the result will always be 'false'");
                        break;
                }
                signalError.showError("");
            }
            left = new CompositeExpr(left, op, right);
        }
        return left;
    }

    private Expr simpleExpr() {
        Symbol op;

        Expr left = term();
        while ((op = lexer.token) == Symbol.MINUS || op == Symbol.PLUS
                || op == Symbol.OR) {
            if (op == Symbol.PLUS && left.getType() == Type.booleanType) {
                signalError.showError("type boolean does not support operation '+'");
            } else if (op == Symbol.MINUS && left.getType() == Type.booleanType) {
                signalError.showError("type boolean does not support operation '-'");
            }
            lexer.nextToken();
            Expr right = term();

            if (op == Symbol.PLUS && left.getType() == Type.intType && right.getType() != Type.intType) {
                signalError.showError("operator '+' of 'int' expects an 'int' value");
            } else if (op == Symbol.MINUS && left.getType() == Type.intType && right.getType() != Type.intType) {
                signalError.showError("operator '-' of 'int' expects an 'int' value");
            }
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
        if ((op = lexer.token) == Symbol.PLUS || op == Symbol.MINUS) {
            lexer.nextToken();
            return new SignalExpr(op, factor());
        } else {
            return factor();
        }
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
                if (lexer.token != Symbol.RIGHTPAR) {
                    signalError.showError(") expected");
                }
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

                if (anExpr.getType() == Type.intType) {
                    signalError.showError("Operator '!' does not accepts 'int' values");
                }

                return new UnaryExpr(anExpr, Symbol.NOT);
            // ObjectCreation ::= "new" Id "(" ")"
            case NEW:
                lexer.nextToken();
                if (lexer.token != Symbol.IDENT) {
                    signalError.showError("Identifier expected");
                }

                String className = lexer.getStringValue();
                /*
			 * // encontre a classe className in symbol table KraClass 
			 *      aClass = symbolTable.getInGlobal(className); 
			 *      if ( aClass == null ) ...
                 */
                KraClass aClass = symbolTable.getInGlobal(className);

                if (aClass == null) {
                    signalError.showError("Class '" + className + "' was not found");
                }

                lexer.nextToken();
                if (lexer.token != Symbol.LEFTPAR) {
                    signalError.showError("( expected");
                }
                lexer.nextToken();
                if (lexer.token != Symbol.RIGHTPAR) {
                    signalError.showError(") expected");
                }
                lexer.nextToken();
                /*
			 * return an object representing the creation of an object
                 */
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
                if (classeAtual.getSuperClass() == null) {
                    signalError.showError("'super' used in class '" + classeAtual.getName() + "' that does not have a superclass");
                }
                lexer.nextToken();
                if (lexer.token != Symbol.DOT) {
                    signalError.showError("'.' expected");
                } else {
                    lexer.nextToken();
                }
                if (lexer.token != Symbol.IDENT) {
                    signalError.showError("Identifier expected");
                }
                messageName = lexer.getStringValue();
                /*
			 * para fazer as confer�ncias sem�nticas, procure por 'messageName'
			 * na superclasse/superclasse da superclasse etc
                 */

                Method m = classeAtual.getSuperMethod(messageName);

                if (m == null) {
                    signalError.showError("Method '" + messageName + "' was not found in the public interface of '" + classeAtual.getSuperClass().getName() + "' or its superclasses");
                }

                lexer.nextToken();
                exprList = realParameters();
                return new MessageSendToSuper(m, exprList);
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
                if (lexer.token != Symbol.DOT) {
                    // Id
                    // retorne um objeto da ASA que representa um identificador
                    if (lexer.token == Symbol.IDENT) {
                        signalError.showError("Type '" + firstId + "' was not found");
                    }
                    Variable var = symbolTable.getInLocal(firstId);

                    if (var == null) {
                        var = metodoAtual.getParam(firstId);
                        if (var == null) {
                            signalError.showError("Variable '" + firstId + "' was not declared");
                        }
                    }

                    return new VariableExpr(var);
                } else { // Id "."
                    lexer.nextToken(); // coma o "."
                    if (lexer.token != Symbol.IDENT) {
                        signalError.showError("Identifier expected");
                    } else {
                        // Id "." Id
                        id = lexer.getStringValue();
                        lexer.nextToken();

                        if (lexer.token == Symbol.DOT) {
                            // Id "." Id "." Id "(" [ ExpressionList ] ")"
                            /*
						 * se o compilador permite vari�veis est�ticas, � poss�vel
						 * ter esta op��o, como
						 *     Clock.currentDay.setDay(12);
						 * Contudo, se vari�veis est�ticas n�o estiver nas especifica��es,
						 * sinalize um erro neste ponto.
                             */
                            signalError.showError("Static Variables not implemented");
                        } else if (lexer.token == Symbol.LEFTPAR) {
                            // Id "." Id "(" [ ExpressionList ] ")"
                            Variable var = symbolTable.getInLocal(firstId);

                            if (var == null) {
                                var = metodoAtual.getParam(firstId);
                                if (var == null) {
                                    signalError.showError("Variable '" + firstId + "' was not declared");
                                }
                            }

                            Type t = var.getType();
                            if (t == Type.booleanType || t == Type.intType || t == Type.stringType || t == Type.undefinedType || t == Type.voidType) {
                                signalError.showError("Message send to a non-object receiver");
                            }

                            KraClass firstClass = symbolTable.getInGlobal(var.getType().getName());
                            Method calledMethod = firstClass.getPublicMethod(id);

                            if (calledMethod == null) {
                                if (id.equals(metodoAtual.getName())) {
                                    calledMethod = metodoAtual;
                                } else {
                                    calledMethod = firstClass.getSuperMethod(id);
                                    if (calledMethod == null) {
                                        signalError.showError("Method '" + id + "' was not found in class '" + var.getType().getName() + "' or its superclasses");
                                    }
                                }
                            }

                            exprList = this.realParameters();
                            /*
						 * para fazer as confer�ncias sem�nticas, procure por
						 * m�todo 'ident' na classe de 'firstId'
                             */

                            return new MessageSendToVariable(calledMethod, firstId, exprList);
                        } else {
                            // retorne o objeto da ASA que representa Id "." Id
                            Variable var = symbolTable.getInLocal(firstId);

                            if (var == null) {
                                var = metodoAtual.getParam(firstId);
                                if (var == null) {
                                    signalError.showError("Variable '" + firstId + "' was not declared");
                                }
                            }

                            Type t = var.getType();
                            if (t == Type.booleanType || t == Type.intType || t == Type.stringType || t == Type.undefinedType || t == Type.voidType) {
                                signalError.showError("Message send to a non-object receiver");
                            }

                            KraClass firstClass = symbolTable.getInGlobal(var.getType().getName());
                            InstanceVariable iv = firstClass.getInstanceVariable(id);

                            if (iv == null) {
                                iv = firstClass.getSuperInstanceVariable(id);
                                if (iv == null) {
                                    signalError.showError("Instance variable '" + id + "' was not found in class '" + var.getType().getName() + "' or its superclasses");
                                }
                            }

                            return new MessageSendToVariable(iv, firstId);
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
                if (lexer.token != Symbol.DOT) {
                    // only 'this'
                    // retorne um objeto da ASA que representa 'this'
                    // confira se n�o estamos em um m�todo est�tico
                    return new MessageSendToSelf(classeAtual);
                } else {
                    lexer.nextToken();
                    if (lexer.token != Symbol.IDENT) {
                        signalError.showError("Identifier expected");
                    }
                    id = lexer.getStringValue();
                    lexer.nextToken();
                    // j� analisou "this" "." Id
                    if (lexer.token == Symbol.LEFTPAR) {
                        // "this" "." Id "(" [ ExpressionList ] ")"
                        /*
					 * Confira se a classe corrente possui um m�todo cujo nome �
					 * 'ident' e que pode tomar os par�metros de ExpressionList
                         */
                        exprList = this.realParameters();

                        Method tm = classeAtual.getMethod(id);

                        if (tm == null) {
                            signalError.showError("Method '" + id + "' was not found in class '" + classeAtual.getName() + "' or its superclasses");
                        }

                        ParamList pl = tm.getParamList();

                        if ((exprList != null && pl.getSize() == 0) || (exprList == null && pl.getSize() != 0)) {
                            signalError.showError("Type error: the type of the real parameter is not subclass of the type of the formal parameter");
                        }

                        if (exprList != null) {
                            Iterator<Parameter> pItr = pl.elements();
                            Iterator<Expr> eItr = exprList.elements();

                            while (eItr.hasNext()) {
                                Expr eAux = eItr.next();
                                Parameter pAux = pItr.next();

                                if (eAux.getType() != pAux.getType()) {
                                    if (eAux.getType() instanceof KraClass && pAux.getType() instanceof KraClass) {
                                        KraClass kc = (KraClass) eAux.getType();

                                        if (!kc.hasSuperClass(pAux.getType().getName())) {
                                            signalError.showError("Type error: the type of the real parameter is not subclass of the type of the formal parameter");
                                        }
                                    } else {
                                        signalError.showError("Type error: the type of the real parameter is not subclass of the type of the formal parameter");
                                    }
                                }
                            }
                        }

                        return new MessageSendToSelf(classeAtual, tm, exprList);
                    } else if (lexer.token == Symbol.DOT) {
                        // "this" "." Id "." Id "(" [ ExpressionList ] ")"
                        lexer.nextToken();
                        if (lexer.token != Symbol.IDENT) {
                            signalError.showError("Identifier expected");
                        }
                        lexer.nextToken();
                        exprList = this.realParameters();
                    } else {
                        // retorne o objeto da ASA que representa "this" "." Id
                        /*
					 * confira se a classe corrente realmente possui uma
					 * vari�vel de inst�ncia 'ident'
                         */

                        InstanceVariable iv = classeAtual.getInstanceVariable(id);

                        if (id == null) {
                            signalError.showError("Instance variable '" + iv.getName() + "' was not declared");
                        }

                        return new MessageSendToSelf(classeAtual, iv);
                    }
                }
                break;
            default:
                signalError.showError("Expression expected");
        }

        return null;
    }

    private Expr literalInt() {

        LiteralInt e = null;

        // the number value is stored in lexer.getToken().value as an object of
        // Integer.
        // Method intValue returns that value as an value of type int.
        int value = lexer.getNumberValue();
        lexer.nextToken();
        e = new LiteralInt(value);
        return e;
    }

    private static boolean startExpr(Symbol token) {

        return token == Symbol.FALSE || token == Symbol.TRUE
                || token == Symbol.NOT || token == Symbol.THIS
                || token == Symbol.LITERALINT || token == Symbol.SUPER
                || token == Symbol.LEFTPAR || token == Symbol.NULL
                || token == Symbol.IDENT || token == Symbol.LITERALSTRING;

    }

    private SymbolTable symbolTable;
    private Lexer lexer;
    private ErrorSignaller signalError;

}
