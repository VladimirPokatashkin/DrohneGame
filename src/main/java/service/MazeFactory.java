package service;

import other.Pair;
import structures.Cell;
import structures.Drohne;
import structures.Maze;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MazeFactory {
	public static Pair<Maze, Drohne> create(List<List<String>> levels) throws IOException {
		int X = levels.getFirst().getFirst().length();
		int Y = levels.getFirst().size();
		int Z = levels.size();

		Cell[][][] map = new Cell[Z][Y][X];
		List<Cell> exits = new ArrayList<>();
		Drohne drohne = null;

		for (int z = 0; z < Z; z++) {
			for (int y = 0; y < Y; y++) {
				for (int x = 0; x < X; x++) {
					char c = levels.get(z).get(y).charAt(x);

					switch (c) {
						case '#' -> map[z][y][x] = new Cell(x, y, z, true);
						case '.' -> map[z][y][x] = new Cell(x, y, z, false);
						case 'S' -> {
							if (drohne != null) {
								throw new IOException("drohne declared twice.");
							}

							drohne = new Drohne(x, y, z);
							map[z][y][x] = new Cell(x, y, z, false);
						}
						case 'E' -> {
							Cell cell = new Cell(x, y, z, false);
							map[z][y][x] = cell;
							exits.add(cell);
						}
					}
				}
			}
		}

		return new Pair<>(new Maze(map, exits), drohne);
	}
}