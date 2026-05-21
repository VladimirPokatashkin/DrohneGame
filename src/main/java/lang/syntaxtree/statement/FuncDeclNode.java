package lang.syntaxtree.statement;

import lang.enums.DataType;
import lang.visitor.ASTVisitor;
import other.Pair;

import java.util.List;

public record FuncDeclNode (
		int line,
		int column,
		String name,
		DataType returnType,
		List<Pair<DataType, String>> args,
		List<Statement> body
) implements Statement {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}