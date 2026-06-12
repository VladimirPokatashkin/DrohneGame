package service;

import lang.enums.DrohneCommandType;
import lombok.Getter;
import other.Pair;
import structures.Cell;
import structures.Drohne;
import structures.Maze;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class MazeService {
	private Maze maze;
	@Getter
	private Drohne drohne;

	private int distToNearestLeftObstacle(int x, int y, int z) {
		Cell[][][] map = maze.map();
		for (int i = x - 1; i >= 0; i--) {
			if (map[z][y][i].isObstacle()) return x - i;
		}
		return Integer.MIN_VALUE;
	}

	private int distToNearestRightObstacle(int x, int y, int z) {
		Cell[][][] map = maze.map();
		for (int i = x + 1; i < map[z][y].length; i++) {
			if (map[z][y][i].isObstacle()) return i - x;
		}
		return Integer.MIN_VALUE;
	}

	private int distToNearestBackObstacle(int x, int y, int z) {
		Cell[][][] map = maze.map();
		for (int i = y + 1; i < map[z].length; i++) {
			if (map[z][i][x].isObstacle()) return i - y;
		}
		return Integer.MIN_VALUE;
	}

	private int distToNearestFrontObstacle(int x, int y, int z) {
		Cell[][][] map = maze.map();
		for (int i = y - 1; i >= 0; i--) {
			if (map[z][i][x].isObstacle()) return y - i;
		}
		return Integer.MIN_VALUE;
	}

	private int distToNearestBottomObstacle(int x, int y, int z) {
		Cell[][][] map = maze.map();
		for (int i = z - 1; i >= 0; i--) {
			if (map[i][y][x].isObstacle()) return z - i;
		}
		return Integer.MIN_VALUE;
	}

	private int distToNearestTopObstacle(int x, int y, int z) {
		Cell[][][] map = maze.map();
		for (int i = z + 1; i < map.length; i++) {
			if (map[i][y][x].isObstacle()) return i - z;
		}
		return Integer.MIN_VALUE;
	}


	public MazeService(Pair<Maze, Drohne> pair) {
		maze = pair.first;
		drohne = pair.second;
	}


	public int getDrohneX() {
		return drohne.getX();
	}

	public int getDrohneY() {
		return drohne.getY();
	}

	public int getDrohneZ() {
		return drohne.getZ();
	}

	public Cell getCell(int x, int y, int z) {
		return maze.map()[z][y][x];
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
					throw new RuntimeException("drohne has crashed ( " + List.of(x, y, z + 1) + " ).");
				}
				drohne.setZ(z + 1);
			}
			case MOVE_DOWN -> {
				if (maze.isObstacle(x, y, z - 1)) {
					throw new RuntimeException("drohne has crashed ( " + List.of(x, y, z - 1) + " ).");
				}
				drohne.setZ(z - 1);
			}
			case MOVE_FORWARD -> {
				if (maze.isObstacle(x, y - 1, z)) {
					throw new RuntimeException("drohne has crashed ( " + List.of(x, y - 1, z) + " ).");
				}
				drohne.setY(y - 1);
			}
			case MOVE_BACK -> {
				if (maze.isObstacle(x, y + 1, z)) {
					throw new RuntimeException("drohne has crashed ( " + List.of(x, y + 1, z) + " ).");
				}
				drohne.setY(y + 1);
			}
			case MOVE_RIGHT -> {
				if (maze.isObstacle(x + 1, y, z)) {
					throw new RuntimeException("drohne has crashed ( " + List.of(x + 1, y, z) + " ).");
				}
				drohne.setX(x + 1);
			}
			case MOVE_LEFT -> {
				if (maze.isObstacle(x - 1, y, z)) {
					throw new RuntimeException("drohne has crashed ( " + List.of(x - 1, y, z) + " ).");
				}
				drohne.setX(x - 1);
			}
		}
	}
}