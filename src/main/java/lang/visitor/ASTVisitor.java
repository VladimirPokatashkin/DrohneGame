package lang.visitor;

import lang.syntaxtree.both.DrohneCommandSeqNode;
import lang.syntaxtree.both.DrohneSingleCommandNode;
import lang.syntaxtree.expression.*;
import lang.syntaxtree.statement.*;

public interface ASTVisitor<T> {
	T visit(DrohneCommandSeqNode node);
	T visit(DrohneSingleCommandNode node);
	T visit(ArrayAccessNode node);
	T visit(BinExprNode node);
	T visit(CellPropertyAccess node);
	T visit(FuncCallNode node);
	T visit(TypeComparasionNode node);
	T visit(UnExpressionNode node);
	T visit(VarAccessNode node);
	T visit(ArrayDeclNode node);
	T visit(FuncDeclNode node);
	T visit(IfNode node);
	T visit(LoopNode node);
	T visit(ProgramNode node);
	T visit(VarAssignationNode node);
	T visit(VarDeclNode node);
}