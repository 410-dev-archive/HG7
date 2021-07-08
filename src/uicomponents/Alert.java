package uicomponents;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import data.ViewDimension;
import main.WindowAllocator;
import main.WindowPositioner;
import uiobjects.GenericWindow;

public class Alert {
	public JPanel alert;
	
	public Alert(String title, String data) {
		ViewDimension alertDimension = new ViewDimension();
		alertDimension.HEIGHT = 70;
		alertDimension.WIDTH = 400;
		alertDimension = WindowPositioner.getNewWindowPositionOfCenter(WindowAllocator.getScreenDimension(), alertDimension);
		alert = new GenericWindow(alertDimension.X, alertDimension.Y, alertDimension.WIDTH, alertDimension.HEIGHT, title);
		
		JLabel content = new JLabel();
		content.setText(data);
		
		JLabel titleText = new JLabel();
		titleText.setText(title);
		
		alert.add(content);
		alert.add(titleText);
		alert.setBackground(Color.white);
		
		
	}
}
