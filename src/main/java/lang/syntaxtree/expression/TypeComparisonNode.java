package lang.syntaxtree.expression;

import lang.visitor.ASTVisitor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TypeComparisonNode implements Expression {
	private int line;
	private int column;
	private Expression left;
	private Expression right;


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

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}