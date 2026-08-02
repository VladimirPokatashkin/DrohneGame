package org.example.lang.syntaxtree.expression.literal;

import org.example.lang.visitor.ASTVisitor;

public record IntLiteralNode(
		int line,
		int column,
		int value
) implements LiteralNode {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}