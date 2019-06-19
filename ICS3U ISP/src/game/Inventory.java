package game;

import java.awt.Color;
import java.awt.Rectangle;

import hsa2x.GraphicsConsole;


public class Inventory {
	
	//Is inventory opened?
	boolean active = false;
	
	//Empty slot
	static final int EMPTY = 9999;
	
	//Player Selections
	int selected = EMPTY;
	int hotBarCol = 0;
	int hotBarColVal = EMPTY;
	int chosenRecipe = 4;
	int tab = 0;
	
	//Materials
	static final int MAT_SOUL = 0;
	static final int MAT_WOOD = 1;
	static final int MAT_STONE = 2;
	static final int MAT_FIRE = 3;
	
	//Products
	static final int PROD_METAL = 4;
	static final int PROD_COAL = 5;
	static final int PROD_STEEL = 6;
	static final int PROD_SAWN_LOG = 7;
	
	//Tabs
	static final int TAB_INV = 0;
	static final int TAB_CRAFTING = 1;
	boolean invHover, craftingHover, craftHover, quitHover;
	
	//Actual Inventory Slots
	int[][] slots = new int[4][10];
	int[] amounts = new int[40];
	
	//Graphics Console
	GraphicsConsole gc;
	
	//Player
	Player player;
	
	//Construct Inventory
	Inventory(GraphicsConsole gc, Player player){
		this.gc = gc;
		this.player = player;
		for(int row = 0; row < 4; row++){
			for(int col = 0; col < 10; col++){
				slots[row][col]=EMPTY;
			}
		}
	}
	
	//Add Item to Inventory
	void add(int itemID){
		boolean hasItem = false;
		//Test if item is already in inventory or the currently picked up item by player
		findexisting:{
			for(int row = 0; row < 4; row++){
				for(int col = 0; col < 10; col++){
					if(slots[row][col]==itemID||selected==itemID){
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
		if(active && !player.dead){
			
			//Bounds for quit button and tabs, as well as mouse position
			int mx = gc.getMouseX();
			int my = gc.getMouseY();
			boolean yRange = (my>20&&my<70);
			Rectangle quitRect = new Rectangle(20, TheCalm.VIEW_V-40, 50, 20);
			quitHover = quitRect.contains(mx, my);
			invHover = (mx>TheCalm.VIEW_H/2+20&&mx<TheCalm.VIEW_H/2+20+(TheCalm.VIEW_H/2-40)/2&&yRange);
			craftingHover = (mx>TheCalm.VIEW_H/2+20+(TheCalm.VIEW_H/2-40)/2&&mx<TheCalm.VIEW_H/2+20+2*(TheCalm.VIEW_H/2-40)/2&&yRange);
			
			//Button logic when clicked
			if(quitHover){
				if(mouseButtonClicked(0)){
					System.exit(0);
				}
			}
			if(invHover){
				if(mouseButtonClicked(0)){
					tab = TAB_INV;
				}
			}
			if(craftingHover){
				if(mouseButtonClicked(0)){
					tab = TAB_CRAFTING;
				}
			}

			switch(tab){
			case TAB_INV:
				
				//Inventory slot management
				
				//Hotbar
				boolean xRange = mx>TheCalm.VIEW_H/2+30&&mx<TheCalm.VIEW_H/2+30+(9*(TheCalm.VIEW_H/2-60)/10)+(TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10;
				if(xRange&&my>90&&my<90+(TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10){
					int gx = Math.abs(TheCalm.VIEW_H/2+30-mx)/((TheCalm.VIEW_H/2-60)/10);
					transfer(gx,0);
				}
				
				//Rest of inventory
				if(xRange&&my>90+2*(TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10&&my<90+5*(TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10-2){
					int gx = Math.abs(TheCalm.VIEW_H/2+30-mx)/((TheCalm.VIEW_H/2-60)/10);
					int gy = Math.abs(90+((TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10)-my)/((TheCalm.VIEW_H/2-60)/10);
					transfer(gx,gy);
				}
				break;
			case TAB_CRAFTING:
				
				//"Craft" button bounds
				Rectangle craftingButtonRect = new Rectangle(TheCalm.VIEW_H/2+120, 350, TheCalm.VIEW_H/2-240, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/5)/10);
				craftHover = craftingButtonRect.contains(mx,my);
				
				//Recipe buttons
				for(int col = 0; col < 4; col++){
					Rectangle rect = new Rectangle(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/4), 90, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/5)/5, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/5)/5);
					if(rect.contains(mx,my)){
						
						//Select chosen recipe for crafting
						if(mouseButtonClicked(0)){
							chosenRecipe = col+4;
						}
					}
				}
				
				//Craft current recipe if possible
				if(craftHover){
					if(mouseButtonClicked(0)&&canCraft(chosenRecipe)){
						craft(chosenRecipe);
					}
				}
				break;
			}
		}
	}
	
	//Craft Items
	void craft(int item){
		switch(item){
		case PROD_METAL:
			for(int i = 0; i < 5; i++){
				remove(MAT_STONE);
			}
			remove(MAT_FIRE);
			add(PROD_METAL);
			break;
		case PROD_COAL:
			for(int i = 0; i < 2; i++){
				remove(MAT_WOOD);
			}
			remove(MAT_FIRE);
			add(PROD_COAL);
			break;
		case PROD_STEEL:
			remove(PROD_METAL);
			remove(PROD_COAL);
			for(int i = 0; i < 2; i++){
				add(PROD_STEEL);
			}
			break;
		case PROD_SAWN_LOG:
			remove(PROD_METAL);
			remove(MAT_WOOD);
			for(int i = 0; i < 5; i++){
				add(PROD_SAWN_LOG);
			}
			break;
		}
	}
	
	//Transfer items from cursor to slot
	void transfer(int gx, int gy){
		if(mouseButtonClicked(0)){
				int temp = selected;
				selected = slots[gy][gx];
				slots[gy][gx] = temp;
		}
	}
	
	//Draw inventory
	void draw(){
		if(active&& !player.dead){
			
			//Dim screen
			gc.setColor(TheCalm.COL_TRANS_BLACK);
			gc.fillRect(0, 0, TheCalm.VIEW_H, TheCalm.VIEW_V);
			
			//Hover effect
			if(quitHover){
				gc.fillRect(20, TheCalm.VIEW_V-40, 50, 20);
			}
			if(invHover){
				gc.fillRect(TheCalm.VIEW_H/2+20, 20, (TheCalm.VIEW_H/2-40)/2, 50);
			}
			if(craftingHover){
				gc.fillRect(TheCalm.VIEW_H/2+20+(TheCalm.VIEW_H/2-40)/2, 20, (TheCalm.VIEW_H/2-40)/2, 50);
			}
			
			//Draw GUI "frame"
			gc.setColor(Color.WHITE);
			gc.setStroke(3);
			gc.drawRect(TheCalm.VIEW_H/2+20, 20, TheCalm.VIEW_H/2-40, TheCalm.VIEW_V-40);
			gc.drawRect(TheCalm.VIEW_H/2+20, 20, (TheCalm.VIEW_H/2-40)/2, 50);
			gc.drawRect(TheCalm.VIEW_H/2+20+(TheCalm.VIEW_H/2-40)/2, 20, (TheCalm.VIEW_H/2-40)/2, 50);
			gc.drawRect(20, TheCalm.VIEW_V-40, 50, 20);
			gc.setFont(TheCalm.HUD_FONT);
			gc.drawString("Inventory", TheCalm.VIEW_H/2+30, 50);
			gc.drawString("Crafting", TheCalm.VIEW_H/2+30+(TheCalm.VIEW_H/2-40)/2, 50);
			gc.drawString("Quit", 30, TheCalm.VIEW_V-25);
			gc.setStroke(1);
			
			switch(tab){
			case TAB_INV:
				
				//Hotbar
				for(int col = 0; col < 10; col++){
					gc.setColor(Color.BLACK);
					gc.fillRect(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 90, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
					gc.setColor(Color.WHITE);
					gc.drawRect(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 90, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
					
					//Draw icons in occupied slots
					if(slots[0][col]!=EMPTY){
						drawItemIcon(slots[0][col],TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 90, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
						int amt = amounts[slots[0][col]];
						gc.setColor(Color.WHITE);
						gc.drawString(String.format("%3d", amt), TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 110);
					}
				}
				
				//Inventory
				for(int row = 1; row < 4; row++){
					for(int col = 0; col < 10; col++){
						gc.setColor(Color.BLACK);
						gc.fillRect(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 90+(row+1)*(TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
						gc.setColor(Color.WHITE);
						gc.drawRect(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 90+(row+1)*(TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
						
						//Draw icons in occupied slots
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
				break;
			case TAB_CRAFTING:
				
				//Products
				for(int col = 0; col < 4; col++){
					gc.setColor(Color.BLACK);
					gc.fillRect(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/4), 90, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/5)/5, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/5)/5);
					gc.setColor(Color.WHITE);
					gc.setStroke(chosenRecipe == col+4 ? 3:1);
					gc.drawRect(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/4), 90, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/5)/5, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/5)/5);	
					drawItemIcon(col+4, TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/4), 90, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/5)/5, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/5)/5);
				}
				
				//Recipe
				gc.setColor(Color.BLACK);
				gc.fillRect(TheCalm.VIEW_H/2+30, 300, TheCalm.VIEW_H/2-60, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/5)/5);
				gc.setColor(Color.WHITE);
				gc.setStroke(1);
				gc.drawRect(TheCalm.VIEW_H/2+30, 300, TheCalm.VIEW_H/2-60, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/5)/5);
				drawRecipe(chosenRecipe);
				
				//Crafting Button
				gc.setColor(canCraft(chosenRecipe) ? Color.GREEN : Color.RED);
				gc.fillRect(TheCalm.VIEW_H/2+120, 350, TheCalm.VIEW_H/2-240, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/5)/10);
				if(craftHover){
					gc.setColor(TheCalm.COL_TRANS_BLACK);
					gc.fillRect(TheCalm.VIEW_H/2+120, 350, TheCalm.VIEW_H/2-240, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/5)/10);
				}
				gc.setColor(Color.WHITE);
				gc.drawRect(TheCalm.VIEW_H/2+120, 350, TheCalm.VIEW_H/2-240, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/5)/10);
				gc.drawString("Craft", TheCalm.VIEW_H/2+140, 365);
				break;
			}
		}else{
			
			//Draw hotbar as a part of player HUD
			gc.setFont(TheCalm.HUD_FONT);
			for(int col = 0; col < 10; col++){
				
				//Current hotbar item will have emboldened highlight
				gc.setStroke(hotBarCol == col? 3:1);
				gc.setColor(Color.BLACK);
				gc.fillRect(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 450, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
				gc.setColor(Color.WHITE);
				gc.drawRect(TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 450, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
				
				//Draw icons in occupied slots
				if(slots[0][col]!=EMPTY){
					drawItemIcon(slots[0][col],TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 450, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10, (TheCalm.VIEW_H/2-30-(TheCalm.VIEW_H/2-60)/10)/10);
					int amt = amounts[slots[0][col]];
					gc.setColor(Color.WHITE);
					gc.drawString(String.format("%3d", amt), TheCalm.VIEW_H/2+30+(col*(TheCalm.VIEW_H/2-60)/10), 470);
				}
			}
		}
	}
	
	//Drawing recipes
	void drawRecipe(int recipe){
		
		//Materials and amounts
		String title;
		int m1, m2, a1, a2, ar;
		switch(recipe){
		case PROD_METAL:
			title = "Metal";
			m1 = MAT_STONE;
			a1 = 5;
			m2 = MAT_FIRE;
			a2 = 1;
			ar = 1;
			break;
		case PROD_COAL:
			title = "Coal";
			m1 = MAT_WOOD;
			a1 = 2;
			m2 = MAT_FIRE;
			a2 = 1;
			ar = 1;
			break;
		case PROD_STEEL:
			title = "Steel";
			m1 = PROD_METAL;
			a1 = 1;
			m2 = PROD_COAL;
			a2 = 1;
			ar = 2;
			break;
		case PROD_SAWN_LOG:
			title = "Sawn Log";
			m1 = MAT_WOOD;
			a1 = 1;
			m2 = PROD_METAL;
			a2 = 1;
			ar = 5;
			break;
		default:
			title = "Metal";
			m1 = MAT_STONE;
			a1 = 5;
			m2 = MAT_FIRE;
			a2 = 1;
			ar = 1;
			break;
		}
		
		//Drawing
		
		//Recipe title
		gc.setColor(Color.WHITE);
		gc.drawString(title ,TheCalm.VIEW_H/2+40, 290);
		
		//Ingredient 1
		drawItemIcon(m1, TheCalm.VIEW_H/2+40, 310, 30, 30);
		gc.setColor(Color.WHITE);
		gc.drawString(String.format("%3d", a1),TheCalm.VIEW_H/2+70, 340);
		
		//+ sign
		gc.drawLine(TheCalm.VIEW_H/2+100,  325, TheCalm.VIEW_H/2+130, 325);
		gc.drawLine(TheCalm.VIEW_H/2+115,  310, TheCalm.VIEW_H/2+115, 340);
		
		//Ingredient 2
		drawItemIcon(m2, TheCalm.VIEW_H/2+140, 310, 30, 30);
		gc.setColor(Color.WHITE);
		gc.drawString(String.format("%3d", a2),TheCalm.VIEW_H/2+170, 340);
		
		//= sign
		gc.drawLine(TheCalm.VIEW_H/2+200,  315, TheCalm.VIEW_H/2+230, 315);
		gc.drawLine(TheCalm.VIEW_H/2+200,  335, TheCalm.VIEW_H/2+230, 335);
		
		//Resulting item
		drawItemIcon(recipe, TheCalm.VIEW_H/2+240, 310, 30, 30);
		gc.setColor(Color.WHITE);
		gc.drawString(String.format("%3d", ar),TheCalm.VIEW_H/2+270, 340);
	}
	
	//Check if recipe is craftable with current resources
	boolean canCraft(int recipe){
		switch(recipe){
		case PROD_METAL:
			return(amounts[MAT_STONE]>=5&&amounts[MAT_FIRE]>=1);
		case PROD_COAL:
			return(amounts[MAT_WOOD]>=2&&amounts[MAT_FIRE]>=1);
		case PROD_STEEL:
			return(amounts[PROD_METAL]>=1&&amounts[PROD_COAL]>=1);
		case PROD_SAWN_LOG:
			return(amounts[MAT_WOOD]>=1&&amounts[PROD_METAL]>=1);
		default:
			return false;
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
		case MAT_FIRE:
			gc.setColor(TheCalm.COL_FIRE);
			gc.fillOval(x, y, width, height);
			break;
		case PROD_METAL:
			gc.setColor(Color.GRAY);
			gc.fillOval(x, y, width, height);
			break;
		case PROD_COAL:
			gc.setColor(Color.DARK_GRAY.darker().darker());
			gc.fillOval(x, y, width, height);
			break;
		case PROD_STEEL:
			gc.setColor(Color.GRAY.brighter());
			gc.fillOval(x, y, width, height);
			break;
		case PROD_SAWN_LOG:
			gc.setColor(Color.ORANGE.darker());
			gc.fillRect(x+4, y+4, width-8, height-8);
			break;
		}
	}
	
	//Get if mouse was clicked this step
	boolean mouseButtonClicked(int button){
		return gc.getMouseClick()>0 && gc.getMouseButton(button);
	}
}
