package org.example.controller;

import org.example.dto.MazeResponse;
import org.example.loader.Loader;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/maze")
public class MazeController {

	@GetMapping("/maze")
	public MazeResponse getMaze() {
		try {
			var resource = new ClassPathResource("maze.txt");
			return new MazeResponse(Loader.loadMaze(new BufferedReader(new InputStreamReader(resource.getInputStream()))), "");
		} catch (Exception e) {
			return new MazeResponse(List.of(), e.getMessage());
		}
	}
}