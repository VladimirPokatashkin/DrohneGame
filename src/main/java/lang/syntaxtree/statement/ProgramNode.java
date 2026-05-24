package lang.syntaxtree.statement;

import lang.visitor.ASTVisitor;

import java.util.List;

public record ProgramNode (
		List<FuncDeclNode> functions,
		List<Statement> statements
)implements Statement {
	@Override
	public int line() {
		return 0;
	}

	@Override
	public int column() {
		return 0;
	}

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}