package lang.interpreter;

import lang.enums.DataType;
import lang.interpreter.symbols.Variable;
import lang.structures.Array;
import lang.structures.Cell;
import lang.syntaxtree.both.DrohneCommandSeqNode;
import lang.syntaxtree.both.FuncCallNode;
import lang.syntaxtree.expression.*;
import lang.syntaxtree.expression.literal.BooleanLiteralNode;
import lang.syntaxtree.expression.literal.IntLiteralNode;
import lang.syntaxtree.expression.literal.TypeLiteralNode;
import lang.syntaxtree.statement.*;
import lang.visitor.ASTVisitor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class Interpreter implements ASTVisitor<Object> {
	private RuntimeContext context;

	private Interpreter(RuntimeContext context) {
		this.context = context;
	}

	private Interpreter(Interpreter other) {
		this.context = other.context;
	}

	private boolean castToBoolean(Object val) {
		if (val instanceof Boolean b) return b;
		if (val instanceof Integer i) return i != 0;
		throw new RuntimeException("invalid operand of boolean operation: " + val.toString());
	}

	private int castToInteger(Object val) {
		if (val instanceof Integer i) return i;
		if (val instanceof Boolean b) return b ? 1 : 0;
		throw new RuntimeException("invalid operand of arithmetic operation: " + val.toString());
	}

	@Override
	public Object visit(DrohneCommandSeqNode node) {
		return null;
	}

	@Override
	public Object visit(BooleanLiteralNode node) {
		return node.value();
	}

	@Override
	public Object visit(IntLiteralNode node) {
		return node.value();
	}

	@Override
	public Object visit(TypeLiteralNode node) {
		return node.value();
	}

	@Override
	public Object visit(ArrayAccessNode node) {
		var array = (Array) context.getVariable(node.name()).getValue();
		int index = (int) node.index().accept(this);
		return array.data().get(index);
	}

	@Override
	public Object visit(BinExprNode node) {
		Object left = node.left().accept(this);
		Object right = node.right().accept(this);

		switch (node.binOperator()) {
			case OR -> {
				return castToBoolean(left) || castToBoolean(right);
			}
			case AND -> {
				return castToBoolean(left) && castToBoolean(right);
			}
			case PLUS -> {
				return castToInteger(left) + castToInteger(right);
			}
			case MINUS -> {
				return castToInteger(left) - castToInteger(right);
			}
			case LESS -> {
				return castToInteger(left) < castToInteger(right);
			}
			case GREATER -> {
				return castToInteger(left) > castToInteger(right);
			}
		}

		throw new RuntimeException("unknown operation: " + node.binOperator());
	}

	@Override
	public Object visit(CellPropertyAccess node) {
		Cell cell = (Cell) node.cell().accept(this);
		switch (node.property()) {
			case X -> {
				return cell.x();
			}
			case Y -> {
				return cell.y();
			}
			case Z -> {
				return cell.z();
			}
			case IS_OBSTACLE -> {
				return cell.isObstacle();
			}
		}

		throw new RuntimeException("unknown cell property: " + node.property());
	}

	@Override
	public Object visit(FuncCallNode node) {
		RuntimeContext innerContext = new RuntimeContext(this.context);

		FuncDeclNode prototype = context.getFunction(node.name());
		for (int i = 0; i < node.args().size(); i++) {
			Object val = node.args().get(i).accept(this);
			innerContext.addVariable(new Variable(prototype.args().get(i), val));
		}

		Interpreter innerInterpreter = new Interpreter(innerContext);

		for (var statement : prototype.body()) {
			Object res = statement.accept(innerInterpreter);
			if (res instanceof ReturnStatementNode ret) {
				return ret.expression().accept(innerInterpreter);
			}
		}

		return null;
	}

	@Override
	public Object visit(TypeComparisonNode node) {
		return node.result();
	}

	@Override
	public Object visit(UnExprNode node) {
		Object operand = node.operand().accept(this);

		switch (node.operator()) {
			case NOT -> {
				return !castToBoolean(operand);
			}
			case JIGEN -> {
				return ((Array) operand).data().size();
			}
		}

		throw new RuntimeException("unknown operation: " + node.operator());
	}

	@Override
	public Object visit(VarAccessNode node) {
		return context.getVariable(node.name());
	}

	@Override
	public Object visit(ArrayDeclNode node) {
		context.addVariable(new Variable(node.name(), DataType.HAIRETSU, new Array()));
		return null;
	}

	@Override
	public Object visit(FuncDeclNode node) {
		context.addFunction(node);
		return null;
	}

	@Override
	public Object visit(IfNode node) {
		if (castToBoolean(node.condition().accept(this))) {
			Interpreter inner = new Interpreter(this);

			for (var statement : node.body()) {
				if (statement instanceof ReturnStatementNode ret) {
					return ret;
				}

				Object res = statement.accept(inner);

				if (res instanceof ReturnStatementNode ret) {
					return ret;
				}
			}
		}
		return null;
	}

	@Override
	public Object visit(LoopNode node) {
		int begin = castToInteger(node.begin().accept(this));
		int end = castToInteger(node.end().accept(this));

		if (begin < end) {
			Interpreter inner = new Interpreter(this);
			String iterator = node.iterator();
			inner.context.addVariable(new Variable(iterator, DataType.SEISU, begin));

			while ((int) inner.context.getVariable(iterator).getValue() < end) {
				for (var statement : node.body()) {
					if (statement instanceof ReturnStatementNode ret) {
						return ret;
					}

					Object res = statement.accept(inner);

					if (res instanceof ReturnStatementNode ret) {
						return ret;
					}
				}
			}
		}

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