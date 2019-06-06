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
				
				//"Wiggle"
				vx+=(dx*a+dx*a)*vMulti;
			}else if(Math.abs(vx)<mv*vMulti){
				vx+=a*dx*vMulti;
			}else{
				vx = mv*dx*vMulti;
			}
		}else{
			//Decelerate
			if(Math.abs(vx)>=a*3){
				vx -= ((a)*dvx);
			}else{
				vx = 0;
			}
		}
		//Vertical
		dvy = getDirVY();
		//Direction to Move
		if(dy!=0){
			//Accelerate
			if(dy == -dvy){
				vy+=(dy*a+dy*a)*vMulti;
			}else if(Math.abs(vy)<mv*vMulti){
				vy+=a*dy*vMulti;
			}else{
				vy = mv*dy*vMulti;
			}
		}else{
			//Decelerate
			if(Math.abs(vy)>=a*3){
				vy -= ((a)*dvy);
			}else{
				vy = 0;
			}
		}	
		
		double oldx = x;
		double oldy = y;
		x += vx;
		
		leftTile = (int)((1600-(TheCalm.VIEW_H/2)+(x+1))/(double)World.GRID_SIZE);
		rightTile = (int)((1600-(TheCalm.VIEW_H/2)+(x+width-1))/(double)World.GRID_SIZE);
		topTile = (int)((1600-(TheCalm.VIEW_V/2)+(y+1))/(double)World.GRID_SIZE);
		bottomTile = (int)((1600-(TheCalm.VIEW_V/2)+(y+height-1))/(double)World.GRID_SIZE);
		
		if(leftTile < 0) leftTile = 0;
		if(rightTile > World.GRID_NUM-1) rightTile = World.GRID_NUM-1;
		if(topTile < 0) topTile = 0;
		if(bottomTile > World.GRID_NUM-1) bottomTile = World.GRID_NUM-1;
		
		for(int i=leftTile; i<=rightTile; i++)
		{
			for(int j=topTile; j<=bottomTile; j++)
			{
				if(world.tileDecor[j][i]!=World.DECO_NONE){
					x = oldx;
					y = oldy;
				}
			}
		}	
		
		oldx = x;
		oldy = y;
		y += vy;
		
		leftTile = (int)((1600-(TheCalm.VIEW_H/2)+(x+1))/(double)World.GRID_SIZE);
		rightTile = (int)((1600-(TheCalm.VIEW_H/2)+(x+width-1))/(double)World.GRID_SIZE);
		topTile = (int)((1600-(TheCalm.VIEW_V/2)+(y+1))/(double)World.GRID_SIZE);
		bottomTile = (int)((1600-(TheCalm.VIEW_V/2)+(y+height-1))/(double)World.GRID_SIZE);
		
		if(leftTile < 0) leftTile = 0;
		if(rightTile > World.GRID_NUM-1) rightTile = World.GRID_NUM-1;
		if(topTile < 0) topTile = 0;
		if(bottomTile > World.GRID_NUM-1) bottomTile = World.GRID_NUM-1;
		
		for(int i=leftTile; i<=rightTile; i++)
		{
			for(int j=topTile; j<=bottomTile; j++)
			{
				if(world.tileDecor[j][i]!=World.DECO_NONE)
				{
					x = oldx;
					y = oldy;
				}
			}
		}	
		
		if(x<-1600+(TheCalm.VIEW_H/2)){
			x = -1600+(TheCalm.VIEW_H/2);
		}
		if((x+width)>1600+(TheCalm.VIEW_H/2)){
			x = 1600-width+(TheCalm.VIEW_H/2);
		}
		if(y<-1600+(TheCalm.VIEW_V/2)){
			y = -1600+(TheCalm.VIEW_V/2);
		}
		if((y+height)>1600+(TheCalm.VIEW_V/2)){
			y = 1600-height+(TheCalm.VIEW_V/2);
		}
	}
	
	void draw(){
		gc.setColor(Color.DARK_GRAY);
		gc.fillRect((int)(x-viewport.getxOffset()), (int)(y -viewport.getyOffset()),width,height);
	}
	
	void drawGUI(){
		gc.setColor(Color.RED);
		gc.fillRect(20, 20, gc.getDrawWidth()/3,20);
		gc.setColor(Color.YELLOW);
		gc.fillRect(20, 20, (int)((stam/mstam)*(gc.getDrawWidth()/3)),20);
	}
	
	ArrayList<Bullet> getBullets(){
		return bullets;
	}
	
	boolean keyDown(char key){
		return gc.isKeyDown(key);
	}

	boolean keyDown(int key){
		return gc.isKeyDown(key);
	}
	
	boolean mouseButtonClicked(int button){
		return gc.getMouseClick()>0 && gc.getMouseButton(button);
	}
	
	boolean mouseButtonDown(int button){
		return gc.getMouseButton(button);
	}
	
	int getDirVX(){
		return (int)Math.signum(vx);
	}
	
	int getDirVY(){
		return (int)Math.signum(vy);
	}
}
