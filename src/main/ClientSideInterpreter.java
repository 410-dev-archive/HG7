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
			
			JSONParser parser = new JSONParser();
	        JSONObject parsed = (JSONObject) parser.parse(data);
			
	        String command = parsed.get("Command").toString();
	        
			if (command.equals("exit")) {
				Logger.info("RTClient will stop client.");
				SocketIO.clientMode("exit", Main.clientHost);
				System.exit(0);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
