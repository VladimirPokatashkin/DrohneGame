package exceptions;

public class CrashException extends RuntimeException {
	public CrashException(String message) {
		super(message);
	}
}