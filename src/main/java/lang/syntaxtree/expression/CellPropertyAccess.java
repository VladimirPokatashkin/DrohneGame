package lang.syntaxtree.expression;

import lang.enums.CellProperty;
import lang.visitor.ASTVisitor;

public record CellPropertyAccess(
		int line,
		int column,
		Expression cell,
		CellProperty property
) implements Expression {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}