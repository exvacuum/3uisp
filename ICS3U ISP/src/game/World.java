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
	
	static final int DECO_NONE = 0;
	static final int DECO_STONE = 1;
	static final int DECO_TREE = 2;
	
	int x;
	int y;
	GraphicsConsole gc;
	float[][] tileVals = new float[GRID_NUM][GRID_NUM];
	float[][] tileDecor = new float[GRID_NUM][GRID_NUM];
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
		float scl = 0.1f;
		for(int row = 0; row < GRID_NUM; row++){
			for(int col = 0; col < GRID_NUM; col++){
				tileVals[row][col] = (float)Noise.noise(row*scl,col*scl)*2;
				tileBounds[row][col] = new Rectangle(0,0,0,0);
				tileBounds[row][col].setBounds(x+(GRID_SIZE*col),y+(GRID_SIZE*row),GRID_SIZE,GRID_SIZE);
			}
		}
		for(int row = 0; row < GRID_NUM; row++){
			for(int col = 0; col < GRID_NUM; col++){
				switch((int)Math.round(Math.abs(tileVals[row][col]))){
				case TILE_GRASSY:
					
					break;
				case TILE_MOSSY:
					gc.setColor(Color.green.darker());
					break;
				default:
					gc.setColor(Color.GREEN);
					break;
				}
			}
		}
	}
	
	void draw(){
		for(int row = 0; row < GRID_NUM; row++){
			for(int col = 0; col < GRID_NUM; col++){	
				switch((int)Math.round(Math.abs(tileVals[row][col]))){
				case TILE_GRASSY:
					gc.setColor(Color.GREEN);
					break;
				case TILE_MOSSY:
					gc.setColor(Color.green.darker());
					break;
				default:
					gc.setColor(Color.GREEN);
					break;
				}
				gc.fillRect((int)(tileBounds[row][col].x-viewport.getxOffset()), (int)(tileBounds[row][col].y-viewport.getyOffset()), GRID_SIZE, GRID_SIZE);
			}
		}
	}
}
