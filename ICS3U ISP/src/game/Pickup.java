package game;

import java.awt.Color;
import java.awt.Rectangle;

import hsa2x.GraphicsConsole;

@SuppressWarnings("serial")
public class Pickup extends Rectangle{
	
	//Constants for pickup types
	static final int PU_SOUL = 0;
	static final int PU_WOOD = 1;
	static final int PU_STONE = 2;
	static final int PU_FIRE = 3;
	
	//GraphicsConsole
	GraphicsConsole gc;
	
	//Viewport
	Viewport viewport;
	
	//Player
	Player player;
	
	//Color
	Color color;
	
	//Position
	double x,y;
	
	//Type
	int type;
	
	//Set type in constructor
	Pickup(double x, double y, GraphicsConsole gc, Viewport viewport, Player player, int type){
		this.x = x;
		this.y = y;
		this.type = type;
		this.gc = gc;
		this.viewport = viewport;
		this.player = player;
		
		switch(type){
		case PU_SOUL:
			color = Color.MAGENTA;
			break;
		case PU_WOOD:
			color = Color.ORANGE.darker().darker().darker();
			break;
		case PU_STONE:
			color = Color.DARK_GRAY;
			break;
		case PU_FIRE:
			color = TheCalm.COL_FIRE;
			break;
		}
		
		//Set Bounds
		setBounds((int)x, (int)y, 16, 16);
	}
	
	//Draw
	void draw(){
		gc.setColor(color);
		gc.fillOval((int)(x-viewport.getxOffset()),(int)(y-viewport.getyOffset()),16,16);
	}
	
	//Give to player
	void givePlayer(){
		if(player.inventory.amounts[type]<999){
			player.inventory.add(type);
		}
	}
	
}
