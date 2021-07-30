package Model.Wall;

import Model.VisibleImage;

public abstract class Wall extends VisibleImage{
	private boolean alive=true;
	public Wall(int x, int y, String url) {
		super(x, y, url);
		// TODO Auto-generated constructor stub
	}
	public boolean isAlive() {
		return alive;
	}
	public void setAlive(boolean alive) {
		this.alive=alive;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Wall) {
			Wall w=(Wall) obj;
			if(w.x==x && w.y==y)
			return true;
		}
		return super.equals(obj);
	}
}

