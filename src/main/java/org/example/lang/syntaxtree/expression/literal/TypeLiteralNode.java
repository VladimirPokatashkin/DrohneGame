package org.example.lang.syntaxtree.expression.literal;

import org.example.lang.enums.DataType;
import org.example.lang.visitor.ASTVisitor;

public record TypeLiteralNode(
		int line,
		int column,
		DataType value
) implements LiteralNode {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}