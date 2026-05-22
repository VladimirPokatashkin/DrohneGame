package lang.syntaxtree.both;

import lang.syntaxtree.expression.Expression;
import lang.syntaxtree.statement.Statement;
import lang.visitor.ASTVisitor;

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