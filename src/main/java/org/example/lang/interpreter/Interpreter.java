package org.example.lang.interpreter;

import org.example.lang.enums.DataType;
import org.example.lang.enums.DrohneCommandType;
import org.example.lang.exceptions.BreakException;
import org.example.lang.exceptions.ReturnException;
import org.example.lang.interpreter.symbols.Variable;
import org.example.lang.syntaxtree.expression.*;
import org.example.lang.syntaxtree.statement.*;
import org.example.structures.Array;
import org.example.structures.Cell;
import org.example.lang.syntaxtree.both.DrohneCommandSeqNode;
import org.example.lang.syntaxtree.both.FuncCallNode;
import org.example.lang.syntaxtree.expression.*;
import org.example.lang.syntaxtree.expression.literal.BooleanLiteralNode;
import org.example.lang.syntaxtree.expression.literal.IntLiteralNode;
import org.example.lang.syntaxtree.expression.literal.TypeLiteralNode;
import org.example.lang.syntaxtree.statement.*;
import org.example.lang.visitor.ASTVisitor;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class Interpreter implements ASTVisitor<Object> {
	private RuntimeContext context;

	private Interpreter(Interpreter other) {
		this.context = other.context;
	}

	private boolean castToBoolean(Object val) {
		if (val instanceof Variable v) return castToBoolean(v);
		if (val instanceof Boolean b) return b;
		if (val instanceof Integer i) return i != 0;
		throw new RuntimeException("invalid operand of boolean operation: " + val.toString());
	}

	private boolean castToBoolean(Variable val) {
		if (val.getType() == DataType.SEISU) return ((int) val.getValue()) != 0;
		if (val.getType() == DataType.RONRI) return (boolean) val.getValue();
		throw new RuntimeException("invalid operand of boolean operation: " + val);
	}

	private int castToInteger(Object val) {
		if (val instanceof Variable v) return castToInteger(v);
		if (val instanceof Integer i) return i;
		if (val instanceof Boolean b) return b ? 1 : 0;
		throw new RuntimeException("invalid operand of arithmetic operation: " + val.toString());
	}

	private int castToInteger(Variable val) {
		if (val.getType() == DataType.SEISU) return (int) val.getValue();
		if (val.getType() == DataType.RONRI) return ((boolean) val.getValue()) ? 1 : 0;
		throw new RuntimeException("invalid operand of arithmetic operation: " + val);
	}

	private DataType typeOf(Object value) {
		if (value instanceof Integer) return DataType.SEISU;
		if (value instanceof Boolean) return DataType.RONRI;
		if (value instanceof Array)   return DataType.HAIRETSU;
		if (value instanceof Cell)	  return DataType.RIPPOTAI;
		throw new RuntimeException("unknown data type.");
	}



	@Override
	public Object visit(DrohneCommandSeqNode node) {
		List<DrohneCommandType> scans = new ArrayList<>();
		DrohneCommandType prev = null;

		int x = context.getDrohneX();
		int y = context.getDrohneY();
		int z = context.getDrohneZ();

		for (var command : node.commands()) {
			if (command == DrohneCommandType.BREAK_SEQ) {
				if (context.distToNearestObstacle(prev, x, y, z) == 1) break;
			}

			switch (command) {
				case SCAN_FORWARD, SCAN_BACK, SCAN_DOWN, SCAN_UP, SCAN_LEFT, SCAN_RIGHT, GET_POS ->
					scans.add(command);
				case MOVE_UP, MOVE_DOWN, MOVE_FORWARD, MOVE_BACK, MOVE_RIGHT, MOVE_LEFT ->
					context.moveDrohne(command);
			}

			prev = command;
		}

		if (scans.isEmpty()) return null;

		List<Object> res = new ArrayList<>();

		x = context.getDrohneX();
		y = context.getDrohneY();
		z = context.getDrohneZ();

		for (var scan : scans) {
			if (scan == DrohneCommandType.GET_POS) {
				res.add(context.getCell(x, y, z));
			} else {
				res.add(context.distToNearestObstacle(scan, x, y, z));
			}
		}

		System.out.println("[LOG]: drohne commands executed: " + node);
		return res.size() > 1 ? res : res.get(0);
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
		List<Integer> indices = new ArrayList<>();
		node.indices().forEach(index -> indices.add(castToInteger(index.accept(this))));
		System.out.println("[LOG]: array access: " + node.name() + ": " + indices);
		return array.get(indices);
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
		return switch (node.property()) {
			case X -> cell.x();
			case Y -> cell.y();
			case Z -> cell.z();
			case IS_OBSTACLE -> cell.isObstacle();
		};
	}

	@Override
	public Object visit(FuncCallNode node) {
		RuntimeContext innerContext = new RuntimeContext(this.context);

		List<Object> args = new ArrayList<>();
		FuncDeclNode prototype = context.getFunction(node.name());
		for (int i = 0; i < node.args().size(); i++) {
			Object val = node.args().get(i).accept(this);
			innerContext.addVariable(new Variable(prototype.args().get(i), val));
			args.add(val);
		}

		var inner = new Interpreter(innerContext);

		System.out.println("[LOG]: function called: " + node.name() + ": " + args);

		try {
			prototype.body().forEach(statement -> statement.accept(inner));
		} catch (ReturnException ret) {
			return ret.getExpression().accept(inner);
		}

		return null;
	}

	@Override
	public Object visit(TypeComparisonNode node) {
		return typeOf(node.left().accept(this)) == typeOf(node.right().accept(this));
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
		System.out.println("[LOG]: variable access: " + node.name());
		return context.getVariable(node.name()).getValue();
	}

	@Override
	public Object visit(ArrayDeclNode node) {
		context.addVariable(new Variable(node.name(), DataType.HAIRETSU, new Array()));
		return null;
	}

	@Override
	public Object visit(ArrayAssignationNode node) {
		var array = (Array) context.getVariable(node.name()).getValue();
		List<Integer> indices = new ArrayList<>();
		node.indices().forEach(index -> indices.add(castToInteger(index.accept(this))));
		array.add(indices, node.value().accept(this));
		System.out.println("[LOG]: array assignation: " + node.name() + ": " + indices);
		return null;
	}

	@Override
	public Object visit(BreakNode node) {
		throw new BreakException("");
	}

	@Override
	public Object visit(FuncDeclNode node) {
		context.addFunction(node);
		return null;
	}

	@Override
	public Object visit(IfNode node) {
		if (castToBoolean(node.condition().accept(this))) {
			var inner = new Interpreter(this);
			node.body().forEach(statement -> statement.accept(inner));
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
				try {
					for (var statement : node.body()) {
						statement.accept(inner);
					}
				} catch (BreakException b) {
					break;
				}
			}
		}

		return null;
	}

	@Override
	public Object visit(ProgramNode node) {
		node.body().stream()
				.filter(statement -> statement instanceof FuncDeclNode)
				.forEach(statement -> statement.accept(this)
		);
		node.body().stream()
				.filter(statement -> !(statement instanceof FuncDeclNode))
				.forEach(statement -> statement.accept(this)
		);
		return null;
	}

	@Override
	public Object visit(ReturnStatementNode node) {
		throw new ReturnException("", node.expression());
	}

	@Override
	public Object visit(VarAssignationNode node) {
		context.changeValueOf(node.name(), node.value().accept(this));
		System.out.println("[LOG]: variable assignation: " + node.name());
		return null;
	}

	@Override
	public Object visit(VarDeclNode node) {
		context.addVariable(new Variable(node.name(), node.type(), node.expression().accept(this)));
		System.out.println("[LOG]: variable declared: " + node.name() + " = " + context.getVariable(node.name()).getValue());
		return null;
	}
}