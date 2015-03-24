package main.entity.mob;

import main.Game;
import main.entity.item.Ammo;
import main.entity.projectile.ShotgunProjectile;
import main.entity.spawner.ParticleSpawner;
import main.graphics.AnimatedSprite;
import main.graphics.Font;
import main.graphics.Renderer;
import main.graphics.Sprite;
import main.graphics.SpriteSheet;
import main.input.Keyboard;
import main.input.Mouse;
import main.net.packets.PMove;
import main.net.packets.PShoot;

public class Player extends Mob {
	
	private Keyboard input;
	private int shootAnimate = 0;
	private boolean isShooting = false;
	private int fireRate = 0;
	private String username;
	private boolean isDead = false;
	private Font font;
	private boolean startTimer = false;
	private long timer;
	private int xSeconds;
	private int respawnRender;
	private Game game;
	//private double shootDirection; //only for sprite purposes
	
	//ammo variables:
	private final int MAX_AMMO = 50;
	private int unloadedAmmo = 10;
	private int loadedAmmo = 10;
	private int clipSize = 10;
	private int movedAmmo = 0;
	
	private int respawns = 0;
	private int stamina = 100;
	private int stamRegen = 0; //timer for stamina regeneration

	private AnimatedSprite down = new AnimatedSprite(32, 32, SpriteSheet.player_down, 3);
	private AnimatedSprite up = new AnimatedSprite(32, 32, SpriteSheet.player_up, 3);
	private AnimatedSprite right = new AnimatedSprite(32, 32, SpriteSheet.player_right, 3);
	private AnimatedSprite left = new AnimatedSprite(32, 32, SpriteSheet.player_left, 3);
	
	private AnimatedSprite chosenSprite = down;
	
	public Player(Keyboard input) {
		this.input = input;
	}

	public Player(int x, int y, Keyboard input, String username, Game game, int respawns) {
		this.x = x;
		this.y = y;
		this.input = input;
		fireRate = ShotgunProjectile.FIRE_RATE;
		speed = 1;
		health = 100;
		this.username = username;
		this.game = game;
		this.respawns = respawns;
		
		font = new Font();
	}
	
	public void update() {
		//DEATH
		if (health <= 0) {
			//stuff that should only execute once
			if (startTimer == false) {
				this.respawns--;
				timer = System.currentTimeMillis(); //time of death
				health = 0;
				hide();
				//only spawns ammo from the players not being controlled
				if (input == null) level.add(new Ammo(this.getX() >> 4, this.getY() >> 4, 10));
				Zombie temp = new Zombie(this.x, this.y, input, this.getUsername());
				level.add(temp);
				new ParticleSpawner((int)x+2, (int)y+2, 100, 100, level, Sprite.player_blood);
				isDead = true;
				startTimer = true;
			}
			
			if (this.respawns > 0) {
				if (System.currentTimeMillis() - timer >= xSeconds*1000) {
					respawnRender = 5 - xSeconds;
					if (respawnRender < 0) respawnRender = 0;
					
					if (System.currentTimeMillis() - timer > 5000) { //executes 5 seconds after death
						remove();
						Player player = new Player(56, 160, this.input, this.username, this.game, this.respawns);
						if (input != null) game.setPlayer(player);
							level.add(player);
					}
					xSeconds++; 
				}
			}
		}
		
		if (fireRate > 0) fireRate--;
		
		//only executes for player being controlled
		if (input != null && isHidden() == false) {
			
			//SPRINT
			if (input.shift && stamina > 0) {
				speed = 2;
				stamina--;
			}
			else {
				speed = 1;
				
				if (stamRegen > 5000) stamRegen = 0;
				else stamRegen++;
				
				if (stamRegen % 50 == 0) {
					stamina += 10;
					if (stamina > 100) stamina = 100;
				}
			}
			
			//RELOAD
			if (input.reload) {
				if (loadedAmmo < 10 && unloadedAmmo > 0) {
					movedAmmo = clipSize - loadedAmmo;
					if (unloadedAmmo >= movedAmmo) {
						loadedAmmo += movedAmmo;
						unloadedAmmo -= movedAmmo;
					}
					else {
						loadedAmmo += unloadedAmmo;
						unloadedAmmo = 0;
					}
				}
			}
			
			//DIRECTION
			int xx = 0, yy = 0;
			
			if (input.left) {
				dir = 3;
				xx--;
			} else if (input.right) {
				dir = 1;
				xx++;
			}
			
			if (input.up) {
				dir = 0;
				yy--;
			} else if (input.down) {
				dir = 2;
				yy++;
			}
			
			//MOVEMENT
			if (xx != 0 || yy != 0) {
				isMoving = true;
				moveSpeed(xx, yy);
				
				PMove packetMove = new PMove(this.getUsername(), this.x, this.y, this.dir);
				packetMove.writeData(Game.game.socketClient);
			}
			else isMoving = false;
			
			//SHOOT
			updateShoot();
		}
		
		//ANIMATION
		updateSprite();
		
		if (isMoving) chosenSprite.update();
		else chosenSprite.setFrame(0);
		
		if (isShooting) shootAnimate++;
		
		if (shootAnimate > 15) {
			shootAnimate = 0;
			isShooting = false;
		}
		
		isMoving = false;
	}
	
	private void updateSprite() {
		if(isShooting) {
			//chosenSprite =  Sprite.player_down_shoot;
		}
		else{
			if (dir == 3) {
				chosenSprite = left;
			}
			else if (dir == 1) {
				chosenSprite = right;
			}
			
			if (dir == 0) {
				chosenSprite = up;
			}
			else if (dir == 2) {
				chosenSprite = down;
			}			
		}
	}
	
	public void updateShoot() {
		if(Mouse.getButton()==1 && fireRate <= 0 && loadedAmmo > 0) {
			double aTrig = Mouse.getX() - Game.getWindowWidth()/2;
			double oTrig = Mouse.getY() - Game.getWindowHeight()/2;
			double direction = Math.atan2(oTrig, aTrig);
			
			//isShooting = true;
			//shoot(this.getUsername(), x, y, direction); //disabled to prevent multiple projectiles from spawning when networking
			new ParticleSpawner((int)x+2, (int)y+2, 5, 25, level, Sprite.weapon_shoot);
			
			loadedAmmo--;
			fireRate = ShotgunProjectile.FIRE_RATE;
			//shootDirection = Math.toDegrees(direction); //for sprite purposes
			
			if (input != null) {
				PShoot packetShoot = new PShoot(this.getUsername(), this.x, this.y, direction);
				packetShoot.writeData(Game.game.socketClient);
			}
		}
	}
	
	//controls animation and decides which player sprite to use
	public void render(Renderer screen) {	
		sprite = chosenSprite.getSprite();
		screen.renderEntity(x-16, y-16, this); //-16 centres player as it is a 32x32 sprite
		font.render(this.getUsername(), this.getX() - (this.getUsername().length()*8/2), this.getY() - 25, true, 0xffffffff, screen, false);
	}
	
	//called through update method to bypass the 'hidden' variable that stops a player from being rendered
	public void spawnTimeRender(Renderer screen) {
		if (respawns == 0) font.render("YOU LOSE", this.getX()-((8*8)/2), this.getY(), true, 0xffffffff, screen, true);
		else font.render("Respawn: "+respawnRender+"...", this.getX()-((13*8)/2), this.getY(), true, 0xffffffff, screen, true);
	}
	
	public int getLoadedAmmo() {
		return loadedAmmo;
	}
	
	public int getUnloadedAmmo() {
		return unloadedAmmo;
	}
	
	public void pickUpAmmo(Ammo ammo) {
		unloadedAmmo += ammo.getAmount();
		if (unloadedAmmo > MAX_AMMO) unloadedAmmo = MAX_AMMO;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public int getStam() {
		return stamina;
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	public int getInput() {
		if (this.input != null)
			return 1;
		return 0;
	}

	public int getRespawns() {
		return respawns;
	}
}
