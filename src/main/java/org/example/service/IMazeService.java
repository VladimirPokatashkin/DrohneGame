package org.example.service;

import org.example.lang.enums.DrohneCommandType;
import org.example.observer.DrohneObserver;
import org.example.other.Pair;
import org.example.structures.Cell;
import org.example.structures.Drohne;
import org.example.structures.Maze;

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