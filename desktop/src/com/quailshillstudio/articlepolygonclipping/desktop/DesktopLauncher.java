package com.quailshillstudio.articlepolygonclipping.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.quailshillstudio.articlepolygonclipping.PolygonClipping;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new PolygonClipping(), config);
	}
}
