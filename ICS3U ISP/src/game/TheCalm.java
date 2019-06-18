package game;

import java.awt.Color;
import java.awt.Font;
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
	World world;
	
	//Player
	Player player;
	
	//Movement restriction var
	boolean canMove;
	
	//Monsters List
	ArrayList<Monster> monsters;
	
	//Pickups List
	ArrayList<Pickup> pickups;
	
	//Lists for deletion of game objects
	ArrayList<Monster> x_monsters;
	ArrayList<Bullet> x_bullets;
	ArrayList<Pickup> x_pickups;
	
	//Monster Control Variables
	Timer monsterSpawnTimer;
	Timer waveTimer;
	int monsterNum;
	int wave;
	int waveTime;
	long timeLeft;
	double completion;
	boolean limitTime;
	boolean waveInProgress;
	long initialTime;
	
	//Fonts
	public static final Font TITLE_FONT = new Font("Helvetica", Font.PLAIN, 24);
	public static final Font HUD_FONT = new Font("Dialog", Font.PLAIN, 14);
	
	//Colors
	static final Color COL_TRANS_BLACK = new Color(0,0,0,100);
	static final Color COL_CLEAR = new Color(0,0,0,0);
	
	//MAIN
	public static void main(String[] args) {
		new TheCalm();
	}
	
	//Main game controls here
	TheCalm(){
		while(true){
			new Noise();
			//Setup window and timers
			setupGame();
			
			//Menu
			menu();
			
			//Initiate Game
			startGame();
			
			//Game loop
			while(true){
				
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
					gc.setFont(HUD_FONT);
					drawGUI();
					
					if(player.hp<=0){
						//Draw game over screen and reset game if try again button is pressed
						if(gameOver()) break;
					}
				}
				
				//Let console sleep 
				gc.sleep(10);
			}
		}
	}
	
	//Menu
	void menu(){
		while(true){
			gc.setFont(TITLE_FONT);
			int mx = gc.getMouseX();
			int my = gc.getMouseY();
			boolean inYRange = (my>=10&&my<=36);
			boolean startHover = (mx>=150&&mx<=220&&inYRange);
			boolean instructionsHover = (mx>=225&&mx<=355&&inYRange);
			boolean quitHover = (mx>=410&&mx<=460&&inYRange);
			synchronized(gc){
				gc.clear();
				gc.setColor(Color.WHITE);
				gc.drawString("The calm.", 10, 30);
				gc.drawString("Begin, instructions, and quit.", 150, 30);
				gc.setColor(COL_TRANS_BLACK);
				if(startHover){ 
					gc.fillRect(150, 10, 70, 26);
					if(gc.getMouseClick()>0&&gc.getMouseButton(0)){
						Timer menuTimer = new Timer();
						TimerTask unfreezePlayer = new TimerTask(){
							public void run(){
								canMove = true;
							}
						};
						menuTimer.schedule(unfreezePlayer, 1000 );
						player.updateGridPos();
						break;
					}
				}
				if(instructionsHover) {
					gc.fillRect(225, 10, 130, 26);
					gc.setColor(Color.WHITE);
					gc.setFont(HUD_FONT);
					gc.drawString("Survive the oncoming waves of interdimensional horrors.",150,70);
					gc.drawString("Controls:",150,100);
					gc.drawString("   LMB: Fire the bullets, pay attention to your ammunition.",150,130);
					gc.drawString("   R: Manually reloaad your weapon.",150,160);
					gc.drawString("   RMB: Swing your sword, but don't wear yourself out.", 150, 190);
					gc.drawString("   SHIFT: Run for your life, as long as you aren't too tired.", 150, 220);
					gc.drawString("   CTRL + LMB: Use your drill to break blocks around you.", 150, 250);
				}
				if(quitHover) {
					gc.fillRect(410, 10, 50, 26);
					if(gc.getMouseClick()>0&&gc.getMouseButton(0)){
						System.exit(0);
					}
				}
			}
		}
	}
	
	void setupGame(){
		
		//Graphics
		
		//Background
		gc.setBackgroundColor(Color.BLACK);
		gc.clear();
		
		//Mouse
		gc.enableMouse();
		gc.enableMouseMotion();
		
		//Center window
		gc.setLocationRelativeTo(null);
		
		//Create World
		world = new World(-(World.WORLD_SIZE/2-(VIEW_H/2)),-(World.WORLD_SIZE/2-(VIEW_V/2)), viewport, gc);
		
		//Create Player
		player = new Player(world, viewport, gc);
		canMove = false;
		
		//Monsters Array
		monsters = new ArrayList<Monster>();
		
		//Pickups Array
		pickups = new ArrayList<Pickup>();
		
		//Lists for deletion of game objects
		x_monsters = new ArrayList<Monster>();
		x_bullets = new ArrayList<Bullet>();
		x_pickups = new ArrayList<Pickup>();
		
		//Vars for spwnaing system
		monsterNum = 20;
		wave = 1;
		timeLeft = waveTime;
		completion = 0.0;
		limitTime = true;
		waveInProgress = false;
	}
	
	void startGame(){
		//Timer for wave 1
		if(limitTime){
			waveTime = (int)(100*Math.log10(wave)+30)*1000;
			timeLeft = waveTime;
			initialTime = System.currentTimeMillis();
		}
	}
	
	void step(){
		
		//Inventory Management
		player.inventory.management();
		
		//Wave Control
		if(waveInProgress&&monsters.size()==0){
			waveInProgress = false;
			endWave();
		}
		
		//Countdown
		if(!waveInProgress&&limitTime){
			timeLeft = waveTime-(System.currentTimeMillis()-initialTime);
			if(timeLeft <= 0){
				waveInProgress = true;
				nextWave();
			}
		}
		
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
		if(canMove) player.input();
		
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
		
		//Other GUI (Wave Info Etc)
		gc.setColor(Color.BLACK);
		gc.drawString("Wave: " + wave + (waveInProgress ? "   Monsters Left: " + monsters.size() : (limitTime ? "   Starting: " + String.format("%.2f", timeLeft/1000.0) : "" )),310,15);
		
		//Inventory
		player.inventory.draw();
	}
	
	//Get the current viewport
	Viewport getViewport(){
		return viewport;
	}
	
	//Spawn monsterNum number of monsters, and then disable monster spawning until timer enables it again
	void spawnMonsters(){
		for(int i = 0; i < monsterNum; i++) {
			int x  = (int)((Math.random()*World.WORLD_SIZE/2-32)+World.WORLD_SIZE/2);
			int y  = (int)((Math.random()*World.WORLD_SIZE/2-32)+1);
			double rand = Math.random();
			Monster m = new Monster(x, y, player, viewport, gc, (rand< 0.4 ? Monster.GHOUL: (rand > 0.4 && rand < 0.75 ? Monster.GOBLIN : Monster.FIRE_ELEMENTAL)));
			monsters.add(m);
		}
		if(wave%5==0){
			Monster b = new Monster(-World.WORLD_SIZE/2,World.WORLD_SIZE/2,player,viewport,gc,Monster.BOSS);
			b.hp = 50*wave/5;
			b.mhp = b.hp;
			monsters.add(b);
		}
	}
	
	//End of wave
	void endWave(){
		waveInProgress = false;
		monsterNum+=(int)(100*Math.log10(wave)+30);
		wave++;
		if(limitTime){
			waveTime = (int)(50*Math.log10(wave)+30)*500;
			timeLeft = waveTime;
			initialTime = System.currentTimeMillis();
		}
	}
	
	//Wave Control
	void nextWave(){
		//Timers for game events
		spawnMonsters();
	}
	
	//Game Over Control
	boolean gameOver(){
		int mx = gc.getMouseX();
		int my = gc.getMouseY();
		boolean inYRange = (my>=VIEW_V/2-20&&my<=VIEW_V/2+4);
		boolean retryHover = (mx>=VIEW_H/2-75&&mx<=VIEW_H/2-15&&inYRange);
		boolean quitHover = (mx>=VIEW_H/2+25&&mx<=VIEW_H/2+70&&inYRange);
		gc.setColor(COL_TRANS_BLACK);
		gc.fillRect(0,0,VIEW_H,VIEW_V);
		gc.setColor(Color.RED);
		gc.setFont(TITLE_FONT);
		gc.drawCenteredString("Game over.",VIEW_H/2, VIEW_V/2-100, TITLE_FONT);
		gc.setColor(Color.WHITE);
		gc.drawString("Retry, or quit.", VIEW_H/2-75, VIEW_V/2);
		gc.setColor(COL_TRANS_BLACK);
		if(retryHover){
			gc.fillRect(VIEW_H/2-75, VIEW_V/2-20, 60, 24);
			if(gc.getMouseClick()>0&&gc.getMouseButton(0)){
				return true;
			}
		}
		if(quitHover){
			gc.fillRect(VIEW_H/2+25, VIEW_V/2-20, 45, 24);
			if(gc.getMouseClick()>0&&gc.getMouseButton(0)){
				System.exit(0);
			}
		}
		return false;
	}
}
