package controller;

import dto.RunRequest;
import dto.RunResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import service.InterpreterService;

@Controller
@RequestMapping("/api/drohne")
@AllArgsConstructor
public class ExecutionController {
	private final InterpreterService service;

	@PostMapping("/run")
	public RunResponse execute(RunRequest request) {
		return service.execute(request);
	}
}