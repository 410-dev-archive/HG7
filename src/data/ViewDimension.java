package data;

public class ViewDimension {

	// X, Y, Width, Height 를 포함
	public int X;
	public int Y;
	public int WIDTH;
	public int HEIGHT;
	
	// JSON 형태로 반환
	public String toString() {
		String s = "{\"X\":" + X + ",\"Y\":" + Y + ", \"WIDTH\":" + WIDTH + ", \"HEIGHT\":" + HEIGHT + "}";
		return s;
	}
}
