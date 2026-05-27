import graphics.GameWindow;
import java_cup.runtime.Symbol;
import lang.exceptions.SemanticException;
import lang.interpreter.Interpreter;
import lang.interpreter.RuntimeContext;
import lang.parser.Lexer;
import lang.parser.Parser;
import lang.semanticanalyzer.SemanticAnalyzer;
import lang.syntaxtree.statement.ProgramNode;
import loader.Loader;
import service.MazeFactory;
import service.MazeService;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		GameWindow window;
		RuntimeContext context;

		ProgramNode program;
		try {
			Path root = Path.of(System.getProperty("user.dir"));
			List<List<String>> levels = Loader.loadMaze(root.resolve("src/main/resources/maze.txt"));

			context = new RuntimeContext(MazeFactory.create(levels));
			window = new GameWindow(levels);
			context.drohne().attach(window);

			Parser parser = new Parser(new Lexer(new FileReader(root.resolve("src/main/resources/code.aboba").toFile())));
			Symbol sym = parser.parse();
			program = (ProgramNode) sym.value;

		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}

		try {
			var semanticAnalyzer = new SemanticAnalyzer();
			semanticAnalyzer.visit(program);

			Interpreter interpreter = new Interpreter(context, new MazeService(context.maze(), context.drohne()));
			interpreter.visit(program);
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
		}
	}
}