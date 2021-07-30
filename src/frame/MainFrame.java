package frame;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;



public class MainFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public MainFrame() {
		this.setTitle("Tank War");
		setSize(800, 600);
		this.setResizable(false);
		Toolkit tool=Toolkit.getDefaultToolkit();
		Dimension d=tool.getScreenSize();
		setLocation((d.width - getWidth()) / 2, (d.height - getHeight()) / 2);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addListener();
		setPanel(new LoginPanel(this));
		this.setVisible(true);
	}

	private void addListener() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int close=JOptionPane.showConfirmDialog(MainFrame.this,"Do you want to leave the game?","HINT!",JOptionPane.YES_NO_OPTION);
				if(close==JOptionPane.YES_OPTION)
					System.exit(0);
			}
		});
		
	}
	public void setPanel(JPanel panel) {
		Container c=this.getContentPane();
		c.removeAll();
		c.add(panel);
		c.validate();
	}
}
