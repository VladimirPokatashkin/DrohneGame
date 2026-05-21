package lang.visitor.semantic;

import lang.enums.DataType;
import lang.syntaxtree.statement.ArrayDeclNode;
import lang.syntaxtree.statement.FuncDeclNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SymbolTable(
		Map<String, DataType> variables,
		Map<String, FuncDeclNode> functions,
		Map<String, ArrayDeclNode> arrays
) {
	public SymbolTable() {
		this(new HashMap<>(), new HashMap<>(), new HashMap<>());
	}

	public SymbolTable(SymbolTable other) {
		this(new HashMap<>(other.variables), new HashMap<>(other.functions), new HashMap<>(other.arrays));
	}

	public boolean isVarDeclared(String name) {
		return variables.containsKey(name);
	}

	public boolean isFuncDeclared(String name) {
		return functions.containsKey(name);
	}

	public boolean isArrayDeclared(String name) {
		return arrays.containsKey(name);
	}

	public DataType typeOf(String name) {
		return variables.get(name);
	}

	public void addArray(String name, ArrayDeclNode array) {
		arrays.put(name, array);
	}

	public void addVariable(String name, DataType type) {
		variables.put(name, type);
	}

	public void addFunction(String name, FuncDeclNode func) {
		functions.put(name, func);
	}

	public FuncDeclNode getFunction(String name) {
		return functions.get(name);
	}

	public ArrayDeclNode getArray(String name) {
		return arrays.get(name);
	}
}