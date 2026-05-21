package lang.enums;

import lombok.Getter;

@Getter
public enum DataType {
	SEISU("int"),
	RONRI("boolean"),
	RIPPOTAI("cell"),
	HAIRETSU("array"),
	ANY("");

	private final String description;

	DataType(String description) {
		this.description = description;
	}
}