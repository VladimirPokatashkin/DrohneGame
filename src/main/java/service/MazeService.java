package service;

import lang.enums.DrohneCommandType;
import structures.Cell;
import structures.Drohne;
import structures.Maze;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MazeService {
	private Maze maze;
	private Drohne drohne;

	private int distToNearestLeftObstacle(int x, int y, int z) {
		Cell[][][] map = maze.map();
		for (int i = x - 1; i >= 0; i--) {
			if (map[z][y][i].isObstacle()) return x - i;
		}
		return -1;
	}

	private int distToNearestRightObstacle(int x, int y, int z) {
		Cell[][][] map = maze.map();
		for (int i = x + 1; i < map.length; i++) {
			if (map[z][y][i].isObstacle()) return i - x;
		}
		return -1;
	}

	private int distToNearestBackObstacle(int x, int y, int z) {
		Cell[][][] map = maze.map();
		for (int i = y - 1; i >= 0; i--) {
			if (map[z][i][x].isObstacle()) return y - i;
		}
		return -1;
	}

	private int distToNearestFrontObstacle(int x, int y, int z) {
		Cell[][][] map = maze.map();
		for (int i = y + 1; i < map[x].length; i++) {
			if (map[z][i][x].isObstacle()) return i - y;
		}
		return -1;
	}

	private int distToNearestBottomObstacle(int x, int y, int z) {
		Cell[][][] map = maze.map();
		for (int i = z - 1; i >= 0; i--) {
			if (map[i][y][x].isObstacle()) return z - i;
		}
		return -1;
	}

	private int distToNearestTopObstacle(int x, int y, int z) {
		Cell[][][] map = maze.map();
		for (int i = z + 1; i < map[x][y].length; i++) {
			if (map[i][y][z].isObstacle()) return i - z;
		}
		return -1;
	}


	public int distToNearestObstacle(DrohneCommandType scan, int x, int y, int z) {
		return switch (scan) {
			case SCAN_LEFT -> distToNearestLeftObstacle(x, y, z);
			case SCAN_RIGHT -> distToNearestRightObstacle(x, y, z);
			case SCAN_BACK -> distToNearestBackObstacle(x, y, z);
			case SCAN_FORWARD -> distToNearestFrontObstacle(x, y, z);
			case SCAN_DOWN -> distToNearestBottomObstacle(x, y, z);
			case SCAN_UP -> distToNearestTopObstacle(x, y, z);
			default -> throw new RuntimeException("invalid scan operator.");
		};
	}

	public void moveDrohne(DrohneCommandType direction) {
		int x = drohne.getX();
		int y = drohne.getY();
		int z = drohne.getZ();

		switch (direction) {
			case MOVE_UP -> {
				if (maze.isObstacle(x, y, z + 1)) {
					throw new RuntimeException("drohne has crashed.");
				}
				drohne.setZ(z + 1);
			}
			case MOVE_DOWN -> {
				if (maze.isObstacle(x, y, z - 1)) {
					throw new RuntimeException("drohne has crashed.");
				}
				drohne.setZ(z - 1);
			}
			case MOVE_FORWARD -> {
				if (maze.isObstacle(x, y + 1, z)) {
					throw new RuntimeException("drohne has crashed.");
				}
				drohne.setY(y + 1);
			}
			case MOVE_BACK -> {
				if (maze.isObstacle(x, y - 1, z)) {
					throw new RuntimeException("drohne has crashed.");
				}
				drohne.setY(y - 1);
			}
			case MOVE_RIGHT -> {
				if (maze.isObstacle(x + 1, y, z)) {
					throw new RuntimeException("drohne has crashed.");
				}
				drohne.setX(x + 1);
			}
			case MOVE_LEFT -> {
				if (maze.isObstacle(x - 1, y, z)) {
					throw new RuntimeException("drohne has crashed.");
				}
				drohne.setX(x - 1);
			}
		}
	}
}