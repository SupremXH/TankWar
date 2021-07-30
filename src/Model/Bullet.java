package Model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import Model.Wall.Brick;
import Model.Wall.Iron;
import Model.Wall.Wall;
import Type.Direction;
import Type.TankType;
import frame.GamePanel;

public class Bullet extends VisibleImage {
	Direction direction;
	static final int LENGTH = 8;
	private int speed = 7;
	private boolean alive = true;
	TankType type;
	Color color = Color.orange;
	private GamePanel gamepanel;

	public Bullet(int x, int y, Direction direction, GamePanel gamepanel, TankType type) {
		super(x, y, LENGTH, LENGTH);
		this.direction = direction;
		this.type = type;
		this.gamepanel = gamepanel;
		init();
	}

	private void init() {
		Graphics g = image.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, LENGTH, LENGTH);
		g.setColor(color);
		g.fillOval(0, 0, LENGTH, LENGTH);
		g.setColor(Color.black);
		g.drawOval(0, 0, LENGTH - 1, LENGTH - 1);
	}

	public void move() {
		switch (direction) {
		case UP:
			upward();
			break;
		case DOWN:
			downward();
			break;
		case LEFT:
			leftward();
			break;
		case RIGHT:
			rightward();
			break;
		}
	}

	private void upward() {
		y -= speed;
		moveToBorder();
	}

	private void downward() {
		y += speed;
		moveToBorder();
	}

	private void leftward() {
		x -= speed;
		moveToBorder();
	}

	private void rightward() {
		x += speed;
		moveToBorder();
	}

	public void hitTank() {
		List<Tank> tanks = gamepanel.getTanks();
		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
			if (t.isAlive() && this.hit(t)) {
				switch (type) {
				case player1:
				case player2:
					if (t instanceof Bot) {
						alive = false;
						t.setAlive(false);
					} else if (t instanceof Tank) {
						alive = false;
					} 
					break;
				case bot:
					if (t instanceof Bot) {
						alive = false;
					} else if (t instanceof Tank) {
						alive = false;
						t.setAlive(false);
					}
					break;
				default:
					alive = false;
					t.setAlive(false);
				}
			}
		}
	}

	public void hitBase() {
		Base b=gamepanel.getBase();
		if(this.hit(b)) {
			alive=false;
			b.setAlive(false);
		}
	}

	public void hitWall() {
		List<Wall> walls = gamepanel.getWalls();
		for (int i = 0; i < walls.size(); i++) {
			Wall w = walls.get(i);
			if (this.hit(w)) {
				if (w instanceof Brick) {
					alive = false;
					w.setAlive(false);
				}
				if (w instanceof Iron)
					alive = false;
			}
		}
	}

	private void moveToBorder() {
		if (x < 0 || x > gamepanel.getWidth() - getWidth() || y < 0 || y > gamepanel.getHeight() - getHeight())
			alive = false;
	}

	public boolean isAlive() {
		return alive;
	}
}
