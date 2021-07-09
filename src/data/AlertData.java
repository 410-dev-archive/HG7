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
	
	public static void main(String[] args) throws Exception {
		AlertData ad = new AlertData("[{\"title\":\"Not Prepared\", \"text\":\"Message\"}, {\"buttonText\":\"ButtonText\", \"buttonAction\":\"EH\"}]");
	}
}
