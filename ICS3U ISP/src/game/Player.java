package game;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import hsa2x.GraphicsConsole;

@SuppressWarnings("serial")
class Player extends Rectangle{
	double x,y;
	double hp = 100;
	double vx = 0, vy = 0, mv = 2.0, a = 0.08, dvx = 1, dvy = 1, stam = 100, mstam = 100;
	int dx = 0, dy = 0, fireRateDelay = 30, vMulti = 1;		
	GraphicsConsole gc;
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	ArrayList<Bullet> trashBullets = new ArrayList<Bullet>();
	boolean canFire = true;
	Viewport viewport;
	
	private class FireRateControl extends TimerTask 
	{ 
	    public void run() 
	    { 
	        canFire = true;
	    } 
	} 
			
	Player(Viewport viewport, GraphicsConsole gc){
		this.gc = gc;
		this.viewport = viewport;
		x = gc.getDrawWidth()/2-16;
		y = gc.getDrawHeight()/2-16;
		width = 32;
		height = 32;
	}
	
	void input(){
		dx=dy=0;
		if(gc.isFocused()){
			if(keyDown('W')){
				dy--;
			}
			if(keyDown('S')){
				dy++;
			}
			if(keyDown('A')){
				dx--;
			}
			if(keyDown('D')){
				dx++;
			}
			if(keyDown(GraphicsConsole.VK_SHIFT)&&stam>0&&(dx!=0||dy!=0)){
				vMulti = 2;
				stam -= 1;
			}else{
				vMulti = 1;
				if(stam<0){
					stam = 0;
				}
				if(stam<mstam){
					stam+=0.1;
				}else{
					stam = mstam;
				}
			}
			if((mouseButtonDown(0)&&canFire)){
				Bullet b = new Bullet((int)x+16,(int)y+16, this, viewport, gc);
				bullets.add(b);
				canFire = false;
				Timer fireRateTimer = new Timer();
				TimerTask fireRateTask = new FireRateControl();
				fireRateTimer.schedule(fireRateTask, fireRateDelay);
			}
		}
		//Horizontal
		dvx = getDirVX();
		//Direction to Move
		if(dx!=0){
				//Accelerate
				if(Math.abs(vx)<mv*vMulti){
					vx+=a*dx*vMulti;
				}else{
					vx = mv*dx*vMulti;
				}
		}else{
			//Decelerate
			if(Math.abs(vx)>=a*3){
				vx -= ((a)*dvx);
			}else{
				vx = 0;
			}
		}
		//Vertical
		dvy = getDirVY();
		//Direction to Move
		if(dy!=0){
				//Accelerate
				if(Math.abs(vy)<mv*vMulti){
					vy+=a*dy*vMulti;
				}else{
					vy = mv*dy*vMulti;
				}
		}else{
			//Decelerate
			if(Math.abs(vy)>=a*3){
				vy -= ((a)*dvy);
			}else{
				vy = 0;
			}
		}
		x+=vx;
		y+=vy;
	}
	
	void draw(){
		gc.setColor(Color.DARK_GRAY);
		gc.fillRect((int)(x-viewport.getxOffset()), (int)(y -viewport.getyOffset()),width,height);
	}
	
	void drawGUI(){
		gc.setColor(Color.RED);
		gc.fillRect(20, 20, gc.getDrawWidth()/3,20);
		gc.setColor(Color.YELLOW);
		gc.fillRect(20, 20, (int)((stam/mstam)*(gc.getDrawWidth()/3)),20);
	}
	
	ArrayList<Bullet> getBullets(){
		return bullets;
	}
	
	boolean keyDown(char key){
		return gc.isKeyDown(key);
	}

	boolean keyDown(int key){
		return gc.isKeyDown(key);
	}
	
	boolean mouseButtonClicked(int button){
		return gc.getMouseClick()>0 && gc.getMouseButton(button);
	}
	
	boolean mouseButtonDown(int button){
		return gc.getMouseButton(button);
	}
	
	int getDirVX(){
		return (int)Math.signum(vx);
	}
	
	int getDirVY(){
		return (int)Math.signum(vy);
	}
}
