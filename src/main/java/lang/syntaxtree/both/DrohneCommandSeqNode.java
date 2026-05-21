package lang.syntaxtree.both;

import lang.visitor.ASTVisitor;

import java.util.List;

public record DrohneCommandSeqNode(
		int line,
		int column,
		List<DrohneSingleCommandNode> commands
) implements DrohneCommandNode {
	@Override
	public <T> T accept(ASTVisitor<T> visitor) {
		return visitor.visit(this);
	}
}