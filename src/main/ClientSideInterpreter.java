package main;

import IO.SocketIO;

public class ClientSideInterpreter {
	// Interprets the data responded from server after client sent signal
	public static void responseInterpreter(String data) {
		
	}
	
	// Interprets the data dispatched from RTServer
	public static void requestInterpreter(String data) {
		try {
			if (data.contains("Command: {exit}")) {
				Logger.info("RTClient will stop client.");
				SocketIO.clientMode("exit", Main.clientHost);
				System.exit(0);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
