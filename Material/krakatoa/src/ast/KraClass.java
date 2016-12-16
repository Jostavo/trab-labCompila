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
        return getName();
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
    
    public void genC(PW pw) {
            pw.println("typedef struct _St_" + this.getCname()+ " {");
            pw.add();
            this.instanceVariableList.genC(pw);
            pw.printlnIdent("Func *vt;");
            pw.sub();
            pw.println("} _class_"+ this.getCname() +";");
            pw.println();
            pw.println("_class_"+ this.getCname() + " *new_"+ this.getCname() +"(void);");
            pw.println();
            //publicMethodList.genC(pw, this.getName());
            //privateMethodList.genC(pw, this.getName());
    }

    private String name;
    private KraClass superclass;
    private InstanceVariableList instanceVariableList;
    private MethodList publicMethodList, privateMethodList;
    // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
    // entre outros m�todos
}
