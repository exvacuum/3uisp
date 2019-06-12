package game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import hsa2x.GraphicsConsole;

/**
 * The Calm: A survival game
 * This is the main game file, so you are in the right place.
 * @author Silas Bartha
 *
 */

public class TheCalm {
	
	//Window Dimensions
	static final int VIEW_H = 640;
	static final int VIEW_V = 480;
	
	//HSA2 GraphicsConsole, the actual window / graphics controller
	GraphicsConsole gc = new GraphicsConsole(VIEW_H, VIEW_V,"The Calm.");
	
	//Game Viewport
	Viewport viewport = new Viewport(0,0, gc);
	
	//World
	World world = new World(-(World.WORLD_SIZE/2-(VIEW_H/2)),-(World.WORLD_SIZE/2-(VIEW_V/2)), viewport, gc);
	
	//Player
	Player player = new Player(world, viewport, gc);
	
	//Monsters List
	ArrayList<Monster> monsters = new ArrayList<Monster>();
	
	//Pickups List
	ArrayList<Pickup> pickups = new ArrayList<Pickup>();
	
	//Lists for deletion of game objects
	ArrayList<Monster> x_monsters = new ArrayList<Monster>();
	ArrayList<Bullet> x_bullets = new ArrayList<Bullet>();
	ArrayList<Pickup> x_pickups = new ArrayList<Pickup>();
	
	//Timers for game events
	Timer monsterSpawnTimer = new Timer();
	TimerTask monsterSpawnTask = new monsterSpawn();
	Timer monsterNumTimer = new Timer();
	TimerTask monsterNumTask = new monsterNumber();
	
	//Variables to decide whether to spawn monsters in a given step, and to control the number spawned
	boolean willSpawnMonsters = false;
	int monsterNum = 10;
	
	//Enable monster spawning
	private class monsterSpawn  extends TimerTask{
		public void run(){
			willSpawnMonsters = true;
		}
	}
	
	//Increment the number of monsters spawned during a spawn event
	private class monsterNumber  extends TimerTask{
		public void run(){
			monsterNum++;
		}
	}
	
	//MAIN
	public static void main(String[] args) {
		new TheCalm();
	}
	
	//Main game controls here
	TheCalm(){
		
		//Setup window and timers
		setup();
		
		//Game loop
		while(true){
		
			//Spawn Monsters if needed
			if(willSpawnMonsters) spawnMonsters();
			
			//Clean up the garbage
			monsters.removeAll(x_monsters);
			player.getBullets().removeAll(x_bullets);
			pickups.removeAll(x_pickups);
			
			//Logic
			step();
			synchronized(gc){
			
				//Clear screen
				gc.clear();
				
				//Draw game objects
				draw();
				
				//Draw things like bars and buttons here
				drawGUI();
			}
			
			//Let console sleep 
			gc.sleep(10);
		}
	}
	
	void setup(){
		
		//Graphics
		
		//Background
		gc.setBackgroundColor(Color.BLACK);
		gc.clear();
		
		//Mouse
		gc.enableMouse();
		gc.enableMouseMotion();
		
		//Center window
		gc.setLocationRelativeTo(null);
		
		//Monsters
		monsterSpawnTimer.schedule(monsterSpawnTask, 0, 5000);
		monsterNumTimer.schedule(monsterNumTask, 5000, 10000);
	}
	
	void step(){
		
		//Reset Trash Lists
		x_monsters = new ArrayList<Monster>();
		x_bullets = new ArrayList<Bullet>();
		
		//Monster pathfinding + movement
		for(Monster m : monsters) {
			m.move();
			m.projectiles.removeAll(m.x_projectiles);
			m.x_projectiles = new ArrayList<Projectile>();
		}
		
		//Bullet Movement
		for(Bullet b : player.getBullets()){
			b.move();
		}
		
		//Get player input / move player / player collisions
		player.input();
		
		//Clean up bullets that are too old
		for(Bullet b: player.getBullets()) {
			if(b.deleteMe){
				x_bullets.add(b);
			}
		}
		
		//Check if monsters should be hurt, or hurt player
		for(Monster m : monsters){
			for(Bullet b : player.getBullets()){
		
				//If bullet is more than grazing the monster, hurt it and destroy the bullet
				if(m.contains(b.getCenterX(),b.getCenterY())){
					m.hurt(b);
					x_bullets.add(b);
				}
			}
			
			//If hit by sword
			if(player.swinging && m.intersectsLine(player.x, player.y, player.bpx,player.bpy)&&(player.bv>=5||player.vx!=0||player.vy!=0)){
				m.hurt();
				//Hurt Player, only if not hurt itself
			}else if(m.intersects(player)&&!player.dead){
				player.hurt(m);
			}
			
			//Monster death
			if(m.hp<=0){
				Pickup p = new Pickup(m.x+8,m.y+8,gc,getViewport(),player,Pickup.PU_SOUL);
				pickups.add(p);
				x_monsters.add(m);
			}
			
			//Monster projectiles
			if(m.shoots){
				for(Projectile p: m.projectiles){
					p.move();
					//Allow player to block shots with sword
					if(player.swinging && p.intersectsLine(player.x, player.y, player.bpx,player.bpy)){
						m.x_projectiles.add(p);
					}else if(p.intersects(player)){
						player.hurt(p);
						m.x_projectiles.add(p);
					}
					if(p.deleteMe){
						m.x_projectiles.add(p);
					}
				}
			}
		}
		
		//Pickups
		for(Pickup p : pickups){
			if(p.intersects(player)){
				p.givePlayer();
				x_pickups.add(p);
			}
		}
		
		//Center viewport on player
		getViewport().trackPlayer(player);
	}
	
	void draw(){
		
		//Draw world (terrain, natural objects)
		world.draw(player.gx, player.gy);
		
		//Draw Bullets
		for(Bullet b : player.getBullets()) {
			b.draw();
		}
		
		//Draw pickups
		for(Pickup p : pickups) {
			p.draw();
		}
		
		//Draw player
		player.draw();
		
		//Draw Monsters
		for(Monster m : monsters) {
			m.draw();		
			if(m.shoots){
				for(Projectile p: m.projectiles){
					p.draw();
				}
			}
		}
	}
	
	void drawGUI(){
		
		//Monster Health Bars
		for(Monster m : monsters){
			m.drawGUI();
		}
		
		//Player GUI
		player.drawGUI();
	}
	
	//Get the current viewport
	Viewport getViewport(){
		return viewport;
	}
	
	//Spawn monsterNum number of monsters, and then disable monster spawning until timer enables it again
	void spawnMonsters(){
		for(int i = 0; i < monsterNum; i++) {
			if(monsters.size()<100){
				int x  = (int)((Math.random()*World.WORLD_SIZE-32)+1);
				int y  = (int)((Math.random()*World.WORLD_SIZE-32)+1);
				Monster m = new Monster(x, y, player, viewport, gc, (int)(Math.random()*3));
				monsters.add(m);
			}
		}
		willSpawnMonsters = false;
	}
}
