package observer;

public interface DrohneObserver {
	void xChanged(int newX);
	void yChanged(int newY);
	void zChanged(int newZ);
}