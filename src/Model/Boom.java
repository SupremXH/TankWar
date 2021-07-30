package Model;

import java.awt.Graphics2D;

import frame.GamePanel;
import util.ImageUtil;

public class Boom extends VisibleImage {
	private int timer = 0;
	private int fresh = GamePanel.FRESH;
	private boolean alive = true;

	public Boom(int x, int y) {
		super(x, y, ImageUtil.Boom);
	}
	public void show(Graphics2D g2) {
		if(timer>=500)
			alive=false;
		else {
			g2.drawImage(getImage(), x, y, null);
			timer+=fresh;
		}
	}
	public boolean isAlive() {
		return alive;
	}
}

