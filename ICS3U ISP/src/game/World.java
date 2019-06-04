package game;

import java.awt.Color;
import java.awt.Rectangle;

import hsa2x.GraphicsConsole;

public class World {
	static final int WORLD_SIZE = 3200;
	static final int GRID_SIZE = 32;
	static final int GRID_NUM = 100;
	
	static final int TILE_GRASSY = 0;
	static final int TILE_MOSSY = 1;
	
	int x;
	int y;
	GraphicsConsole gc;
	int[][] tileVals = new int[GRID_NUM][GRID_NUM];
	Rectangle[][] tileBounds = new Rectangle[GRID_NUM][GRID_NUM];
	Viewport viewport;
	
	World(int x, int y, Viewport viewport, GraphicsConsole gc){
		this.viewport = viewport;
		this.gc = gc;
		this.x = x;
		this.y = y;
		generate();
	}
	
	void generate(){
		for(int row = 0; row < GRID_NUM; row++){
			for(int col = 0; col < GRID_NUM; col++){
				tileVals[row][col] = (int)(Math.random()*2);
				tileBounds[row][col] = new Rectangle(0,0,0,0);
				tileBounds[row][col].setBounds(x+(GRID_SIZE*col),y+(GRID_SIZE*row),GRID_SIZE,GRID_SIZE);
			}
		}
	}
	
	void draw(){
		for(int row = 0; row < GRID_NUM; row++){
			for(int col = 0; col < GRID_NUM; col++){	
				switch(tileVals[row][col]){
				case TILE_GRASSY:
					gc.setColor(Color.GREEN);
					break;
				case TILE_MOSSY:
					gc.setColor(Color.green.darker());
					break;
				}
				gc.fillRect((int)(tileBounds[row][col].x-viewport.getxOffset()), (int)(tileBounds[row][col].y-viewport.getyOffset()), GRID_SIZE, GRID_SIZE);
			}
		}
	}
	
}
