package uiobjects;


import javax.swing.JPanel;

import main.Logger;

// JPanel 을 상속 받아서 GenericWindow 객체가 JPanel 의 속성을 가짐
public class GenericWindow extends JPanel {

	// 이 창의 이름을 지정
	public String windowName = "";


	// 생성자 
	// 받는 패러미터 - 창의 좌측 상단의 X Y 위치, 넓이, 높이, 창의 이름
	public GenericWindow(int x, int y, int width, int height, String title) {

		// 상속받은 클래스의 생성자
		super();
		
		// 로그
		Logger.info("GenericWindow object is created with name: " + title);
		Logger.info("Width: " + width);
		Logger.info("Height: " + height);
		Logger.info("X-Pos: " + x);
		Logger.info("Y-Pos: " + y);
		
		// 크기 설정
		this.setBounds(x, y, width, height);

		// 창 이름 설정
		windowName = title;
	}

}
