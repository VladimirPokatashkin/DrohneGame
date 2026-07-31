package dto;

import java.util.List;

public record RunResponse(
		List<StepDTO> steps,
		String error,
		String message
) {}