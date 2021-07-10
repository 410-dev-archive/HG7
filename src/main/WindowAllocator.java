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

	// 마지막으로 할당된 PID 저장 (고유한 값을 위해 사용)
	private static long lastPid = 0;
	
	// 마지막으로 띄워진 창의 깊이값 저장 (Dock 과 Background 를 제외함)
	private static int zIndex = 2;
	
	// 메인 JFrame 및 윈도우 표시 클래스
	private static Screen mainScreen;
	private static JLayeredPane layer;
	
	// 열린 창들을 저장
	private static ArrayList<Component> innerContents = new ArrayList<>();
	private static ArrayList<Long> innerContentsPID = new ArrayList<>();
	
	// JFrame 이 생성되었는지 확인 하는 flag
	public static boolean mainWindowGenerated = false;
	
	// JFrame 생성
	// 받는 패러미터 - 창의 X 위치, Y 위치, 넓이, 높이
	public static void createObjects(int x, int y, int screenWidth, int screenHeight) {

		// Screen Class 를 가진 JFrame 생성
		mainScreen = new Screen(x, y, screenWidth, screenHeight, "HG7Server");

		// 레이어 구성
		layer = new JLayeredPane();

		// 레이어 크기 지정
		layer.setBounds(x, y, screenWidth, screenHeight);

		// 레이아웃을 레어어 페인으로 지정
		mainScreen.setLayout(null);
		mainScreen.add(layer);

		// flag 변경
		mainWindowGenerated = true;
		Logger.info("Created main screen.");
	}
	
	// 새로운 PID 지급
	public static long getNewPID() {
		return lastPid++;
	}
	
	// 현재 띄우는 창의 PID 를 리턴 받음
	public static long getPendingPID() {
		return lastPid;
	}
	
	// 윈도우 추가하기 (인덱스 값을 받지 않음)
	// 받는 패러미터 - javax.swing 의 컴포넌트
	public static long addWindow(Component window) {
		return addWindow(window, zIndex++); // Z 인덱스를 하나 높여서 넘김
	}
	
	// 윈도우 추가하기 (인덱스 값을 받음)
	// 받는 패러미터 - javax.swing 의 컴포넌트, 깊이 인덱스 값 (높을수록 위에 표시됨)
	public static long addWindow(Component window, int index) {
		Logger.info("Adding new window to View...");

		// JFrame 에서 레이어 정렬 갱신을 위해 레이어 페인 제거 (레이어 정렬이 먼저 될 경우 클래스가 달라져서 제거가 안되고, 그러면 갱신을 못함)
		mainScreen.remove(layer); 

		// 레이어 페인에 윈도우 추가
		layer.add(window, Integer.valueOf(index)); 

		// ArrayList 에 윈도우 객체를 추가
		innerContents.add(window);

		// 새로운 PID 를 받고, ArrayList 에 추가
		long newPID = getNewPID();
		Logger.info("Got PID: " + newPID);
		innerContentsPID.add(newPID);

		// JFrame 에 다시 레이어페인 추가
		mainScreen.add(layer);

		// 갱신
		refresh();
		Logger.info("mainScreen is updated.");
		return newPID;
	}
	

	// JFrame 의 디멘션을 ViewDimension 객체로 리턴
	public static ViewDimension getScreenDimension() {
		ViewDimension vd = new ViewDimension();
		vd.X = mainScreen.getX(); // X
		vd.Y = mainScreen.getY(); // Y
		vd.WIDTH = mainScreen.getWidth(); // 폭
		vd.HEIGHT = mainScreen.getHeight(); // 높이
		Logger.info("Current mainScreen Dimension: " + vd.toString());
		return vd;
	}
	
	// 창 닫기
	// 받는 패러미터 - GenericWindow 객체
	public static void removeWindow(GenericWindow window) {

		// innerContentsPID 에서 innerContents 내 객체의 인덱스에 있는 값을 삭제
		innerContentsPID.remove(innerContents.indexOf(window));

		// innerContents 에서 객체 삭제
		innerContents.remove(innerContents.indexOf(window));

		// 레이어에서 객체 삭제
		layer.remove(window);

		// 화면 갱신
		refresh();
	}
	
	// 창 닫기
	// 받는 패러미터 - PID long 값
	public static void removeWindow(long pid) {

		// 레이어에서 객체 삭제
		layer.remove(innerContents.get(innerContentsPID.indexOf(pid)));

		// InnerContents 에서 객체 삭제
		innerContents.remove(innerContentsPID.indexOf(pid));

		// PID 삭제
		innerContentsPID.remove(innerContentsPID.indexOf(pid));

		// 화면 갱신
		refresh();
	}
	
	// JFrame 이 널값이 아닐때 갱신
	public static void refresh() {
		if (mainScreen != null) SwingUtilities.updateComponentTreeUI(mainScreen);
	}
	
	// JFrame 닫기
	public static void closeFrame() {

		// ArrayList 초기화
		innerContents.clear();
		innerContentsPID.clear();

		// 레이어 페인 널값으로 설정
		layer = null;

		// JFrame 안보이게 함 (닫기)
		mainScreen.setVisible(false);

		// JFrame 널값으로 설정
		mainScreen = null;

		// Flag 변경
		mainWindowGenerated = false;
	}
	

	// JFrame 활성화
	public static void show() {
		// 설정을 위해 프레임 끄기
		mainScreen.setVisible(false);

		// 타이틀바 및 닫기 버튼등을 안보이게 하기
		mainScreen.setUndecorated(true);

		// 프레임 켜기
		mainScreen.setVisible(true);
	}
	

	// PID ArrayList 반환
	public static ArrayList<Long> getAllPID() {
		return innerContentsPID;
	}

	// WindowEvent 를 JFrame 으로 전달
	public static void dispatchEvent(int eventID) {
		mainScreen.dispatchEvent(new WindowEvent(mainScreen, eventID));
	}
}
