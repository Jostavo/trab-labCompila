//Enrique Sampaio dos Santos e Gustavo Rodrigues
package comp;

import ast.*;
import lexer.*;
import java.io.*;
import java.util.*;

public class Compiler {

    private static KraClass classeAtual;
    private static Method metodoAtual;

    // compile must receive an input with an character less than
    // p_input.lenght
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
        Program program = new Program(kraClassList, metaobjectCallList, compilationErrorList);
        try {
            while (lexer.token == Symbol.MOCall) {
                metaobjectCallList.add(metaobjectCall());
            }
            classDec();
            while (lexer.token == Symbol.CLASS) {
                classDec();
            }
            if (lexer.token != Symbol.EOF) {
                signalError.showError("End of file expected");
            }
        } catch (RuntimeException e) {
            // if there was an exception, there is a compilation signalError
        }
        return program;
    }

    /**
     * parses a metaobject call as <code>{@literal @}ce(...)</code> in <br>
     * <code>
     *
     * @ce(5, "'class' expected") <br>
     * clas Program <br>
     * public void run() { } <br>
     * end <br>
     * </code>
     *
     *
     */
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

    private void classDec() {
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

            lexer.nextToken();
        }
        if (lexer.token != Symbol.LEFTCURBRACKET) {
            signalError.showError("{ expected", true);
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
        if (lexer.token != Symbol.RIGHTCURBRACKET) {
            signalError.showError("public/private or \"}\" expected");
        }
        lexer.nextToken();

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
        
        if (classeAtual.getName().equals("Program") && name.equals("run") && qualifier == Symbol.PRIVATE) {
            signalError.showError("Method 'run' of class 'Program' cannot be private");
        }

        if (classeAtual.getName().equals("Program") && name.equals("run") && type != Type.voidType) {
            signalError.showError("Method 'run' of class 'Program' with a return value type different from 'void'");
        }
        
        metodoAtual = new Method(name, type);
        
        lexer.nextToken();
        if (lexer.token != Symbol.RIGHTPAR) {
            formalParamDec();
        }
        if (lexer.token != Symbol.RIGHTPAR) {
            signalError.showError(") expected");
        }

        lexer.nextToken();
        if (lexer.token != Symbol.LEFTCURBRACKET) {
            signalError.showError("{ expected");
        }

        lexer.nextToken();
        
        metodoAtual.setStatementList(statementList());
        
        if (lexer.token != Symbol.RIGHTCURBRACKET) {
            signalError.showError("} expected");
        }

        lexer.nextToken();

        return metodoAtual;
    }

    private void localDec() {
        // LocalDec ::= Type IdList ";"

        Type type = type();
        if (lexer.token != Symbol.IDENT) {
            signalError.showError("Identifier expected");
        }
        Variable v = new Variable(lexer.getStringValue(), type);
        symbolTable.putInLocal(v.getName(), v);
        lexer.nextToken();
        while (lexer.token == Symbol.COMMA) {
            lexer.nextToken();
            if (lexer.token != Symbol.IDENT) {
                signalError.showError("Identifier expected");
            }
            v = new Variable(lexer.getStringValue(), type);
            symbolTable.putInLocal(v.getName(), v);
            lexer.nextToken();
        }
    }

    private void formalParamDec() {
        // FormalParamDec ::= ParamDec { "," ParamDec }

        paramDec();
        while (lexer.token == Symbol.COMMA) {
            lexer.nextToken();
            paramDec();
        }
    }

    private void paramDec() {
        // ParamDec ::= Type Id

        type();
        if (lexer.token != Symbol.IDENT) {
            signalError.showError("Identifier expected");
        }
        lexer.nextToken();
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
                assignExprLocalDec();
                break;
            case ASSERT:
                assertStatement();
                break;
            case RETURN:
                return returnStatement();
            case READ:
                readStatement();
                break;
            case WRITE:
                writeStatement();
                break;
            case WRITELN:
                writelnStatement();
                break;
            case IF:
                ifStatement();
                break;
            case BREAK:
                breakStatement();
                break;
            case WHILE:
               return whileStatement();
            case SEMICOLON:
                nullStatement();
                break;
            case LEFTCURBRACKET:
                compositeStatement();
                break;
            default:
                signalError.showError("Statement expected");
        }
        
        return null;
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
    private Expr assignExprLocalDec() {

        if (lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN
                || lexer.token == Symbol.STRING
                || // token � uma classe declarada textualmente antes desta
                // instru��o
                (lexer.token == Symbol.IDENT && isType(lexer.getStringValue()))) {
            /*
			 * uma declara��o de vari�vel. 'lexer.token' � o tipo da vari�vel
			 * 
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec 
			 * LocalDec ::= Type IdList ``;''
             */
            localDec();
        } else {
            /*
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ]
             */
            Expr left = expr();
            if (lexer.token == Symbol.ASSIGN) {
                lexer.nextToken();
                Expr right = expr();

                if (left.getType() != right.getType()) {
                    signalError.showError("Type error: value of the right-hand side is not subtype of the variable of the left-hand side.");
                }

                if (lexer.token != Symbol.SEMICOLON) {
                    signalError.showError("';' expected", true);
                } else {
                    lexer.nextToken();
                }
            }
        }
        return null;
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
        
        return new WhileStatement(e, s);
    }

    private void ifStatement() {

        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) {
            signalError.showError("( expected");
        }
        lexer.nextToken();
        expr();
        if (lexer.token != Symbol.RIGHTPAR) {
            signalError.showError(") expected");
        }
        lexer.nextToken();
        statement();
        if (lexer.token == Symbol.ELSE) {
            lexer.nextToken();
            statement();
        }
    }

    private ReturnStatement returnStatement() {

        if (metodoAtual.getType() == Type.voidType) {
            signalError.showError("Illegal 'return' statement. Method returns 'void'");
        }
        
        lexer.nextToken();
        
        Expr e = expr();
        
        if (lexer.token != Symbol.SEMICOLON) {
            signalError.show(ErrorSignaller.semicolon_expected);
        }
        lexer.nextToken();
        
        return new ReturnStatement(e);
    }

    private void readStatement() {
        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) {
            signalError.showError("( expected");
        }
        lexer.nextToken();
        while (true) {
            if (lexer.token == Symbol.THIS) {
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
    }

    private void writeStatement() {
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
    }

    private void writelnStatement() {

        lexer.nextToken();
        if (lexer.token != Symbol.LEFTPAR) {
            signalError.showError("( expected");
        }
        lexer.nextToken();
        exprList();
        if (lexer.token != Symbol.RIGHTPAR) {
            signalError.showError(") expected");
        }
        lexer.nextToken();
        if (lexer.token != Symbol.SEMICOLON) {
            signalError.show(ErrorSignaller.semicolon_expected);
        }
        lexer.nextToken();
    }

    private void breakStatement() {
        lexer.nextToken();
        if (lexer.token != Symbol.SEMICOLON) {
            signalError.show(ErrorSignaller.semicolon_expected);
        }
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
        if (op == Symbol.EQ || op == Symbol.NEQ || op == Symbol.LE
                || op == Symbol.LT || op == Symbol.GE || op == Symbol.GT) {
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
                break;
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
                    Variable var = symbolTable.getInLocal(firstId);

                    if (var == null) {
                        signalError.showError("Variable '" + firstId + "' was not declared");
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
                            lexer.nextToken();
                            if (lexer.token != Symbol.IDENT) {
                                signalError.showError("Identifier expected");
                            }
                            messageName = lexer.getStringValue();
                            lexer.nextToken();
                            exprList = this.realParameters();
                            
                            return new MessageSendToSuper(exprList);
                        } else if (lexer.token == Symbol.LEFTPAR) {
                            // Id "." Id "(" [ ExpressionList ] ")"
                            Variable var = symbolTable.getInLocal(firstId);

                            if (var == null) {
                                signalError.showError("Variable '" + firstId + "' was not declared");
                            }

                            KraClass firstClass = symbolTable.getInGlobal(var.getType().getName());
                            Method calledMethod = firstClass.getPublicMethod(id);

                            if (calledMethod == null) {
                                signalError.showError(
                                        "Method '" + id + "' was not found in class '" + var.getType().getName() + "' or its superclasses");
                            }

                            exprList = this.realParameters();
                            /*
						 * para fazer as confer�ncias sem�nticas, procure por
						 * m�todo 'ident' na classe de 'firstId'
                             */

                            return new MessageSendToVariable(calledMethod.getType());
                        } else {
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
                if (lexer.token != Symbol.DOT) {
                    // only 'this'
                    // retorne um objeto da ASA que representa 'this'
                    // confira se n�o estamos em um m�todo est�tico
                    return null;
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
                        return null;
                    }
                }
                break;
            default:
                signalError.showError("Expression expected");
        }

        return null;
    }

    private LiteralInt literalInt() {

        LiteralInt e = null;

        // the number value is stored in lexer.getToken().value as an object of
        // Integer.
        // Method intValue returns that value as an value of type int.
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

    private SymbolTable symbolTable;
    private Lexer lexer;
    private ErrorSignaller signalError;

}
