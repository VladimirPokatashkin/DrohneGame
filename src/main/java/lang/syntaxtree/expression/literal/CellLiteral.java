package lang.syntaxtree.expression.literal;

import lang.syntaxtree.expression.Expression;
import lang.visitor.ASTVisitor;

public record CellLiteral(
		int line,
		int column,
		IntLiteral x,
		IntLiteral y,
		IntLiteral z,
		BooleanLiteral isObstacle
) implements LiteralNode {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}