package main;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import IO.SocketIO;

public class ClientSideInterpreter {
	// Interprets the data responded from server after client sent signal
	public static void responseInterpreter(String data) {
		
	}
	
	// Interprets the data dispatched from RTServer
	public static void requestInterpreter(String data) {
		try {
			
			// JSON 파싱 시작
			JSONParser parser = new JSONParser();
	        JSONObject parsed = (JSONObject) parser.parse(data);
			
			// JSON 으로부터 Command 키의 값을 가져옴
	        String command = parsed.get("Command").toString();
	        
	        // 명령 처리
			if (command.equals("exit")) {
				Logger.info("RTClient will stop client.");
				Logger.info("RTClient will send stop signal to server.");

				// 서버로 종료 명령 전송
				SocketIO.clientMode(command, Main.clientHost);
				System.exit(0);
			}else { // 서버 요청을 이해하지 못했을 때 수신된 명령을 다시 서버로 반환 (프론트엔드 처리 명령일 가능성이 큼)
				Logger.error("Server requested unknown command. Sending command back to server.");
				SocketIO.clientMode(command, Main.clientHost); // 서버로 전송
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
