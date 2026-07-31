package dto;

import java.util.List;

public record MazeResponse(
		List<List<String>> levels,
		String error
) {}