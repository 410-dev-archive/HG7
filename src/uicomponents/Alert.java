package uicomponents;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.json.simple.JSONObject;

import IO.SocketIO;
import data.AlertData;
import data.ButtonData;
import data.ViewDimension;
import main.Logger;
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
		
		Logger.info("Adding button...");
		for(ButtonData buttonData : data.buttons) {
			JButton button = new JButton();
			Logger.info("Button: " + buttonData.text);
			Logger.info("Button Action: " + buttonData.onClickCommand);
			button.setText(buttonData.text);
			button.addActionListener(new ActionListener() {
			    @Override
			    public void actionPerformed(ActionEvent e) {
			    	JSONObject object = new JSONObject();
			    	object.put("Id", data.parentID);
			    	object.put("Command", buttonData.onClickCommand);
			        SocketIO.sendMessageToClient(object.toString());
			    }
			});
			button.setVisible(true);
			alert.add(button);
		}
		
		alert.add(content);
		alert.add(titleText);
		alert.setBackground(Color.white);
		
		
	}
}
