package graphics;

import observer.DrohneObserver;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameWindow extends JPanel implements DrohneObserver {
	private final int TILE_SIZE = 50;

	private char[][][] map;
	private int drohneX = 1;
	private int drohneY = 1;
	private int drohneZ = 0;

	private final Queue<Runnable> actionQueue = new LinkedList<>();
	private final Timer animationTimer;

	public GameWindow(List<List<String>> levels) {
		initMap(levels);

		setPreferredSize(new Dimension(map[0][0].length * TILE_SIZE, map[0].length * TILE_SIZE));

		animationTimer = new Timer(300, _ -> {
			if (!actionQueue.isEmpty()) {
				Runnable action = actionQueue.poll();
				action.run();
				repaint();
			}
		});
	}

	private void initMap(List<List<String>> levels) {
		int Z = levels.size();
		int Y = levels.getFirst().size();
		int X = levels.getFirst().getFirst().length();

		map = new char[Z][Y][X];

		for (int z = 0; z < Z; z++) {
			for (int y = 0; y < Y; y++) {
				for (int x = 0; x < X; x++) {
					map[z][y][x] = levels.get(z).get(y).charAt(x);
					if (map[z][y][x] == 'S') {
						drohneX = x;
						drohneY = y;
						drohneZ = z;
						map[z][y][x] = '.';
					}
				}
			}
		}
	}

	public void startAnimation() {
		animationTimer.start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		char[][] currentLayer = map[drohneZ];

		for (int y = 0; y < currentLayer.length; y++) {
			for (int x = 0; x < currentLayer[y].length; x++) {
				if (currentLayer[y][x] == '#') {
					g2d.setColor(Color.DARK_GRAY);
				} else {
					g2d.setColor(Color.LIGHT_GRAY);
				}
				g2d.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				g2d.setColor(Color.BLACK);
				g2d.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			}
		}

		g2d.setColor(Color.BLUE);
		int margin = 5;
		g2d.fillOval(drohneX * TILE_SIZE + margin, drohneY * TILE_SIZE + margin,
				TILE_SIZE - 2 * margin, TILE_SIZE - 2 * margin);

		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial", Font.BOLD, 16));
		g2d.drawString("layer (Z): " + drohneZ, 10, 20);
	}


	@Override
	public void xChanged(int newX) {
		actionQueue.add(() -> drohneX = newX);
	}

	@Override
	public void yChanged(int newY) {
		actionQueue.add(() -> drohneY = newY);
	}

	@Override
	public void zChanged(int newZ) {
		actionQueue.add(() -> drohneZ = newZ);
	}
}