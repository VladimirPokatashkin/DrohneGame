package structures;

import java.util.List;

public record Maze(
		Cell[][][] map,
		List<Cell> exits
) {
	private boolean outOfBounds(int x, int y, int z) {
		return z < 0 || z >= map.length || y < 0 || y >= map[z].length || x < 0 || x >= map[z][y].length;
	}

	public boolean isObstacle(int x, int y, int z) {
		return outOfBounds(x, y, z) || map[z][y][x].isObstacle();
	}

	public Cell get(int x, int y, int z) {
		return outOfBounds(x, y, z) ? null : map[z][y][x];
	}
}