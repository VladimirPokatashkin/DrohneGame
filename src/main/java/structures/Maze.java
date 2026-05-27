package structures;

import java.util.List;

public record Maze(
		Cell[][][] map,
		List<Cell> exits
) {
	public boolean isObstacle(int x, int y, int z) {
		return map[z][y][x].isObstacle();
	}

	public Cell get(int x, int y, int z) {
		if (z >= map.length || y >= map[z].length || z >= map[z][y].length) return null;
		return map[z][y][x];
	}
}