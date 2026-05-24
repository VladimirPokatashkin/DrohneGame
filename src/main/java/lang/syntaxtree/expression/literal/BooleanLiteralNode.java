package lang.syntaxtree.expression.literal;

import lang.visitor.ASTVisitor;

public record BooleanLiteralNode(
		int line,
		int column,
		boolean value
) implements LiteralNode {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
