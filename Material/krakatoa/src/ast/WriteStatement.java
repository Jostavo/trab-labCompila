// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author enrique
 */
public class WriteStatement extends Statement {
	private ExprList el;
	private boolean isLn;

	public WriteStatement(ExprList el) {
		this.el = el;
		this.isLn = false;
	}

	public WriteStatement(ExprList el, boolean isLn) {
		this.el = el;
		this.isLn = isLn;
	}

	@Override
	public void genC(PW pw) {
		Iterator<Expr> eItr = el.elements();

		while (eItr.hasNext()) {
			Expr e = eItr.next();

			if (e.getType() == Type.stringType) {
				pw.printIdent("puts(");
				e.genC(pw, false);
				pw.println(");");
			} else {
				pw.printIdent("printf(\"%d \", ");
				e.genC(pw, false);
				pw.println(");");
			}
		}

		if (this.isLn) {
			pw.printlnIdent("printf(\"\\n\");");
		}
	}

	@Override
	public void genKra(PW pw) {
		if (this.isLn) {
			pw.printIdent("writeln(");
		} else {
			pw.printIdent("write(");
		}

		ArrayList<Expr> el = this.el.getList();
		int size = el.size();

		for (Expr e : el) {
			e.genKra(pw, false);
			if (--size > 0) {
				pw.print(",");
			}
		}

		pw.println(");");
	}

}
