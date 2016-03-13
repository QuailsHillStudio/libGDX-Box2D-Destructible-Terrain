package com.quailshillstudio.polygonClippingUtils;

public class UserData {
	public static final int GROUND = 0;
	public static final int BOMB = 1;
	public static final int BALL = 2;
	public int type;
	public boolean mustDestroy;
	public boolean destroyed;
	
	public UserData(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
}
