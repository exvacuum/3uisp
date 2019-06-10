package game;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Timer;
import java.util.TimerTask;

import hsa2x.GraphicsConsole;

@SuppressWarnings("serial")
class Monster extends Rectangle{
	
	//Monster Types
	static final int GHOUL = 0;
	static final int GOBLIN = 1;
	
	//Point to move towards
	Point target;
	
	//Player
	Player player;
	
	//World
	World world;
	
	//Tiles surrounding monster
	int leftTile, rightTile, topTile, bottomTile;
	
	//Window
	GraphicsConsole gc;
	
	//Position, direction, and health
	double x, y, dx, dy, dir, hp, mhp;
	
	//Direction + magnitude (Component vectors I guess)
	double dxmag, dymag;
	
	//Color, Default Color
	Color color;
	Color dColor;
	
	//Viewport
	Viewport viewport;
	
	//Boolean to tell whether to delete this
	boolean remove = false;
	
	//Set color back to normal after hit frames
	class HitControl extends TimerTask{
		public void run(){
			color = dColor;
		}
	}
	
	Monster(int x, int y, Player player, Viewport viewport, GraphicsConsole gc, int type){
		this.x = x;
		this.y = y;
		this.player = player;
		this.gc = gc;
		this.viewport = viewport;
		this.world = player.world;
		
		//Size
		width = 32;
		height = 32;
		
		//Locate target
		target = new Point((int)player.x, (int)player.y);
		
		//Component vectors of direction to target
		dxmag = target.x - x;
	    dymag = target.y - y;

	    //Purely the angle to target
	    dir = Math.atan2(dymag, dxmag);
	    
	    //Components of the direction
	    dx = Math.cos(dir);
	    dy = Math.sin(dir);
	    
	    //Specialize the monster
	    switch(type){
	    case GHOUL:
	    	hp = 2;
	    	mhp = hp;
	    	dColor = new Color(100,0,100);
	    	color = dColor;
	    	break;
	    case GOBLIN:
	    	hp = 1;
	    	mhp = hp;
	    	dColor = new Color(0,100,0);
	    	color = dColor;
	    	break;
	    }
	}
	
	void draw(){
		//Draw the Monster
		gc.setColor(color);
		gc.fillRect((int)(x-viewport.getxOffset()), (int)(y-viewport.getyOffset()), width, height);
	}
	
	void drawGUI(){
		//If the monster has been hurt, draw a healthbar
		if(hp!=mhp){
			gc.setColor(Color.RED);
			gc.fillRect((int)(x-viewport.getxOffset()), (int)(y-15-viewport.getyOffset()), width, 10);
			gc.setColor(Color.GREEN);
			gc.fillRect((int)(x-viewport.getxOffset()), (int)(y-15-viewport.getyOffset()), (int)(width*(hp/mhp)), 10);
		}
	}
	
	void seek(){
		
		//1/10 chance the monster will re-target player (Saves memory)
		if(Math.random()<=.10){
			//Locate target
			target = new Point((int)player.x, (int)player.y);
			
			//Component vectors of direction to target
			dxmag = target.x - x;
		    dymag = target.y - y;

		    //Purely the angle to target
		    dir = Math.atan2(dymag, dxmag);
		    
		    //Components of the direction
		    dx = Math.cos(dir);
		    dy = Math.sin(dir);
			
		}
	}
	
	//Move
	void move(){
		
		//Monster's old position
		double oldx = x;
		double oldy = y;
		
		//Move Horizontally
		x += dx;
		
		//Horizontal collision
		collisions(oldx, oldy);
		
		//Monster's old position
		oldx = x;
		oldy = y;
		
		/*Now we must repeat the process in the y-axis (this allows the preservation of motion in one component when moving diagonally)
		Move Vertically */
		y += dy;
		
		//Vertical collision
		collisions(oldx, oldy);
		
		//Collisions for if the monster tries to leave the map.
		if(x<-World.WORLD_SIZE/2+(TheCalm.VIEW_H/2)) x = -World.WORLD_SIZE/2+(TheCalm.VIEW_H/2);
		if((x+width)>World.WORLD_SIZE/2+(TheCalm.VIEW_H/2)) x = World.WORLD_SIZE/2-width+(TheCalm.VIEW_H/2);
		if(y<-World.WORLD_SIZE/2+(TheCalm.VIEW_V/2)) y = -World.WORLD_SIZE/2+(TheCalm.VIEW_V/2);
		if((y+height)>World.WORLD_SIZE/2+(TheCalm.VIEW_V/2)) y = World.WORLD_SIZE/2-height+(TheCalm.VIEW_V/2);
		
		
		//Unstick stuck monsters
		if(isStuck()){
			x++;
		}
		
		//Set Bounds
		setBounds((int)x,(int)y,width,height);
	}
	
	//Respond to being hit (knockback, flash red)
	void hurt(Bullet b){
		
		//Monster's old position
		double oldx = x;
		double oldy = y;
		
		//Horizontal Knockback
		x+=b.dx*3;
		
		//Horizontal Collision
		collisions(oldx, oldy);
		
		//Monster's old position
		oldx = x;
		oldy = y;
				
		//Vertical Knockback
		y+=b.dy*3;
				
		//Vertical Collision
		collisions(oldx, oldy);
		
		hp--;
		color = new Color(100,0,0);
		Timer hitTimer = new Timer();
		TimerTask hitTask = new HitControl();
		hitTimer.schedule(hitTask, 200);
	}
	
	void collisions(double oldx, double oldy){
		
		/*Collision Checking
		 * 
		 *Basically, this retrieves the place each of the monster's sides
		 *left tile and right tile return the columns either side of the monster is in
		 *top tile and bottom tile return the rows on top and below the tile
		 */
		leftTile = (int)((World.WORLD_SIZE/2-(TheCalm.VIEW_H/2)+(x+1))/(double)World.GRID_SIZE);
		rightTile = (int)((World.WORLD_SIZE/2-(TheCalm.VIEW_H/2)+(x+width-1))/(double)World.GRID_SIZE);
		topTile = (int)((World.WORLD_SIZE/2-(TheCalm.VIEW_V/2)+(y+1))/(double)World.GRID_SIZE);
		bottomTile = (int)((World.WORLD_SIZE/2-(TheCalm.VIEW_V/2)+(y+height-1))/(double)World.GRID_SIZE);
		
		//Limit this system to the size of the world
		if(leftTile < 0) leftTile = 0;
		if(rightTile > World.GRID_NUM-1) rightTile = World.GRID_NUM-1;
		if(topTile < 0) topTile = 0;
		if(bottomTile > World.GRID_NUM-1) bottomTile = World.GRID_NUM-1;
		
		//Get the 4 grid spaces surrounding the monster.
		for(int i=leftTile; i<=rightTile; i++)
		{
			for(int j=topTile; j<=bottomTile; j++)
			{
				
				//If monster is inside a solid
				if(world.tileDecor[j][i]!=World.DECO_NONE){
					
					//Cancel Movement
					x = oldx;
					y = oldy;
				}
			}
		}	
	}
	
	boolean isStuck(){
		/*Collision Checking for Spawning
		 * 
		 *Basically, this retrieves the place each of the monster's sides
		 *left tile and right tile return the columns either side of the monster is in
		 *top tile and bottom tile return the rows on top and below the tile
		 */
		leftTile = (int)((World.WORLD_SIZE/2-(TheCalm.VIEW_H/2)+(x+1))/(double)World.GRID_SIZE);
		rightTile = (int)((World.WORLD_SIZE/2-(TheCalm.VIEW_H/2)+(x+width-1))/(double)World.GRID_SIZE);
		topTile = (int)((World.WORLD_SIZE/2-(TheCalm.VIEW_V/2)+(y+1))/(double)World.GRID_SIZE);
		bottomTile = (int)((World.WORLD_SIZE/2-(TheCalm.VIEW_V/2)+(y+height-1))/(double)World.GRID_SIZE);
		
		//Limit this system to the size of the world
		if(leftTile < 0) leftTile = 0;
		if(rightTile > World.GRID_NUM-1) rightTile = World.GRID_NUM-1;
		if(topTile < 0) topTile = 0;
		if(bottomTile > World.GRID_NUM-1) bottomTile = World.GRID_NUM-1;
		
		//Get the 4 grid spaces surrounding the monster.
		for(int i=leftTile; i<=rightTile; i++)
		{
			for(int j=topTile; j<=bottomTile; j++)
			{
				
				//If monster is inside a solid
				if(world.tileDecor[j][i]!=World.DECO_NONE){
					
					return true;
				}
			}
		}	
		return false;
	}
}
