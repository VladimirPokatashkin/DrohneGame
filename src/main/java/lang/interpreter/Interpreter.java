package lang.interpreter;

import lang.interpreter.symbols.Variable;
import lang.syntaxtree.both.DrohneCommandSeqNode;
import lang.syntaxtree.both.FuncCallNode;
import lang.syntaxtree.expression.*;
import lang.syntaxtree.expression.literal.BooleanLiteralNode;
import lang.syntaxtree.expression.literal.IntLiteralNode;
import lang.syntaxtree.expression.literal.TypeLiteralNode;
import lang.syntaxtree.statement.*;
import lang.visitor.ASTVisitor;

public class Interpreter implements ASTVisitor<Object> {
	private RuntimeContext context;

	@Override
	public Object visit(DrohneCommandSeqNode node) {
		return null;
	}

	@Override
	public Object visit(BooleanLiteralNode node) {
		return null;
	}

	@Override
	public Object visit(IntLiteralNode node) {
		return null;
	}

	@Override
	public Object visit(TypeLiteralNode node) {
		return null;
	}

	@Override
	public Object visit(ArrayAccessNode node) {
		return null;
	}

	@Override
	public Object visit(BinExprNode node) {
		return null;
	}

	@Override
	public Object visit(CellPropertyAccess node) {
		return null;
	}

	@Override
	public Object visit(FuncCallNode node) {

		return null;
	}

	@Override
	public Object visit(TypeComparisonNode node) {
		return null;
	}

	@Override
	public Object visit(UnExprNode node) {
		return null;
	}

	@Override
	public Object visit(VarAccessNode node) {
		return context.getVariable(node.name());
	}

	@Override
	public Object visit(ArrayDeclNode node) {
		return null;
	}

	@Override
	public Object visit(FuncDeclNode node) {
		context.addFunction(node);
		return null;
	}

	@Override
	public Object visit(IfNode node) {
		return null;
	}

	@Override
	public Object visit(LoopNode node) {
		return null;
	}

	@Override
	public Object visit(ProgramNode node) {
		node.functions().forEach(func -> func.accept(this));
		node.statements().forEach(statement -> statement.accept(this));
		return null;
	}

	@Override
	public Object visit(ReturnStatementNode node) {
		return node.expression().accept(this);
	}

	@Override
	public Object visit(VarAssignationNode node) {
		context.changeValueOf(node.name(), node.value());
		return null;
	}

	@Override
	public Object visit(VarDeclNode node) {
		context.addVariable(new Variable(node.name(), node.type(), node.expression().accept(this)));
		return null;
	}
}