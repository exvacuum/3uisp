package game;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import hsa2x.GraphicsConsole;

@SuppressWarnings("serial")
class Monster extends Rectangle{
	
	//Monster Types
	static final int GHOUL = 0;
	static final int GOBLIN = 1;
	static final int FIRE_ELEMENTAL = 2;
	
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
	
	//Position, direction, velocity, and health
	double x, y, dx, dy, dir, v, hp, mhp;
	
	//Special variables for enemies that shoot
	ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	ArrayList<Projectile> x_projectiles = new ArrayList<Projectile>();
	boolean canFire = true;
	int fireRateDelay;
	
	//Special variables for enemies that camp
	double aggroRad, deAggroRad;
	boolean camping;
	
	//Booleans for attributes
	boolean collides = false;
	boolean shoots = false;
	boolean camps = false;
	
	//Invincibility boolean (For I-frames)
	boolean invincible = false;
	
	//Direction + magnitude (Component vectors I guess)
	double dxmag, dymag;
	
	//Color, Default Color
	Color color;
	Color dColor;
	
	//Viewport
	Viewport viewport;
	
	//Boolean to tell whether to delete this
	boolean remove = false;
	
	//Retargeting boolean
	boolean shouldReaquire = false;
	
	//Set color back to normal after hit frames
	class HitControl extends TimerTask{
		public void run(){
			invincible = false;
			color = dColor;
		}
	}
	
	//Allow retargeting of player
	class ReaquireControl extends TimerTask{
		public void run(){
			if(!player.dead){
				targetPlayer();
			}
		}
	}
	
	//Fire Rate Control
	private class FireRateControl extends TimerTask 
	{ 
	    public void run() 
	    { 
	        canFire = true;
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
		
		//Target Player
		targetPlayer();
	    
	    //Start reaquisition timer real quick
	    Timer reaquireTimer = new Timer();
		TimerTask reaquireTask = new ReaquireControl();
		reaquireTimer.schedule(reaquireTask, 100, 100);
	    
	    //Specialize the monster
	    switch(type){
	    case GHOUL:
	    	hp = 2;
	    	v = 1.5;
	    	mhp = hp;
	    	dColor = new Color(100,0,100,200);
	    	color = dColor;
	    	collides = false;
	    	camps = false;
	    	break;
	    case GOBLIN:
	    	hp = 5;
	    	v = 1;
	    	mhp = hp;
	    	dColor = new Color(0,100,0);
	    	color = dColor;
	    	collides = true;
	    	camps = false;
	    	break;
	    case FIRE_ELEMENTAL:
	    	hp = 1;
	    	v = 1;
	    	mhp = hp;
	    	dColor = new Color(200,100,0,200);
	    	color = dColor;
	    	collides = false;
	    	camps = true;
	    	shoots = true;
	    	aggroRad = 100;
	    	deAggroRad = 300;
	    	fireRateDelay = 3000;
	    }
	}
	
	void draw(){
		//Draw the Monster if on screen
		if((Math.abs(player.x-x)<700)&&(Math.abs(player.y-y)<500)){
			gc.setColor(color);
			gc.fillRect((int)(x-viewport.getxOffset()), (int)(y-viewport.getyOffset()), width, height);
		}
	}
	
	void drawGUI(){
		//If the monster has been hurt, and is on screen, draw a healthbar
		if((Math.abs(player.x-x)<700)&&(Math.abs(player.y-y)<500)){
			if(hp!=mhp){
				gc.setColor(Color.RED);
				gc.fillRect((int)(x-viewport.getxOffset()), (int)(y-15-viewport.getyOffset()), width, 10);
				gc.setColor(Color.GREEN);
				gc.fillRect((int)(x-viewport.getxOffset()), (int)(y-15-viewport.getyOffset()), (int)(width*(hp/mhp)), 10);
			}
		}
	}
	
	//Move
	void move(){
		if(!camping){
			//Monster's old position
			double oldx = x;
			double oldy = y;
			
			//Move Horizontally
			x += dx*v;
			
			//Horizontal collision
			collisions(oldx, oldy);
			
			//Monster's old position
			oldx = x;
			oldy = y;
			
			/*Now we must repeat the process in the y-axis (this allows the preservation of motion in one component when moving diagonally)
			Move Vertically */
			y += dy*v;
			
			//Vertical collision
			collisions(oldx, oldy);
			
			//Collisions for if the monster tries to leave the map.
			if(x<-World.WORLD_SIZE/2+(TheCalm.VIEW_H/2)) x = -World.WORLD_SIZE/2+(TheCalm.VIEW_H/2);
			if((x+width)>World.WORLD_SIZE/2+(TheCalm.VIEW_H/2)) x = World.WORLD_SIZE/2-width+(TheCalm.VIEW_H/2);
			if(y<-World.WORLD_SIZE/2+(TheCalm.VIEW_V/2)) y = -World.WORLD_SIZE/2+(TheCalm.VIEW_V/2);
			if((y+height)>World.WORLD_SIZE/2+(TheCalm.VIEW_V/2)) y = World.WORLD_SIZE/2-height+(TheCalm.VIEW_V/2);
			
			
			//Unstick stuck monsters
			if(isStuck()&&collides){
				x++;
			}
			
			//Set Bounds
			setBounds((int)x,(int)y,width,height);
		}
		
		//Camp
		if(camps){
			if(Math.abs(x-player.x)<=aggroRad||Math.abs(y-player.y)<=aggroRad){
				camping=true;
			}else if(Math.abs(x-player.x)>=deAggroRad||Math.abs(y-player.y)>=deAggroRad){
				camping = false;
			}
		}
		
		//Shoot
		if((shoots&&canFire&&(!camps||camping))){
			Projectile p = new Projectile((int)x+12,(int)y+12, player, viewport, gc, 2, Projectile.FIREBALL);
			projectiles.add(p);
			canFire = false;
			Timer fireRateTimer = new Timer();
			TimerTask fireRateTask = new FireRateControl();
			fireRateTimer.schedule(fireRateTask, fireRateDelay);
		}
	}
	
	//Respond to being hit with melee (knockback, flash red)
		void hurt(){
			if(!invincible) {
				invincible = true;
				hp--;
				color = new Color(100,0,0);
				Timer hitTimer = new Timer();
				TimerTask hitTask = new HitControl();
				hitTimer.schedule(hitTask, 200);
			}
		}
	
	//Respond to being hit by bullet (knockback, flash red)
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
			invincible = true;
			color = new Color(100,0,0);
			Timer hitTimer = new Timer();
			TimerTask hitTask = new HitControl();
			hitTimer.schedule(hitTask, 200);
	}
	
	void collisions(double oldx, double oldy){
		if(collides){
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
	
	void targetPlayer(){
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
