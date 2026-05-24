package lang.interpreter.symbols;

import lang.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Variable {
	private String name;
	private DataType type;
	@Setter
	private Object value;
}