package lang.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Bool {
	SHINRI(true),
	USO(false);

	private final boolean value;
}