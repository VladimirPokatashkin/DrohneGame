package org.example;

public class Main {
//	static void main() {
//		GameWindow window;
//		RuntimeContext context;
//
//		ProgramNode program;
//		try {
//			Path root = Path.of(System.getProperty("user.dir"));
//			List<List<String>> levels = Loader.loadMaze(root.resolve("src/main/resources/maze.txt"));
//
//			context = new RuntimeContext(new MazeService(MazeFactory.create(levels)));
//			window = new GameWindow(levels);
//			context.mazeService().getDrohne().attach(window);
//
//			var parser = new Parser(new Lexer(new FileReader(root.resolve("src/main/resources/code.txt").toFile())));
//			program = (ProgramNode) parser.parse().value;
//
//		} catch (Exception e) {
//			System.err.println(e.getMessage());
//			return;
//		}
//
//		try {
//			var semanticAnalyzer = new SemanticAnalyzer();
//			semanticAnalyzer.visit(program);
//
//			JFrame frame = new JFrame("Drohne Maze Runner");
//			frame.add(window);
//			frame.pack();
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			frame.setLocationRelativeTo(null);
//			frame.setVisible(true);
//
//			new Thread(() -> {
//				var interpreter = new Interpreter(context);
//				interpreter.visit(program);
//				SwingUtilities.invokeLater(window::startAnimation);
//			}).start();
//		} catch (RuntimeException ex) {
//			System.err.println(ex.getMessage());
//		}
//	}
}