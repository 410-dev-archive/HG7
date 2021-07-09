package main;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import data.ViewDimension;
import uiobjects.GenericWindow;
import uiobjects.Screen;

public class WindowAllocator {
	private static long lastPid = 0;
	
	private static int zIndex = 2;
	
	private static Screen mainScreen;
	private static JLayeredPane layer;
	
	private static ArrayList<Component> innerContents = new ArrayList<>();
	private static ArrayList<Long> innerContentsPID = new ArrayList<>();
	
	public static boolean mainWindowGenerated = false;
	
	public static void createObjects(int x, int y, int screenWidth, int screenHeight) {
		mainScreen = new Screen(x, y, screenWidth, screenHeight, "HG7Server");
		layer = new JLayeredPane();
		layer.setBounds(x, y, screenWidth, screenHeight);
		mainScreen.setLayout(null);
		mainScreen.add(layer);
		mainWindowGenerated = true;
		Logger.info("Created main screen.");
	}
	
	
	public static long getNewPID() {
		return lastPid++;
	}
	
	public static long getPendingPID() {
		return lastPid;
	}
	
	public static long addWindow(Component window) {
		return addWindow(window, zIndex++);
	}
	
	public static long addWindow(Component window, int index) {
		Logger.info("Adding new window to View...");
		mainScreen.remove(layer);
		layer.add(window, Integer.valueOf(index));
		innerContents.add(window);
		long newPID = getNewPID();
		Logger.info("Got PID: " + newPID);
		innerContentsPID.add(newPID);
		mainScreen.add(layer);
		refresh();
		Logger.info("mainScreen is updated.");
		return newPID;
	}
	
	public static ViewDimension getScreenDimension() {
		ViewDimension vd = new ViewDimension();
		vd.X = mainScreen.getX();
		vd.Y = mainScreen.getY();
		vd.WIDTH = mainScreen.getWidth();
		vd.HEIGHT = mainScreen.getHeight();
		Logger.info("Current mainScreen Dimension: " + vd.toString());
		return vd;
	}
	
	public static void removeWindow(GenericWindow window) {
		innerContentsPID.remove(innerContents.indexOf(window));
		innerContents.remove(innerContents.indexOf(window));
		layer.remove(window);
		refresh();
	}
	
	public static void removeWindow(long pid) {
		layer.remove(innerContents.get(innerContentsPID.indexOf(pid)));
		innerContents.remove(innerContentsPID.indexOf(pid));
		innerContentsPID.remove(pid);
		refresh();
	}
	
	public static void refresh() {
		if (mainScreen != null) SwingUtilities.updateComponentTreeUI(mainScreen);
	}
	
	public static void closeFrame() {
		innerContents.clear();
		innerContentsPID.clear();
		layer = null;
		mainScreen.setVisible(false);
		mainScreen = null;
		mainWindowGenerated = false;
	}
	
	public static void show() {
		mainScreen.setVisible(false);
		mainScreen.setUndecorated(true);
		mainScreen.setVisible(true);
	}
	
	public static ArrayList<Long> getAllPID() {
		return innerContentsPID;
	}

	public static void dispatchEvent(int eventID) {
		mainScreen.dispatchEvent(new WindowEvent(mainScreen, eventID));
	}
}
