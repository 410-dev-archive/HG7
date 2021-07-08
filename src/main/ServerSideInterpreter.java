package main;

import java.awt.Color;

import IO.SocketIO;
import data.ViewDimension;
import data.WindowData;
import uicomponents.Dock;
import uiobjects.GenericWindow;

public class ServerSideInterpreter {
	public static String requestInterpreter(String input) throws Exception {
		if (input.equals("exit")) {
			SocketIO.shouldClose = true;
			return "SERVER:EXIT";
		}else if (input.startsWith("start")) {
			input = input.replace("start", "");
			try {
				Main.start(input.split(" "));
				return "SERVER:OK";
			}catch(Exception e) {
				Logger.writeExceptionLog(e, "interpreter.<start>");
				return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e) + "}";
			}
		}else if (input.startsWith("dock ")) {
			input = input.replace("dock ", "");
			try {
				return makeDock(input);
			}catch(Exception e) {
				Logger.writeExceptionLog(e, "interpreter.<start>");
				return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e) + "}";
			}
		}else if (input.startsWith("alert ")) {
			input = input.replace("alert ", "");
			try {
				return makeAlert(input);
			}catch(Exception e) {
				Logger.writeExceptionLog(e, "interpreter.<start>");
				return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e) + "}";
			}
		}else if (input.equals("uisvreboot")) {
			try {
				ViewDimension origd = WindowAllocator.getScreenDimension();
				
				Main.start(new String[] {origd.WIDTH + "", origd.HEIGHT + ""});
				return "SERVER:OK";
			}catch(Exception e) {
				Logger.writeExceptionLog(e, "interpreter.<start>");
				return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e) + "}";
			}
		}else {
			return "SERVER:INTERPRETER_FAILED";
		}
	}
	
	private static String makeAlert(String windata) {
		return "";
	}
	
	private static String makeDock(String docklib) {
		try {
			ViewDimension viewDimension = WindowAllocator.getScreenDimension();
			
			GenericWindow background = new GenericWindow(0, 0, viewDimension.WIDTH, viewDimension.HEIGHT, "WindowServer");
			background.setBackground(Color.DARK_GRAY);
			
			Dock d = new Dock(viewDimension.X, viewDimension.Y, viewDimension.WIDTH, viewDimension.HEIGHT, docklib);
			
			WindowAllocator.addWindow(d.dock);
			WindowAllocator.addWindow(background);
			WindowAllocator.refresh();
			return "SERVER:OK";
		}catch(Exception e) {
			return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e) + "}";
		}
	}
}
