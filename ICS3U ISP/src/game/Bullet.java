package game;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import hsa2x.GraphicsConsole;

@SuppressWarnings("serial")
class Bullet extends Rectangle{
		
		//Stuff to get from player
		GraphicsConsole gc;
		double x,y,dx,dy;
		Player player;
		Viewport viewport;
		
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
		
		Bullet(int x, int y, Player player, Viewport viewport, GraphicsConsole gc){
			this.x = x;
		    this.y = y;
		    this.player = player;
		    this.gc = gc;
		    this.viewport = viewport;
		    
		    //Inaccuracy / Spread
		    Random random = new Random();
		    double rand = Math.random()/15 * (random .nextBoolean() ? -1 : 1);
		    
		    //Magnitudes of the direction + account for strafing with player direction
		    double dxmag = gc.getMouseX()+viewport.getxOffset() - x + player.vx*20;
		    double dymag = gc.getMouseY()+viewport.getyOffset() - y + player.vy*20;
		    
		    //Actual angle for direction
		    double dir = Math.atan2(dymag, dxmag);
		    
		    //x and y components with set magnitudes so that distance from player to mouse is irrelevant, with spread added
		    dx = 5*Math.cos(dir+rand);
		    dy = 5*Math.sin(dir+rand);
		    
		    //Bullet size
			width = 8;
			height = 8;
			
			//Life Timer
			Timer decomposeTimer = new Timer();
			TimerTask decomposeTask = new DecomposeControl();
			decomposeTimer.schedule(decomposeTask, 5000);
		}
		
		void draw(){
			
			//Draw Bullet, account for offset caused by viewport
			gc.setColor(Color.YELLOW);
			gc.fillRect((int)(x-viewport.getxOffset()), (int)(y-viewport.getyOffset()), width, height);
		}
		
		void move(){
			
			//Move in set direction
			x+=dx;
			y+=dy;
			setBounds((int)x,(int)y,width,height);
		}
	}