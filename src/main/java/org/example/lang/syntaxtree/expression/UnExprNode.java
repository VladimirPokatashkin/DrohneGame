package org.example.lang.syntaxtree.expression;

import org.example.lang.enums.UnOperator;
import org.example.lang.visitor.ASTVisitor;

public record UnExprNode(
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
