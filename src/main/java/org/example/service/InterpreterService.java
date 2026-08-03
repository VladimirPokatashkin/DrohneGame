package org.example.service;

import org.example.dto.RunRequest;
import org.example.dto.RunResponse;
import org.example.exceptions.CrashException;
import org.example.lang.exceptions.LexicalException;
import org.example.lang.exceptions.SemanticException;
import org.example.lang.exceptions.SyntaxException;
import org.example.lang.interpreter.Interpreter;
import org.example.lang.interpreter.RuntimeContext;
import lang.parser.Lexer;
import lang.parser.Parser;
import org.example.lang.semanticanalyzer.SemanticAnalyzer;
import org.example.lang.syntaxtree.statement.ProgramNode;
import org.example.loader.Loader;
import lombok.AllArgsConstructor;
import org.example.observer.StepRecorder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

@Service
@AllArgsConstructor
public class InterpreterService {
	private final MazeFactory mazeFactory;
	private final MazeService mazeService;


	public RunResponse execute(RunRequest request) {
		var observer = new StepRecorder();
		try {
			var resource = new ClassPathResource("maze.txt");
			List<List<String>> levels = Loader.loadMaze(new BufferedReader(new InputStreamReader(resource.getInputStream())));

			mazeService.init(mazeFactory.create(levels));
			mazeService.attach(observer);

			var program = (ProgramNode) (new Parser(new Lexer(new StringReader(request.code())))).parse().value;

			var semanticAnalyzer = new SemanticAnalyzer();
			semanticAnalyzer.visit(program);

			var context = new RuntimeContext(mazeService);
			var interpreter = new Interpreter(context);
			interpreter.visit(program);

			return new RunResponse(observer.getSteps(), "", "");

		} catch (LexicalException e) {
			return new RunResponse(List.of(), "lexical error", e.getMessage());
		} catch (SyntaxException e) {
			return new RunResponse(List.of(), "syntax error", e.getMessage());
		} catch (CrashException e) {
			return new RunResponse(observer.getSteps(),"crashed", e.getMessage());
		} catch (SemanticException e) {
			return new RunResponse(observer.getSteps(), "semantic error", e.getMessage());
		} catch (Exception e) {
			return new RunResponse(observer.getSteps(), "error", e.getMessage());
		}
	}
}