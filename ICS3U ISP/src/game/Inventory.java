package game;

import java.awt.Color;

import hsa2x.GraphicsConsole;


public class Inventory {
	
	//Is inventory opened?
	boolean active = false;
	
	//Empty slot
	static final int EMPTY = 9999;
	
	//Materials
	int selected = EMPTY;
	static final int MAT_SOUL = 0;
	static final int MAT_WOOD = 1;
	static final int MAT_STONE = 2;
	
	//TABS
	int tab = 0;
	static final int TAB_INV = 0;
	static final int TAB_CRAFTING = 1;
	static final int TAB_PLAYER = 2;
	
	//Actual Inventory Slots
	int[][] slots = new int[4][10];
	int[] amounts = new int[40];
	
	//Graphics Console
	GraphicsConsole gc;
	
	//Construct Inventory
	Inventory(GraphicsConsole gc){
		this.gc = gc;
		for(int row = 0; row < 4; row++){
			for(int col = 0; col < 10; col++){
				slots[row][col]=EMPTY;
			}
		}
	}
	
	//Add Item to Inventory
	void add(int itemID){
		boolean hasItem = false;
		//Test if item is already in inventory
		findexisting:{
			for(int row = 0; row < 4; row++){
				for(int col = 0; col < 10; col++){
					if(slots[row][col]==itemID){
						amounts[itemID]++;
						hasItem = true;
						break findexisting;
					}
				}
			}
		}
		
		//Tie item to first empty slot
		if(!hasItem){
			findempty:{
				for(int row = 0; row < 4; row++){
					for(int col = 0; col < 10; col++){
						if(slots[row][col]==EMPTY){
							slots[row][col] = itemID;
							amounts[itemID]++;
							break findempty;
						}
					}
				}
			}
		}
	}
	
	//Remove Item from Inventory
	void remove(int itemID){
		
		//Remove item from inventory
		if(amounts[itemID]>0){
			amounts[itemID]--;
			
			//Free inventory space if empty
			if(amounts[itemID]==0){
				freespace:{
					for(int row = 0; row < 4; row++){
						for(int col = 0; col < 10; col++){
							if(slots[row][col]==itemID){
								slots[row][col]=EMPTY;
								break freespace;
							}
						}
					}
				}
			}
		}
	}
	
	//Inventory Management
	void management(){
		if(active){
			//System.out.println(gc.getMouseButton(0));
			int mx = gc.getMouseX();
			int my = gc.getMouseY();
			boolean xRange = mx>=TheCalm.VIEW_H/2+30&&mx<=TheCalm.VIEW_H/2+30+(9*(TheCalm.VIEW_H/2-60)/10)+(TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10;
			if(xRange&&my>=90&&my<=90+(TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10){
				int gx = Math.abs(TheCalm.VIEW_H/2+30-mx)/((TheCalm.VIEW_H/2-60)/10);
				transfer(gx,0);
			}
			if(xRange&&my>=90+2*(TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10&&my<=90+5*(TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10){
				int gx = Math.abs(TheCalm.VIEW_H/2+30-mx)/((TheCalm.VIEW_H/2-60)/10);
				int gy = Math.abs(90+((TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10)-my)/((TheCalm.VIEW_H/2-60)/10);
				System.out.println(gx + ", " + gy);
				transfer(gx,gy);
			}
		}
	}
	
	//Transfer items
	void transfer(int gx, int gy){
		if(mouseButtonClicked(0)){
			if(slots[gy][gx]!=EMPTY&&selected==EMPTY){
				selected = slots[gy][gx];
				slots[gy][gx] = EMPTY;
			}else if(slots[gy][gx]==EMPTY&&selected!=EMPTY){
				slots[gy][gx]= selected;
				selected = EMPTY;
			}			
		}
	}
	
	//Draw Inventory
	void draw(){
		if(active){
			gc.setColor(TheCalm.COL_TRANS_BLACK);
			gc.fillRect(0, 0, TheCalm.VIEW_H, TheCalm.VIEW_V);
			gc.setColor(Color.WHITE);
			gc.setStroke(3);
			gc.drawRect(TheCalm.VIEW_H/2+20, 20, TheCalm.VIEW_H/2-40, TheCalm.VIEW_V-40);
			gc.drawRect(TheCalm.VIEW_H/2+20, 20, (TheCalm.VIEW_H/2-40)/3, 50);
			gc.drawRect(TheCalm.VIEW_H/2+20+(TheCalm.VIEW_H/2-40)/3, 20, (TheCalm.VIEW_H/2-40)/3, 50);
			gc.drawRect(TheCalm.VIEW_H/2+20+2*(TheCalm.VIEW_H/2-40)/3, 20, (TheCalm.VIEW_H/2-40)/3, 50);
			gc.setFont(TheCalm.HUD_FONT);
			gc.drawString("Inventory", TheCalm.VIEW_H/2+30, 50);
			gc.drawString("Crafting", TheCalm.VIEW_H/2+30+(TheCalm.VIEW_H/2-40)/3, 50);
			gc.drawString("Player", TheCalm.VIEW_H/2+30+2*(TheCalm.VIEW_H/2-40)/3, 50);
			gc.setStroke(1);
			
			//Hotbar
			for(int col = 0; col < 10; col++){
				gc.setColor(Color.BLACK);
				gc.fillRect(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 90, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
				gc.setColor(Color.WHITE);
				gc.drawRect(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 90, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
				if(slots[0][col]!=EMPTY){
					drawItemIcon(slots[0][col],TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 90, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
					int amt = amounts[slots[0][col]];
					gc.setColor(Color.WHITE);
					gc.drawString(String.format("%d", amt), TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 110);
				}
			}
			
			//Inventory
			for(int row = 1; row < 4; row++){
				for(int col = 0; col < 10; col++){
					gc.setColor(Color.BLACK);
					gc.fillRect(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 90+(row+1)*(TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
					gc.setColor(Color.WHITE);
					gc.drawRect(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 90+(row+1)*(TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
					if(slots[row][col]!=EMPTY){
						drawItemIcon(slots[row][col],TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 90+(row+1)*(TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
						int amt = amounts[slots[row][col]];
						gc.setColor(Color.WHITE);
						gc.drawString(String.format("%3d", amt), TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 110+(row+1)*(TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
					}
				}
			}
			
			//Cursor icon
			if(selected != EMPTY){
				drawItemIcon(selected, gc.getMouseX()-8, gc.getMouseY()-8, 16, 16);
			}
		}else{
			gc.setStroke(1);
			gc.setFont(TheCalm.HUD_FONT);
			for(int col = 0; col < 10; col++){
				gc.setColor(Color.BLACK);
				gc.fillRect(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 450, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
				gc.setColor(Color.WHITE);
				gc.drawRect(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 450, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
				if(slots[0][col]!=EMPTY){
					drawItemIcon(slots[0][col],TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 450, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
					int amt = amounts[slots[0][col]];
					gc.setColor(Color.WHITE);
					gc.drawString(String.format("%3d", amt), TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 470);
				}
			}
		}
	}
	
	//Item Icons
	void drawItemIcon(int itemID, int x, int y, int width, int height){
		switch(itemID){
		case MAT_SOUL:
			gc.setColor(Color.MAGENTA);
			gc.fillOval(x, y, width, height);
			break;
		case MAT_WOOD:
			gc.setColor(Color.ORANGE.darker().darker().darker().darker());
			gc.fillOval(x, y, width, height);
			break;
		case MAT_STONE:
			gc.setColor(Color.DARK_GRAY);
			gc.fillOval(x, y, width, height);
			break;
		}
	}
	
	//Get if mouse was clicked this step
	boolean mouseButtonClicked(int button){
		return gc.getMouseClick()>0 && gc.getMouseButton(button);
	}
}
