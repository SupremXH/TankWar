package Model;

import java.io.File;
import java.io.FileNotFoundException;


import util.MapIO;

public class Level {
	private static int nextLevel=1;
	private static int preLevel=-1;
	private static int count;
	static {
		try {
			File f=new File(MapIO.DATA_PATH);
			if(!f.exists()) {
				throw new FileNotFoundException("map file missing");
			}
			File fs[]=f.listFiles();
			count=fs.length;
			if(count==0)
				throw new FileNotFoundException("map file missing");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int nextLevel() {
		preLevel=nextLevel;
		nextLevel++;
		if(nextLevel>count) {
			nextLevel=1;
		}
		return preLevel;
	}
	public static int preLevel() {
		return preLevel;
	}
}
