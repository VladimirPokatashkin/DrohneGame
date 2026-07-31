package service;

import dto.RunRequest;
import dto.RunResponse;
import exceptions.CrashException;
import lang.exceptions.SemanticException;
import lang.interpreter.Interpreter;
import lang.interpreter.RuntimeContext;
import lang.parser.Lexer;
import lang.parser.Parser;
import lang.semanticanalyzer.SemanticAnalyzer;
import lang.syntaxtree.statement.ProgramNode;
import loader.Loader;
import lombok.AllArgsConstructor;
import observer.StepRecorder;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.nio.file.Path;
import java.util.List;

@Service
@AllArgsConstructor
public class InterpreterService {
	private final MazeFactory mazeFactory;
	private final MazeService mazeService;


	public RunResponse execute(RunRequest request) {
		var observer = new StepRecorder();
		try {
			Path root = Path.of(System.getProperty("user.dir"));
			List<List<String>> levels = Loader.loadMaze(root.resolve("src/main/resources/maze.txt"));


			mazeService.init(mazeFactory.create(levels));
			mazeService.attach(observer);

			var program = (ProgramNode) (new Parser(new Lexer(new StringReader(request.code())))).parse().value;

			var semanticAnalyzer = new SemanticAnalyzer();
			semanticAnalyzer.visit(program);

			var context = new RuntimeContext(mazeService);
			var interpreter = new Interpreter(context);
			interpreter.visit(program);

			return new RunResponse(observer.getSteps(), "", "");

		} catch (CrashException e) {
			return new RunResponse(observer.getSteps(),"crashed", e.getMessage());
		} catch (SemanticException e) {
			return new RunResponse(observer.getSteps(), "semantic error", e.getMessage());
		} catch (Exception e) {
			return new RunResponse(observer.getSteps(), "error", e.getMessage());
		}
	}
}