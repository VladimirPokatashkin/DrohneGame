package org.example.lang.syntaxtree.both;

import org.example.lang.enums.DrohneCommandType;
import org.example.lang.syntaxtree.expression.Expression;
import org.example.lang.syntaxtree.statement.Statement;
import org.example.lang.visitor.ASTVisitor;

import java.util.List;

public record DrohneCommandSeqNode(
		int line,
		int column,
		List<DrohneCommandType> commands
) implements Expression, Statement {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		commands.forEach(command -> sb.append(command.toString()));
		return sb.toString();
	}
}