package lang.syntaxtree.expression;

import lang.enums.UnOperator;
import lang.visitor.ASTVisitor;

public record UnExpressionNode(
		int line,
		int column,
		Expression operand,
		UnOperator operator
) implements Expression {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
