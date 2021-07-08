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
	
	public static final int socketPort = 62000;
	public static final int RTSocketPort = 62001;
	public static boolean shouldClose = false;
	
	private static String RTSend = "";
	
	public static void serverMode() throws Exception {
		ServerSocket serverSocket = new ServerSocket(socketPort);
		Socket socketUser = null;
		Logger.info("Server started: " + socketPort);
        while(!shouldClose) {
            socketUser = serverSocket.accept(); 
            Logger.info("Client connected: " + socketUser.getLocalAddress()); 
            InputStream input = socketUser.getInputStream(); 
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String recv = reader.readLine();
            Logger.info("Client sent: " + recv);
            OutputStream out = socketUser.getOutputStream();
            PrintWriter writer = new PrintWriter(out, true);
            String interpreted = ServerSideInterpreter.requestInterpreter(recv);
    	    writer.println(interpreted);
    	    if (interpreted.equals("SERVER:EXIT")) {
    	    	shouldClose = true;
    	    	Main.endServer();
    	    }
        }
        serverSocket.close();
        System.exit(0);
	}
	
	public static void sendMessageToClient(String rt) {
		Logger.info("Sending client: " + rt);
		RTSend = rt;
	}
	
	public static void RTServerMode() throws Exception {
		
		ServerSocket serverSocket = new ServerSocket(RTSocketPort);
		Socket socketUser = null;
		Logger.info("RTServer started: " + RTSocketPort);
        while(!shouldClose) {
            socketUser = serverSocket.accept(); 
            Logger.info("RTClient connected: " + socketUser.getLocalAddress()); 
            while (!shouldClose) {
            	OutputStream out = socketUser.getOutputStream();
                PrintWriter writer = new PrintWriter(out, true);
                if (!RTSend.equals("")) {
                	writer.println(RTSend);
                	RTSend = "";
                	Logger.info("Successfully sent packet.");
                }
            }
        }
        serverSocket.close();
	}
	
	public static void clientMode(String toSend, String host) throws Exception {
		Socket socket = new Socket(host, socketPort);
		OutputStream out = socket.getOutputStream();
		PrintWriter writer = new PrintWriter(out, true);

		writer.println(toSend);
		InputStream input = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String response = reader.readLine();
		Logger.info("Server responded: " + response);
		ClientSideInterpreter.responseInterpreter(response);
		
		socket.close();
	}
	
	public static void clientRealtimeRECV(String host) throws Exception {
		Thread RTRECV = new Thread() {
			public void run() {
				try {
					Socket socket = new Socket(host, RTSocketPort);
					Logger.info("RTClient Connection established.");
					while(true) {
						InputStream input = socket.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(input));
						String in = reader.readLine();
						if (in == null) {
							Logger.error("RTClient received invalid data from server. Stopping client.");
							System.exit(9);
						}
						Logger.info("RTServer sent: " + in);
						ClientSideInterpreter.requestInterpreter(in);
						this.sleep(100);
					}
				}catch(Exception e) {
					e.printStackTrace();
					Logger.writeExceptionLog(e, "RTRECV Thread");
				}
			}
		};
		RTRECV.start();
	}
}
