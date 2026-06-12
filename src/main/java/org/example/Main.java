package org.example;

import graphics.GameWindow;
import java_cup.runtime.Symbol;
import lang.interpreter.Interpreter;
import lang.interpreter.RuntimeContext;
import lang.parser.Lexer;
import lang.parser.Parser;
import lang.semanticanalyzer.SemanticAnalyzer;
import lang.syntaxtree.statement.ProgramNode;
import loader.Loader;
import service.MazeFactory;
import service.MazeService;

import javax.swing.*;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;

public class Main {
	static void main() {
		GameWindow window;
		RuntimeContext context;

		ProgramNode program;
		try {
			Path root = Path.of(System.getProperty("user.dir"));
			List<List<String>> levels = Loader.loadMaze(root.resolve("src/main/resources/maze.txt"));

			context = new RuntimeContext(new MazeService(MazeFactory.create(levels)));
			window = new GameWindow(levels);
			context.mazeService().getDrohne().attach(window);

			var parser = new Parser(new Lexer(new FileReader(root.resolve("src/main/resources/code.txt").toFile())));
			program = (ProgramNode) parser.parse().value;

		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}

		try {
			var semanticAnalyzer = new SemanticAnalyzer();
			semanticAnalyzer.visit(program);

			JFrame frame = new JFrame("Drohne Maze Runner");
			frame.add(window);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			new Thread(() -> {
				var interpreter = new Interpreter(context);
				interpreter.visit(program);
				SwingUtilities.invokeLater(window::startAnimation);
			}).start();
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
		}
	}
}