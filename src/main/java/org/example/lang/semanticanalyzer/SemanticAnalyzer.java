package org.example.lang.semanticanalyzer;

import org.example.lang.enums.*;
import org.example.lang.enums.*;
import org.example.lang.exceptions.SemanticException;
import org.example.lang.syntaxtree.both.DrohneCommandSeqNode;
import org.example.lang.syntaxtree.both.FuncCallNode;
import org.example.lang.syntaxtree.expression.*;
import org.example.lang.syntaxtree.expression.literal.*;
import org.example.lang.syntaxtree.statement.*;
import org.example.lang.syntaxtree.expression.*;
import org.example.lang.syntaxtree.expression.literal.BooleanLiteralNode;
import org.example.lang.syntaxtree.expression.literal.IntLiteralNode;
import org.example.lang.syntaxtree.expression.literal.TypeLiteralNode;
import org.example.lang.syntaxtree.statement.*;
import org.example.lang.visitor.ASTVisitor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class SemanticAnalyzer implements ASTVisitor<DataType> {
	private SemanticSymbolTable env = new SemanticSymbolTable();
	private static int loopDepth = 0;

	private SemanticAnalyzer(SemanticAnalyzer other) {
		this.env = new SemanticSymbolTable(other.env);
	}

	private SemanticAnalyzer(Map<String, FuncDeclNode> functions) {
		env = new SemanticSymbolTable(functions);
	}

	private boolean areCompatibleTypes(DataType given, DataType expected) {
		if (expected == given || expected == DataType.ANY) return true;
		return given == DataType.SEISU && expected == DataType.RONRI ||
				given == DataType.RONRI && expected == DataType.SEISU;
	}

	private void analyzeInnerScope(List<Statement> scope) {
		var innerAnalyzer = new SemanticAnalyzer(this);
		scope.forEach(statement -> statement.accept(innerAnalyzer));
	}

	private void analyzeFunctionBody(FuncDeclNode node) {
		var innerAnalyzer = new SemanticAnalyzer(this.env.functions());
		node.args().forEach(arg -> innerAnalyzer.env.addVariable(arg.second, arg.first));

		boolean returnStatementFound = false;
		for (var statement : node.body()) {
			DataType type = statement.accept(innerAnalyzer);

			if (statement instanceof ReturnStatementNode) {
				returnStatementFound = true;

				if (!areCompatibleTypes(type, node.returnType())) {
					throw new SemanticException("invalid type of return statement in function \"" + node.name() + "\"", node.line() + 1);
				}
			}
		}

		if (!returnStatementFound)  {
			throw new SemanticException("missing modoru statement in function \"" + node.name() + "\"", node.line() + 1);
		}
	}


	@Override
	public DataType visit(DrohneCommandSeqNode node) {
		DrohneCommandType prev = null;
		int scansCnt = 0;
		int getPosCnt = 0;

		for (var command : node.commands()) {
			if (command == DrohneCommandType.BREAK_SEQ) {
				if (prev == null || prev.ordinal() < 7 || prev.ordinal() > 12) {
					throw new SemanticException("break sequence operator must be after scan operator", node.line() + 1);
				}
			}

			if (command.ordinal() >= 7 && command.ordinal() <= 12) ++scansCnt;

			if (command.ordinal() == 13) ++getPosCnt;

			prev = command;
		}

		if (scansCnt + getPosCnt > 1) return DataType.HAIRETSU;
		if (scansCnt == 1) return DataType.SEISU;
		if (getPosCnt == 1) return DataType.RIPPOTAI;
		return null;
	}

	@Override
	public DataType visit(BooleanLiteralNode node) {
		return DataType.RONRI;
	}

	@Override
	public DataType visit(IntLiteralNode node) {
		return DataType.SEISU;
	}

	@Override
	public DataType visit(TypeLiteralNode node) {
		return node.value();
	}

	@Override
	public DataType visit(ArrayAccessNode node) {
		if (!env.isVarDeclared(node.name())) {
			throw new SemanticException("error: array \"" + node.name() + "\" was not declared in this scope.", node.line() + 1);
		}
		node.indices().forEach(index -> {
			if ((areCompatibleTypes(index.accept(this), DataType.SEISU))) {
				throw new SemanticException("type of array index must be compatible to seisu.", node.line() + 1);
			}
		});
		return DataType.ANY;
	}

	@Override
	public DataType visit(BinExprNode node) {
		var operator = node.binOperator();
		DataType typeOfLeft = node.left().accept(this);
		DataType typeOfRight = node.right().accept(this);

		if (operator == BinOperator.PLUS || operator == BinOperator.MINUS) {
			if (!areCompatibleTypes(typeOfLeft, DataType.SEISU) || !areCompatibleTypes(typeOfRight, DataType.SEISU)) {
				throw new SemanticException("error: invalid operand of arithmetic operation.", node.line() + 1);
			}
			return DataType.SEISU;
		}

		if (!areCompatibleTypes(typeOfLeft, DataType.RONRI) || !areCompatibleTypes(typeOfRight, DataType.RONRI)) {
			throw new SemanticException("error: invalid operand of boolean operation.", node.line() + 1);
		}

		return DataType.RONRI;
	}

	@Override
	public DataType visit(CellPropertyAccess node) {
		return node.property() == CellProperty.IS_OBSTACLE ? DataType.RONRI : DataType.SEISU;
	}

	@Override
	public DataType visit(FuncCallNode node) {
		if (!env.isFuncDeclared(node.name())) {
			throw new SemanticException("error: function \"" + node.name() + "\" was not declared.", node.line() + 1);
		}

		var declared = env.getFunction(node.name());
		if (declared.args().size() != node.args().size()) {
			throw new SemanticException("error: invalid arguments of \"" + declared.name() + "\" function", node.line() + 1);
		}

		for (int i = 0; i < node.args().size(); ++i) {
			DataType given = node.args().get(i).accept(this);
			DataType expected = declared.args().get(i).first;

			if (given != expected && !areCompatibleTypes(given, expected)) {
				throw new SemanticException("error: invalid type of argument #" + i + " in function \"" + node.name() + "\"", node.line() + 1);
			}
		}
		return env.typeOf(node.name());
	}

	@Override
	public DataType visit(TypeComparisonNode node) {
		return DataType.RONRI;
	}

	@Override
	public DataType visit(UnExprNode node) {
		DataType typeOfOperand = node.operand().accept(this);
		if (node.operator() == UnOperator.JIGEN && typeOfOperand != DataType.HAIRETSU) {
			throw new SemanticException("invalid argument of \"jigen\" operator (it must be hairetsu).", node.line() + 1);
		}

		if (node.operator() == UnOperator.NOT && !areCompatibleTypes(DataType.RONRI, typeOfOperand)) {
			throw new SemanticException("invalid argument of \"not\" operator (it must be ronri or seisu).", node.line() + 1);
		}
		return node.operator() == UnOperator.NOT ? DataType.RONRI : DataType.SEISU;
	}

	@Override
	public DataType visit(VarAccessNode node) {
		if (!env.isVarDeclared(node.name())) {
			throw new SemanticException("error: variable \"" + node.name() + "\" was not declared.", node.line() + 1);
		}
		return env.typeOf(node.name());
	}

	@Override
	public DataType visit(ArrayDeclNode node) {
		if (env.isVarDeclared(node.name())) {
			throw new SemanticException("error: array \"" + node.name() + "\" was already declared", node.line() + 1);
		}
		env.addVariable(node.name(), DataType.HAIRETSU);
		return DataType.HAIRETSU;
	}

	@Override
	public DataType visit(ArrayAssignationNode node) {
		if (!env.isVarDeclared(node.name())) {
			throw new SemanticException("error: array \"" + node.name() + "\" was not declared", node.line() + 1);
		}
		if (env.typeOf(node.name()) != DataType.HAIRETSU) {
			throw new SemanticException("error: var \"" + node.name() + "\" is not an array", node.line() + 1);
		}
		node.indices().forEach(index -> {
			if (!areCompatibleTypes(index.accept(this), DataType.SEISU)) {
				throw new SemanticException("indices of hairetsu must be seisu", node.line() + 1);
			}
		});
		return null;
	}

	@Override
	public DataType visit(BreakNode node) {
		if (loopDepth == 0) {
			throw new SemanticException("kowasu statement must be inside shuki.", node.line() + 1);
		}
		return null;
	}

	@Override
	public DataType visit(FuncDeclNode node) {
		if (env.isFuncDeclared(node.name())) {
			throw new SemanticException("error: function \"" + node.name() + "\" was already declared", node.line());
		}
		env.addFunction(node.name(), node);
		analyzeFunctionBody(node);
		return node.returnType();
	}

	@Override
	public DataType visit(IfNode node) {
		if (!areCompatibleTypes(node.condition().accept(this), DataType.RONRI)) {
			throw new SemanticException(
					"error: sorenara condition must be of type ronri or compatible to ronri.", node.line() + 1);
		}
		analyzeInnerScope(node.body());
		return null;
	}

	@Override
	public DataType visit(LoopNode node) {
		DataType typeOfBegin = node.begin().accept(this);
		DataType typeOfEnd = node.end().accept(this);
		if (!areCompatibleTypes(typeOfBegin, DataType.SEISU) || !areCompatibleTypes(typeOfEnd, DataType.SEISU)) {
			throw new SemanticException("error: loop bounds must be of type seisu.", node.line() + 1);
		}

		if (env.isVarDeclared(node.iterator())) {
			throw new SemanticException("error: variable \"" + node.iterator() + "\" was already declared.", node.line() + 1);
		}

		var innerAnalyzer = new SemanticAnalyzer(this);
		innerAnalyzer.env.addVariable(node.iterator(), typeOfBegin);
		++loopDepth;
		node.body().forEach(statement -> statement.accept(innerAnalyzer));
		--loopDepth;

		return null;
	}

	@Override
	public DataType visit(ProgramNode node) {
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
	public DataType visit(ReturnStatementNode node) {
		return node.expression().accept(this);
	}

	@Override
	public DataType visit(VarAssignationNode node) {
		if (!env.isVarDeclared(node.name())) {
			throw new SemanticException("error: variable \"" + node.name() + "\" was not declared in this scope.", node.line() + 1);
		}
		if (!areCompatibleTypes(node.value().accept(this), env.typeOf(node.name()))) {
			throw new SemanticException("error: invalid type of variable \"" + node.name() + "\" new value.", node.line() + 1);
		}
		return null;
	}

	@Override
	public DataType visit(VarDeclNode node) {
		if (env.isVarDeclared(node.name())) {
			throw new SemanticException("error: variable \"" + node.name() + "\" was already declared.", node.line() + 1);
		}
		env.addVariable(node.name(), node.type());
		return node.type();
	}
}