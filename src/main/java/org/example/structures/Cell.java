package org.example.structures;

public record Cell(
		int x,
		int y,
		int z,
		boolean isObstacle
) {}