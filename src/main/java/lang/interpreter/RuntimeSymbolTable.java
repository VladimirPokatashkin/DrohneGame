package lang.interpreter;

import lang.interpreter.symbols.Variable;
import lang.syntaxtree.statement.FuncDeclNode;

import java.util.Map;

public record RuntimeSymbolTable(
		Map<String, Variable> variables,
		Map<String, FuncDeclNode> functions
) {
	public Variable getVariable(String name) {
		return variables.get(name);
	}

	public FuncDeclNode getFunction(String name) {
		return functions.get(name);
	}

	public void addFunction(FuncDeclNode node) {
		functions.put(node.name(), node);
	}

	public void addVariable(Variable variable) {
		variables.put(variable.getName(), variable);
	}

	public void changeValueOf(String name, Object newValue) {
		variables.get(name).setValue(newValue);
	}
}