package main;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import data.ViewDimension;
import uiobjects.GenericWindow;
import uiobjects.Screen;

public class WindowAllocator {
	private static long lastPid = 0;
	
	private static Screen mainScreen;
	private static Container contentPane;
	
	private static ArrayList<Component> innerContents = new ArrayList<>();
	private static ArrayList<Long> innerContentsPID = new ArrayList<>();
	
	
	public static void createObjects(int x, int y, int screenWidth, int screenHeight) {
		mainScreen = new Screen(x, y, screenWidth, screenHeight, "HG7Server");
		contentPane = mainScreen.getContentPane();
		Logger.info("Created main screen.");
	}
	
	
	public static long getNewPID() {
		return lastPid++;
	}
	
	public static long addWindow(Component window) {
		return addWindow(window, 100);
	}
	
	public static long addWindow(Component window, int index) {
		Logger.info("Adding new window to View...");
		mainScreen.layerPane.add(window, index);
		contentPane.add(window);
		innerContents.add(window);
		long newPID = getNewPID();
		Logger.info("Got PID: " + newPID);
		innerContentsPID.add(newPID);
		SwingUtilities.updateComponentTreeUI(mainScreen);
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
		contentPane.remove(window);
	}
	
	public static void removeWindow(long pid) {
		innerContentsPID.remove(innerContentsPID.indexOf(pid));
		contentPane.remove(innerContents.get(innerContentsPID.indexOf(pid)));
		innerContents.remove(innerContentsPID.indexOf(pid));
	}
	
	public static void refresh() {
		if (mainScreen != null) SwingUtilities.updateComponentTreeUI(mainScreen);
	}
	
	public static void closeFrame() {
		innerContents.clear();
		innerContentsPID.clear();
		contentPane = null;
		mainScreen.setVisible(false);
		mainScreen = null;
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
