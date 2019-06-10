package game;

import java.awt.Color;
import java.awt.Rectangle;

import hsa2x.GraphicsConsole;

public class World {
	
	//Game size constants
	static final int WORLD_SIZE = 3200;
	static final int GRID_SIZE = 32;
	static final int GRID_NUM = 100;
	
	//Tile type constants
	static final int TILE_GRASSY = -1;
	static final int TILE_DIRTY = 0;
	static final int TILE_MOSSY = 1;
	
	//Decoration (solids) constants
	static final int DECO_NONE = 0;
	static final int DECO_STONE = 1;
	static final int DECO_TREE = 2;
	
	//Positioning of world relative to console
	int x;
	int y;
	
	//HSA2 GraphicsConsole
	GraphicsConsole gc;
	
	//2D arrays for holding tile values (terrain type), tile decorations (solid type), and tile bounds (used for drawing)
	float[][] tileVals = new float[GRID_NUM][GRID_NUM];
	int[][] tileDecor = new int[GRID_NUM][GRID_NUM];
	Rectangle[][] tileBounds = new Rectangle[GRID_NUM][GRID_NUM];
	
	//Viewport
	Viewport viewport;
	
	
	//Constructor, initiates world generation
	World(int x, int y, Viewport viewport, GraphicsConsole gc){
		this.viewport = viewport;
		this.gc = gc;
		this.x = x;
		this.y = y;
		generate();
	}
	
	//Generate World
	void generate(){
		
		//Scale the noise, higher values increase the fuzziness of noise
		float scl = 0.1f;
		
		//For each tile in the world
		for(int row = 0; row < GRID_NUM; row++){
			for(int col = 0; col < GRID_NUM; col++){
				
				//Use Perlin noise to assign a float value to that tile (The coefficient on the end causes the values to be more or less extreme)
				tileVals[row][col] = (float)Noise.noise(row*scl,col*scl)*8;
				
				//Debug Code to see a visual of the numbers constituting the tilemap (Uncomment below println function as well)
				//System.out.printf("%2d",(int)Math.round(tileVals[row][col]));
				
				//Set bounds for each tile
				tileBounds[row][col] = new Rectangle(0,0,0,0);
				tileBounds[row][col].setBounds(x+(GRID_SIZE*col),y+(GRID_SIZE*row),GRID_SIZE,GRID_SIZE);
			}
			//System.out.println();
		}
		
		//Decoration generation
		for(int row = 0; row < GRID_NUM; row++){
			for(int col = 0; col < GRID_NUM; col++){
				
				//Leave space for player to spawn in
				if(!((row==50||row==49)&&(col==50||col==49))){
					
					//Retrieve rounded value from the Perlin noise generation for that tile
					float val = Math.round(tileVals[row][col]);
					
					//Rocks on Mossy areas, but not near the edge (TILE_MOSSY = 1)
					if(val>=2){
						
						//25% change any mossy tile sufficiently inwards will have a rock on top
						if(Math.random()>0.75){
							tileDecor[row][col] = DECO_STONE;
						}
					}
					
					//Trees on Grassy areas, but not near the edge (TILE_GRASSY = -1)
					if(val<=-2){
						
						//15% change any grassy tile sufficiently inwards will have a tree growing
						if(Math.random()>0.85){
							tileDecor[row][col] = DECO_TREE;
						}
					}
				}
			}
		}
	}
	
	//Draw the world
	void draw(){
		
		//For every tile (there should be like 10000 or something crazy like that)
		for(int row = 0; row < GRID_NUM; row++){
			for(int col = 0; col < GRID_NUM; col++){	
				
				//Check tile value and set color
				switch((int)Math.signum(Math.round(tileVals[row][col]))){
				case TILE_GRASSY:
					gc.setColor(new Color(Color.HSBtoRGB(113/360f, 0.85f, 0.87f*((float)(-tileVals[row][col])/6)+0.1f)));
					break;
				case TILE_DIRTY:
					gc.setColor(new Color(Color.HSBtoRGB(50/360f, 0.85f, 0.97f*((float)(tileVals[row][col])/6)+0.4f)));
					break;
				case TILE_MOSSY:
					gc.setColor(new Color(Color.HSBtoRGB(90/360f, 0.85f, 0.77f*((float)(tileVals[row][col])/6)+0.1f)));
					break;
				}
				//draw bg
				gc.fillRect((int)(tileBounds[row][col].x-viewport.getxOffset()), (int)(tileBounds[row][col].y-viewport.getyOffset()), GRID_SIZE, GRID_SIZE);
				
				//Check whether to draw this tile, and set color if so
				boolean drawThis = true;
				switch(tileDecor[row][col]){
				case DECO_TREE:
					gc.setColor(new Color(Color.HSBtoRGB(70/360f, 0.85f, 0.4f*((float)(-tileVals[row][col])/6)+0.5f)));
					break;
				case DECO_STONE:
					gc.setColor(new Color(Color.HSBtoRGB(0/360f, 0f, 0.3f*((float)(tileVals[row][col])/6)+0.2f)));
					break;
				default:
					drawThis = false;
					break;
				}
				
				//Draw Solids
				if(drawThis){
					gc.fillRect((int)(tileBounds[row][col].x-viewport.getxOffset()), (int)(tileBounds[row][col].y-viewport.getyOffset()), GRID_SIZE, GRID_SIZE);
				}
			}
		}
	}
}
