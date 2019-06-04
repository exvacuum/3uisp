package game;

import java.awt.Rectangle;

import hsa2x.GraphicsConsole;

public class Viewport {
	
	private float xOffset, yOffset;
	private Rectangle screenBounds = new Rectangle(0,0,0,0);
	GraphicsConsole gc;
	private Rectangle screenSize;
	
	Viewport(float xOffset, float yOffset, GraphicsConsole gc){
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.gc = gc;
		screenSize = gc.getBounds();
	}

	public void trackPlayer(Player player){
		xOffset = (int)(player.x-640/2);
		yOffset = (int)(player.y-480/2);
	}
	
	public void move(float xAmt, float yAmt){
		xOffset += xAmt;
		yOffset += yAmt;
		screenBounds = new Rectangle((int)(screenSize.x-xOffset), (int)(screenSize.y-yOffset), screenSize.width, screenSize.height);
	}
	
	public float getxOffset() {
		return xOffset;
	}

	public void setxOffset(float xOffset) {
		this.xOffset = xOffset;
	}

	public float getyOffset() {
		return yOffset;
	}

	public void setyOffset(float yOffset) {
		this.yOffset = yOffset;
	}

	public Rectangle getScreenBounds() {
		return screenBounds;
	}

	public void setScreenBounds(Rectangle screenBounds) {
		this.screenBounds = screenBounds;
	}
	
}
