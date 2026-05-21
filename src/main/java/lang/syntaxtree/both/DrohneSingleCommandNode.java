package lang.syntaxtree.both;

import lang.enums.DrohneCommandType;
import lang.syntaxtree.statement.Statement;
import lang.visitor.ASTVisitor;

public record DrohneSingleCommandNode(
		int line,
		int column,
		DrohneCommandType type
) implements Statement {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}