package lang.syntaxtree.expression;

import lang.visitor.ASTVisitor;

import java.util.List;

public record ArrayAccessNode(
		int line,
		int column,
		String name,
		List<Expression> indices
) implements Expression {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}