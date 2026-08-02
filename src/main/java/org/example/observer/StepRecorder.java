package org.example.observer;

import org.example.dto.StepDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class StepRecorder implements DrohneObserver {
	private final List<StepDTO> steps = new ArrayList<>();

	@Override
	public void start(int x, int y, int z) {
		steps.add(new StepDTO(x, y, z, "start", Map.of()));
	}

	@Override
	public void moved(int x, int y, int z) {
		steps.add(new StepDTO(x, y, z, "move", Map.of()));
	}

	@Override
	public void crashed(int x, int y, int z) {
		steps.add(new StepDTO(x, y, z, "crash", Map.of()));
	}

	@Override
	public void scanned(int x, int y, int z, String direction) {
		steps.add(new StepDTO(x, y, z, "scan", Map.of("direction", direction)));
	}
}