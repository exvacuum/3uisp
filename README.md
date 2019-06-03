# 3uisp
The culminating assignment from my ICS3U course.

# ICS3U ISP ACTION PLAN

Part 1: Program Outline

The program I will be creating for this culminating assignment will be a survival game, taking place in on a large, procedurally generated map (using perlin noise or some other means). Various types of monsters will approach the player from the edge of the map , each type with varying physical capabilities and attack styles. The player will have to take advantage of their surroundings&#39; natural resources, as  well as their own skill to construct various defensive structures and increase the potency of their own attacks. The game will have a wave-based enemy spawning system, with a build phase in between (with optional time limit). The graphical style of the game will be entirely simple, colored polygons (unless time allows for custom spritework to be possible). The game will end when the player is overcome by the enemy and their HP drops to 0. A simple menu will be enough for the starting screen, with an instructions option available if necessary. Game over menu will display score, kills, time, a message, and the options to restart or quit to menu.

 Part 2: Requirements
   Objectives:

- Player Movement System
  - Direction Detection
  - Acceleration / Deceleration
  - Max Speed
  - Dash &amp; Stamina
- Player Weapons System
  - Ranged Weapon
    - Projectile
      - Angle
      - Spread
    - Rate of Fire
    - Player Knockback
    - Weaker Enemy Knockback
  - Melee Weapon
    - Area of Effect
    - Enemy Knockback
    - Delay
    - Exhaustion
  - Ability to Toggle
  - Disable on Build Phase
- Player Health Control
  - Healing
  - Taking Damage
  - Invincibility Frames on Hit
  - Health Bar
  - Death/Game Over
- Enemy AI
  - Wave System for Spawning
    - Enemy Number
    - Enemy Variety
  - Enemy Types
    - Simple Player Chasing Enemies
    - Enemies with Ranged Attacks which Chase Player Until in Range
  - Enemy Pathfinding
    - Move Around Solids
  - Aesthetic
    - Shade Variation Within Types
  - Health System
    - Death
    - Health Bars
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
        - Option A (Optional): Perlin Noise Generation
        - Option B: Create &quot;Tiler Machines&quot; that Will Move Around Pseudo-Randomly Placing Blocks of a Certain Type, Resolve Oddities (e.g. A Ring of sand Could Contain a Pond), Sprinkle Environmental Objects Around Afterwards
      - Size: 1000 32x32 Places in Either Direction

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
