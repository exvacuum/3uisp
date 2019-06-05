package game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import hsa2x.GraphicsConsole;

public class TheCalm {
	static final int VIEW_H = 640;
	static final int VIEW_V = 480;
	int viewX;
	int viewY;
	GraphicsConsole gc = new GraphicsConsole(VIEW_H, VIEW_V,"The Calm.");
	Viewport viewport = new Viewport(0,0, gc);
	World world = new World(-(1600-(VIEW_H/2)),-(1600-(VIEW_V/2)), viewport, gc);
	Player player = new Player(world, viewport, gc);
	static ArrayList<Monster> monsters = new ArrayList<Monster>();
	ArrayList<Monster> x_monsters = new ArrayList<Monster>();
	ArrayList<Bullet> x_bullets = new ArrayList<Bullet>();
	Timer monsterSpawnTimer = new Timer();
	TimerTask monsterSpawnTask = new monsterSpawn();
	Timer monsterNumTimer = new Timer();
	TimerTask monsterNumTask = new monsterNumber();
	
	int monsterNum = 1;
	
	private class monsterSpawn  extends TimerTask{
		public void run(){
			for(int i = 0; i < monsterNum; i++) {
				if(monsters.size()<10000){
					int x  = (int)((Math.random()*640)+1);
					int y  = (int)((Math.random()*480)+1);
					Monster m = new Monster(x, y, player, viewport, gc, (int)(Math.random()*2));
					monsters.add(m);
				}
			}
		}
	}
	
	private class monsterNumber  extends TimerTask{
		public void run(){
			monsterNum++;
		}
	}
	
	public static void main(String[] args) {
		new TheCalm();
	}
	
	TheCalm(){
		setup();
		while(true){
			monsters.removeAll(x_monsters);
			player.getBullets().removeAll(x_bullets);
			step();
			synchronized(gc){
				gc.clear();
				draw();
				drawGUI();
			}
			gc.sleep(10);
		}
	}
	
	void setup(){
		//Graphics
		gc.setBackgroundColor(Color.BLACK);
		gc.clear();
		gc.enableMouse();
		gc.enableMouseMotion();
		gc.setLocationRelativeTo(null);
		//Monsters
		monsterSpawnTimer.schedule(monsterSpawnTask, 0, 5000);
		monsterNumTimer.schedule(monsterNumTask, 5000, 10000);
	}
	
	void step(){
		x_monsters = new ArrayList<Monster>();
		x_bullets = new ArrayList<Bullet>();
		for(Monster m : monsters) {
			m.seek();
			m.move();
		}
		
		for(Bullet b : player.getBullets()){
			b.move();
		}
		
		player.input();
		
		for(Bullet b: player.getBullets()) {
			if(b.deleteMe){
				x_bullets.add(b);
			}
		}
		
		for(Monster m : monsters){
			for(Bullet b : player.getBullets()){
				if(m.contains(b.getCenterX(),b.getCenterY())){
					m.hurt(b);
					x_bullets.add(b);
				}
			}
			if(m.hp<=0){
				x_monsters.add(m);
			}
		}

		getViewport().trackPlayer(player);
	}
	
	void draw(){
		world.draw();
		for(Bullet b : player.getBullets()) {
			b.draw();
		}
		player.draw();
		for(Monster m : monsters) {
			m.draw();		
		}
	}
	
	void drawGUI(){
		for(Monster m : monsters){
			m.drawGUI();
		}
		player.drawGUI();
	}
	
	Viewport getViewport(){
		return viewport;
	}
}
