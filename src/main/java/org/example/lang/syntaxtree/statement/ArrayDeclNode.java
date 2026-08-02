package org.example.lang.syntaxtree.statement;

import org.example.lang.syntaxtree.expression.Expression;
import org.example.lang.visitor.ASTVisitor;

import java.util.List;

public record ArrayDeclNode(
		int line,
		int column,
		String name,
		List<Expression> sizes
) implements Statement {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}