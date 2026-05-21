package lang.syntaxtree.expression.literal;

import lang.visitor.ASTVisitor;

public record IntLiteral(
		int line,
		int column,
		int value
) implements LiteralNode {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}