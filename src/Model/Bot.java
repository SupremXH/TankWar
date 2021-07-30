package Model;

import java.awt.Rectangle;
import java.util.List;
import java.util.Random;

import Model.Wall.River;
import Model.Wall.Brick;
import Model.Wall.Iron;
import Model.Wall.Grass;
import Model.Wall.Wall;
import Type.Direction;
import Type.TankType;
import frame.GamePanel;
import util.ImageUtil;

public class Bot extends Tank {
	private Random ran = new Random();
	private Direction dir;
	private int fresh = GamePanel.FRESH;
	private int MoveTimer = 0;
	private int facebricktime=0;
	public Bot(int x, int y, GamePanel gamepanel, TankType type) {
		super(x, y, ImageUtil.Bot_down, gamepanel, type);
		dir=randomDirection();
		setAttackCoolDownTime(1000-fresh*2*Level.preLevel());
	}
	
	
	public void go() {
		if(isAttackCoolDown()) {
			attack();
		}
		if(MoveTimer>=3000) {
			dir=randomDirection();
			MoveTimer=0;
		}else {
			MoveTimer+=fresh;
		}
		
		switch(dir) {
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
	protected boolean moveToBorder(int x,int y) {
		if(x<=0 || x >= gamepanel.getWidth() - width || y<=0 || y >= gamepanel.getHeight() - height) {
			dir=randomDirection();
			return true;
		}
		return false;
	}
	boolean hitWall(int x,int y) {
		Rectangle next=new Rectangle(x,y,width,height);
		List<Wall> walls=gamepanel.getWalls();
		for(int i=0;i<walls.size();i++) {
			Wall w=walls.get(i);
			if(w instanceof Grass) {
				continue;
			}else if(w.hit(next)) {
				if(w instanceof Iron) {
					dir=randomDirection();
				}
				if((w instanceof Brick) || (w instanceof River)) {
					facebricktime+=fresh;
					if(facebricktime>1000) {
						dir=randomDirection();
						facebricktime=0;
					}
				}
				return true;
			}
		}
		return false;
	}
	 boolean hitTank(int x,int y) {
		Rectangle next=new Rectangle(x,y,width,height);
		List<Tank> tanks=gamepanel.getTanks();
		for(int i=0;i<tanks.size();i++) {
			Tank t=tanks.get(i);
			if(!this.equals(t)) {
				if(t.isAlive()&& t.hit(next)) {
					if(t instanceof Bot){
						dir=randomDirection();
					}
					return true;
				}
			}
		}
		return false;
	}
	private Direction randomDirection() {
		int r=ran.nextInt(4);
		switch(r) {
		case 0:
			return Direction.UP;
		case 1:
			return Direction.DOWN;
		case 2:
			return Direction.LEFT;
		default:
			return Direction.RIGHT;
		}
	}

	@Override
	public void attack() {
		int r=ran.nextInt(100);
		if(r<6+10*Level.preLevel()) {
			super.attack();
		}
	}
}
