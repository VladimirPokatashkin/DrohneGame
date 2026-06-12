package lang.exceptions;

public class SemanticException extends RuntimeException {
	public SemanticException(String message, int line) {
		super(message + " (line: " + line + ")");
	}
}