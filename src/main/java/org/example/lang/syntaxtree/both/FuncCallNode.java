package org.example.lang.syntaxtree.both;

import org.example.lang.syntaxtree.expression.Expression;
import org.example.lang.syntaxtree.statement.Statement;
import org.example.lang.visitor.ASTVisitor;

import java.util.List;

public record FuncCallNode(
		int line,
		int column,
		String name,
		List<Expression> args
) implements Expression, Statement {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}