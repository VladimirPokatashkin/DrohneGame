package lang.visitor;

import lang.syntaxtree.both.DrohneCommandSeqNode;
import lang.syntaxtree.both.FuncCallNode;
import lang.syntaxtree.expression.*;
import lang.syntaxtree.expression.literal.*;
import lang.syntaxtree.statement.*;

public interface ASTVisitor<T> {
	T visit(DrohneCommandSeqNode node);
	T visit(BooleanLiteralNode node);
	T visit(IntLiteralNode node);
	T visit(TypeLiteralNode node);
	T visit(ArrayAccessNode node);
	T visit(BinExprNode node);
	T visit(CellPropertyAccess node);
	T visit(FuncCallNode node);
	T visit(TypeComparisonNode node);
	T visit(UnExprNode node);
	T visit(VarAccessNode node);
	T visit(ArrayDeclNode node);
	T visit(BreakNode node);
	T visit(FuncDeclNode node);
	T visit(IfNode node);
	T visit(LoopNode node);
	T visit(ProgramNode node);
	T visit(ReturnStatementNode node);
	T visit(VarAssignationNode node);
	T visit(VarDeclNode node);
}