package structures;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import observer.DrohneObserver;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Drohne {
	private int x;
	private int y;
	private int z;
	private List<DrohneObserver> observers;

	public Drohne(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		observers = new ArrayList<>();
	}

	public void attach(DrohneObserver observer) {
		observers.add(observer);
	}

	public void detach(DrohneObserver observer) {
		observers.remove(observer);
	}

	public void setX(int newX) {
		x = newX;
		observers.forEach(observer -> observer.xChanged(x));
	}

	public void setY(int newY) {
		y = newY;
		observers.forEach(observer -> observer.yChanged(y));
	}

	public void setZ(int newZ) {
		z = newZ;
		observers.forEach(observer -> observer.zChanged(z));
	}
}