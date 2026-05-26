package lang.structures;

import java.util.ArrayList;
import java.util.List;

public record Array(
		List<Object> data
) {
	public Array() {
		this(new ArrayList<>());
	}
}