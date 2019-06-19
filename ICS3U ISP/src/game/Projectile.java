package game;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import hsa2x.GraphicsConsole;

@SuppressWarnings("serial")
public class Projectile extends Rectangle{
	
	//Boolean to determine whether to remove this
	boolean deleteMe = false;
	
	//When time is up, delete me
	private class DecomposeControl extends TimerTask 
	{ 
	    public void run() 
	    { 
	        deleteMe = true;
	    } 
	} 

	//Projectile Types
	static final int FIREBALL = 0;
	static final int BALL_OF_HATE = 1;
	
	int type;
	
	//Viewport
	Viewport viewport;
	
	//Graphics console
	GraphicsConsole gc;
	
	//Player
	Player player;
	
	//Color
	Color color;
	
	//Velocity, position, direction
	double v, x, y, dx, dy;
	
	Projectile(int x, int y, Player player, Viewport viewport, GraphicsConsole gc, double v, int type){
		this.x = x;
		this.y = y;
		this.player = player;
		this.viewport = viewport;
		this.gc = gc;
		this.type = type;
		this.v = v;
		
		//Type
		switch(type){
		case FIREBALL:
			color = new Color(200,100,0,200);
			width = 8;
			height = 8;
			break;
		case BALL_OF_HATE:
			color = new Color(0,0,0,200);
			width = 16;
			height = 16;
			break;
		}
		
		//Inaccuracy / Spread
	    Random random = new Random();
	    double rand = Math.random()/15 * (random .nextBoolean() ? -1 : 1);
	    
	    //Magnitudes of the direction + account for strafing with player direction
	    double dxmag = player.x + - x;
	    double dymag = player.y + - y;
	    
	    //Actual angle for direction
	    double dir = Math.atan2(dymag, dxmag);
	    
	    //x and y components with set magnitudes so that distance from player to mouse is irrelevant, with spread added
	    dx = v*Math.cos(dir+rand);
	    dy = v*Math.sin(dir+rand);
		
		//Life Timer
		Timer decomposeTimer = new Timer();
		TimerTask decomposeTask = new DecomposeControl();
		decomposeTimer.schedule(decomposeTask, 5000);
		
	}
	
	void draw(){
		
		//Draw Bullet, account for offset caused by viewport
		gc.setColor(color);
		if(type != BALL_OF_HATE) {
			gc.fillRect((int)(x-viewport.getxOffset()), (int)(y-viewport.getyOffset()), width, height);
		}else{
			gc.fillOval((int)(x-viewport.getxOffset()), (int)(y-viewport.getyOffset()), width, height);
		}
	}
	
	void move(){
		
		//Move in set direction
		x+=dx;
		y+=dy;
		setBounds((int)x,(int)y,width,height);
	}
}
