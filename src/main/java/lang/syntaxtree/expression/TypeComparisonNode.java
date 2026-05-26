package lang.syntaxtree.expression;

import lang.visitor.ASTVisitor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
public class TypeComparisonNode implements Expression {
	private int line;
	private int column;
	private Expression left;
	private Expression right;
	@Setter
	private boolean result;

	public TypeComparisonNode(int line, int column, Expression left, Expression right) {
		this(line, column, left, right, false);
	}

	@Override
	public int line() {
		return line;
	}

	@Override
	public int column() {
		return column;
	}

	public Expression left() {
		return left;
	}

	public Expression right() {
		return right;
	}

	public boolean result() {
		return result;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}