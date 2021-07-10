package main;

import data.ViewDimension;

public class WindowPositioner {

	// GenericWindow 의 중앙 좌표값으로 실제 창의 위치값을 다시 계산함
	// 받는 패러미터 - 새로 만들 창의 ViewDimension, 창의 중앙 X 좌표, 창의 중앙 Y 좌표
	public static ViewDimension getNewWindowPosition(ViewDimension newWindowDimension, int xPositionOfCenter, int yPositionOfCenter) {
		ViewDimension vd = new ViewDimension();
		vd.X = xPositionOfCenter - (newWindowDimension.WIDTH/2);
		vd.Y = yPositionOfCenter - (newWindowDimension.HEIGHT/2);
		vd.WIDTH = newWindowDimension.WIDTH;
		vd.HEIGHT = newWindowDimension.HEIGHT;
		return vd;
	}
	
	// 화면의 센터로 위치
	public static ViewDimension getNewWindowPositionOfCenter(ViewDimension screenDimension, ViewDimension newWindowDimension) {
		return getNewWindowPosition(newWindowDimension, screenDimension.WIDTH / 2, screenDimension.HEIGHT / 2);
	}
}
