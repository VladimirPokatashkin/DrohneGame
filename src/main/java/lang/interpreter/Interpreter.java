package lang.interpreter;

import lang.enums.DataType;
import lang.enums.DrohneCommandType;
import lang.interpreter.symbols.Variable;
import service.MazeService;
import structures.Array;
import structures.Cell;
import lang.syntaxtree.both.DrohneCommandSeqNode;
import lang.syntaxtree.both.FuncCallNode;
import lang.syntaxtree.expression.*;
import lang.syntaxtree.expression.literal.BooleanLiteralNode;
import lang.syntaxtree.expression.literal.IntLiteralNode;
import lang.syntaxtree.expression.literal.TypeLiteralNode;
import lang.syntaxtree.statement.*;
import lang.visitor.ASTVisitor;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class Interpreter implements ASTVisitor<Object> {
	private RuntimeContext context;
	private MazeService mazeService;

	private Interpreter(Interpreter other) {
		this.context = other.context;
		this.mazeService = other.mazeService;
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
		List<DrohneCommandType> scans = new ArrayList<>();
		DrohneCommandType prev = null;

		int x = context.drohne().getX();
		int y = context.drohne().getY();
		int z = context.drohne().getZ();

		for (var command : node.commands()) {
			if (command == DrohneCommandType.BREAK_SEQ) {
				if (mazeService.distToNearestObstacle(prev, x, y, z) == 1) break;
			}

			switch (command) {
				case SCAN_FORWARD, SCAN_BACK, SCAN_DOWN, SCAN_UP, SCAN_LEFT, SCAN_RIGHT, GET_POS ->
					scans.add(command);
				case MOVE_UP, MOVE_DOWN, MOVE_FORWARD, MOVE_BACK, MOVE_RIGHT, MOVE_LEFT ->
					mazeService.moveDrohne(command);
			}

			prev = command;
		}

		if (scans.isEmpty()) return null;

		List<Object> res = new ArrayList<>();

		x = context.drohne().getX();
		y = context.drohne().getY();
		z = context.drohne().getZ();

		for (var scan : scans) {
			if (scan == DrohneCommandType.GET_POS) {
				res.add(context.getCell(x, y, z));
			} else {
				res.add(mazeService.distToNearestObstacle(scan, x, y, z));
			}
		}

		return res;
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

		FuncDeclNode prototype = context.getFunction(node.name());
		for (int i = 0; i < node.args().size(); i++) {
			Object val = node.args().get(i).accept(this);
			innerContext.addVariable(new Variable(prototype.args().get(i), val));
		}

		Interpreter innerInterpreter = new Interpreter(innerContext, this.mazeService);

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
	public Object visit(BreakNode node) {
		return node;
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
				if (statement instanceof BreakNode || statement instanceof ReturnStatementNode) return statement;

				Object res = statement.accept(inner);

				if (res instanceof BreakNode || res instanceof ReturnStatementNode) return statement;
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
					if (statement instanceof BreakNode) break;
					if (statement instanceof ReturnStatementNode) return statement;

					Object res = statement.accept(inner);

					if (res instanceof BreakNode) break;
					if (res instanceof ReturnStatementNode) return res;
				}
			}
		}

		return null;
	}

	@Override
	public Object visit(ProgramNode node) {
		node.body().forEach(statement -> statement.accept(this));
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