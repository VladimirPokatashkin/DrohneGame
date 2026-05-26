package lang.interpreter.symbols;

import lang.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import other.Pair;

@Getter
@AllArgsConstructor
public class Variable {
	private String name;
	private DataType type;
	@Setter
	private Object value;

	public Variable(Pair<DataType, String> pair, Object value) {
		name = pair.second;
		type = pair.first;
		this.value = value;
	}
}