// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

import java.util.Iterator;

/*
 * Krakatoa Class
 */
public class KraClass extends Type {

    public KraClass(String name) {
        super(name);
        publicMethodList = new MethodList();
        privateMethodList = new MethodList();
        instanceVariableList = new InstanceVariableList();
    }

    @Override
    public String getCname() {
        return "_class_" + this.getName();
    }

    public void setSuperClass(KraClass k) {
        this.superclass = k;
    }

    public KraClass getSuperClass() {
        return this.superclass;
    }

    public boolean hasSuperClass(String className) {
        if (this.superclass == null) {
            return false;
        } else if (this.superclass.getName().equals(className)) {
            return true;
        } else {
            return this.superclass.hasSuperClass(className);
        }
    }

    public Method getSuperMethod(String methodName) {
        if (this.superclass == null) {
            return null;
        } else {
            Method m = this.superclass.getPublicMethod(methodName);
            if (m != null) {
                return m;
            } else {
                return this.superclass.getSuperMethod(methodName);
            }
        }
    }
    
    public KraClass getSuperOwner(String methodName) {
    	if (this.superclass == null) {
    		return null;
    	} else {
    		Method m = this.superclass.getPublicMethod(methodName);
    		
    		if (m != null) {
    			return this.superclass;
    		} else {
    			return this.superclass.getSuperOwner(methodName);
    		}
    	}
    }

    public InstanceVariable getInstanceVariable(String instanceVariableName) {
        Iterator<InstanceVariable> instanceVariableItr = this.instanceVariableList.elements();

        while (instanceVariableItr.hasNext()) {
            InstanceVariable instanceVariable = instanceVariableItr.next();
            if (instanceVariable.getName().equals(instanceVariableName)) {
                return instanceVariable;
            }
        }

        return null;
    }

    public InstanceVariable getSuperInstanceVariable(String instanceVaraibleName) {
        if (this.superclass == null) {
            return null;
        } else {
            InstanceVariable iv = this.superclass.getInstanceVariable(instanceVaraibleName);

            if (iv != null) {
                return iv;
            } else {
                return this.superclass.getSuperInstanceVariable(instanceVaraibleName);
            }
        }
    }

    public void setInstanceVariable(InstanceVariable i) {
        this.instanceVariableList.addElement(i);
    }

    public Method getPublicMethod(String methodName) {
        Iterator<Method> methodItr = this.publicMethodList.elements();

        while (methodItr.hasNext()) {
            Method method = methodItr.next();
            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        return null;
    }

    public void setPublicMethod(Method m) {
        this.publicMethodList.addElement(m);
    }

    public Method getPrivateMethod(String methodName) {
        Iterator<Method> methodItr = this.privateMethodList.elements();

        while (methodItr.hasNext()) {
            Method method = methodItr.next();
            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        return null;
    }

    public void setPrivateMethod(Method m) {
        this.privateMethodList.addElement(m);
    }

    public Method getMethod(String methodName) {
        Iterator<Method> methodItr = this.publicMethodList.elements();

        while (methodItr.hasNext()) {
            Method method = methodItr.next();
            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        methodItr = this.privateMethodList.elements();

        while (methodItr.hasNext()) {
            Method method = methodItr.next();
            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        if (this.superclass == null) {
            return null;
        } else {
            Method m = this.superclass.getPublicMethod(methodName);
            if (m != null) {
                return m;
            } else {
                return this.superclass.getSuperMethod(methodName);
            }
        }
    }
    
    public void genKra(PW pw) {
    	pw.printlnIdent("class " + this.getName() + " {");
    	pw.add();
    	this.instanceVariableList.genKra(pw);
    	this.privateMethodList.genKra(pw);
    	this.publicMethodList.genKra(pw);
    	pw.sub();
    	pw.printlnIdent("}");
    }

    public void genC(PW pw) {
        pw.println("typedef struct _St_" + this.getName() + " {");
        pw.add();
        pw.printlnIdent("Func *vt;");

        this.printInstanceVariables(pw);

        pw.sub();
        pw.println("} " + this.getCname() + ";");
        pw.println();
        pw.println(this.getCname() + " *new_" + this.getName() + "(void);");
        pw.println();
        this.privateMethodList.genC(pw, this.getName());
        this.publicMethodList.genC(pw, this.getName());
        pw.printlnIdent("Func VTclass_" + this.getName() + "[] = {");
        pw.add();
        this.printMethodsPrototype(pw, null);
        pw.sub();
        pw.printlnIdent("};");
        pw.println();
        pw.printlnIdent(this.getCname() + " *new_" + this.getName() + "()");
        pw.printlnIdent("{");
        pw.add();
        pw.printlnIdent(this.getCname() + " *t;");
        pw.println();
        pw.printlnIdent("if ( (t = malloc(sizeof(" + this.getCname() + "))) != NULL )");
        pw.add();
        pw.printlnIdent("t->vt = VTclass_" + this.getName() + ";");
        pw.sub();
        pw.println();
        pw.printlnIdent("return t;");
        pw.sub();
        pw.printlnIdent("}");
    }

    public void printMethodsPrototype(PW pw, KraClass child) {
        if (this.superclass != null) {
            this.superclass.printMethodsPrototype(pw, this);
        }

        Iterator<Method> mItr = this.publicMethodList.elements();

        while (mItr.hasNext()) {
            Method m = mItr.next();
            if (child == null || child.getPublicMethod(m.getName()) == null) {
                pw.printIdent("( void (*)() ) _" + this.getName() + "_" + m.getName());
                if (mItr.hasNext() || child != null) {
                    pw.println(",");
                } else {
                    pw.println();
                }
            }
        }
    }

    public int getMethodPosition(String methodName) {
        Iterator<Method> mItr = this.publicMethodList.elements();
        int counter = 0;

        while (mItr.hasNext()) {
            Method m = mItr.next();

            if (m.getName().equals(methodName)) {
                if (this.superclass != null) {
                    counter += this.getMethodsBefore();
                }

                return counter;
            }

            counter++;
        }

        return this.superclass.getMethodPosition(methodName);
    }
    
    public boolean isPrivate(String methodName) {
    	Iterator<Method> mItr = this.privateMethodList.elements();
    	
    	while (mItr.hasNext()) {
    		Method m = mItr.next();
    		
    		if (m.getName().equals(methodName)) {
    			return true;
    		}
    	}
    	
    	return false;
    }

    public int getMethodsBefore() {
        int counter = 0;

        if (this.superclass != null) {
            counter += this.superclass.getMethodsBefore();
            
            MethodList superMethods = this.superclass.getPublicMethodList();
            
            Iterator<Method> smItr = superMethods.elements();
            
            while (smItr.hasNext()) {
            	if (this.getPublicMethod(smItr.next().getName()) == null) {
            		counter++;
            	}
            }
        }

        return counter;
    }
    
    public MethodList getPublicMethodList() {
    	return this.publicMethodList;
    }

    public void printInstanceVariables(PW pw) {
        if (this.superclass != null) {
            this.superclass.printInstanceVariables(pw);
        }

        this.instanceVariableList.genC(pw, this.getName());
    }

    private KraClass superclass;
    private InstanceVariableList instanceVariableList;
    private MethodList publicMethodList, privateMethodList;
    // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
    // entre outros m�todos
}
