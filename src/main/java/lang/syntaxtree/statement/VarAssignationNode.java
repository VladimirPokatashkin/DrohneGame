package lang.syntaxtree.statement;

import lang.syntaxtree.expression.Expression;
import lang.visitor.ASTVisitor;

public record VarAssignationNode(
		int line,
		int column,
		String name,
		Expression value
) implements Statement {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return null;
	}
}