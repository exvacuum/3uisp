package game;

public class Viewport {
	
	private float xOffset, yOffset;
	
	Viewport(float xOffset, float yOffset){
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public void trackPlayer(Player player){
		xOffset = (int)(player.x-640/2);
		yOffset = (int)(player.y-480/2);
	}
	
	public void move(float xAmt, float yAmt){
		xOffset += xAmt;
		yOffset += yAmt;
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
	
	
}
