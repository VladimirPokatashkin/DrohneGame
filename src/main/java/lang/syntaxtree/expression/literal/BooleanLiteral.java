package lang.syntaxtree.expression.literal;

import lang.visitor.ASTVisitor;

public record BooleanLiteral(
		int line,
		int column,
		boolean value
) implements LiteralNode {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
