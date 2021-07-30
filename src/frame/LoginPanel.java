package frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

import Model.Level;
import Type.GameType;
import util.ImageUtil;

public class LoginPanel extends JPanel implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MainFrame frame;
	private Image background;
	private JButton one_player;
	private JButton two_player;
	private GameType type;

	public LoginPanel(MainFrame frame) {

		this.frame = frame;
		addButton();
		try {
			background = ImageIO.read(new File(ImageUtil.Login_background));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addButton() {
		setLayout(null);
		one_player = new JButton("1 PLAYER");
		two_player = new JButton("2 PLAYER");
		one_player.setBounds(350, 400, 200, 100);
		one_player.setContentAreaFilled(false);
		one_player.setFocusPainted(false);
		one_player.setBorderPainted(false);
		one_player.setForeground(Color.red);
		one_player.setFont(new Font("SansSerif", Font.ITALIC, 20));
		one_player.addMouseListener(this);
		this.add(one_player);

		two_player.setBounds(500, 400, 200, 100);
		two_player.setContentAreaFilled(false);
		two_player.setFocusPainted(false);
		two_player.setBorderPainted(false);
		two_player.setForeground(Color.red);
		two_player.setFont(new Font("SansSerif", Font.ITALIC, 20));
		two_player.addMouseListener(this);
		this.add(two_player);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
	}

	private void gotoGamePanel() {
		System.out.println("game start");
		
		frame.setPanel(new LevelPanel(Level.nextLevel(),frame, type));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JButton source = (JButton) e.getSource();

		if (source.equals(one_player)) {
			type = GameType.one_player;
			System.out.println("one player");
		} else {
			type = GameType.two_player;
			System.out.println("two player");
		}
		gotoGamePanel();
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

}
