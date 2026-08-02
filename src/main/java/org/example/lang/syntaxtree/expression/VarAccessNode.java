package org.example.lang.syntaxtree.expression;

import org.example.lang.visitor.ASTVisitor;

public record VarAccessNode(
		int line,
		int column,
		String name
) implements Expression {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}