package lang.structures;

import java.util.List;

public record Maze(
		boolean[][][] map,
		List<Cell> exits
) {

}