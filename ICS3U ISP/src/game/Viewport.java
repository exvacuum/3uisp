package game;

import hsa2x.GraphicsConsole;

public class Viewport {
	
	//Offset for drawing everything
	private float xOffset, yOffset;
	
	//Graphics Console
	GraphicsConsole gc;
	
	Viewport(float xOffset, float yOffset, GraphicsConsole gc){
		//Starting Offset(Usually should be 0,0) 
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.gc = gc;
	}

	//Center View on player
	public void trackPlayer(Player player){
		xOffset = (int)(player.x-640/2);
		yOffset = (int)(player.y-480/2);
	}
	
	//Getters for offset
	public float getxOffset() {
		return xOffset;
	}

	public float getyOffset() {
		return yOffset;
	}

}
