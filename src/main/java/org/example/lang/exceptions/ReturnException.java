package org.example.lang.exceptions;

import org.example.lang.syntaxtree.expression.Expression;
import lombok.Getter;

public class ReturnException extends RuntimeException {
	@Getter
	private final Expression expression;

	public ReturnException(String message, Expression expression) {
		super(message);
		this.expression = expression;
	}
}