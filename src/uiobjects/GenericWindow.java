package uiobjects;


import javax.swing.JPanel;

import main.Logger;

public class GenericWindow extends JPanel {

	public String windowName = "";

	public GenericWindow(int x, int y, int width, int height, String title) {
		super();
		
		Logger.info("GenericWindow object is created with name: " + title);
		Logger.info("Width: " + width);
		Logger.info("Height: " + height);
		Logger.info("X-Pos: " + x);
		Logger.info("Y-Pos: " + y);
		
		this.setBounds(x, y, width, height);
		windowName = title;
	}

}
