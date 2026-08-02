package org.example.lang.syntaxtree.statement;

import org.example.lang.enums.DataType;
import org.example.lang.syntaxtree.expression.Expression;
import org.example.lang.visitor.ASTVisitor;


public record VarDeclNode (
		int line,
		int column,
		DataType type,
		String name,
		Expression expression
) implements Statement {

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}