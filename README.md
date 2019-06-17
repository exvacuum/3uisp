# Survival Game: The Calm
The culminating assignment from my ICS3U course.

Trello to see current progress: https://trello.com/b/OildaD2A/ics3u-isp

# NOTE: 

HEAT'S ON! BEHIND SCHEDULE NOW BUT HOPING TO BE DONE BY JUNE 18

# ICS3U ISP ACTION PLAN

Part 1: Program Outline

The program I will be creating for this culminating assignment will be a survival game, taking place in on a large, procedurally generated map (using perlin noise or some other means). Various types of monsters will approach the player from the edge of the map , each type with varying physical capabilities and attack styles. The player will have to take advantage of their surroundings&#39; natural resources, as  well as their own skill to construct various defensive structures and increase the potency of their own attacks. The game will have a wave-based enemy spawning system, with a build phase in between (with optional time limit). The graphical style of the game will be entirely simple, colored polygons (unless time allows for custom spritework to be possible). The game will end when the player is overcome by the enemy and their HP drops to 0. A simple menu will be enough for the starting screen, with an instructions option available if necessary. Game over menu will display score, kills, time, a message, and the options to restart or quit to menu.

 Part 2: Requirements
   Objectives:

- Player Movement System
  - Direction Detection (1 for right, -1 for left, 0 for none, etc.)
  - Acceleration / Deceleration (vx += a, x+=vx, etc.)
  - Max Speed (if(Math.abs(vx)<vmax))
  - Dash &amp; Stamina (Detect if shift held, then if player is moving)
- Player Weapons System
  - Ranged Weapon
    - Projectile
      - Angle (Math.atan2() for angles)
      - Spread (+- a certain number (probably less than 10 is fine) to each component of the angle)
    - Rate of Fire (java.util.Timer)
    - Player Knockback (Just move x & y in opposite direction to bullet)
    - Weaker Enemy Knockback (Move enemy in direction of bullet)
  - Melee Weapon
    - Area of Effect (Rectangle Collisions)
    - Enemy Knockback (get magnitude of a lerp between the player and enemy and apply force in that direction)
    - Delay (Timer)
    - Exhaustion (Stamina Affects Swing Speed)
  - Ability to Toggle (Key, or Right click can automatically melee, I'm not sure what I want here yet)
  - Disable on Build Phase (Simple boolean check, toggle boolean between phases)
- Player Health Control
  - Healing (hp += n)
  - Taking Damage (hp -= n)
  - Invincibility Frames on Hit (boolean + Timer)
  - Health Bar (rectangle with width w + rectangle with width (int)((hp/maxhp)*w))
  - Death/Game Over (if(hp<=0))
- Enemy AI
  - Wave System for Spawning (Timer + Button to Skip Countdown)
    - Enemy Number (Increment every wave)
    - Enemy Variety (Use an if statement to control types of enemies allowed at that wave, Math.random() if statements to decide which type they will be)
  - Enemy Types
    - Simple Player Chasing Enemies (move to player.x, player.y)
    - Enemies with Ranged Attacks which Chase Player Until in Range (while distance to player is greater than n, move to player.x, player.y, attack from there, if player then moves to point farther than n*1.25, resume chase.)
  - Enemy Pathfinding
    - Move Around Solids (I've heard A* algorithm is good, I'll need to learn it.)
  - Aesthetic
    - Shade Variation Within Types (HSVtoRGB where hue & saturation will remain the same but value will vary)
  - Health System
    - Death (if hp<=0, remove from arraylist)
    - Health Bars (see player health system, but these will hover above enemies after they have been hit at least once.)
- Game Control
  - Main Menu
    - Start Game
      - Checkbox for &quot;Limit Build Phase&quot;, which will add time constraints to the build phase.
    - Instructions Page
    - Quit Game
  - Game Over Menu
    - Display Game Stats
    - Restart
    - Quit to Menu
  - HUD
    - Display Current Game Info During Main Game
  - Viewport Control
    - Make Viewport size, say 640x480
    - Follow Player After Certain Margin of Screen is Reached, Maybe ¼ on Either Side
  - Terrain
    - Generation
      - Grid / Constant Based Tiling
      - Method
        - Option A (Optional): Perlin Noise Generation with denser pockets of solids at higher absolute values
      - Size: 100 32x32 Places in Either Direction

1. Part 3: Optimistic Deadlines

1. Player movement will be done by June 4, 2019
2. Viewport will be done by June 4, 2019
3. Terrain generation will be done by June 9, 2019
4. Enemy pathfinding will be done by June 10, 2019
5. Player weapons system will be done by June 11, 2019
6. Player health system will be done by June 11, 2019
7. Enemy spawning will be done by June 13, 2019
8. Menus will be done by June 14, 2019
9. Player HUD will be done by June 15, 2019

  1. Optimal Completion of Project On June 15, 2019

At this point, the project will be copied.

 12. Remaining time will be used to create custom sprites for the game. If these are not    completed by the project deadline, I will submit the polygon-based copy, and will continue to  work on the sprites, releasing the final work personally at a later date.


CREDITS
--------
Perlin Noise (Noise.java)
Copyright Ken Perlin, 2001
