package org.example.service;

import lombok.NoArgsConstructor;
import org.example.exceptions.CrashException;
import org.example.lang.enums.DrohneCommandType;
import lombok.Getter;
import org.example.observer.DrohneObserver;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import org.example.other.Pair;
import org.example.structures.Cell;
import org.example.structures.Drohne;
import org.example.structures.Maze;

import java.util.ArrayList;
import java.util.List;

@Service
@RequestScope
@NoArgsConstructor
public class MazeService implements IMazeService {
	private Maze maze;
	@Getter
	private Drohne drohne;
	private final List<DrohneObserver> observers = new ArrayList<>();

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

	@Override
	public void init(Pair<Maze, Drohne> pair) {
		maze = pair.first;
		drohne = pair.second;
		observers.clear();
	}


	@Override
	public void attach(DrohneObserver observer) {
		observers.add(observer);
		observer.start(drohne.getX(), drohne.getY(), drohne.getZ());
	}

	@Override
	public void detach(DrohneObserver observer) {
		observers.remove(observer);
	}


	@Override
	public int getDrohneX() {
		return drohne.getX();
	}

	@Override
	public int getDrohneY() {
		return drohne.getY();
	}

	@Override
	public int getDrohneZ() {
		return drohne.getZ();
	}

	@Override
	public Cell getCell(int x, int y, int z) {
		return maze.map()[z][y][x];
	}

	@Override
	public int distToNearestObstacle(DrohneCommandType scan, int x, int y, int z) {
		return switch (scan) {
			case SCAN_LEFT -> {
				observers.forEach(o -> o.scanned(x, y, z, "left"));
				yield distToNearestLeftObstacle(x, y, z);
			}
			case SCAN_RIGHT -> {
				observers.forEach(o -> o.scanned(x, y, z, "right"));
				yield distToNearestRightObstacle(x, y, z);
			}
			case SCAN_BACK -> {
				observers.forEach(o -> o.scanned(x, y, z, "back"));
				yield distToNearestBackObstacle(x, y, z);
			}
			case SCAN_FORWARD -> {
				observers.forEach(o -> o.scanned(x, y, z, "forward"));
				yield distToNearestFrontObstacle(x, y, z);
			}
			case SCAN_DOWN -> {
				observers.forEach(o -> o.scanned(x, y, z, "down"));
				yield distToNearestBottomObstacle(x, y, z);
			}
			case SCAN_UP -> {
				observers.forEach(o -> o.scanned(x, y, z, "up"));
				yield distToNearestTopObstacle(x, y, z);
			}
			default -> throw new RuntimeException("invalid scan operator.");
		};
	}

	@Override
	public void moveDrohne(DrohneCommandType direction) {
		int x = drohne.getX();
		int y = drohne.getY();
		int z = drohne.getZ();

		switch (direction) {
			case MOVE_UP -> {
				if (maze.isObstacle(x, y, z + 1)) {
					observers.forEach(o -> o.crashed(x, y, z + 1));
					throw new CrashException("drohne has crashed ( " + List.of(x, y, z + 1) + " ).");
				}
				drohne.setZ(z + 1);
			}
			case MOVE_DOWN -> {
				if (maze.isObstacle(x, y, z - 1)) {
					observers.forEach(o -> o.crashed(x, y, z - 1));
					throw new CrashException("drohne has crashed ( " + List.of(x, y, z - 1) + " ).");
				}
				drohne.setZ(z - 1);
			}
			case MOVE_FORWARD -> {
				if (maze.isObstacle(x, y - 1, z)) {
					observers.forEach(o -> o.crashed(x, y - 1, z));
					throw new CrashException("drohne has crashed ( " + List.of(x, y - 1, z) + " ).");
				}
				drohne.setY(y - 1);
			}
			case MOVE_BACK -> {
				if (maze.isObstacle(x, y + 1, z)) {
					observers.forEach(o -> o.crashed(x, y + 1, z));
					throw new CrashException("drohne has crashed ( " + List.of(x, y + 1, z) + " ).");
				}
				drohne.setY(y + 1);
			}
			case MOVE_RIGHT -> {
				if (maze.isObstacle(x + 1, y, z)) {
					observers.forEach(o -> o.crashed(x + 1, y, z));
					throw new CrashException("drohne has crashed ( " + List.of(x + 1, y, z) + " ).");
				}
				drohne.setX(x + 1);
			}
			case MOVE_LEFT -> {
				if (maze.isObstacle(x - 1, y, z)) {
					observers.forEach(o -> o.crashed(x - 1, y, z));
					throw new CrashException("drohne has crashed ( " + List.of(x - 1, y, z) + " ).");
				}
				drohne.setX(x - 1);
			}
		}

		observers.forEach(o -> o.moved(drohne.getX(), drohne.getY(), drohne.getZ()));
	}
}