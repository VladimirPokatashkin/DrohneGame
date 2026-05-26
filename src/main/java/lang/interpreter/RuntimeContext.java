package lang.interpreter;

import lang.interpreter.symbols.Variable;
import lang.structures.Drohne;
import lang.structures.Maze;
import lang.syntaxtree.statement.FuncDeclNode;

public record RuntimeContext(
		Maze maze,
		Drohne drohne,
		RuntimeSymbolTable symbolTable
) {
	public RuntimeContext(RuntimeContext other) {
		this(other.maze, other.drohne, new RuntimeSymbolTable(other.symbolTable));
	}

	public void addFunction(FuncDeclNode node) {
		symbolTable.addFunction(node);
	}

	public void addVariable(Variable variable) {
		symbolTable.addVariable(variable);
	}

	public void changeValueOf(String name, Object newValue) {
		symbolTable.changeValueOf(name, newValue);
	}

	public Variable getVariable(String name) {
		return symbolTable.getVariable(name);
	}

	public FuncDeclNode getFunction(String name) {
		return symbolTable.getFunction(name);
	}
}