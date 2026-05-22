package lang.syntaxtree.both;

import lang.enums.DrohneCommandType;
import lang.syntaxtree.expression.Expression;
import lang.syntaxtree.statement.Statement;
import lang.visitor.ASTVisitor;

import java.util.List;

public record DrohneCommandSeqNode(
		int line,
		int column,
		List<DrohneCommandType> commands
) implements Expression, Statement {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}