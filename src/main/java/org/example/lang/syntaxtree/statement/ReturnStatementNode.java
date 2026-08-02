package org.example.lang.syntaxtree.statement;

import org.example.lang.syntaxtree.expression.Expression;
import org.example.lang.visitor.ASTVisitor;

public record ReturnStatementNode(
		int line,
		int column,
		Expression expression
) implements Statement {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}