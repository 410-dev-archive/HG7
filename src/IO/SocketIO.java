package IO;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import main.ClientSideInterpreter;
import main.Logger;
import main.Main;
import main.ServerSideInterpreter;

public class SocketIO {
	
	// 포트 통신 포트 설정
	public static final int socketPort = 62000; // 동기식 포트
	public static final int RTSocketPort = 62001; // RT 커뮤니케이션용 포트

	// 서버에서 명령 수신을 종료할지 결정함. True 일 때 수신 종료
	public static boolean shouldClose = false;
	
	// RTServer 에서 참조함
	private static String RTSend = "";
	
	// 동기식 서버
	public static void serverMode() throws Exception {

		// 소켓 정의
		ServerSocket serverSocket = new ServerSocket(socketPort);
		Socket socketUser = null;
		Logger.info("Server started: " + socketPort);

		// 수신 종료하기 전까지 무한 루프
        while(!shouldClose) {
            socketUser = serverSocket.accept(); // 클라이언트가 접속할 때 까지 대기
            Logger.info("Client connected: " + socketUser.getLocalAddress());

            // 클라이언트로부터 메시지 수신 대기
            InputStream input = socketUser.getInputStream(); 
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String recv = reader.readLine(); // 수신한 내용을 recv 로 저장
            Logger.info("Client sent: " + recv);

            // 출력 준비
            OutputStream out = socketUser.getOutputStream();
            PrintWriter writer = new PrintWriter(out, true);

            // ServerSideInterpreter 클래스의 받은 요청을 처리하는 인터프리터로 수신값을 넘기고, 반환되는 스트링 값을 다시 클라이언트로 넘김
            String interpreted = ServerSideInterpreter.requestInterpreter(recv);
    	    writer.println(interpreted); // 클라이언트로 전송

    	    // 만약 인터프리터 리턴값이 SERVER:EXIT 일 경우 서버 수신 종료 및 서버 그래픽 종료
    	    if (interpreted.equals("SERVER:EXIT")) {
    	    	shouldClose = true;
    	    	Main.endServer();
    	    }
        }

        // 메모리 누수 방지
        serverSocket.close();

        // 0 으로 종료
        System.exit(0);
	}
	
	// 비동기식 RTServer 로 데이터를 넘길때 사용
	// 받는 패러미터 - 클라이언트로 전송할 메시지
	public static void sendMessageToClient(String rt) {
		Logger.info("Sending client: " + rt);
		RTSend = rt;
	}
	
	// 비동기식 RTServer
	public static void RTServerMode() throws Exception {
		
		// 소켓 정의
		ServerSocket serverSocket = new ServerSocket(RTSocketPort);
		Socket socketUser = null;
		Logger.info("RTServer started: " + RTSocketPort);

        while(!shouldClose) {
            socketUser = serverSocket.accept(); // 클라이언트 연결 대기
            Logger.info("RTClient connected: " + socketUser.getLocalAddress()); 

            // 수신이 활성화 되어있을때
            while (!shouldClose) {
            	Thread.sleep(100);

            	// 만약 RTSend 값이 "" 가 아니라면 송신 프로세스 시작
                if (!RTSend.equals("")) {
                	
                	Logger.info("RTServer response: Sending");
                	
                	// 송신 객체 생성
                	OutputStream out = socketUser.getOutputStream();
                	PrintWriter writer = new PrintWriter(out, true);

                	writer.println(RTSend); // RTSend 를 클라이언트로 전송
                	RTSend = ""; // RTSend 를 초기화 (무한 전송 방지)
                	Logger.info("Successfully sent packet.");
                }
            }
        }
        serverSocket.close();
        Logger.info("RTServer closed.");
	}
	
	// 동기식 클라이언트
	// 받는 패러미터 - 서버로 전송할 메시지, 서버 호스트 IP 주소
	public static void clientMode(String toSend, String host) throws Exception {

		// 소켓 정의
		Socket socket = new Socket(host, socketPort);

		// 송신 객체 생성
		OutputStream out = socket.getOutputStream();
		PrintWriter writer = new PrintWriter(out, true);

		// 송신
		writer.println(toSend);

		// 서버로부터의 반응 대기
		InputStream input = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String response = reader.readLine();

		// Response 를 ClientSideInterpreter 에서 처리
		Logger.info("Server responded: " + response);
		ClientSideInterpreter.responseInterpreter(response);
		
		// 소켓 종료
		socket.close();
		
		Logger.info("Response processed.");
	}
	
	// 비동기식 RTClient
	// 받는 패러미터 - 서버 호스트
	public static void clientRealtimeRECV(String host) throws Exception {

		// 비동기 스레드 생성
		Thread RTRECV = new Thread() {
			public void run() {
				try {
					// 소켓 정의
					Socket socket = new Socket(host, RTSocketPort);
					Logger.info("RTClient Connection established.");

					// 무한반복
					while(true) {
						Logger.info("Waiting for signal...");

						// 서버로부터 데이터를 수신
						InputStream input = socket.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(input));
						String in = reader.readLine();

						// 만약 null 이 수신되었으면 종료하기 (서버가 종료되었다는 의미일 가능성이 큼)
						if (in == null) {
							Logger.error("RTClient received invalid data from server. Stopping client.");
							System.exit(9);
						}

						Logger.info("RTServer sent: " + in);
						// 서버로부터의 요청을 ClientSideInterpreter 에서 처리
						ClientSideInterpreter.requestInterpreter(in);

						// 스레드 100밀리초 정지 (컴퓨터 부하 방지)
						this.sleep(100);
					}
				}catch(Exception e) {
					// 오류 발생시 stackTrace 를 출력
					e.printStackTrace();

					// Exception log 작성
					Logger.writeExceptionLog(e, "RTRECV Thread");
					Logger.error("Exception: " + Logger.convertStackTraceToString(e));
				}
			}
		};

		// 비동기 스레드 시작
		RTRECV.start();
	}
}
