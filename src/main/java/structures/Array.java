package structures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Array(
		Map<List<Integer>, Object> data
) {
	public Array() {
		this(new HashMap<>());
	}

	public void add(List<Integer> indices, Object value) {
		data.put(indices, value);
	}

	public Object get(List<Integer> indices) {
		return data.get(indices);
	}
}