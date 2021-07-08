package main;

import data.ViewDimension;

public class WindowPositioner {
	public static ViewDimension getNewWindowPosition(ViewDimension newWindowDimension, int xPositionOfCenter, int yPositionOfCenter) {
		ViewDimension vd = new ViewDimension();
		vd.X = xPositionOfCenter - (newWindowDimension.WIDTH/2);
		vd.Y = yPositionOfCenter - (newWindowDimension.HEIGHT/2);
		vd.WIDTH = newWindowDimension.WIDTH;
		vd.HEIGHT = newWindowDimension.HEIGHT;
		return vd;
	}
	
	public static ViewDimension getNewWindowPositionOfCenter(ViewDimension screenDimension, ViewDimension newWindowDimension) {
		return getNewWindowPosition(newWindowDimension, screenDimension.WIDTH / 2, screenDimension.HEIGHT / 2);
	}
}
