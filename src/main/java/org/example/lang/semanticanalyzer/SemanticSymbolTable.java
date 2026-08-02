package org.example.lang.semanticanalyzer;

import org.example.lang.enums.DataType;
import org.example.lang.syntaxtree.statement.FuncDeclNode;

import java.util.HashMap;
import java.util.Map;

public record SemanticSymbolTable(
		Map<String, DataType> variables,
		Map<String, FuncDeclNode> functions
) {
	public SemanticSymbolTable() {
		this(new HashMap<>(), new HashMap<>());
	}

	public SemanticSymbolTable(Map<String, FuncDeclNode> functions) {
		this(new HashMap<>(), functions);
	}

	public SemanticSymbolTable(SemanticSymbolTable other) {
		this(new HashMap<>(other.variables), new HashMap<>(other.functions));
	}

	public boolean isVarDeclared(String name) {
		return variables.containsKey(name);
	}

	public boolean isFuncDeclared(String name) {
		return functions.containsKey(name);
	}

	public DataType typeOf(String name) {
		if (variables.containsKey(name)) return variables.get(name);
		return functions.get(name).returnType();
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
}