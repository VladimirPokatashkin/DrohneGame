package lang.syntaxtree.expression;

import lang.visitor.ASTVisitor;

public record TypeComparisonNode(
		int line,
		int column,
		Expression left,
		Expression right
) implements Expression {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}