package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import Model.Wall.Brick;
import Model.Wall.Grass;
import Model.Wall.Iron;
import Model.Wall.River;
import Model.Wall.Wall;
import Type.WallType;

public class MapIO {
	public final static String DATA_PATH = "map/data/";
	public final static String IMAGE_PATH = "map/image";
	public final static String DATA_SUFFIX = ".map";
	public final static String IMAGE_SUFFIX = ".jpg";

	public static List<Wall> readMap(String mapName) {
		File file = new File(DATA_PATH + mapName + DATA_SUFFIX);
		return readMap(file);
	}

	private static List<Wall> readMap(File file) {
		Properties pro = new Properties();
		List<Wall> walls = new ArrayList<>();
		try {
			pro.load(new FileInputStream(file));
			String brickStr = (String) pro.get(WallType.brick.name());
			String grassStr = (String) pro.get(WallType.grass.name());
			String riverStr = (String) pro.get(WallType.river.name());
			String ironStr = (String) pro.get(WallType.iron.name());
			if (brickStr != null) {
				walls.addAll(readWall(brickStr,WallType.brick));
			}
			if(grassStr!=null)
				walls.addAll(readWall(grassStr,WallType.grass));
			if(riverStr!=null)
				walls.addAll(readWall(riverStr,WallType.river));
			if(ironStr!=null) {
				walls.addAll(readWall(ironStr,WallType.iron));
			}
			return walls;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static List<Wall> readWall(String data, WallType type) {
		String walls[]=data.split(";");
		Wall wall;
		List<Wall> w=new LinkedList<>();
		switch(type) {
		case brick:
			for(String s:walls) {
				String axes[]=s.split(",");
				wall=new Brick(Integer.parseInt(axes[0]),Integer.parseInt((axes[1])));
				w.add(wall);
			}
			break;
		case grass:
			for(String s:walls) {
				String axes[]=s.split(",");
				wall=new Grass(Integer.parseInt(axes[0]),Integer.parseInt((axes[1])));
				w.add(wall);
			}
			break;
		case river:
			for(String s:walls) {
				String axes[]=s.split(",");
				wall=new River(Integer.parseInt(axes[0]),Integer.parseInt((axes[1])));
				w.add(wall);
			}
			break;
		case iron:
			for(String s:walls) {
				String axes[]=s.split(",");
				wall=new Iron(Integer.parseInt(axes[0]),Integer.parseInt((axes[1])));
				w.add(wall);
			}
			break;
		default:
			break;
		}
		
		return w;
	}
}
