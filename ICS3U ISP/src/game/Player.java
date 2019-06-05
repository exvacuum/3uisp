package game;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import hsa2x.GraphicsConsole;

@SuppressWarnings("serial")
class Player extends Rectangle{
	double x,y;
	int gx, gy;
	double hp = 100;
	double vx = 0, vy = 0, mv = 2.0, a = 0.08, dvx = 1, dvy = 1, stam = 100, mstam = 100;
	int dx = 0, dy = 0, fireRateDelay = 100, vMulti = 1;		
	GraphicsConsole gc;
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	ArrayList<Bullet> trashBullets = new ArrayList<Bullet>();
	boolean canFire = true;
	Viewport viewport;
	World world;
	int left_tile, right_tile, top_tile, bottom_tile;
	
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
		x = gc.getDrawWidth()/2-16;
		y = gc.getDrawHeight()/2-16;
		width = 32;
		height = 32;
	}
	
	void input(){
		dx=dy=0;
		if(gc.isFocused()){
			if(keyDown('W')){
				dy--;
			}
			if(keyDown('S')){
				dy++;
			}
			if(keyDown('A')){
				dx--;
			}
			if(keyDown('D')){
				dx++;
			}
			if(keyDown(GraphicsConsole.VK_SHIFT)&&stam>0&&(dx!=0||dy!=0)){
				vMulti = 2;
				stam -= 1;
			}else{
				vMulti = 1;
				if(stam<0){
					stam = 0;
				}
				if(stam<mstam){
					stam+=0.1;
				}else{
					stam = mstam;
				}
			}
			if((mouseButtonDown(0)&&canFire)){
				Bullet b = new Bullet((int)x+12,(int)y+12, this, viewport, gc);
				bullets.add(b);
				canFire = false;
				Timer fireRateTimer = new Timer();
				TimerTask fireRateTask = new FireRateControl();
				fireRateTimer.schedule(fireRateTask, fireRateDelay);
			}
		}
		//Horizontal
		dvx = getDirVX();
		//Direction to Move
		if(dx!=0){
				//Accelerate
			if(dx == -dvx){
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
		
		left_tile = (int)((1600-(TheCalm.VIEW_H/2)+(x+1))/(double)World.GRID_SIZE);
		right_tile = (int)((1600-(TheCalm.VIEW_H/2)+(x+width-1))/(double)World.GRID_SIZE);
		top_tile = (int)((1600-(TheCalm.VIEW_V/2)+(y+1))/(double)World.GRID_SIZE);
		bottom_tile = (int)((1600-(TheCalm.VIEW_V/2)+(y+height-1))/(double)World.GRID_SIZE);
		
		if(left_tile < 0) left_tile = 0;
		if(right_tile > World.GRID_NUM-1) right_tile = World.GRID_NUM-1;
		if(top_tile < 0) top_tile = 0;
		if(bottom_tile > World.GRID_NUM-1) bottom_tile = World.GRID_NUM-1;
		
		for(int i=left_tile; i<=right_tile; i++)
		{
			for(int j=top_tile; j<=bottom_tile; j++)
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
		
		left_tile = (int)((1600-(TheCalm.VIEW_H/2)+(x+1))/(double)World.GRID_SIZE);
		right_tile = (int)((1600-(TheCalm.VIEW_H/2)+(x+width-1))/(double)World.GRID_SIZE);
		top_tile = (int)((1600-(TheCalm.VIEW_V/2)+(y+1))/(double)World.GRID_SIZE);
		bottom_tile = (int)((1600-(TheCalm.VIEW_V/2)+(y+height-1))/(double)World.GRID_SIZE);
		
		if(left_tile < 0) left_tile = 0;
		if(right_tile > World.GRID_NUM-1) right_tile = World.GRID_NUM-1;
		if(top_tile < 0) top_tile = 0;
		if(bottom_tile > World.GRID_NUM-1) bottom_tile = World.GRID_NUM-1;
		
		for(int i=left_tile; i<=right_tile; i++)
		{
			for(int j=top_tile; j<=bottom_tile; j++)
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
