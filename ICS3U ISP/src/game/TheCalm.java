package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
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
	boolean limitTime;
	boolean waveInProgress;
	boolean skipHover;
	long initialTime;
	
	//Fonts
	public static final Font TITLE_FONT = new Font("Helvetica", Font.PLAIN, 24);
	public static final Font HUD_FONT = new Font("Dialog", Font.PLAIN, 14);
	
	//Colors
	static final Color COL_TRANS_BLACK = new Color(0,0,0,100);
	static final Color COL_CLEAR = new Color(0,0,0,0);
	static final Color COL_FIRE = new Color(200,100,0);
	
	//MAIN
	public static void main(String[] args) {
		new TheCalm();
	}
	
	//Main game controls here
	TheCalm(){
		
		//Play game over and over
		while(true){
			
			//Re-generate world
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
				
				//Drawing
				synchronized(gc){
				
					//Clear screen
					gc.clear();
					
					//Draw game objects
					draw();
					
					//Draw things like bars and buttons here
					gc.setFont(HUD_FONT);
					drawGUI();
					
					//If player dies, draw game over screen
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
			
			//Bounds for hovering over menu buttons, as well as mouse position
			int mx = gc.getMouseX();
			int my = gc.getMouseY();
			boolean inYRange = (my>=10&&my<=36);
			boolean startHover = (mx>=150&&mx<=220&&inYRange);
			boolean instructionsHover = (mx>=225&&mx<=355&&inYRange);
			boolean quitHover = (mx>=410&&mx<=460&&inYRange);
			
			//Draw Menu
			synchronized(gc){
				gc.clear();
				gc.setColor(Color.WHITE);
				gc.setFont(TITLE_FONT);
				gc.drawString("The calm.", 10, 30);
				gc.drawString("Begin, instructions, and quit.", 150, 30);
				
				//Hover Color for buttons
				gc.setColor(COL_TRANS_BLACK);
				
				//"Start" button
				if(startHover){ 
					gc.fillRect(150, 10, 70, 26);
					
					//Start game if clicked, freeze player for a little bit to avoid accidental input
					if(gc.getMouseClick()>0&&gc.getMouseButton(0)){
						Timer menuTimer = new Timer();
						TimerTask unfreezePlayer = new TimerTask(){
							public void run(){
								canMove = true;
							}
						};
						menuTimer.schedule(unfreezePlayer, 500 );
						
						//Make sure player's place on map is set properly
						player.updateGridPos();
						
						//Start Game
						break;
					}
				}
				
				//"instructions" button
				if(instructionsHover) {
					gc.fillRect(225, 10, 130, 26);
					
					//Instructions dialog
					gc.setColor(Color.WHITE);
					gc.setFont(HUD_FONT);
					gc.drawString("Survive the oncoming waves of interdimensional horrors.",150,70);
					gc.drawString("Controls:",150,100);
					gc.drawString("   LMB: Fire the bullets, pay attention to your ammunition.",150,130);
					gc.drawString("   R: Manually reloaad your weapon.",150,160);
					gc.drawString("   RMB: Swing your sword, but don't wear yourself out.", 150, 190);
					gc.drawString("   SHIFT: Run for your life, as long as you aren't too tired.", 150, 220);
					gc.drawString("   CTRL + LMB: Use your drill to break blocks around you.", 150, 250);
					gc.drawString("   CTRL + RMB: Place any blocks(Square Icons) ", 150, 280);
					gc.drawString("   currently highlighted in your hotbar.", 150, 300);
					gc.drawString("   ESC: Open inventory / crafting menu.", 150, 330);
					gc.drawString("   Right/Left Arrow Keys: Navigate hotbar.", 150, 360);
					gc.drawString("   H: Heal by absorbing collected souls.", 150, 390);
				}
				
				//"Quit" button
				if(quitHover) {
					gc.fillRect(410, 10, 50, 26);
					
					//Quit game
					if(mouseButtonClicked(0)){
						System.exit(0);
					}
				}
			}
		}
	}
	
	//Setup window and other important game objects
	void setupGame(){
		
		//Graphics
		
		//Background
		gc.setBackgroundColor(Color.BLACK);
		gc.clear();
		
		//Mouse
		gc.enableMouse();
		gc.enableMouseMotion();
		
		//Window Icon
		gc.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/icon.png")));
		
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
		
		//Vars for spawning system
		monsterNum = 20;
		wave = 1;
		timeLeft = waveTime;
		limitTime = true;
		waveInProgress = false;
	}
	
	//Make wave spawning timer kick in
	void startGame(){
		
		//Timer for wave 1
		if(limitTime){
			waveTime = (int)(100*Math.log10(wave)+30)*1000;
			timeLeft = waveTime;
			initialTime = System.currentTimeMillis();
		}
	}
	
	//Handle all game logic
	void step(){
		
		//Inventory Management
		player.inventory.management();
		
		//Wave Control
		if(waveInProgress&&monsters.size()==0){
			waveInProgress = false;
			endWave();
		}
		
		//Countdown, and skip button
		if(!waveInProgress&&limitTime){
			
			//Mouse Position
			int mx = gc.getMouseX();
			int my = gc.getMouseY();
			
			//Quit Button Bounds
			Rectangle quitRect = new Rectangle(VIEW_H-55, 5, 50, 20);
			
			//Get if cursor is over quit button
			skipHover = (quitRect.contains(mx, my)&&!player.inventory.active);
			
			//Time Left will be equal to the total time allowed minus the difference between the system's time and the time the timer was started
			timeLeft = waveTime-(System.currentTimeMillis()-initialTime);
			
			//Start Wave
			if(timeLeft <= 0 || (skipHover && mouseButtonClicked(0))){
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
				m.die();
				x_monsters.add(m);
			}
			
			//Monster projectiles
			if(m.shoots){
				for(Projectile p: m.projectiles){
					p.move();
					
					//Allow player to block shots with sword
					if(player.swinging && p.intersectsLine(player.x, player.y, player.bpx,player.bpy)){
						m.x_projectiles.add(p);
					
					//Hurt player
					}else if(p.intersects(player)){
						player.hurt(p);
						m.x_projectiles.add(p);
					}
					
					//Delete old projectiles
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
	
	//Draw game objects
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
	
	//Draw User Interface elements and HUD
	void drawGUI(){
		
		//Monster Health Bars
		for(Monster m : monsters){
			m.drawGUI();
		}
		
		//Player GUI
		player.drawGUI();
		
		//Other GUI (Wave Info Etc)
		gc.setColor(Color.WHITE);
		gc.drawString("Wave: " + wave + (waveInProgress ? "   Monsters Left: " + monsters.size() : (limitTime ? "   Starting: " + String.format("%.2f", timeLeft/1000.0) : "" )),310,15);
		
		//"Now" button
		if(!waveInProgress){
			if(skipHover){
				gc.setColor(COL_TRANS_BLACK);
				gc.fillRect(VIEW_H-55, 5, 50, 20);
			}
			gc.setColor(Color.WHITE);
			gc.setStroke(1);
			gc.drawRect(VIEW_H-55, 5, 50, 20);
			gc.drawString("Now", VIEW_H-45, 20);	
		}
		
		//Inventory
		player.inventory.draw();
	}
	
	//Get the current viewport
	Viewport getViewport(){
		return viewport;
	}
	
	//Spawn monsterNum number of monsters, and then disable monster spawning until timer enables it again
	void spawnMonsters(){
		
		//Regular monsters
		for(int i = 0; i < monsterNum; i++) {
			int x  = (int)((Math.random()*World.WORLD_SIZE)*(Math.random()>0.5 ? 1:-1));
			int y  = (int)((Math.random()*World.WORLD_SIZE)*(Math.random()>0.5 ? 1:-1));
			double rand = Math.random();
			Monster m = new Monster(x, y, player, viewport, gc, this, (rand< 0.4 ? Monster.GHOUL: (rand > 0.4 && rand < 0.75 ? Monster.GOBLIN : Monster.FIRE_ELEMENTAL)));
			monsters.add(m);
		}
		
		//Boss monsters, spawn once every five waves
		if(wave%5==0){
			
			//Number spawned increases each boss wave
			for(int i = 0; i < wave/5; i++){
				int x  = (int)((Math.random()*World.WORLD_SIZE)*(Math.random()>0.5 ? 1:-1));
				int y  = (int)((Math.random()*World.WORLD_SIZE)*(Math.random()>0.5 ? 1:-1));
				Monster b = new Monster(x,y,player,viewport,gc, this,Monster.BOSS);
				b.hp = 50*wave/5;
				b.mhp = b.hp;
				monsters.add(b);
			}
		}
	}
	
	//End of wave
	void endWave(){
		waveInProgress = false;
		
		//Increase monster number for next wave
		monsterNum+=(int)(100*Math.log10(wave)+30);
		wave++;
		if(limitTime){
			
			//More prep time each wave
			waveTime = (int)(50*Math.log10(wave)+30)*500;
			timeLeft = waveTime;
			initialTime = System.currentTimeMillis();
		}
	}
	
	//Wave Control
	void nextWave(){
		spawnMonsters();
	}
	
	//Game Over Control
	boolean gameOver(){
		
		//Mouse Location and hovering for buttons
		int mx = gc.getMouseX();
		int my = gc.getMouseY();
		boolean inYRange = (my>=VIEW_V/2-20&&my<=VIEW_V/2+4);
		boolean retryHover = (mx>=VIEW_H/2-75&&mx<=VIEW_H/2-15&&inYRange);
		boolean quitHover = (mx>=VIEW_H/2+25&&mx<=VIEW_H/2+70&&inYRange);
		
		//Dim screen
		gc.setColor(COL_TRANS_BLACK);
		gc.fillRect(0,0,VIEW_H,VIEW_V);
		
		//Draw box
		gc.setColor(Color.BLACK);
		gc.fillRect(VIEW_H/2-100,VIEW_V/2-150,200,300);
		gc.setStroke(1);
		gc.setColor(Color.WHITE);
		gc.drawRect(VIEW_H/2-100,VIEW_V/2-150,200,300);
		
		//Text
		gc.setColor(Color.RED);
		gc.setFont(TITLE_FONT);
		gc.drawCenteredString("Game over.",VIEW_H/2, VIEW_V/2-100, TITLE_FONT);
		gc.setColor(Color.WHITE);
		gc.drawString("Retry, or quit.", VIEW_H/2-75, VIEW_V/2);
		
		//Hover effect
		gc.setColor(COL_TRANS_BLACK);
		if(retryHover){
			gc.fillRect(VIEW_H/2-75, VIEW_V/2-20, 60, 24);
			
			//Reset game
			if(mouseButtonClicked(0)){
				return true;
			}
		}
		if(quitHover){
			gc.fillRect(VIEW_H/2+25, VIEW_V/2-20, 45, 24);
			
			//Exit game
			if(mouseButtonClicked(0)){
				System.exit(0);
			}
		}
		
		//Do nothing
		return false;
	}
	
	//Get if mouse was clicked this step
		boolean mouseButtonClicked(int button){
			return gc.getMouseClick()>0 && gc.getMouseButton(button);
		}
}
