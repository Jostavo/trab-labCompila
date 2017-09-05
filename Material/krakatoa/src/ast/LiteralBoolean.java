// Enrique Sampaio dos Santos
// Gustavo Rodrigues

package ast;

public class LiteralBoolean extends Expr {

	public LiteralBoolean(boolean value) {
		this.value = value;
	}

	@Override
	public void genC(PW pw, boolean putParenthesis) {
		if (putParenthesis) {
			pw.print("(");
		}
		pw.print(value ? "true" : "false");
		if (putParenthesis) {
			pw.print(")");
		}
	}
	
	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		if (putParenthesis) {
			pw.print("(");
		}
		pw.print(value ? "true" : "false");
		if (putParenthesis) {
			pw.print(")");
		}
	}

	@Override
	public Type getType() {
		return Type.booleanType;
	}

	public static LiteralBoolean True = new LiteralBoolean(true);
	public static LiteralBoolean False = new LiteralBoolean(false);

	private boolean value;
}
