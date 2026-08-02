package org.example.lang.interpreter;

import org.example.lang.enums.DrohneCommandType;
import org.example.lang.interpreter.symbols.Variable;
import org.example.service.IMazeService;
import org.example.structures.Cell;
import org.example.lang.syntaxtree.statement.FuncDeclNode;

public record RuntimeContext(
		IMazeService mazeService,
		RuntimeSymbolTable symbolTable
) {
	public RuntimeContext(RuntimeContext other) {
		this(other.mazeService, new RuntimeSymbolTable(other.symbolTable));
	}

	public RuntimeContext(IMazeService mazeService) {
		this(mazeService, new RuntimeSymbolTable());
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

	public void moveDrohne(DrohneCommandType direction) {
		mazeService.moveDrohne(direction);
	}

	public int distToNearestObstacle(DrohneCommandType scan, int x, int y, int z) {
		return mazeService.distToNearestObstacle(scan, x, y, z);
	}

	public int getDrohneX() {
		return mazeService.getDrohneX();
	}

	public int getDrohneY() {
		return mazeService.getDrohneY();
	}

	public int getDrohneZ() {
		return mazeService.getDrohneZ();
	}

	public Variable getVariable(String name) {
		return symbolTable.getVariable(name);
	}

	public FuncDeclNode getFunction(String name) {
		return symbolTable.getFunction(name);
	}

	public Cell getCell(int x, int y, int z) {
		return mazeService.getCell(x, y, z);
	}
}