package lang.syntaxtree.statement;

import lang.enums.DataType;
import lang.syntaxtree.expression.Expression;
import lang.visitor.ASTVisitor;


public record VarDeclNode (
		int line,
		int column,
		DataType type,
		String name,
		Expression expression
) implements Statement {

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}