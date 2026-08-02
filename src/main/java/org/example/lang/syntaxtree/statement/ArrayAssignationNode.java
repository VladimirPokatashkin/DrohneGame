package org.example.lang.syntaxtree.statement;

import org.example.lang.syntaxtree.expression.Expression;
import org.example.lang.visitor.ASTVisitor;

import java.util.List;

public record ArrayAssignationNode(
		int line,
		int column,
		String name,
		List<Expression> indices,
		Expression value
) implements Statement {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}