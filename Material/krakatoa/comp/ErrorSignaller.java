package comp;
import java.io.*;
import java.util.ArrayList;
import lexer.*;

public class ErrorSignaller {


    public ErrorSignaller( PrintWriter out, ArrayList<CompilationError> compilationErrorList ) {
          // output of an error is done in out
        this.out = out;
        foundCompilerError = false;
        this.compilationErrorList = compilationErrorList;
    }


    public void setLexer( Lexer lexer ) {
        this.lexer = lexer;
    }


    public boolean wasAnErrorSignalled() {
        return foundCompilerError;
    }


    public void showError( String strMessage ) {
        showError( strMessage, false);
    }


    public void showError( String strMessage, boolean goPreviousToken ) {
        // is goPreviousToken is true, the error is signalled at the line of the
        // previous token, not the last one.
        if ( goPreviousToken )
           showError( strMessage, lexer.getLineBeforeLastToken(),
                 lexer.getLineNumberBeforeLastToken() );
        else
           showError( strMessage, lexer.getCurrentLine(), lexer.getLineNumber() );
    }


   public void showError( String strMessage, String lineWithError, int lineNumber ) {
      /* String msg = lineNumber + " : " + strMessage;
      out.println(msg);
      out.println(lineWithError); */
      //System.out.println( msg );
      //System.out.println( lineWithError );
      out.flush();
      if ( out.checkError() )
         System.out.println("Error in signaling an error");
      foundCompilerError = true;
      CompilationError newError = new CompilationError(strMessage, lineNumber, lineWithError);
      compilationErrorList.add(newError);
      throw new RuntimeException();
   }


   public void show( int messageNumber ) {
      if ( messageNumber < 0 || messageNumber >= last_error )
         showError("Internal error: unidentified error number");
      else {
         if ( messageNumber == semicolon_expected )
            showError(strError[messageNumber], true);
         else
            showError(strError[messageNumber], false);
      }
   }

	public ArrayList<CompilationError> getCompilationErrorList() {
		return compilationErrorList;
	}


    public final static int
       ident_expected = 0,
       semicolon_expected = 1,
       last_error = 2;
    public final static String strError[] = {
       "Identifier expected",
       "; expected",
    };
    private Lexer lexer;
    private PrintWriter out;
    private boolean foundCompilerError;
    
    private ArrayList<CompilationError> compilationErrorList;

}

/*
begin expected

*/