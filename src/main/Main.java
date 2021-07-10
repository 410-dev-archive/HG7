package main;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import IO.FileIO;
import IO.SocketIO;

import uicomponents.Alert;
import uicomponents.Dock;

import uiobjects.GenericWindow;
import uiobjects.Screen;

public class Main {
	
	public static String clientHost = "127.0.0.1"; // All communications are done locally

	// 시작 메인 메서드 - 시작 모드 결정을 위해 커맨드라인 argument 를 받음
	// 사용 가능한 arguments: servermode, clientmode, clientmode-fs [통신용 파일 위치]
	public static void main(String[] args){
		
		Logger.info("HG7 Graphics Framework");
		Logger.info("++++++++++++++++++++++");
		Logger.info("HG7 7.0.0 InDev 1");
		Logger.info("Server integrated.");
		Logger.info("");
		
		try {
			
			// 서버 모드로 시작하기
			if (args[0].equals("servermode")) {
				Logger.info("Running in [SERVER] mode...");
				Logger.info("");

				// 송신용 RTServer 소켓 비동기 스레드 구성
				Thread async = new Thread() {
					public void run() {
						try {
							SocketIO.RTServerMode();
						} catch (Exception e) {
							Logger.error(Logger.convertStackTraceToString(e));
							System.exit(9);
						}
					}
				};

				// 비동기 스레드 시작
				async.start();

				// 수신용 동기 서버 소켓 시작 
				SocketIO.serverMode();

			// Scanner 를 이용하는 클라이언트 모드로 시작하기
			// !!! 중요 !!! 명령어를 입력한 후 전송하기 전까지 수신 서버에 연결하지 않음.
			}else if (args[0].equals("clientmode")) {
				Logger.info("Running in [CLIENT-UINTERACTIVE] mode...");
				Logger.info("");

				// 수신용 RTClient 비동기 소켓 활성화
				SocketIO.clientRealtimeRECV(clientHost);

				// Input definition
				Scanner input = new Scanner(System.in);

				// 데이터 받기
				for(;;) {
					System.out.print(">> ");
					SocketIO.clientMode(input.nextLine() /* Scanner 로 받은 텍스트를 바로 전송 */, clientHost); // 입력받은 데이터를 동기식 데이터 서버로 전송
				}


			// 일반 파일을 통해 시스템 (서버 아님) 과 클라이언트 통신 방식
				// 예: bash 에서 echo "명령어" > "통신파일"
			}else if (args[0].equals("clientmode-fs")) {
				Logger.info("Running in [CLIENT] mode...");
				Logger.info("");

				// 수신용 RTClient 비동기 소켓 활성화
				SocketIO.clientRealtimeRECV(clientHost);

				// argument 에 통신 파일 위치가 있는지 체크
				if (args.length > 1) {
					File f = new File(args[1]); // args[1] 을 통신 파일로 인식
					for(;;) { // 무한루프
						if (f.isFile()) { // 만약 통신 파일이 존재할 경우
							String readFile = FileIO.readString(args[1]); // 통신 파일의 문자열 데이터를 읽어들이고
							String[] commands = readFile.split("\n"); // linebreak 를 기준으로 나눈 후
							for(String command : commands) // 각 줄을 서버로 전송하기
								SocketIO.clientMode(command, clientHost);
						}
					}
				}
			}

			// 이 모든 과정에서 Exception 발생시
		}catch(Exception e) {
			Logger.error(Logger.convertStackTraceToString(e)); // StackTrace 를 String 으로 변환한 후 출력
			System.exit(9); // 종료 리턴 9
		}
	}
	
	
	// [서버] start 명령이 수신되었을 때
	public static void start(String[] args /* 문자열 배열로 받음. */) throws Exception {
		
		// JFrame 폭과 넓이 초기값 720x480
		int screenWidth = 720;
		int screenHeight = 480;
		
		// 만약 패러미터에서 충분한 디멘션이 제공되지 않았을 경우 물리적 화면 크기를 받아온 후 설정
		if (args.length < 2) {
			Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
			screenWidth = size.width;
			screenHeight = size.height;
			Logger.error("Minimum arguments are not given - Setting as hardware size: " + screenWidth + "x" + screenHeight);
		}else {
			// 디멘션 값이 제공되었을 경우 숫자를 파싱해와서 설정
			// 만약 파싱에 실패할 경우 main 메서드의 catch 문으로 들어가짐. (throws Exception 시그니쳐가 있으므로)
			screenWidth = Integer.parseInt(args[0]);
			screenHeight = Integer.parseInt(args[1]);
		}
		
		
		Logger.info("Main screen width: " + screenWidth);
		Logger.info("Main screen height: " + screenHeight);
		

		// WindowAllocator 에서 JFrame 및 기타 설정을 함
		WindowAllocator.createObjects(0, 0, screenWidth, screenHeight); // JFrame 의 좌측 상단 (시작 좌표) 는 화면의 0,0 위치 (화면의 좌측 상단)
		WindowAllocator.show(); // JFrame 보이게 하기
	}
	

	// [서버] exit 명령이 수신되었을 때
	public static void endServer() throws Exception {
		Logger.info("Dispatching event...");

		// WindowAllocator 로 WINDOW_CLOSING 이벤트를 전송
		WindowAllocator.dispatchEvent(WindowEvent.WINDOW_CLOSING);
	}

}
