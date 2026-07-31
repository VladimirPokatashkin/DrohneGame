package dto;

import java.util.Map;

public record StepDTO(
		int x,
		int y,
		int z,
		String type,
		Map<String, String> properties
) {}