package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import IO.SocketIO;

public class ClientSideInterpreter {
	// Interprets the data responded from server after client sent signal
	public static void responseInterpreter(String data) {
		return;
	}
	
	// Interprets the data dispatched from RTServer
	public static void requestInterpreter(String data) {
		try {
			
			// JSON 파싱 시작
			JSONParser parser = new JSONParser();
	        JSONObject parsed = (JSONObject) parser.parse(data);
			
			// JSON 으로부터 Command 키의 값을 가져옴
	        String cmd = parsed.get("Command").toString();
	        String[] commands = cmd.split("<next>");
		    
	        for(String command : commands) {
		        // 명령 처리
				if (command.equals("exit")) {
					Logger.info("RTClient will stop client.");
					Logger.info("RTClient will send stop signal to server.");
	
					// 서버로 종료 명령 전송
					SocketIO.clientMode(command, Main.clientHost);
					System.exit(0);
				
				// 셸 명령어를 처리하는 명령
				}else if (command.startsWith("<exec> ")) {
					
					// 명령어 분리
					command = command.substring("<exec> ".length());
					Logger.info("RTClient will execute shell script: " + command);
					
					// 셸 명령 실행
					Runtime run = Runtime.getRuntime();
					Process pr = run.exec(command);
					pr.waitFor();
					
					// 명령 실행 이후 출력값을 받아옴
					BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
					String line = "";
					String tmpLine = "";
					while ((tmpLine=buf.readLine())!=null) {
						line += tmpLine + "<linebreak>";
					}
					
					Logger.info("Shell returned: " + line);
					
					// 서버로 전송
					SocketIO.clientMode(line, Main.clientHost);
				}else { // 서버 요청을 이해하지 못했을 때 수신된 명령을 다시 서버로 반환 (프론트엔드 처리 명령일 가능성이 큼)
					Logger.error("Server requested unknown command. Sending command back to server.");
					SocketIO.clientMode(command.replace("<inside_next>", "<next>"), Main.clientHost); // 서버로 전송
				}
				Logger.info("Interpretation OK for line: " + command);
	        }
		}catch(Exception e) {
			e.printStackTrace();
			Logger.error(Logger.convertStackTraceToString(e));
		}
	}
}
