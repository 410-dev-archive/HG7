package main;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import IO.FileIO;
import IO.SocketIO;
import data.ViewDimension;
import uicomponents.Alert;
import uicomponents.Dock;

import uiobjects.GenericWindow;
import uiobjects.Screen;

public class Main {
	
	private static String clientHost = "127.0.0.1";

	public static void main(String[] args){
		try {
			if (args[0].equals("servermode")) {
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
				async.start();
				SocketIO.serverMode();
			}else if (args[0].equals("clientmode")) {
				SocketIO.clientRealtimeRECV(clientHost);
				Scanner input = new Scanner(System.in);
				for(;;) {
					System.out.print(">> ");
					SocketIO.clientMode(input.nextLine(), clientHost);
				}
			}else if (args[0].equals("clientmode-f-in")) {
				SocketIO.clientRealtimeRECV(clientHost);
				if (args.length > 1) {
					File f = new File(args[1]);
					for(;;) {
						if (f.isFile()) {
							String readFile = FileIO.readString(args[1]);
							String[] commands = readFile.split("\n");
							for(String command : commands)
								SocketIO.clientMode(command, clientHost);
						}
					}
				}
			}
		}catch(Exception e) {
			Logger.error(Logger.convertStackTraceToString(e));
			System.exit(9);
		}
	}
	
	
	public static String interpreter(String input) throws Exception {
		if (input.equals("exit")) {
			SocketIO.shouldClose = true;
			return "SERVER:EXIT";
		}else if (input.startsWith("start ")) {
			input = input.replace("start ", "");
			try {
				start(input.split(" "));
				return "SERVER:OK";
			}catch(Exception e) {
				Logger.writeExceptionLog(e, "interpreter.<start>");
				return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e) + "}";
			}
		}else {
			return "SERVER:INTERPRETER_FAILED";
		}
	}
	
	public static void start(String[] args) throws Exception {
		
		int screenWidth = Integer.parseInt(args[0]);
		int screenHeight = Integer.parseInt(args[1]);
		
		if (args.length < 2) {
			Logger.error("Minimum arguments are not given - Setting as 720x480");
			screenWidth = 720;
			screenHeight = 480;
		}
		
		
		
		Logger.info("Main screen width: " + screenWidth);
		Logger.info("Main screen height: " + screenHeight);
		
		WindowAllocator.createObjects(0, 0, screenWidth, screenHeight);
		ViewDimension viewDimension = WindowAllocator.getScreenDimension();
		
		// Load main screen
		GenericWindow background = new GenericWindow(0, 0, screenWidth, screenHeight, "WindowServer");
		background.setBackground(Color.DARK_GRAY);
		
		// Load dock
		Dock d = new Dock(viewDimension.X, viewDimension.Y, viewDimension.WIDTH, viewDimension.HEIGHT);
		
		// Show UI
		WindowAllocator.addWindow(d.dock);
		WindowAllocator.addWindow(background);
		WindowAllocator.show();
		
		try {
//			interpreter();
		}catch(Exception e) {
			e.printStackTrace();
			Alert a = new Alert("Error", Logger.convertStackTraceToString(e));
			WindowAllocator.addWindow(a.alert);
		}
	}
	
	
	// Interprets the data responded from server after client sent signal
	public static void serverResponseInterpreter(String data) {
		
	}
	
	// Interprets the data dispatched from RTServer
	public static void serverSignalInterpreter(String data) {
		try {
			if (data.contains("Command: {exit}")) {
				Logger.info("RTClient will stop client.");
				SocketIO.clientMode("exit", clientHost);
				System.exit(0);
			}
		}catch(Exception e) {
			e.printStackTrace();
			
		}
	}
	

	public static void endServer() throws Exception {
		Logger.info("Dispatching event...");
		WindowAllocator.dispatchEvent(WindowEvent.WINDOW_CLOSING);
	}

}
