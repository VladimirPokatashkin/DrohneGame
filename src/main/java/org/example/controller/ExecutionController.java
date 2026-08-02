package org.example.controller;

import org.example.dto.RunRequest;
import org.example.dto.RunResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.service.InterpreterService;

@RestController
@RequestMapping("/api/drohne")
@AllArgsConstructor
public class ExecutionController {
	private final InterpreterService service;

	@PostMapping("/run")
	public RunResponse execute(@RequestBody RunRequest request) {
		return service.execute(request);
	}
}