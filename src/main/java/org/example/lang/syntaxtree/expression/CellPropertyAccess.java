package org.example.lang.syntaxtree.expression;

import org.example.lang.enums.CellProperty;
import org.example.lang.visitor.ASTVisitor;

public record CellPropertyAccess(
		int line,
		int column,
		Expression cell,
		CellProperty property
) implements Expression {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}