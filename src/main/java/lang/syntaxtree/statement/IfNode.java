package lang.syntaxtree.statement;

import lang.syntaxtree.expression.Expression;
import lang.visitor.ASTVisitor;

import java.util.List;

public record IfNode  (
		int line,
		int column,
		Expression condition,
		List<Statement> body
) implements Statement {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}