package frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;


import Type.GameType;

public class LevelPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MainFrame frame;
	private int level;
	private GameType type;
	private String levelStr;
	private String ready="";
	public LevelPanel(int level, MainFrame frame, GameType type) {
		this.frame=frame;
		this.level=level;
		this.type=type;
		levelStr="Level "+level;
	    setFocusable(true);
	    requestFocus();
	    requestFocusInWindow();
		Thread t=new LevelPanelThread();
		t.start();
	}
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setFont(new Font("Consolas", Font.BOLD, 50));
		g.setColor(Color.BLACK);
		g.drawString(levelStr, 260, 300);
		g.setColor(Color.RED);
		g.drawString(ready, 270, 400);
	}
	private void gotoGamePanel() {
		frame.setPanel(new GamePanel(frame,level,type));
	}
	private class LevelPanelThread extends Thread{
		public void run() {
			for(int i=0;i<6;i++) {
				if(i%2==0)
					levelStr="Level "+level;
				else
					levelStr="";
				if(i==4)
					ready="Ready !";
				repaint();
				try {
					Thread.sleep(500);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			gotoGamePanel();
		}
	}
}

