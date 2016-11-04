package ast;

import java.util.Iterator;

public class KraClass extends Type {
	
   public KraClass( String name ) {
      super(name);
	  superclass = null;
      instanceVariableList = new InstanceVariableList();
      publicMethodList = new MethodList();
      privateMethodList = new MethodList();
   }
   
   public String getCname() {
      return getName();
   }
   
   private String name;
   private KraClass superclass;
   private InstanceVariableList instanceVariableList;
   private MethodList publicMethodList;
   private MethodList privateMethodList;

   public void setSuper(KraClass superClasse){
      this.superclass = superClasse;
   }

   public KraClass getSuper(){
      return superclass;
   }
   
   public InstanceVariable getVariable(String m){
      return instanceVariableList.getVariable(m);
   }

   public void setPublicMethodList(MethodList publicm){
      this.publicMethodList = publicm;
   }

   public void setPrivateMethodList(MethodList privatem){
      this.privateMethodList = privatem;
   }

   public void setVariableList(InstanceVariableList ivl){
      this.instanceVariableList = ivl;
   }
   
   public boolean hasPublicMethod() {
	   return this.publicMethodList.getSize() != 0;
   }

   public boolean hasPublicMethod(String m){
      return this.publicMethodList.contains(m);
   }

   public boolean hasPrivateMethod(String m){
      return this.privateMethodList.contains(m);
   }

   public boolean hasInstanceVariable(String m){
      return this.instanceVariableList.contains(m);
   }

   public Method getMethod(String m){
	  Iterator<Method> methodIterator = publicMethodList.elements();
	  while (methodIterator.hasNext()) {
		  Method aux = methodIterator.next();
		  if (aux.getName().equals(m)) {
			  return aux;
		  }
	  }
	  
	  return null;
   }
   
   public Method getMethodS(String m){
       if(this.superclass != null){
           Method retorno = this.superclass.getMethod(m);
           
           if (retorno == null)
               return this.superclass.getMethodS(m);
           else
               return retorno;
       }      
       
       return null;
   }
   
   public void genKra(PW pw) {
	   pw.print("class " + this.getCname());
	   if (this.superclass != null) {
		   pw.print(" extends " + this.superclass.getName());
	   }
	   pw.print(" {\n\n");
	   pw.add();
	   instanceVariableList.genKra(pw);
	   privateMethodList.genKra(pw);
	   publicMethodList.genKra(pw);
	   pw.sub();
	   pw.printlnIdent("}");
   }
}
