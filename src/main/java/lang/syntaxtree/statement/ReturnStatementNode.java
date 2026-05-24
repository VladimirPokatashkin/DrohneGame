package lang.syntaxtree.statement;

import lang.syntaxtree.expression.Expression;
import lang.visitor.ASTVisitor;

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