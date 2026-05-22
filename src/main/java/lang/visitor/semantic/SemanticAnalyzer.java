package lang.visitor.semantic;

import lang.enums.BinOperator;
import lang.enums.CellProperty;
import lang.enums.DataType;
import lang.enums.UnOperator;
import lang.exceptions.SemanticException;
import lang.syntaxtree.both.DrohneCommandSeqNode;
import lang.syntaxtree.both.FuncCallNode;
import lang.syntaxtree.expression.*;
import lang.syntaxtree.expression.literal.BooleanLiteral;
import lang.syntaxtree.expression.literal.CellLiteral;
import lang.syntaxtree.expression.literal.IntLiteral;
import lang.syntaxtree.expression.literal.TypeLiteral;
import lang.syntaxtree.statement.*;
import lang.visitor.ASTVisitor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class SemanticAnalyzer implements ASTVisitor<DataType> {
	private SymbolTable env = new SymbolTable();

	private SemanticAnalyzer(SemanticAnalyzer other) {
		this.env = new SymbolTable(other.env);
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


	@Override
	public DataType visit(DrohneCommandSeqNode node) {
		boolean hasScan = node.commands().stream().anyMatch(cmd -> cmd.ordinal() > 6);
		return hasScan ? DataType.RIPPOTAI : null;
	}

	@Override
	public DataType visit(BooleanLiteral node) {
		return DataType.RONRI;
	}

	@Override
	public DataType visit(CellLiteral node) {
		return DataType.RIPPOTAI;
	}

	@Override
	public DataType visit(IntLiteral node) {
		return DataType.SEISU;
	}

	@Override
	public DataType visit(TypeLiteral node) {
		return node.value();
	}

	@Override
	public DataType visit(ArrayAccessNode node) {
		if (!env.isArrayDeclared(node.name())) {
			throw new SemanticException("error: array \"" + node.name() + "\" was not declared in this scope.");
		}

		var declared = env.getArray(node.name());
		int size = declared.sizes().size();
		if (node.indices().size() > size) {
			throw new SemanticException("error: invalid indices amount of array \"" + node.name() + "\" (actual size: " + size + ")");
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
		return DataType.RONRI;
	}

	@Override
	public DataType visit(UnExpressionNode node) {
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
		if (env.isArrayDeclared(node.name())) {
			throw new SemanticException("error: array \"" + node.name() + "\" was already declared");
		}
		env.addArray(node.name(), node);
		return DataType.HAIRETSU;
	}

	@Override
	public DataType visit(FuncDeclNode node) {
		if (env.isFuncDeclared(node.name())) {
			throw new SemanticException("error: function \"" + node.name() + "\" was already declared");
		}

		analyzeInnerScope(node.body());
		env.addFunction(node.name(), node);
		return node.returnType();
	}

	@Override
	public DataType visit(IfNode node) {
		node.condition().accept(this);
		analyzeInnerScope(node.body());
		return null;
	}

	@Override
	public DataType visit(LoopNode node) {
		DataType typeOfBegin = node.begin().accept(this);
		DataType typeOfEnd = node.end().accept(this);
		if (typeOfBegin != typeOfEnd && !areCompatibleTypes(typeOfEnd, typeOfBegin)) {
			throw new SemanticException("error: loop bounds must be of the same type.");
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
		node.globalStatements().forEach(statement -> statement.accept(this));
		return null;
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