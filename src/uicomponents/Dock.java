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
	
	// Dock 의 JFrame 에 대한 비율
	final float widthRatio = 0.8f;
	final float heightRatio = 0.1f;
	final float iconRatio = 0.8f;
	
	// Dock 에 있는 아이콘들
	private ArrayList<String> itemID = new ArrayList<>(); // ID
	private ArrayList<DockElement> items = new ArrayList<>(); // 실제 객체
	
	// Dock 에 있는 리소스 파일 위치들
	public String DockItemsDatabaseLocation;
	public String ID;
	
	// Dock 색
	public final Color dockColor = Color.white;
	
	// JPanel 객체
	public JPanel dock;
	

	// 생성자
	// 받는 패러미터 - JFrame 의 X, Y (둘 다 기본 0), 폭, 높이, Dock 의 리소스 위치
	public Dock(int parentScreenX, int parentScreenY, int parentScreenWidth, int parentScreenHeight, String DockItemsDBLocation) throws Exception {
		
		// 전역 변수에 데이터 할당
		DockItemsDatabaseLocation = DockItemsDBLocation;
		ID = DockItemsDatabaseLocation + "id.data";
		
		// Dock 의 크기 설정 (비율 계산)
		final int dockWidth = (int) (parentScreenWidth * widthRatio);
		final int dockHeight = (int) (parentScreenHeight * heightRatio);
		final int dockX = (parentScreenWidth - dockWidth) / 2;
		final int dockY = (parentScreenHeight - dockHeight);
		
		// GenericWindow 클래스로 Dock 의 기본 패널을 설정
		dock = new GenericWindow(dockX, dockY, dockWidth, dockHeight, "Dock");
		
		// 리소스 폴더의 id.data 파일을 읽어들인 후 linebreak 기준으로 분석
		String[] ids = FileIO.readString(ID).split("\n");
		
		// 해당 ID 를 전역변수 ArrayList 로 옮겨 담음
		for(String id : ids) {
			Logger.info("Adding element: " + id);
			itemID.add(id);
		}
		
		// ID 를 기반으로 리소스 폴더에서 DockElement 를 생성한 후 ArrayList 에 저장
		for (int i = 0; i < itemID.size(); i++) {
			try {
				DockElement item = new DockElement(itemID.get(i), DockItemsDatabaseLocation + "bundles/", (int) (dockHeight * iconRatio), dockColor);
				items.add(item);
			}catch(Exception e) {
				throw e;
			}
		}
		
		// Dock 에 ArrayList 에 있는 Element 를 JPanel 에 추가
		for(DockElement item : items) {
			dock.add(item);
		}
		
		// Dock 컬러 설정
		dock.setBackground(dockColor);
	}

}

class DockElement extends JPanel {
	
	// 엘리멘트 데이터
	private final String itemID;
	private final String itemIconPath; // 아이콘 위치
	private final String itemExecutionCommand; // 아이콘을 눌렀을때 클라이언트로 전송될 명령
	private final String itemRealPath; // 엘리멘트 번들 위치
	
	// 아이콘 로드 실패시 (아이콘 파일이 없을 경우) 대체할 이미지 파일
	private String loadFailedIcon = "";
	private BufferedImage image; // 아이콘 이미지

	// 생성자
	// 받는 패러미터 - 엘리멘트 식별 아이디, 실제 번들 위치, 아이콘 가로세로 크기, Dock 배경색
    public DockElement(String id, String dbLocation, int iconDimension, Color dockBackground) throws Exception {
    	
    	// 객체 전역변수에 추가
    	Logger.info("Adding dock element data...");
    	itemID = id;
    	itemRealPath = dbLocation + id + ".hxgb";
    	itemIconPath = itemRealPath + "/icon.png";
    	itemExecutionCommand = FileIO.readString(itemRealPath + "/exec.data"); // exec.data 파일에서 실행 명령 텍스트를 불러옴
    	
    	try {
    		Logger.info("Loading icon: " + itemIconPath);
    	    image = ImageIO.read(new File(itemIconPath)); // 아이콘 설정
    	} catch (Exception ex) {

    		// 아이콘 설정 실패시
    		Logger.error("Failed loading icon: ");
    		ex.printStackTrace();

    		// 더미 아이콘 로드
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

    	// 아이콘을 Swing 컴포넌트로 변형
    	ImageIcon imicon = new ImageIcon(image);
    	Image img = imicon.getImage();
    	img = img.getScaledInstance(iconDimension, iconDimension, Image.SCALE_SMOOTH);
    	JLabel icon = new JLabel(new ImageIcon(img));
    	icon.setBackground(dockBackground);

    	// 아이콘 크기 조절
    	Logger.info("Icon size: " + iconDimension);
    	icon.setSize(iconDimension, iconDimension);

    	// 이 Dock 엘리멘트에 아이콘 올림
    	this.add(icon);
    	Logger.info("Icon loaded.");

    	// 배경색을 Dock 이랑 통일
    	this.setBackground(dockBackground);
    	this.repaint();

    	// 클릭시 실행할 명령
    	this.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    			try {
    				
    				// Id 와 Command 키를 JSON 으로 묶음
    				JSONObject jsonData = new JSONObject();
    				jsonData.put("Id", itemID);
    				jsonData.put("Command", itemExecutionCommand.replace("\n", ""));
    				
    				// RTServer 를 통해 클라이언트로 전송
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
