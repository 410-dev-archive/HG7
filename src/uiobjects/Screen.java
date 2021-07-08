package uiobjects;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

public class Screen extends JFrame{

	public JLayeredPane layerPane = new JLayeredPane();

	public Screen(int x, int y, int width, int height, String title) {
		super();
		this.setTitle(title);
		this.setBounds(x, y, width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
		layerPane.setOpaque(false);
		this.add(layerPane);
	}

}
