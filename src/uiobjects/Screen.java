package uiobjects;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

// JFrame 을 상속 받아서 해당 클래스의 특성을 사용
public class Screen extends JFrame{

	// 생성자
	// 받는 패러미터 - 좌측 상단의 X, Y 값, 넓이, 높이, JFrame 이름
	public Screen(int x, int y, int width, int height, String title) {

		// JFrame 생성자 호출
		super();

		// JFrame 이름 설정
		this.setTitle(title);

		// JFrame 크기 지정
		this.setBounds(x, y, width, height);

		// JFrame 이 닫혔을때 서버 종료
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 레이아웃 관리자 제거
		this.getContentPane().setLayout(null);
	}

}
