package main;

import java.awt.Color;

import IO.SocketIO;
import data.AlertData;
import data.ViewDimension;
import uicomponents.Alert;
import uicomponents.Dock;
import uiobjects.GenericWindow;

public class ServerSideInterpreter {
	
	// 클라이언트로부터의 요청 처리
	// 받는 패러미터 - 클라이언트로부터의 요청 명령 문자열
	public static String requestInterpreter(String input) throws Exception {
		if (input.equals("exit")) { // 명령이 exit 일때
			SocketIO.shouldClose = true; // 비동기 / 동기 서버 모두 종료
			return "SERVER:EXIT"; // 서버 종료 상태를 클라이언트로 다시 전송

		// 명령이 start 로 시작할 때 (뒤에 패러미터가 남아있음)
		// JFrame (메인 윈도우) 를 처음에 생성하는 명령
		}else if (input.startsWith("start")) {
			input = input.replace("start ", ""); // 패러미터가 있을 경우 패러미터만 남겨놓음
			input = input.replace("start", ""); // 패러미터가 없을 경우 아무것도 남겨놓지 않음

			// 만약 이미 JFrame 이 만들어졌다면 NOT_OK 를 클라이언트로 리턴 (메서드 여기서 종료)
			if (WindowAllocator.mainWindowGenerated) return "SERVER:NOT_OK:Main Window is already generated.";

			try {
				// 아닐 경우 Main 의 start 메서드 호출, 패러미터를 스페이스로 분할한 어레이를 줌
				Main.start(input.split(" "));
				return "SERVER:OK";
			}catch(Exception e) {
				Logger.writeExceptionLog(e, "interpreter.<start>");
				return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e) + "}";
			}

		// Dock 을 생성하는 명령
		// 반드시 Dock 의 리소스를 가지고 있는 폴더의 경로를 알려줘야함
		}else if (input.startsWith("dock ")) {
			input = input.replace("dock ", ""); // 패러미터만 남기기
			return makeDock(input); // makeDock 의 리턴값을 클라이언트로 전송


		// Alert 를 생성하는 명령
		// 반드시 지정된 형태를 띤 JSON 값을 패러미터로 넘겨줘야함
		// JSON 형태는 AlertData 클래스 참조
		}else if (input.startsWith("alert ")) {
			input = input.replace("alert ", ""); // 패러미터만 남기기
			return makeAlert(input); // makeAlert 의 리턴값을 클라이언트로 전송


		// 특정 윈도우를 닫는 명령
		// 반드시 닫으려는 윈도우의 PID 를 패러미터로 넘겨줘야 함
		// 윈도우 또는 Alert 생성시 SERVER:OK: 뒤에 나오는 숫자가 PID 값임
		}else if (input.startsWith("close ")) {
			input = input.replace("close ", ""); // 패러미터만 남기기
			return closeWindow(input); // close 의 리턴값을 클라이언트로 전송

		// 서버 재시작
		}else if (input.equals("uisvreboot")) {
			return uiReboot();
		}else {

			// 해석 실패 메시지 전송
			return "SERVER:INTERPRETER_FAILED";
		}
	}
	
	// 윈도우 닫는 명령
	private static String closeWindow(String pid) {
		try {

			// Long 형태로 PID 해석
			long PID = Long.parseLong(pid);

			// WindowAllocator 에서 해당 PID 를 가진 윈도우 삭제
			WindowAllocator.removeWindow(PID);

			// Exception 발생이 없을 경우 OK 송신
			return "SERVER:OK";
		}catch(Exception e) {
			return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e) + "}";
		}
	}
	

	// 서버 재시동 명령
	private static String uiReboot() {
		try {
			// 현재 Dimension 가져오기
			ViewDimension origd = WindowAllocator.getScreenDimension();

			// JFrame 제거 및 WindowAllocator 값들 초기화
			WindowAllocator.closeFrame();

			// Dimension 값들을 형태에 맞게 문자열 배열로 재구성 후 창 다시 생성
			Main.start(new String[] {origd.WIDTH + "", origd.HEIGHT + ""});

			// Exception 이 발생하지 않았을 경우 OK 송신
			return "SERVER:OK";
		}catch(Exception e) {
			return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e) + "}";
		}
	}
	

	// Alert 생성 명령
	private static String makeAlert(String jsonIn) {
		try {
			// JSON 데이터를 AlertData 객체 형태로 변환 (설정값 컴포넌트)
			AlertData alertData = new AlertData(jsonIn);

			// AlertData 를 가지고 있는 Alert 객체를 만듦 (실제 그래픽 컴포넌트)
			Alert a = new Alert(alertData);

			// WindowAllocator 에서 창을 새로 띄워준 후 해당 창의 PID 를 같이 반환
			long returnedPID = WindowAllocator.addWindow(a.alert);
			return "SERVER:OK:" + returnedPID;

		}catch(Exception e) {
			return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e).replace("\n", "") + "}";
		}
	}
	

	// Dock 생성 명령
	private static String makeDock(String docklib) {
		try {

			// Dock 리소스를 가진 디렉터리 값 끝에 / 가 없을 경우 임의로 추가
			if (!docklib.endsWith("/")) docklib += "/";

			// JFrame 크기를 ViewDimension 클래스 형태로 받아옴
			ViewDimension viewDimension = WindowAllocator.getScreenDimension();
			
			// GenericWindow 클래스로 배경 화면 크기를 지정
			GenericWindow background = new GenericWindow(0, 0, viewDimension.WIDTH, viewDimension.HEIGHT, "WindowServer");

			// 배경화면 색 처리
			background.setBackground(Color.DARK_GRAY);
			

			// Dock 객체 생성
			Dock d = new Dock(viewDimension.X, viewDimension.Y, viewDimension.WIDTH, viewDimension.HEIGHT, docklib);

			// WindowAllocator 에다 추가 후 화면 갱신
			WindowAllocator.addWindow(background, 0);
			WindowAllocator.addWindow(d.dock, 300);
			WindowAllocator.refresh();
			return "SERVER:OK";
		}catch(Exception e) {
			return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e) + "}";
		}
	}
}
