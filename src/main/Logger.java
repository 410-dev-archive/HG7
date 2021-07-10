package main;

import java.io.PrintWriter;
import java.io.StringWriter;

import IO.FileIO;

public class Logger {
	
	// 제대로 작동 안한는 코드
	public static void writeExceptionLog(Exception e, String errorLocation) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionStackTrace = sw.toString();
		
		// try {
		//  String exceptionLogLocation = "/Users/hoyounsong/elements/exceptions.l";
		// 	FileIO.appendStringNoCheck(exceptionLogLocation, exceptionStackTrace);
		// }catch(Exception ee) {
		// 	ee.printStackTrace();
		// }

		error(exceptionStackTrace);
	}
	
	public static void info(String s) {
		System.out.println("[*] " + s);
	}
	
	public static void warn(String w) {
		System.out.println("[!] " + w);
	}
	
	public static void error(String e) {
		System.out.println("[-] " + e);
	}
	
	// Exception e 의 스택트레이스를 String 값으로 변환
	public static String convertStackTraceToString(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
