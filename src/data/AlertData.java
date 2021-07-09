package data;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import main.Logger;

public class AlertData {
	public String title;
	public String text;
	public String parentID;
	public int width;
	public int height;
	public ArrayList<ButtonData> buttons = new ArrayList<>();
	
	
	/* Required properties
	
	title				String value that is displayed as title
	text				String value that is displayed as content
	parentID			String value of application ID
	uuid				String value of UUID for individual window
	width				Integer value of window width
	height				Integer value of window height
	
	[Button Array]
	
	*/
	
	/* Optional properties
	 
	allowMultiWindows		Boolean value that defines if this application ID can have multiple windows
	doShowWindowTitleBar	Boolean value that defines if this window has title bar
	allowClose				Boolean value that defines if this window is allowed to close with red closing button
	
	 
	*/
	
	public AlertData(String json) throws Exception {
		
		JSONParser parser = new JSONParser();
        JSONArray parentArrayParser = (JSONArray) parser.parse(json);
        
        String innerJSON = parentArrayParser.get(0).toString();
        JSONObject innerParser = (JSONObject) parser.parse(innerJSON);
        
        title = (String) innerParser.get("title");
        text = (String) innerParser.get("text");
        parentID = (String) innerParser.get("parentID");
        
        width = Integer.parseInt((String) innerParser.get("width"));
        height = Integer.parseInt((String) innerParser.get("height"));
        
        for(int i = 1;;i++) {
        	try {
        		innerJSON = parentArrayParser.get(i).toString();
        		innerParser = (JSONObject) parser.parse(innerJSON);
        		
        		Logger.info("Adding button data: " + innerParser.toString());
        		
        		ButtonData b = new ButtonData();
        		b.text = (String) innerParser.get("buttonText");
        		b.onClickCommand = (String) innerParser.get("buttonAction");
        		
        		buttons.add(b);
        	}catch(Exception e) {
        		if (e.toString().contains("IndexOutOfBounds")) break;
        	}
        }
	}
}
