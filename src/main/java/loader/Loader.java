package loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Loader {
	public static List<List<String>> loadMaze(Path mazePath) throws IOException {
		List<List<String>> levels = new ArrayList<>();
		List<String> currentLevel = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(mazePath.toFile()))) {
			String line;

			while ((line = br.readLine()) != null) {
				line = line.trim();

				if (line.isEmpty()) {
					if (!currentLevel.isEmpty()) {
						levels.add(currentLevel);
						currentLevel = new ArrayList<>();
					}
				} else {
					currentLevel.add(line.replace(" ", ""));
				}
			}
		}

		if (!currentLevel.isEmpty()) levels.add(currentLevel);
		return levels;
	}
}