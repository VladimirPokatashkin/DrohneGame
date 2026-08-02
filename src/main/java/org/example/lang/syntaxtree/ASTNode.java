package org.example.lang.syntaxtree;

import org.example.lang.visitor.ASTVisitor;

public interface ASTNode {
	int line();
	int column();

	<T>
	T accept(ASTVisitor<T> visitor);
}