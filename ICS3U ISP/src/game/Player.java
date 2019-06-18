package game;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import hsa2x.GraphicsConsole;

@SuppressWarnings("serial")
class Player extends Rectangle{
	
	//Keys Pressed
	ArrayList<Integer> keysPressed = new ArrayList<Integer>();
	ArrayList<Character> keyCharsPressed = new ArrayList<Character>();
	
	//Position
	double x,y;
	
	//Health
	double hp = 20, mhp = 20;
	
	//Velocity, acceleration, stamina, ammo, reload speed, body heat
	double vx = 0, vy = 0, mv = 2.0, a = 0.08, dvx = 1, dvy = 1, stam = 100, mstam = 100, ammo = 10, mammo = 10, reloadSpeed = 0.1, heat = 0, mheat = 100;
	
	//Direction, fire & swing rate, ammo, speed multiplier
	int dx = 0, dy = 0, fireRateDelay = 200, swingDelay = 500, vMulti = 1;		
	
	
	//Graphics Console
	GraphicsConsole gc;
	
	//Inventory
	Inventory inventory;
	
	//Bullets List
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	
	//Booleans for regulating rate of fire and reloading state
	boolean canFire = true;
	boolean reloading = false;
	
	//Blade direction and position, and velocity
	double bdx,bdy, bpx, bpy, angle, oangle, bv;
	
	//Booleans for regulating swinging and overheating
	boolean swinging = false;
	boolean overHeated = false;
	
	//Drill boolean and position
	boolean drilling = false;
	int dgx = 0;
	int dgy = 0;
	
	//Boolean for death
	boolean dead = false;
	
	//Viewport
	Viewport viewport;
	
	//World
	World world;
	
	//Colors
	Color dColor = Color.WHITE;
	Color color = dColor;
	
	//I-frame boolean
	boolean invincible = false;
	
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
	
	//Set color back to normal after hit frames
	class HitControl extends TimerTask{
		public void run(){
			invincible = false;
			color = dColor;
		}
	}
	
	Player(World world, Viewport viewport, GraphicsConsole gc){
		this.world = world;
		this.gc = gc;
		this.viewport = viewport;
		inventory = new Inventory(gc);
		
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
		if(!dead){
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
				if(ammo>0&&!reloading&&!keyDown('R')&&!inventory.active){
					if((mouseButtonDown(0)&&canFire&&!keyDown(GraphicsConsole.VK_CONTROL))){
							Bullet b = new Bullet((int)x+12,(int)y+12, this, viewport, gc);
							bullets.add(b);
							canFire = false;
							Timer fireRateTimer = new Timer();
							TimerTask fireRateTask = new FireRateControl();
							fireRateTimer.schedule(fireRateTask, fireRateDelay);
							ammo--;
					}
				}else{
					if(!reloading){
						reloading = true;
					}else{
						if(ammo < mammo){
							ammo+=reloadSpeed;
						}else{
							ammo = mammo;
							reloading = false;
						}
					}
					
				}
				
				//Break blocks
				if(!overHeated){
					if((mouseButtonDown(0)&&keyDown(GraphicsConsole.VK_CONTROL))){
						
						heat += 3;
						drilling = true;
						
						//Angle
						angle = Math.atan2(x + 16 - (gc.getMouseX() + viewport.getxOffset()), y + 16 - (gc.getMouseY() + viewport.getyOffset()));
					    
					    //x and y components with set magnitudes so that distance from player to mouse is irrelevant
					    bdx = 25*Math.sin(angle);
					    bdy = 25*Math.cos(angle);
						
					    //Endpoint for blade
					    bpx = (x+width/2-bdx);
					    bpy = (y+height/2-bdy);
					    
					    //Drilling block
					    dgx = (int)((World.WORLD_SIZE/2-(TheCalm.VIEW_H/2)+(bpx))/(double)World.GRID_SIZE);
						dgy = (int)((World.WORLD_SIZE/2-(TheCalm.VIEW_V/2)+(bpy))/(double)World.GRID_SIZE);
						
						//Resource Collection
						switch(world.tileDecor[dgy][dgx]){
						case World.DECO_NONE:
							break;
						case World.DECO_STONE:
							world.tileDecor[dgy][dgx]=World.DECO_NONE;
							if(inventory.amounts[Inventory.MAT_STONE]<999){
								inventory.add(Inventory.MAT_STONE);
							}
							break;
						case World.DECO_TREE:
							world.tileDecor[dgy][dgx]=World.DECO_NONE;
							if(inventory.amounts[Inventory.MAT_WOOD]<999){
								inventory.add(Inventory.MAT_WOOD);
							}
							break;
						}
					}else{
						drilling = false;
					}
					if(heat>=mheat){
						overHeated = true;
					}
				}else{
					drilling = false;
				}
				
				//Melee attacks
				if(!overHeated&&!keyDown(GraphicsConsole.VK_CONTROL)){
					if(mouseButtonClicked(2)){
						heat+=10;
					}
					if(mouseButtonDown(2)){
						
						//Heat up and enable swinging
						if(bv!=0||vx!=0||vy!=0){
							heat += 0.75;
						}else{
							heat += 0.3;
						}
						swinging =  true;
						
						//Angle
						
						//Old angle
						oangle = angle;
						
						//New angle
						angle = Math.atan2(x + 16 - (gc.getMouseX() + viewport.getxOffset()), y + 16 - (gc.getMouseY() + viewport.getyOffset()));
					    
					    //x and y components with set magnitudes so that distance from player to mouse is irrelevant
					    bdx = 75*Math.sin(angle);
					    bdy = 75*Math.cos(angle);
						
					    //Endpoint for blade
					    bpx = (x+width/2-bdx);
					    bpy = (y+height/2-bdy);
					    
					    //Get swing speed
					    bv = 100*Math.abs(angle-oangle);
					    
					}else{
						swinging = false;
					}
					if(heat>=mheat){
						overHeated = true;
					}
				}else{
					swinging = false;
				}
				
				//Natural cooling (Independent of input)
				if(heat>0){
					heat-=0.25;
				}else{
					heat = 0;
					if(overHeated){
						overHeated = false;
					}
				}
				

				//HP regen
				if(hp<mhp){
					hp+=0.001;
				}else{
					hp = mhp;
				}
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
		updateGridPos();
		
		setBounds((int)x,(int)y,width,height);
		
		//Healing
		if(keyPressed('H')&&inventory.amounts[Inventory.MAT_SOUL]>0&&hp<mhp){
			inventory.remove(Inventory.MAT_SOUL);
			if(hp<mhp-1){
				hp++;
			}else{
				hp = mhp;
			}
		}
		
		//Inventory/Pause Screen
		if(keyPressed(GraphicsConsole.VK_ESCAPE)){
			inventory.active = !inventory.active;
		}
	}
	
	//Draw player
	void draw(){
		if(swinging){
			gc.setStroke(10);
			gc.setColor(Color.GRAY);
			gc.drawLine((int)(x-viewport.getxOffset()+16), (int)(y-viewport.getyOffset()+16), (int)(bpx-viewport.getxOffset()), (int)(bpy-viewport.getyOffset()));
		}
		
		if(drilling){
			gc.setStroke(15);
			gc.setColor(Color.DARK_GRAY);
			gc.drawLine((int)(x-viewport.getxOffset()+16), (int)(y-viewport.getyOffset()+16), (int)(bpx-viewport.getxOffset()+(Math.random()*3*(Math.random()>0.5 ? 1:-1))), (int)(bpy-viewport.getyOffset()+(Math.random()*3*(Math.random()>0.5 ? 1:-1))));
		}
		
		gc.setColor(dead ? Color.BLACK : color);
		gc.fillRect((int)(x-viewport.getxOffset()), (int)(y -viewport.getyOffset()),width,height);
	}
	
	//Draw player's GUI/HUD elements
	void drawGUI(){
		
		//HP
		gc.setColor(Color.BLACK);
		gc.fillRect(5, 5, 300, 10);
		gc.setColor((hp/mhp<0.2) ? Color.RED : Color.GREEN);
		gc.fillRect(5, 5, (int)((hp/mhp)*300),10);
		
		//Heat
		gc.setColor((overHeated) ? new Color(150,150,150) : new Color(255,100,0));
		gc.fillRect(5, 20, 25, 35);
		gc.setColor(Color.BLACK);
		gc.fillRect(5, 20, 25, (int)((1-(heat/mheat))*35));
		
		//Stamina
		gc.setColor(Color.BLACK);
		gc.fillRect(35, 20, gc.getDrawWidth()/3,20);
		gc.setColor(Color.YELLOW);
		gc.fillRect(35, 20, (int)((stam/mstam)*(gc.getDrawWidth()/3)),20);
		
		//Ammo
		gc.setColor(Color.BLACK);
		gc.fillRect(35, 45, gc.getDrawWidth()/4,10);
		gc.setColor(Color.BLUE.brighter());
		gc.fillRect(35, 45, (int)((ammo/mammo)*(gc.getDrawWidth()/4)),10);
	}
	
	//Getter for bullet arraylist
	ArrayList<Bullet> getBullets(){
		return bullets;
	}
	
	//Checking for key down, with code or char
	boolean keyDown(char key){
		return gc.isKeyDown(key);
	}
	
	boolean keyDown(int key){
		return gc.isKeyDown(key);
	}
	
	//Checking for key press, code or char
	boolean keyPressed(int key){
		if(gc.isKeyDown(key)&&!keysPressed.contains(key)){
			keysPressed.add(key);
			return true;
		}else{
			if(!gc.isKeyDown(key)&&keysPressed.contains(key)) keysPressed.remove(keysPressed.indexOf(key));
			return false;
		}
	}
	
	boolean keyPressed(char key){
		if(gc.isKeyDown(key)&&!keyCharsPressed.contains(key)){
			keyCharsPressed.add(key);
			return true;
		}else{
			if(!gc.isKeyDown(key)&&keyCharsPressed.contains(key)) keyCharsPressed.remove(keyCharsPressed.indexOf(key));
			return false;
		}
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
	
	//Respond to being hit by Monster (knockback, flash red)
	void hurt(Monster m){
		if(!invincible) {
			
			//Player's old position
			double oldx = x;
			double oldy = y;
			
			//Horizontal Knockback
			vx+=m.dx*5;
			
			//Horizontal Collision
			collisions(oldx, oldy);
			
			//Monster's old position
			oldx = x;
			oldy = y;
					
			//Vertical Knockback
			vy+=m.dy*5;
					
			//Vertical Collision
			collisions(oldx, oldy);
			
			hp-=5;
			invincible = true;
			color = new Color(100,0,0);
			Timer hitTimer = new Timer();
			TimerTask hitTask = new HitControl();
			hitTimer.schedule(hitTask, 1000);
			if(hp<=0){
				dead = true;
			}
		}
	}
	
	//Respond to being hit by projectile (knockback, flash red)
		void hurt(Projectile p){
			if(!invincible) {
				
				//Player's old position
				double oldx = x;
				double oldy = y;
				
				//Horizontal Knockback
				vx+=p.dx;
				
				//Horizontal Collision
				collisions(oldx, oldy);
				
				//Monster's old position
				oldx = x;
				oldy = y;
						
				//Vertical Knockback
				vy+=p.dy;
						
				//Vertical Collision
				collisions(oldx, oldy);
				
				hp-=(p.type==Projectile.BALL_OF_HATE ? 10 : 5);
				
				invincible = true;
				color = new Color(100,0,0);
				Timer hitTimer = new Timer();
				TimerTask hitTask = new HitControl();
				hitTimer.schedule(hitTask, 1000);
				if(hp<=0){
					dead = true;
				}
			}
		}
		
		void updateGridPos(){
			gx = (int)((World.WORLD_SIZE/2-(TheCalm.VIEW_H/2)+(x+width/2))/(double)World.GRID_SIZE);
			gy = (int)((World.WORLD_SIZE/2-(TheCalm.VIEW_V/2)+(y+height/2))/(double)World.GRID_SIZE);
		}
}
