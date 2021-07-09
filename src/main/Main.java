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
import data.ViewDimension;
import uicomponents.Alert;
import uicomponents.Dock;

import uiobjects.GenericWindow;
import uiobjects.Screen;

public class Main {
	
	public static String clientHost = "127.0.0.1";

	public static void main(String[] args){
		
		Logger.info("HG7 Graphics Framework");
		Logger.info("++++++++++++++++++++++");
		Logger.info("HG7 7.0.0 InDev 1");
		Logger.info("Server integrated.");
		Logger.info("");
		
		try {
			if (args[0].equals("servermode")) {
				Logger.info("Running in [SERVER] mode...");
				Logger.info("");
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
				Logger.info("Running in [CLIENT-UINTERACTIVE] mode...");
				Logger.info("");
				SocketIO.clientRealtimeRECV(clientHost);
				Scanner input = new Scanner(System.in);
				for(;;) {
					System.out.print(">> ");
					SocketIO.clientMode(input.nextLine(), clientHost);
				}
			}else if (args[0].equals("clientmode-fs")) {
				Logger.info("Running in [CLIENT] mode...");
				Logger.info("");
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
	
	
	public static void start(String[] args) throws Exception {
		
		int screenWidth = 720;
		int screenHeight = 480;
		
		if (args.length < 2) {
			Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
			screenWidth = size.width;
			screenHeight = size.height;
			Logger.error("Minimum arguments are not given - Setting as hardware size: " + screenWidth + "x" + screenHeight);
		}else {
			screenWidth = Integer.parseInt(args[0]);
			screenHeight = Integer.parseInt(args[1]);
		}
		
		
		Logger.info("Main screen width: " + screenWidth);
		Logger.info("Main screen height: " + screenHeight);
		
		WindowAllocator.createObjects(0, 0, screenWidth, screenHeight);
		WindowAllocator.show();
	}
	

	public static void endServer() throws Exception {
		Logger.info("Dispatching event...");
		WindowAllocator.dispatchEvent(WindowEvent.WINDOW_CLOSING);
	}

}
