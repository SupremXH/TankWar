package frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ActionMap;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import Model.Base;
import Model.Boom;
import Model.Bot;
import Model.Bullet;
import Model.Level;
import Model.Map;
import Model.Tank;
import Model.Wall.Wall;
import Type.GameType;
import Type.TankType;
import util.ImageUtil;

public class GamePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	public static final int FRESH = 20;
	private MainFrame frame;
	private Graphics2D g2;
	private GameType type;
	private int level;
	private volatile boolean finish = false;
	private boolean w_key, s_key, a_key, d_key, j_key, up_key, down_key, left_key, right_key, num2_key;
	private volatile List<Tank> allTanks;
	private int[] initpoint = { 10, 367, 740 };
	private List<Wall> walls;
	private BufferedImage image;
	private List<Tank> playerTanks;
	private List<Tank> botTanks;
	private Tank play1, play2;
	private List<Bullet> bullets;
	private int botCount;
	private int botReadyCount;
	private int botSurplusCount;
	private List<Boom> boomImage;
	private Base base;
	private int createBotTimer = 0;
	private int botMaxInMap = 6;
	private Random ran = new Random();
	private Tank survivor;

	public GamePanel(MainFrame frame, int level, GameType type) {
		this.frame = frame;
		this.level = level;
		this.type = type;
		botCount = 3 + 2*level;
		botReadyCount = botCount;
		botSurplusCount = botCount;
		init();
		Thread t = new FreshThread();
		t.start();
		addkeyBindings();
	}

	private void init() {
		botTanks = new ArrayList<>();
		bullets = new ArrayList<>();
		walls = new ArrayList<>();
		allTanks = new ArrayList<>();
		boomImage = new ArrayList<>();
		image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_BGR);
		g2 = image.createGraphics();

		playerTanks = new ArrayList<>();
		play1 = new Tank(278, 525, ImageUtil.Player1_up, this, TankType.player1);
		playerTanks.add(play1);
		if (type == GameType.two_player) {
			play2 = new Tank(448, 525, ImageUtil.Player2_up, this, TankType.player2);
			playerTanks.add(play2);
		}

		for (int i : initpoint)
			botTanks.add(new Bot(i, 1, this, TankType.bot));
		botReadyCount -= 3;
		allTanks.addAll(playerTanks);
		allTanks.addAll(botTanks);
		base = new Base(367, 532);
		initWalls();
	}

	private void initWalls() {
		Map map = Map.getMap(level);
		walls.addAll(map.getWalls());

	}

	public void paint(Graphics g) {
		paintTankMove();
		createBot();
		paintImage();
		g.drawImage(image, 0, 0, this);
	}

	private void paintImage() {
		g2.setColor(Color.black);
		g2.fillRect(0, 0, image.getWidth(), image.getHeight());
		paintWalls();
		paintPlayerTanks();
		paintBullets();
		paintBot();
		paintBoom();
		paintBotCount();
		allTanks.addAll(playerTanks);
		allTanks.addAll(botTanks);

		if (botSurplusCount == 0) {
			stopThread();
			paintBotCount();
			g2.setFont(new Font("Helvetica", Font.BOLD, 50));
			g2.setColor(Color.red);
			g2.drawString("Win !", 350, 300);
			gotoNextLevel();
		}
		if (type == GameType.one_player) {
			if (!play1.isAlive()) {
				stopThread();
				boomImage.add(new Boom(play1.x, play1.y));
				paintBoom();
				paintGameOver();
				gotoPreviousLevel();
			}
		} else {
			if (play1.isAlive() && !play2.isAlive()) {
				survivor = play1;
			} else if (!play1.isAlive() && play2.isAlive()) {
				survivor = play2;
			} else if (!play1.isAlive() && !play2.isAlive()) {
				stopThread();
				boomImage.add(new Boom(survivor.x, survivor.y));
				paintBoom();
				paintGameOver();
				gotoPreviousLevel();
			}
		}
		if (!base.isAlive()) {
			stopThread();
			paintGameOver();
			base.setImage(ImageUtil.Break_base);
			gotoPreviousLevel();
		}
		g2.drawImage(base.getImage(), base.x, base.y, this);
	}

	private void gotoPreviousLevel() {
		Thread jump = new JumpPageThread(Level.preLevel());
		jump.start();
	}

	private void gotoNextLevel() {
		Thread jump = new JumpPageThread(Level.nextLevel());
		jump.start();
	}

	private void paintGameOver() {
		g2.setFont(new Font("seris", Font.BOLD, 50));
		g2.setColor(Color.red);
		g2.drawString("Game Over!", 250, 300);
	}

	private void stopThread() {
		finish = true;
	}

	private void paintBotCount() {
		g2.setColor(Color.blue);
		g2.drawString("Tanks: " + this.botSurplusCount, 337, 15);
	}

	private void paintBoom() {
		for (int i = 0; i < boomImage.size(); i++) {
			Boom b = boomImage.get(i);
			if (b.isAlive())
				b.show(g2);
			else {
				boomImage.remove(i);
				i--;
			}
		}
	}

	private void createBot() {
		createBotTimer += FRESH;
		if (botTanks.size() < botMaxInMap && botReadyCount > 0 && createBotTimer >= 4000) {
			int index = ran.nextInt(3);
			Rectangle bornRect = new Rectangle(initpoint[index], 1, 35, 35);
			for (int i = 0; i < allTanks.size(); i++) {
				Tank t = allTanks.get(i);
				if (t.isAlive() && t.hit(bornRect))
					return;
			}
			botTanks.add(new Bot(initpoint[index], 1, GamePanel.this, TankType.bot));
			botReadyCount--;
			createBotTimer = 0;
		}
	}

	private void paintBullets() {
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			if (b.isAlive()) {
				b.move();
				b.hitBase();
				b.hitTank();
				b.hitWall();
				g2.drawImage(b.getImage(), b.x, b.y, this);
			} else {
				bullets.remove(i);
				i--;
			}
		}
	}

	public void paintTankMove() {
		if (this.a_key)
			play1.leftward();
		if (this.w_key)
			play1.upward();
		if (this.s_key)
			play1.downward();
		if (this.d_key)
			play1.rightward();

		if (this.j_key)
			play1.attack();
		if (this.type == GameType.two_player) {
			if (this.num2_key)
				play2.attack();
			if (this.up_key)
				play2.upward();
			if (this.down_key)
				play2.downward();
			if (this.left_key)
				play2.leftward();
			if (this.right_key)
				play2.rightward();
		}
	}

	private void paintPlayerTanks() {
		for (int i = 0; i < playerTanks.size(); i++) {
			Tank t = playerTanks.get(i);
			if (t.isAlive()) {
				g2.drawImage(t.getImage(), t.x, t.y, this);
			} else {
				playerTanks.remove(i);
				i--;
			}
		}
	}

	private void paintBot() {
		for (int i = 0; i < botTanks.size(); i++) {
			Bot b = (Bot) botTanks.get(i);
			if (b.isAlive()) {
				b.go();
				g2.drawImage(b.getImage(), b.x, b.y, this);
			} else {
				botTanks.remove(i);
				i--;
				boomImage.add(new Boom(b.x, b.y));
				botSurplusCount--;
			}
		}
	}

	private void paintWalls() {
		for (int i = 0; i < walls.size(); i++) {
			Wall w = walls.get(i);
			if (w.isAlive()) {
				g2.drawImage(w.getImage(), w.x, w.y, this);
			} else {
				walls.remove(i);
				i--;
			}
		}
	}

	private class FreshThread extends Thread {
		public void run() {
			while (!finish) {
				repaint();
				try {
					Thread.sleep(FRESH);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Base getBase() {
		return base;
	}

	public List<Wall> getWalls() {
		return walls;
	}

	public List<Tank> getTanks() {
		return allTanks;
	}

	public void addBullet(Bullet b) {
		bullets.add(b);
	}

	private class JumpPageThread extends Thread {
		int level;

		public JumpPageThread(int level) {
			this.level = level;
		}

		public void run() {
			try {
				Thread.sleep(1000);
				frame.setPanel(new LevelPanel(level, frame, type));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void addkeyBindings() {
		InputMap test1 = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap test2 = getActionMap();
		test1.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0, false), "y_pressed");
		test2.put("y_pressed", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				botSurplusCount = 0;
			}
		});
		
		InputMap wi = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap wa = getActionMap();
		wi.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "w_pressed");
		wi.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "w_released");
		wa.put("w_pressed", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				w_key = true;
				s_key = false;
				a_key = false;
				d_key = false;
			}
		});
		wa.put("w_released", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				w_key = false;
			}
		});
		InputMap si = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap sa = getActionMap();
		si.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "s_pressed");
		si.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "s_released");
		sa.put("s_pressed", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				w_key = false;
				s_key = true;
				a_key = false;
				d_key = false;
			}
		});
		sa.put("s_released", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				s_key = false;
			}
		});
		InputMap ai = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap aa = getActionMap();
		ai.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "a_pressed");
		ai.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "a_released");
		aa.put("a_pressed", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				w_key = false;
				s_key = false;
				a_key = true;
				d_key = false;
			}
		});
		aa.put("a_released", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				a_key = false;
			}
		});
		InputMap di = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap da = getActionMap();
		di.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "d_pressed");
		di.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "d_released");
		da.put("d_pressed", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				w_key = false;
				s_key = false;
				a_key = false;
				d_key = true;
			}
		});
		da.put("d_released", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				d_key = false;
			}
		});
		InputMap ji = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap ja = getActionMap();
		ji.put(KeyStroke.getKeyStroke(KeyEvent.VK_J, 0, false), "j_pressed");
		ji.put(KeyStroke.getKeyStroke(KeyEvent.VK_J, 0, true), "j_released");
		ja.put("j_pressed", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				botSurplusCount = 0;
				j_key = true;
			}
		});
		ja.put("j_released", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				j_key = false;
			}
		});
		InputMap upi = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap upa = getActionMap();
		upi.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up_pressed");
		upi.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "up_released");
		upa.put("up_pressed", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				up_key = true;
				down_key = false;
				right_key = false;
				left_key = false;
			}
		});
		upa.put("up_released", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				up_key = false;
			}
		});
		InputMap downi = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap downa = getActionMap();
		downi.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "down_pressed");
		downi.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "down_released");
		downa.put("down_pressed", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				up_key = false;
				down_key = false;
				right_key = false;
				left_key = false;
			}
		});
		downa.put("down_released", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				down_key = false;
			}
		});
		InputMap li = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap la = getActionMap();
		li.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left_pressed");
		li.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "left_released");
		la.put("left_pressed", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				up_key = false;
				down_key = false;
				right_key = false;
				left_key = true;
			}
		});
		la.put("left_released", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				left_key = false;
			}
		});
		InputMap ri = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap ra = getActionMap();
		ri.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right_pressed");
		ri.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "right_released");
		ra.put("right_pressed", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				up_key = false;
				down_key = false;
				right_key = true;
				left_key = false;
			}
		});
		ra.put("right_released", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				right_key = false;
			}
		});
		InputMap ni = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap na = getActionMap();
		ni.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2,0,false), "num2_pressed");
		ni.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, 0, true), "num2_released");
		na.put("num2_pressed", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				num2_key = true;
			}
		});
		na.put("nums2_released", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				num2_key = false;
			}
		});
	}

}
