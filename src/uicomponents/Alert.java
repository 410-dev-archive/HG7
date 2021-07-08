package uicomponents;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import IO.SocketIO;
import data.AlertData;
import data.ButtonData;
import data.ViewDimension;
import main.WindowAllocator;
import main.WindowPositioner;
import uiobjects.GenericWindow;

public class Alert {
	public JPanel alert;
	public String id;
	
	public Alert(AlertData data) {
		ViewDimension alertDimension = new ViewDimension();
		alertDimension.HEIGHT = data.height;
		alertDimension.WIDTH = data.width;
		alertDimension = WindowPositioner.getNewWindowPositionOfCenter(WindowAllocator.getScreenDimension(), alertDimension);
		alert = new GenericWindow(alertDimension.X, alertDimension.Y, alertDimension.WIDTH, alertDimension.HEIGHT, data.title);
		id = data.parentID;
		
		JLabel content = new JLabel();
		content.setText(data.text);
		
		JLabel titleText = new JLabel();
		titleText.setText(data.title);
		
		
		for(ButtonData buttonData : data.buttons) {
			JButton button = new JButton();
			button.setText(buttonData.text);
			button.addActionListener(new ActionListener() {
			    @Override
			    public void actionPerformed(ActionEvent e) {
			        SocketIO.sendMessageToClient("ButtonEvent_" + data.parentID + ":" + buttonData.onClickCommand);
			    }
			});
		}
		
		alert.add(content);
		alert.add(titleText);
		alert.setBackground(Color.white);
		
		
	}
}
