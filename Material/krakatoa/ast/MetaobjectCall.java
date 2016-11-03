/**
  
 */
package ast;

import java.util.ArrayList;

/** This class represents a metaobject call as <code>{@literal @}ce(...)</code> in <br>
 * <code>
 * @ce(5, "'class' expected") <br>
 * clas Program <br>
 *     public void run() { } <br>
 * end <br>
 * </code>
 * 
   @author Jos�
   
 */
public class MetaobjectCall {

	public MetaobjectCall(String name, ArrayList<Object> paramList) {
		this.name = name;
		this.paramList = paramList;
	}
	
	public ArrayList<Object> getParamList() {
		return paramList;
	}
	public String getName() {
		return name;
	}
	
	public void genKra(PW pw) {
		pw.printIdent("@ " + this.getName());
		if(paramList != null) {
			int size = paramList.size();
			pw.add();
			pw.print("(");
			for (Object o: paramList) {
				if (size == paramList.size()) {
					pw.print("\"" + o + "\"");
				} else {
					pw.printIdent("\"" + o + "\"");
				}
				if ( --size > 0 ) {
	    			pw.println(",");
	    		}
			}
			pw.sub();
			pw.printlnIdent(")");
		}
	}

	private String name;
	private ArrayList<Object> paramList;

}
