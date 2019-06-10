package game;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import hsa2x.GraphicsConsole;

@SuppressWarnings("serial")
class Player extends Rectangle{
	
	//Position
	double x,y;
	
	//Health
	double hp = 100;
	
	//Velocity, acceleration, stamina
	double vx = 0, vy = 0, mv = 2.0, a = 0.08, dvx = 1, dvy = 1, stam = 100, mstam = 100;
	
	//Direction, fire rate, speed multiplier
	int dx = 0, dy = 0, fireRateDelay = 100, vMulti = 1;		
	
	//Graphics Console
	GraphicsConsole gc;
	
	//Bullets List
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	
	//Boolean for regulating rate of fire
	boolean canFire = true;
	
	//Viewport
	Viewport viewport;
	
	//World
	World world;
	
	//Grid position on world
	int gx = 0;
	int gy = 0;
	
	//Tiles surrounding player,
	int leftTile, rightTile, topTile, bottomTile;
	
	//Fire Rate Control
	private class FireRateControl extends TimerTask 
	{ 
	    public void run() 
	    { 
	        canFire = true;
	    } 
	} 
			
	Player(World world, Viewport viewport, GraphicsConsole gc){
		this.world = world;
		this.gc = gc;
		this.viewport = viewport;
		
		//Put player in the center of the screen
		x = gc.getDrawWidth()/2-16;
		y = gc.getDrawHeight()/2-16;
		
		//Size
		width = 32;
		height = 32;
	}
	
	void input(){
		
		//Move
		
		//By default directions are 0 
		dx=dy=0;
		if(gc.isFocused()){
			
			//UP
			if(keyDown('W')){
				dy--;
			}
			
			//DOWN
			if(keyDown('S')){
				dy++;
			}
			
			//LEFT
			if(keyDown('A')){
				dx--;
			}
			
			//RIGHT
			if(keyDown('D')){
				dx++;
			}
			
			//Sprint if shift is pressed and moving
			if(keyDown(GraphicsConsole.VK_SHIFT)&&stam>0&&(dx!=0||dy!=0)){
				
				//Double acceleration and maximum velocity
				vMulti = 2;
				
				//Drain Stamina
				stam -= 1;
			}else{
				
				//Reset speed multiplier
				vMulti = 1;
				
				//Catch stamina when it dips below 0
				if(stam<0){
					stam = 0;
				}
				
				//Regenerate stamina
				if(stam<mstam){
					stam+=0.1;
				}else{
					
					//catch stamina when it reaches its max
					stam = mstam;
				}
			}
			
			//Fire Bullets from the center of the player, accounting for delay caused by fire rate
			if((mouseButtonDown(0)&&canFire)){
				Bullet b = new Bullet((int)x+12,(int)y+12, this, viewport, gc);
				bullets.add(b);
				canFire = false;
				Timer fireRateTimer = new Timer();
				TimerTask fireRateTask = new FireRateControl();
				fireRateTimer.schedule(fireRateTask, fireRateDelay);
			}
		}
		
		//Horizontal velocity direction
		dvx = getDirVX();
		
		//Direction to Move
		if(dx!=0){
			
			//Accelerate
			
			//If moving the opposite direction of the current velocity
			if(dx == -dvx){
				
				//"Wiggle" Decelerate before re-accelerating when changing directions
				vx+=(dx*a+dx*a)*vMulti;
				
			//If below max speed
			}else if(Math.abs(vx)<mv*vMulti){
				
				//Accelerate
				vx+=a*dx*vMulti;
			}else{
				
				//Constant velocity
				vx = mv*dx*vMulti;
			}
		}else{
			
			//Decelerate
			if(Math.abs(vx)>=a*3){
				vx -= ((a)*dvx);
			}else{
				
				//Stop
				vx = 0;
			}
		}
		
		//Vertical velocity direction
		dvy = getDirVY();
		
		//Direction to Move
		if(dy!=0){
			
			//Accelerate
			
			//If moving the opposite direction of the current velocity
			if(dy == -dvy){
				
				//"Wiggle" Decelerate before re-accelerating when changing directions
				vy+=(dy*a+dy*a)*vMulti;
				
			//If below max speed	
			}else if(Math.abs(vy)<mv*vMulti){
				
				//Accelerate
				vy+=a*dy*vMulti;
			}else{
				
				//Constant velocity
				vy = mv*dy*vMulti;
			}
		}else{
			
			//Decelerate
			if(Math.abs(vy)>=a*3){
				vy -= ((a)*dvy);
			}else{
				
				//Stop
				vy = 0;
			}
		}	
		
		//Player's old position
		double oldx = x;
		double oldy = y;
		
		//Move Horizontally
		x += vx;
		
		//Horizontal Collision Checking
		collisions(oldx, oldy);
		
		//Player's old position
		oldx = x;
		oldy = y;
		
		/*Now we must repeat the process in the y-axis (this allows the preservation of motion in one component when moving diagonally)
		Move Vertically */
		y += vy;
		
		//Vertical Collision Checking
		collisions(oldx, oldy);
		
		//Collisions for if the player tries to leave the map.
		if(x<-World.WORLD_SIZE/2+(TheCalm.VIEW_H/2)) x = -World.WORLD_SIZE/2+(TheCalm.VIEW_H/2);
		if((x+width)>World.WORLD_SIZE/2+(TheCalm.VIEW_H/2)) x = World.WORLD_SIZE/2-width+(TheCalm.VIEW_H/2);
		if(y<-World.WORLD_SIZE/2+(TheCalm.VIEW_V/2)) y = -World.WORLD_SIZE/2+(TheCalm.VIEW_V/2);
		if((y+height)>World.WORLD_SIZE/2+(TheCalm.VIEW_V/2)) y = World.WORLD_SIZE/2-height+(TheCalm.VIEW_V/2);
		
		//Get grid position
		gx = (int)((World.WORLD_SIZE/2-(TheCalm.VIEW_H/2)+(x+width/2))/(double)World.GRID_SIZE);
		gy = (int)((World.WORLD_SIZE/2-(TheCalm.VIEW_V/2)+(y+height/2))/(double)World.GRID_SIZE);
	}
	
	//Draw player
	void draw(){
		gc.setColor(Color.DARK_GRAY);
		gc.fillRect((int)(x-viewport.getxOffset()), (int)(y -viewport.getyOffset()),width,height);
	}
	
	//Draw player's GUI/HUD elements
	void drawGUI(){
		gc.setColor(Color.RED);
		gc.fillRect(20, 20, gc.getDrawWidth()/3,20);
		gc.setColor(Color.YELLOW);
		gc.fillRect(20, 20, (int)((stam/mstam)*(gc.getDrawWidth()/3)),20);
	}
	
	//Getter for bullet arraylist
	ArrayList<Bullet> getBullets(){
		return bullets;
	}
	
	//Checking for key press, with code or char
	boolean keyDown(char key){
		return gc.isKeyDown(key);
	}
	
	boolean keyDown(int key){
		return gc.isKeyDown(key);
	}
	
	//Get if mouse was clicked this step
	boolean mouseButtonClicked(int button){
		return gc.getMouseClick()>0 && gc.getMouseButton(button);
	}
	
	//Get if mouse is held down (combine with !mouseButtonClicked() if you want it to be exclusive)
	boolean mouseButtonDown(int button){
		return gc.getMouseButton(button);
	}
	
	//Return the direction the player is moving horizontally (not the input direction)
	int getDirVX(){
		return (int)Math.signum(vx);
	}
	
	//Return the direction the player is moving vertically (not the input direction)
	int getDirVY(){
		return (int)Math.signum(vy);
	}
	
	//Collision Checking
	void collisions(double oldx, double oldy){
		
		/*Collision Checking
		 * 
		 *Basically, this retrieves the place each of the player's sides
		 *left tile and right tile return the columns either side of the player is in
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
		
		//Get the 4 grid spaces surrounding the player.
		for(int i=leftTile; i<=rightTile; i++)
		{
			for(int j=topTile; j<=bottomTile; j++)
			{
				
				//If player is inside a solid
				if(world.tileDecor[j][i]!=World.DECO_NONE){
					
					//Cancel Movement
					x = oldx;
					y = oldy;
				}
			}
		}	
	}
}
