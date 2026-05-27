package lang.syntaxtree.statement;

import lang.visitor.ASTVisitor;

public record BreakNode(
		int line,
		int column
) implements Statement {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
