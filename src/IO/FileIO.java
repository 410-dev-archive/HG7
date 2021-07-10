package IO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import main.Logger;

public class FileIO {
	
	// String 값을 파일에서 읽어옴
	public static String readString(String path) {
		if (!new File(path).canRead()) return null;
		
		String toReturn = "";
		try {
			List<String> lines = Files.readAllLines(Paths.get(path));
		    for(String line : lines) {
		        toReturn += line + "\n";
		    }
		}catch(Exception e) {
			Logger.writeExceptionLog(e, "FileIO.readString");
			toReturn = null;
		}
	    return toReturn;
	}
	
	// 파일에 String 값을 씀 (덮어쓰기). 성공시 true 반환.
	public static boolean writeString(String path, String content) {
		File file = new File(path);
		if (! file.canWrite()) return false;

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
		    writer.write(content);
		} catch (Exception e) {
		    Logger.writeExceptionLog(e, "FileIO.writeString");
		}
		
		return new File(path).isFile() && content.startsWith(readString(path));
	}
	
	// 파일에 String 값을 덧붙임. 성공시 true 반환.
	public static boolean appendString(String path, String content) {
		
		if (!new File(path).canWrite()) return false;
		
		try {
			appendStringNoCheck(path, content);
		}catch(Exception e) {
			Logger.writeExceptionLog(e, "FileIO.appendString");
		}
		
		return content.endsWith(readString(path));
	}
	
	// 안전 체크 안하고 파일에 String 값을 덧붙임
	public static void appendStringNoCheck(String path, String content) throws Exception {
		if (!new File(path).isFile()) {
			writeString(path, content);
		}else {
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(path)));
			w.append(content);
			w.close();
		}
	}
	
	// 파일 삭제. 성공시 true 반환.
	public static boolean deleteFile(String path) {
		File f = new File(path);
		if (f.isFile()) {
			if (!f.canWrite()) return false;
			f.delete();
		}
		return f.isFile();
	}
}
