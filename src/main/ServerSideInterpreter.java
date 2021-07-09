package main;

import java.awt.Color;

import IO.SocketIO;
import data.AlertData;
import data.ViewDimension;
import uicomponents.Alert;
import uicomponents.Dock;
import uiobjects.GenericWindow;

public class ServerSideInterpreter {
	
	public static String requestInterpreter(String input) throws Exception {
		if (input.equals("exit")) {
			SocketIO.shouldClose = true;
			return "SERVER:EXIT";
		}else if (input.startsWith("start")) {
			input = input.replace("start ", "");
			input = input.replace("start", "");
			if (WindowAllocator.mainWindowGenerated) return "SERVER:NOT_OK:Main Window is already generated.";
			try {
				Main.start(input.split(" "));
				return "SERVER:OK";
			}catch(Exception e) {
				Logger.writeExceptionLog(e, "interpreter.<start>");
				return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e) + "}";
			}
		}else if (input.startsWith("dock ")) {
			input = input.replace("dock ", "");
			return makeDock(input);
		}else if (input.startsWith("alert ")) {
			input = input.replace("alert ", "");
			return makeAlert(input);
		}else if (input.startsWith("close ")) {
			input = input.replace("close ", "");
			return closeWindow(input);
		}else if (input.equals("uisvreboot")) {
			return uiReboot();
		}else {
			return "SERVER:INTERPRETER_FAILED";
		}
	}
	
	private static String closeWindow(String pid) {
		try {
			long PID = Long.parseLong(pid);
			WindowAllocator.removeWindow(PID);
			return "SERVER:OK";
		}catch(Exception e) {
			return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e) + "}";
		}
	}
	
	private static String uiReboot() {
		try {
			ViewDimension origd = WindowAllocator.getScreenDimension();
			WindowAllocator.closeFrame();
			Main.start(new String[] {origd.WIDTH + "", origd.HEIGHT + ""});
			return "SERVER:OK";
		}catch(Exception e) {
			return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e) + "}";
		}
	}
	
	private static String makeAlert(String jsonIn) {
		try {
			AlertData alertData = new AlertData(jsonIn);
			Alert a = new Alert(alertData);
			long returnedPID = WindowAllocator.addWindow(a.alert);
			return "SERVER:OK:" + returnedPID;
		}catch(Exception e) {
			return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e).replace("\n", "") + "}";
		}
	}
	
	private static String makeDock(String docklib) {
		try {
			ViewDimension viewDimension = WindowAllocator.getScreenDimension();
			
			GenericWindow background = new GenericWindow(0, 0, viewDimension.WIDTH, viewDimension.HEIGHT, "WindowServer");
			background.setBackground(Color.DARK_GRAY);
			
			Dock d = new Dock(viewDimension.X, viewDimension.Y, viewDimension.WIDTH, viewDimension.HEIGHT, docklib);

			WindowAllocator.addWindow(background, 0);
			WindowAllocator.addWindow(d.dock, 300);
			WindowAllocator.refresh();
			return "SERVER:OK";
		}catch(Exception e) {
			return "SERVER:EXCEPTION:{" + Logger.convertStackTraceToString(e) + "}";
		}
	}
}
