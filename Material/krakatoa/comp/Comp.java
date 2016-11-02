package comp;
import java.io.*;
import java.util.ArrayList;
import ast.*;

public class Comp {

	public static void main( String []args ) {
		new Comp().run(args);
	}
	
    public void run( String []args ) {

        File file;
        if ( args.length < 1 ||  args.length > 2 )  {
            System.out.println("Usage:\n   comp input");
            System.out.println("input is the file or directory to be compiled");
            System.out.println("the output file will be created in the current directory");
        }
        else {
        	
           numSourceFilesWithErros = 0;
           int numSourceFiles = 0;
           shouldButWereNotList = new ArrayList<>(); 
           wereButShouldNotList = new ArrayList<>(); 
           wereButWrongLineList = new ArrayList<>();
           correctList = new ArrayList<>();



           PrintWriter outError;
           outError = new PrintWriter(System.out);

           PrintWriter report;
 	       FileOutputStream reportStream = null;
           try {
        	   reportStream = new FileOutputStream("report.txt");
           } catch ( FileNotFoundException  e) {
              outError.println("Could not create 'report.txt'");
              return ;
           }
           report = new PrintWriter(reportStream);
           
 

           file = new File(args[0]);
           if ( ! file.exists() || ! file.canRead() ) {
               String msg = "Either the file " + args[0] + " does not exist or it cannot be read";
               System.out.println(msg);
               outError.println("-1 : " + msg);
               outError.close();
               report.close();
               return ;
             }           
           if ( file.isDirectory() ) {
        	   // compile all files in this directory
        	   File fileList[] = file.listFiles();
        	   for ( File f : fileList ) {
        		   String filename = f.getName();
        		   int lastIndexDot = filename.lastIndexOf('.');
        		   String ext = filename.substring(lastIndexDot + 1);
        		   if ( ext.equalsIgnoreCase("kra") ) {
        			   numSourceFiles++;
        			   try {
        			       compileProgram(f, filename, outError);
        			   } catch (RuntimeException e ) {
        				   System.out.println("Runtime exception");
        			   }
        			   catch (Throwable t) {
        				   System.out.println("Throwable exception");
        			   }
        		   }
        	   }
        	   printReport(numSourceFiles, report);
               
           }
           else {
               compileProgram(file, args[0], outError);
        	   printReport(1, report);           
           }


            report.close();
            System.out.println("Krakatoa compiler finished");
            /**
             // Just a test
             StatementAssert stat = new StatementAssert( new VariableExpr( new Variable("name", Type.intType)), 10, "operator '+' in '1 + 1 == 2'");
             PW pw = new PW();
             pw.set(outError);
             stat.genC(pw);
             outError.flush();
             
             */
            
        }
   }

	/**
	   @param numSourceFiles
	   @param report
	 */
	private void printReport(int numSourceFiles, PrintWriter report) {
		boolean compilerOk = true;
		report.println("Relatório do Compilador");
		   report.println();
		   if ( numSourceFilesWithErros > 0 ) {
			   report.println(this.shouldButWereNotList.size() + " de um total de " + numSourceFilesWithErros + 
					   " erros que deveriam ser sinalizados não o foram (" +
			              (int ) (100.0*this.shouldButWereNotList.size()/this.numSourceFilesWithErros) + "%)");
			   report.println(this.wereButWrongLineList.size() + " erros foram sinalizados na linha errada (" 
					   + (int ) (100.0*this.wereButWrongLineList.size()/this.numSourceFilesWithErros) + "%)");
		   }
		   if ( numSourceFiles -  numSourceFilesWithErros != 0 ) {
			   report.println(this.wereButShouldNotList.size() +  
			              " erros foram sinalizados em " + (numSourceFiles -  numSourceFilesWithErros) 
			              + " arquivos sem erro (" + 
			              (int ) (100.0*this.wereButShouldNotList.size()/(numSourceFiles -  numSourceFilesWithErros)) + "%)"
					   );
		   }

		   if ( numSourceFilesWithErros > 0 ) {
			   if ( shouldButWereNotList.size() == 0 ) {
				   report.println("Todos os erros que deveriam ter sido sinalizados o foram");
			   }
			   else {
				   compilerOk = false;
				   report.println();
				   report.println("Erros que deveriam ser sinalizados mas não foram:");
				   report.println();
				   for (String s : this.shouldButWereNotList) {
					   report.println(s);
					   report.println();
				   }
			   }
			   
			   if ( wereButWrongLineList.size() == 0 ) {
				   report.println("Um ou mais arquivos de teste tinham erros, mas estes foram sinalizados nos números de linhas corretos");
			   }
			   else {
				   compilerOk = false;
				   report.println("######################################################");
				   report.println("Erros que foram sinalizados na linha errada:");
				   report.println();
				   for (String s : this.wereButWrongLineList) {
					   report.println(s);
					   report.println();
				   }

			   }
			   
		   }
		   if ( numSourceFiles -  numSourceFilesWithErros != 0  ) {
			   if ( wereButShouldNotList.size() == 0 ) {
				   report.println("O compilador não sinalizou nenhum erro que não deveria ter sinalizado");
			   }
			   else {
				   compilerOk = false;
				   report.println("######################################################");
				   report.println("Erros que foram sinalizados mas não deveriam ter sido:");
				   report.println();
				   for (String s : this.wereButShouldNotList) {
					   report.println(s);
					   report.println();
				   }
			   }
		   }
		   
		   if ( correctList.size() > 0 ) {
			   report.println("######################################################");
			   report.print("Em todos os testes abaixo, o compilador sinalizou o erro na linha correta (quando o teste tinha erros) ");
			   report.print("ou não sinalizou o erro (quando o teste NÃO tinha erros). Mas é necessário conferir se as ");
			   report.print("mensagens emitidas pelo compilador são compatíveis com as mensagens de erro sugeridas pelas chamadas aos ");
			   report.print("metaobjetos dos testes. ");
			   report.println();
			   report.println();
			   report.println("A lista abaixo contém o nome do arquivo de teste, a mensagem que ele sinalizou e a mensagem sugerida pelo arquivo de teste");
			   report.println();
			   for (String s : this.correctList ) {
				   report.println(s);
				   report.println();
			   }
		   }
		   if ( compilerOk ) {
			   if ( numSourceFiles == 1 ) 
				   report.println("Para o caso de teste que você utilizou, o compilador está correto");
			   else
				   report.println("Para os casos de teste que você utilizou, o compilador está correto");

		   }
		   
	}
    
	/**
	   @param args
	   @param stream
	   @param numChRead
	   @param outError
	   @param printWriter
	 * @throws IOException 
	 */
	private void compileProgram(File file, String filename, PrintWriter outError)  {
		Program program;
        FileReader stream;
        int numChRead;

          try {
            stream = new FileReader(file);
           } catch ( FileNotFoundException e ) {
               String msg = "Something wrong: file does not exist anymore";
               outError.println(msg);
               return ;
           }
               // one more character for '\0' at the end that will be added by the
               // compiler
           char []input = new char[ (int ) file.length() + 1 ];

           try {
             numChRead = stream.read( input, 0, (int ) file.length() );
             if ( numChRead != file.length() ) {
                 outError.println("Read error");
                 stream.close();
                 return ;
             }
             stream.close();
           } catch ( IOException e ) {
               String msg = "Error reading file " + filename;
               outError.println(msg);
               try { stream.close(); } catch (IOException e1) { }
               return ;
           }


           Compiler compiler = new Compiler();


           program = null;
             // the generated code goes to a file and so are the errors
           program  = compiler.compile(input, outError );
           callMetaobjects(filename, program, outError);

           
           if ( ! program.hasCompilationErrors() ) {
        	   
               String outputFileName;
        	   
               int lastIndex;
               if ( (lastIndex = filename.lastIndexOf('.')) == -1 )
                  lastIndex = filename.length();
               outputFileName = filename.substring(0, lastIndex);
               if ( (lastIndex = filename.lastIndexOf('\\')) != -1 )
            	   outputFileName = filename.substring(lastIndex + 1);
        	   
        	   
        	   
               FileOutputStream  outputStream;
               try {
                  outputStream = new FileOutputStream(outputFileName + ".kra2");
               } catch ( IOException e ) {
                   String msg = "File " + outputFileName + " was not found";
                   outError.println(msg);
                   return ;
               }
               PrintWriter printWriter = new PrintWriter(outputStream);
        	   
        	   
              PW pw = new PW();
              pw.set(printWriter);
              program.genKra( pw );
              if ( printWriter.checkError() ) {
                 outError.println("There was an error in the output");
              }
              printWriter.close();
           }
	}

	/**
	 * number of tests with erros. That is, the number of tests in which there is a call to metaobject {@literal @}ce.
	 */
	private int	numSourceFilesWithErros;

    
    
	public void callMetaobjects(String filename, Program program, PrintWriter outError) {
		
		boolean foundCE = false;
		boolean foundNCE = false;
		for ( MetaobjectCall moCall : program.getMetaobjectCallList() ) {
			if ( moCall.getName().equals("ce") ) {
				this.numSourceFilesWithErros++;
				
				String message = (String ) moCall.getParamList().get(2);
				int lineNumber = (Integer ) moCall.getParamList().get(0);
				if ( ! program.hasCompilationErrors() ) {
					// there was no compilation error. There should be no call @ce(...)
					// the source code, through calls to "@ce(...)", informs that 
					// there are errors
					String whatToCorrect = "";
					if ( moCall.getParamList().size() >= 4 ) {
						whatToCorrect = (String ) moCall.getParamList().get(3);
						whatToCorrect = " (" + whatToCorrect + ")";
					}
					this.shouldButWereNotList.add(filename + ", " + lineNumber + ", " + message + 
							whatToCorrect 
							);
					if ( foundCE ) 
						outError.println("More than one 'ce' metaobject calls in the same source file '" + filename + "'");
					foundCE = true;
				}
				else {
					// there was a compilation error. Check it.
					int lineOfError = program.getCompilationErrorList().get(0).getLineNumber();
					String ceMessage = (String ) moCall.getParamList().get(2);
					String compilerMessage = program.getCompilationErrorList().get(0).getMessage();
					if ( lineNumber != lineOfError ) {
						
						String whatToCorrect = "";
						if ( moCall.getParamList().size() >= 4 ) {
							whatToCorrect = (String ) moCall.getParamList().get(3);
							whatToCorrect = "(" + whatToCorrect + ")";
						}
						
						
						this.wereButWrongLineList.add(filename + "\n" + 
							     "    correto:    " + lineNumber + ", " + ceMessage + " " + whatToCorrect + "\n" +
							     "    sinalizado: " + lineOfError + ", " + compilerMessage);
					}
					else {
						// the compiler is correct. Add to correctList the message
						// that the compiler signalled and the message of the test, given in @ce
						correctList.add(filename + "\r\n" + 
						    "The compiler message was: \"" + compilerMessage + "\"\r\n" + 
						    "The 'ce' message is:      \"" + ceMessage + "\"\r\n" );
					}
				}
			}
			else if ( moCall.getName().equals("nce") ) {
				if ( foundNCE ) 
					outError.println("More than one 'nce' metaobject calls in the same source file '" + filename + "'");
				foundNCE = true;
				if ( program.hasCompilationErrors() ) {
					int lineOfError = program.getCompilationErrorList().get(0).getLineNumber();
					String message = program.getCompilationErrorList().get(0).getMessage();
					this.wereButShouldNotList.add(filename + ", " + lineOfError + ", " + message);
				}
			}
		}
		if ( foundCE && foundNCE )
			outError.println("Calls to metaobjects 'ce' and 'nce' in the same source code '" + filename + "'");
		
	}
	
	ArrayList<String> shouldButWereNotList, wereButShouldNotList, wereButWrongLineList, correctList;
    
}
