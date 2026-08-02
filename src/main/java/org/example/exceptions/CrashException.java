package org.example.exceptions;

public class CrashException extends RuntimeException {
	public CrashException(String message) {
		super(message);
	}
}