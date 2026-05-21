package lang.syntaxtree.statement;

import lang.visitor.ASTVisitor;

import java.util.List;

public record ProgramNode (
		int line,
		int column,
		List<FuncDeclNode> functions,
		List<Statement> globalStatements
)implements Statement {

	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}