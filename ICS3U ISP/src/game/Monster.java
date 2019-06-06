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
			target = new Point((int)player.x, (int)player.y);
			dxmag = target.x - x;
		    dymag = target.y - y;
		    dir = Math.atan2(dymag, dxmag);
		    dx = Math.cos(dir);
		    dy = Math.sin(dir);
		}
	}
	
	//Move
	void move(){
		x+=dx;
		y+=dy;
		setBounds((int)x,(int)y,width,height);
	}
	
	//Respond to being hit (knockback, flash red)
	void hurt(Bullet b){
		hp--;
		color = new Color(100,0,0);
		Timer hitTimer = new Timer();
		TimerTask hitTask = new HitControl();
		hitTimer.schedule(hitTask, 200);
		x+=b.dx*3;
		y+=b.dy*3;
	}
	
}
