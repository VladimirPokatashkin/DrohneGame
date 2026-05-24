package lang.syntaxtree.expression.literal;

import lang.enums.DataType;
import lang.visitor.ASTVisitor;

public record TypeLiteralNode(
		int line,
		int column,
		DataType value
) implements LiteralNode {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}