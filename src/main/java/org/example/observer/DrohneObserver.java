package org.example.observer;

public interface DrohneObserver {
	void start(int x, int y, int z);
	void moved(int x, int y, int z);
	void crashed(int x, int y, int z);
	void scanned(int x, int y, int z, String direction);
}