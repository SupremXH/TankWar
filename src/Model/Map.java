package Model;

import java.util.ArrayList;
import java.util.List;

import Model.Wall.Brick;
import Model.Wall.Wall;
import util.MapIO;

public class Map {
	private static List<Wall> walls = new ArrayList<>();

	private Map() {

	}

	public static Map getMap(String level) {
		walls.clear();
		walls.addAll(MapIO.readMap(level));

		for (int i = 347; i <= 407; i += 20) {
			for (int j = 512; j <= 572; j += 20) {
				if (i >= 367 && i <= 387 && j >= 532)
					continue;
				else
					walls.add(new Brick(i, j));
			}
		}
		return new Map();
	}

	public static Map getMap(int level) {
		return getMap(String.valueOf(level));
	}

	public List<Wall> getWalls() {
		return walls;
	}
}
