package controller;

import dto.MazeResponse;
import loader.Loader;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/api/maze")
public class MazeController {

	@GetMapping("/maze")
	public MazeResponse getMaze() {
		try {
			Path root = Path.of(System.getProperty("user.dir"));
			return new MazeResponse(Loader.loadMaze(root.resolve("src/main/resources/maze.txt")), "");
		} catch (IOException e) {
			return new MazeResponse(List.of(), e.getMessage());
		}
	}
}