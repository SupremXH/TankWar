package Model;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import Model.Wall.Grass;
import Model.Wall.Wall;
import Type.Direction;
import Type.TankType;
import frame.GamePanel;
import util.ImageUtil;

public class Tank extends VisibleImage{
	Direction direction;
	protected boolean alive=true;
	protected int speed=3;
	private boolean attackCoolDown =true;
	private int attackCoolDownTime=500;
	TankType type;
	private String upImage;
	private String leftImage;
	private String rightImage;
	private String downImage;
	GamePanel gamepanel;
	public Tank(int x,int y, String url,GamePanel gamepanel,TankType type) {
		super(x,y,url);
		this.gamepanel=gamepanel;
		this.type=type;
		direction=Direction.UP;
		switch(type) {
		case player1:
			upImage=ImageUtil.Player1_up;
			leftImage=ImageUtil.Player1_left;
			rightImage=ImageUtil.Player1_right;
			downImage=ImageUtil.Player1_down;
			break;
		case player2:
			upImage=ImageUtil.Player2_up;
			leftImage=ImageUtil.Player2_left;
			rightImage=ImageUtil.Player2_right;
			downImage=ImageUtil.Player2_down;
			break;
		case bot:
			upImage=ImageUtil.Bot_up;
			leftImage=ImageUtil.Bot_left;
			rightImage=ImageUtil.Bot_right;
			downImage=ImageUtil.Bot_down;
			break;
		}
	}
	public void leftward() {
		if(direction !=Direction.LEFT) {
			setImage(leftImage);
		}
		direction=Direction.LEFT;
		if(!hitWall(x-speed,y) && !hitTank(x-speed,y) &&!moveToBorder(x-speed,y)) 
			x-=speed;
	}
	public void rightward() {
		if(direction!=Direction.RIGHT) {
			setImage(rightImage);
		}
		direction=Direction.RIGHT;
		if(!hitWall(x+speed,y) && !hitTank(x+speed,y) &&!moveToBorder(x+speed,y))
			x+=speed;
	}
	public void upward() {
		if(direction!=Direction.UP) {
			setImage(upImage);
		}
		direction=Direction.UP;
		if(!hitWall(x,y-speed) && !hitTank(x,y-speed) &&!moveToBorder(x,y-speed))
			y-=speed;
	}
	public void downward() {
		if(direction!=Direction.DOWN) {
			setImage(downImage);
		}
		direction=Direction.DOWN;
		if(!hitWall(x,y+speed) && !hitTank(x,y+speed) &&!moveToBorder(x,y+speed))
			y+=speed;
	}
	protected boolean moveToBorder(int x, int y) {
		if(x<0 || x>gamepanel.getWidth()-width || y<0 || y>gamepanel.getHeight()-height)
			return true;
		return false;
	}
	boolean hitWall(int x,int y) {
		Rectangle next=new Rectangle(x,y,width,height);
		List<Wall> walls=gamepanel.getWalls();
		for(int i=0;i<walls.size();i++) {
			Wall w=walls.get(i);
			if(w instanceof Grass) {
				continue;
			}else if(w.hit(next))
				return true;
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
					return true;
				}
			}
		}
		return false;
	}

	public boolean isAlive() {
		return alive;
	}
	public void setAlive(boolean alive) {
		this.alive=alive;
	}
	
	private Point BulletHead() {
		Point p=new Point();
		switch(direction) {
		case UP:
			p.x=x+width/2;
			p.y=y;
			break;
		case DOWN:
			p.x=x+width/2;
			p.y=y+height;
			break;
		case LEFT:
			p.x=x;
			p.y=y+height/2;
			break;
		case RIGHT:
			p.x=x+width;
			p.y=y+height/2;
			break;
		default:
			p=null;
		}
		return p;
	}
	public void attack() {
		if(attackCoolDown) {
			Point p=BulletHead();
			Bullet b=new Bullet(p.x-Bullet.LENGTH/2,p.y-Bullet.LENGTH/2,direction,gamepanel,type);
			gamepanel.addBullet(b);
			new AttackCD().start();
		}
	}
	private class AttackCD extends Thread{
		public void run() {
			attackCoolDown=false;
			try {
				Thread.sleep(attackCoolDownTime);
			}catch (Exception e) {
				e.printStackTrace();
			}
			attackCoolDown=true;
		}
	}
	public void setAttackCoolDownTime(int act) {
		this.attackCoolDownTime=act;
	}
	public boolean isAttackCoolDown() {
		return attackCoolDown;
	}
}
