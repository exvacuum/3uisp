package game;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import hsa2x.GraphicsConsole;

@SuppressWarnings("serial")
public class Projectile extends Rectangle{
	
	//Boolean to determine whether the bullet is waiting to die
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
	    
	    //Bullet size
		width = 8;
		height = 8;
		
		//Life Timer
		Timer decomposeTimer = new Timer();
		TimerTask decomposeTask = new DecomposeControl();
		decomposeTimer.schedule(decomposeTask, 5000);
		
		//Type
		switch(type){
		case FIREBALL:
			color = new Color(200,100,0,200);
			break;
		}
	}
	
	void draw(){
		
		//Draw Bullet, account for offset caused by viewport
		gc.setColor(color);
		gc.fillRect((int)(x-viewport.getxOffset()), (int)(y-viewport.getyOffset()), width, height);
	}
	
	void move(){
		
		//Move in set direction
		x+=dx;
		y+=dy;
		setBounds((int)x,(int)y,width,height);
	}
}
