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

	// JPanel 엘리멘트
	public JPanel alert;

	// Alert 구분용 ID
	public String id;

	// PID
	public long winPid;
	

	// 생성자
	// 받는 패러미터 - 초기화 및 값 설정이 완료된 AlertData
	public Alert(AlertData data) {

		// 초기 디멘션 설정
		ViewDimension alertDimension = new ViewDimension();
		alertDimension.HEIGHT = data.height;
		alertDimension.WIDTH = data.width;

		// 센터에 해당하는 X Y 가져오기
		alertDimension = WindowPositioner.getNewWindowPositionOfCenter(WindowAllocator.getScreenDimension(), alertDimension);

		// JPanel 엘리멘트를 GenericWindow 에서 가져옴
		alert = new GenericWindow(alertDimension.X, alertDimension.Y, alertDimension.WIDTH, alertDimension.HEIGHT, data.title);
		
		// ID 설정
		id = data.parentID;
		
		// 텍스트용 JLabel 설정
		JLabel content = new JLabel();
		content.setText(data.text);
		
		// 제목용 JLabel 설정
		JLabel titleText = new JLabel();
		titleText.setText(data.title);
		
		// 이 창을 의미하는 PID 가져오기
		winPid = WindowAllocator.getPendingPID();
		
		// 버튼 추가
		// JSON 에서 두번째 어레이 데이터부터 버튼으로 인식 후 해당 데이터로부터 버튼 텍스트와 실행 명령을 받아온 후 JButton 으로 변환
		Logger.info("Adding button...");
		for(ButtonData buttonData : data.buttons) {

			// 객체 생성
			JButton button = new JButton();
			Logger.info("Button: " + buttonData.text);
			Logger.info("Button Action: " + buttonData.onClickCommand);
			
			// 텍스트 설정
			button.setText(buttonData.text);

			// 클릭 시 동작 설정
			button.addActionListener(new ActionListener() {
			    @Override
			    public void actionPerformed(ActionEvent e) {
			    	// JSON 을 생성함
			    	JSONObject object = new JSONObject();

			    	// Id 로 Alert 에 해당하는 아이디를 받아온 후 JSON 에 데이터 추가
			    	object.put("Id", data.parentID);

			    	// Command 를 받아온 후, JSON 에 데이터 추가
			    	// __windowpid 는 이 창의 PID 로 바뀜
			    	object.put("Command", buttonData.onClickCommand.replace("__windowpid", winPid + ""));

			    	// RTClient 로 명령 전송
			        SocketIO.sendMessageToClient(object.toString());
			    }
			});

			// 이 버튼을 보이게 하기
			button.setVisible(true);

			// JPanel 에 버튼 추가
			alert.add(button);
		}
		
		// 텍스트 및 타이틀을 JPanel 에 추가
		alert.add(content);
		alert.add(titleText);

		// 배경 색 설정
		alert.setBackground(Color.white);
		
	}
}
