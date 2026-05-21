package lang.syntaxtree.expression;

import lang.visitor.ASTVisitor;
import lang.enums.BinOperator;

public record BinExprNode (
		int line,
		int column,
		Expression left,
		Expression right,
		BinOperator binOperator
) implements Expression {

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}