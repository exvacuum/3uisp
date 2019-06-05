package game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
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
	Timer monsterSpawnTimer = new Timer();
	TimerTask monsterSpawnTask = new monsterSpawn();
	Timer monsterNumTimer = new Timer();
	TimerTask monsterNumTask = new monsterNumber();
	
	int monsterNum = 1;
	
	private class monsterSpawn  extends TimerTask{
		public void run(){
			for(int i = 0; i < monsterNum; i++) {
				if(monsters.size()<100){
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
		//Monsters
		monsterSpawnTimer.schedule(monsterSpawnTask, 0, 5000);
		monsterNumTimer.schedule(monsterNumTask, 5000, 10000);
	}
	
	void step(){
		for(Iterator<Monster> mi = monsters.iterator(); mi.hasNext();){
			Monster m = mi.next();
			m.seek();
			m.move();
		}
		
		for(Bullet b : player.getBullets()){
			b.move();
		}
		
		player.input();
		
		for(Iterator<Bullet> bi = player.getBullets().iterator();bi.hasNext();){
			Bullet b = bi.next();
			if(b.deleteMe){
				bi.remove();
			}
		}
		
		for(Monster m : monsters){
			for(Iterator<Bullet> bi = player.getBullets().iterator();bi.hasNext();){
				Bullet b = bi.next();
				if(m.contains(b.getCenterX(),b.getCenterY())){
					m.hurt(b);
					bi.remove();
					m.hp--;
					if(m.hp<=0){
						m.remove = true;
					}
				}
			}
		}
		
		for(Iterator<Monster> mi = monsters.iterator(); mi.hasNext();){
			Monster m = mi.next();
			if(m.remove){
				mi.remove();
			}
		}

		getViewport().trackPlayer(player);
	}
	
	void draw(){
		world.draw();
		player.draw();
		for(Bullet b : player.getBullets()) {
			b.draw();
		}
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
