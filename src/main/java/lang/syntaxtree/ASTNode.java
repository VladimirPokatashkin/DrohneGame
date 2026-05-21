package lang.syntaxtree;

import lang.visitor.ASTVisitor;

public interface ASTNode {
	int line();
	int column();

	<T>
	T accept(ASTVisitor<T> visitor);
}