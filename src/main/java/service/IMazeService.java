package service;

import lang.enums.DrohneCommandType;
import observer.DrohneObserver;
import other.Pair;
import structures.Cell;
import structures.Drohne;
import structures.Maze;

public interface IMazeService {
	void init(Pair<Maze, Drohne> pair);
	void attach(DrohneObserver observer);
	void detach(DrohneObserver observer);
	int getDrohneX();
	int getDrohneY();
	int getDrohneZ();
	Cell getCell(int x, int y, int z);
	int distToNearestObstacle(DrohneCommandType scan, int x, int y, int z);
	void moveDrohne(DrohneCommandType direction);
}