package game;

import java.awt.Color;
import java.awt.Rectangle;

import hsa2x.GraphicsConsole;

public class World {
	static final int WORLD_SIZE = 3200;
	static final int GRID_SIZE = 32;
	static final int GRID_NUM = 100;
	
	static final int TILE_GRASSY = -1;
	static final int TILE_DIRTY = 0;
	static final int TILE_MOSSY = 1;
	
	static final int DECO_NONE = 0;
	static final int DECO_STONE = 1;
	static final int DECO_TREE = 2;
	
	int x;
	int y;
	GraphicsConsole gc;
	float[][] tileVals = new float[GRID_NUM][GRID_NUM];
	int[][] tileDecor = new int[GRID_NUM][GRID_NUM];
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
		float scl = 0.09f;
		for(int row = 0; row < GRID_NUM; row++){
			for(int col = 0; col < GRID_NUM; col++){
				tileVals[row][col] = (float)Noise.noise(row*scl,col*scl)*6;
				System.out.printf("%2d",(int)Math.round(tileVals[row][col]));
				tileBounds[row][col] = new Rectangle(0,0,0,0);
				tileBounds[row][col].setBounds(x+(GRID_SIZE*col),y+(GRID_SIZE*row),GRID_SIZE,GRID_SIZE);
			}
			System.out.println();
		}
		for(int row = 0; row < GRID_NUM; row++){
			for(int col = 0; col < GRID_NUM; col++){
				if((row!=50&&col!=50)&&(row!=49&&col!=49)){
					float val = Math.round(tileVals[row][col]);
					if(val>=2){
						if(Math.random()>0.75){
							tileDecor[row][col] = DECO_STONE;
						}
					}
					if(val<=-2){
						if(Math.random()>0.8){
							tileDecor[row][col] = DECO_TREE;
						}
					}
				}
			}
		}
	}
	
	void draw(){
		for(int row = 0; row < GRID_NUM; row++){
			for(int col = 0; col < GRID_NUM; col++){	
				switch((int)Math.signum(Math.round(tileVals[row][col]))){
				case TILE_GRASSY:
					gc.setColor(Color.GREEN);
					break;
				case TILE_DIRTY:
					gc.setColor(Color.ORANGE.darker());
					break;
				case TILE_MOSSY:
					gc.setColor(Color.GREEN.darker());
					break;
				}
				gc.fillRect((int)(tileBounds[row][col].x-viewport.getxOffset()), (int)(tileBounds[row][col].y-viewport.getyOffset()), GRID_SIZE, GRID_SIZE);
				boolean drawThis = true;
				switch(tileDecor[row][col]){
				case DECO_TREE:
					gc.setColor(Color.GREEN.darker().darker().darker());
					break;
				case DECO_STONE:
					gc.setColor(Color.DARK_GRAY.darker());
					break;
				default:
					drawThis = false;
					break;
				}
				if(drawThis){
					gc.fillRect((int)(tileBounds[row][col].x-viewport.getxOffset()), (int)(tileBounds[row][col].y-viewport.getyOffset()), GRID_SIZE, GRID_SIZE);
				}
			}
		}
	}
}
