package main;

import java.io.PrintWriter;
import java.io.StringWriter;

import IO.FileIO;

public class Logger {
	
	public static String exceptionLogLocation = "/Users/hoyounsong/elements/exceptions.l";
	
	public static void writeExceptionLog(Exception e, String errorLocation) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionStackTrace = sw.toString();
		
		try {
			FileIO.appendStringNoCheck(exceptionLogLocation, exceptionStackTrace);
		}catch(Exception ee) {
			ee.printStackTrace();
		}
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
	
	public static String convertStackTraceToString(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
