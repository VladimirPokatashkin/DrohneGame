package lang.syntaxtree.statement;

import lang.syntaxtree.expression.Expression;
import lang.visitor.ASTVisitor;

import java.util.List;

public record LoopNode(
		int line,
		int column,
		String iterator,
		Expression begin,
		Expression end,
		List<Statement> body
) implements Statement {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}