package lang.semanticanalyzer;

import lang.enums.BinOperator;
import lang.enums.CellProperty;
import lang.enums.DataType;
import lang.enums.UnOperator;
import lang.exceptions.SemanticException;
import lang.syntaxtree.both.DrohneCommandSeqNode;
import lang.syntaxtree.both.FuncCallNode;
import lang.syntaxtree.expression.*;
import lang.syntaxtree.expression.literal.*;
import lang.syntaxtree.statement.*;
import lang.visitor.ASTVisitor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class SemanticAnalyzer implements ASTVisitor<DataType> {
	private SemanticSymbolTable env = new SemanticSymbolTable();

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
		node.args().forEach(arg ->
			innerAnalyzer.env.addVariable(arg.second, arg.first)
		);

		boolean returnStatementFound = false;
		for (var statement : node.body()) {
			DataType type = statement.accept(innerAnalyzer);

			if (statement instanceof ReturnStatementNode) {
				returnStatementFound = true;

				if (!areCompatibleTypes(type, node.returnType())) {
					throw new SemanticException("invalid type of return statement in function \"" + node.name() + "\"");
				}
			}
		}

		if (!returnStatementFound)  {
			throw new SemanticException("missing modoru statement in function \"" + node.name() + "\"");
		}
	}


	@Override
	public DataType visit(DrohneCommandSeqNode node) {
		boolean hasScan = node.commands().stream().anyMatch(cmd -> cmd.ordinal() > 6);
		return hasScan ? DataType.RIPPOTAI : null;
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
			throw new SemanticException("error: array \"" + node.name() + "\" was not declared in this scope.");
		}
		if ((areCompatibleTypes(node.index().accept(this), DataType.SEISU))) {
			throw new SemanticException("type of array index must be compatible to seisu.");
		}
		return DataType.ANY;
	}

	@Override
	public DataType visit(BinExprNode node) {
		var operator = node.binOperator();
		if (operator == BinOperator.PLUS || operator == BinOperator.MINUS) {
			return DataType.SEISU;
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
			throw new SemanticException("error: function \"" + node.name() + "\" was not declared.");
		}

		var declared = env.getFunction(node.name());
		if (declared.args().size() != node.args().size()) {
			throw new SemanticException("error: invalid arguments of \"" + declared.name() + "\" function");
		}

		for (int i = 0; i < node.args().size(); ++i) {
			DataType given = node.args().get(i).accept(this);
			DataType expected = declared.args().get(i).first;

			if (given != expected && !areCompatibleTypes(given, expected)) {
				throw new SemanticException("error: invalid type of argument #" + i + " in function \"" + node.name() + "\"");
			}
		}
		return env.typeOf(node.name());
	}

	@Override
	public DataType visit(TypeComparisonNode node) {
		node.setResult(node.left().accept(this) == node.right().accept(this));
		return DataType.RONRI;
	}

	@Override
	public DataType visit(UnExprNode node) {
		DataType typeOfOperand = node.accept(this);
		if (node.operator() == UnOperator.JIGEN && typeOfOperand != DataType.HAIRETSU) {
			throw new SemanticException("invalid argument of \"jigen\" operator (it must be hairetsu).");
		}

		if (node.operator() == UnOperator.NOT && !areCompatibleTypes(DataType.RONRI, typeOfOperand)) {
			throw new SemanticException("invalid argument of \"not\" operator (it must be ronri or seisu).");
		}
		return node.operator() == UnOperator.NOT ? DataType.RONRI : DataType.SEISU;
	}

	@Override
	public DataType visit(VarAccessNode node) {
		if (!env.isVarDeclared(node.name())) {
			throw new SemanticException("error: variable \"" + node.name() + "\" was not declared.");
		}
		return env.typeOf(node.name());
	}

	@Override
	public DataType visit(ArrayDeclNode node) {
		if (env.isVarDeclared(node.name())) {
			throw new SemanticException("error: array \"" + node.name() + "\" was already declared");
		}
		env.addVariable(node.name(), DataType.HAIRETSU);
		return DataType.HAIRETSU;
	}

	@Override
	public DataType visit(FuncDeclNode node) {
		if (env.isFuncDeclared(node.name())) {
			throw new SemanticException("error: function \"" + node.name() + "\" was already declared");
		}

		analyzeFunctionBody(node);
		env.addFunction(node.name(), node);
		return node.returnType();
	}

	@Override
	public DataType visit(IfNode node) {
		if (areCompatibleTypes(node.condition().accept(this), DataType.RONRI)) {
			throw new SemanticException("sorenara condition must be of type ronri or compatible to ronri.");
		}
		analyzeInnerScope(node.body());
		return null;
	}

	@Override
	public DataType visit(LoopNode node) {
		DataType typeOfBegin = node.begin().accept(this);
		DataType typeOfEnd = node.end().accept(this);
		if (!areCompatibleTypes(typeOfBegin, DataType.SEISU) || !areCompatibleTypes(typeOfEnd, DataType.SEISU)) {
			throw new SemanticException("error: loop bounds must be of type seisu.");
		}

		if (env.isVarDeclared(node.iterator())) {
			throw new SemanticException("error: variable \"" + node.iterator() + "\" was already declared.");
		}

		var innerAnalyzer = new SemanticAnalyzer(this);
		innerAnalyzer.env.addVariable(node.iterator(), typeOfBegin);
		node.body().forEach(statement -> statement.accept(innerAnalyzer));

		return null;
	}

	@Override
	public DataType visit(ProgramNode node) {
		node.functions().forEach(func -> func.accept(this));
		node.statements().forEach(statement -> statement.accept(this));
		return null;
	}

	@Override
	public DataType visit(ReturnStatementNode node) {
		return node.expression().accept(this);
	}

	@Override
	public DataType visit(VarAssignationNode node) {
		if (!env.isVarDeclared(node.name())) {
			throw new SemanticException("error: variable \"" + node.name() + "\" was not declared in this scope.");
		}
		return null;
	}

	@Override
	public DataType visit(VarDeclNode node) {
		if (env.isVarDeclared(node.name())) {
			throw new SemanticException("error: variable \"" + node.name() + "\" was already declared.");
		}
		env.addVariable(node.name(), node.type());
		return node.type();
	}
}