package uicomponents;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.json.simple.JSONObject;

import IO.FileIO;
import IO.SocketIO;
import main.Logger;
import main.Main;
import uiobjects.GenericWindow;

public class Dock {
	
	final float widthRatio = 0.8f;
	final float heightRatio = 0.1f;
	final float iconRatio = 0.8f;
	
	private ArrayList<String> itemID = new ArrayList<>();
	private ArrayList<DockElement> items = new ArrayList<>();
	
	public String DockItemsDatabaseLocation = "/Users/hoyounsong/Desktop/elements/";
	public String ID = DockItemsDatabaseLocation + "id.data";
	
	public final Color dockColor = Color.white;
	
	public JPanel dock;
	
	public Dock(int parentScreenX, int parentScreenY, int parentScreenWidth, int parentScreenHeight, String DockItemsDBLocation) throws Exception {
		
		DockItemsDatabaseLocation = DockItemsDBLocation;
		ID = DockItemsDatabaseLocation + "id.data";
		
		final int dockWidth = (int) (parentScreenWidth * widthRatio);
		final int dockHeight = (int) (parentScreenHeight * heightRatio);
		final int dockX = (parentScreenWidth - dockWidth) / 2;
		final int dockY = (parentScreenHeight - dockHeight);
		
		dock = new GenericWindow(dockX, dockY, dockWidth, dockHeight, "Dock");
		
		String[] ids = FileIO.readString(ID).split("\n");
		
		for(int i = 0; i < ids.length; i++) {
			Logger.info("Adding element: " + ids[i]);
			itemID.add(ids[i]);
		}
		
		for (int i = 0; i < itemID.size(); i++) {
			try {
				DockElement item = new DockElement(itemID.get(i), DockItemsDatabaseLocation + "bundles/", (int) (dockHeight * iconRatio), dockColor);
				items.add(item);
			}catch(Exception e) {
				throw e;
			}
		}
		
		for(DockElement item : items) {
			dock.add(item);
		}
		
		dock.setBackground(dockColor);
	}

}

class DockElement extends JPanel {
	
	private final String itemID;
	private final String itemIconPath;
	private final String itemExecutionCommand;
	private final String itemRealPath;
	
	private String loadFailedIcon = "";
	private BufferedImage image;

    public DockElement(String id, String dbLocation, int iconDimension, Color dockBackground) throws Exception {
    	
    	Logger.info("Adding dock element data...");
    	itemID = id;
    	itemRealPath = dbLocation + id + ".hxgb";
    	itemIconPath = itemRealPath + "/icon.png";
    	itemExecutionCommand = FileIO.readString(itemRealPath + "/exec.data");
    	
    	try {
    		Logger.info("Loading icon: " + itemIconPath);
    	    image = ImageIO.read(new File(itemIconPath));
    	} catch (Exception ex) {
    		Logger.error("Failed loading icon: ");
    		ex.printStackTrace();
    		try {
    			Logger.info("Loading dummy icon: " + loadFailedIcon);
    			image = ImageIO.read(new File(loadFailedIcon));
    		}catch(Exception e) {
    			Logger.error("Failed loading dummy icon.");
    			Logger.writeExceptionLog(e, "DockElement.<constructor>");
    			Logger.info("Printing stacktrace...");
    			throw e;
    		}
    	}
    	ImageIcon imicon = new ImageIcon(image);
    	Image img = imicon.getImage();
    	img = img.getScaledInstance(iconDimension, iconDimension, Image.SCALE_SMOOTH);
    	JLabel icon = new JLabel(new ImageIcon(img));
    	icon.setBackground(dockBackground);
    	Logger.info("Icon size: " + iconDimension);
    	icon.setSize(iconDimension, iconDimension);
    	this.add(icon);
    	Logger.info("Icon loaded.");
    	this.setBackground(dockBackground);
    	this.repaint();
    	this.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    			try {
    				
    				JSONObject jsonData = new JSONObject();
    				jsonData.put("Id", itemID);
    				jsonData.put("Command", itemExecutionCommand.replace("\n", ""));
    				
    				Logger.info("[" + itemID + "]@Dock: Clicked. Sending packet to client.");
    				String packet = jsonData.toString();
    				SocketIO.sendMessageToClient(packet);
					Logger.info("[" + itemID + "]@Dock: Successfully sent packet to client. Packet: " + packet);
				} catch (Exception e1) {
					Logger.writeExceptionLog(e1, itemID + " clicked");
				}
    		}
    	});
    }
}
