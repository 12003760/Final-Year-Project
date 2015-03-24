package main.entity.mob;

import java.util.List;

import main.entity.spawner.ParticleSpawner;
import main.graphics.AnimatedSprite;
import main.graphics.Renderer;
import main.graphics.Sprite;
import main.graphics.SpriteSheet;
import main.input.Keyboard;
import main.level.Node;
import main.utilities.Vector;

public class Zombie extends Mob {
	private int xx = 0, yy = 0;
	private int time = 0;
	private int aggroRange = 200;
	private int damage = 10;
	private List<Node> path = null;
	private String username;
	private int i = 0;
	
	private AnimatedSprite down = new AnimatedSprite(32, 32, SpriteSheet.zombie_down, 3);
	private AnimatedSprite up = new AnimatedSprite(32, 32, SpriteSheet.zombie_up, 3);
	private AnimatedSprite right = new AnimatedSprite(32, 32, SpriteSheet.zombie_right, 3);
	private AnimatedSprite left = new AnimatedSprite(32, 32, SpriteSheet.zombie_left, 3);
	
	private AnimatedSprite chosenSprite = down;
	 
	public Zombie(int x, int y, Keyboard input, String username) {
		this(x, y);
		this.username = username + i++; //sets the players username to the zombie
	}
	
	public Zombie(int x, int y) {
		this.x = x;
		this.y = y;
		health = 30;
		speed = 0.5;
	}
	
	public Player findNearestPlayer(List<Player> players) {
		double lowestDistance = 10000;
		Player playerToReturn = null;
		
		for (int i = 0; i < players.size(); i++) {
			Vector start = new Vector(this.x, this.y);
			Vector finish = new Vector(players.get(i).getX(), players.get(i).getY());
			double distance = start.getDistanceTo(finish);
			
			if (distance < lowestDistance) {
				lowestDistance = distance;
				playerToReturn = players.get(i);
			}
		}
		return playerToReturn;
	}
	
	public void chase() {
		List<Player> players = level.getPlayersInRange(this, aggroRange);
		if (players.size() > 0) {
			xx = 0;
			yy = 0;
			
			Player player = findNearestPlayer(players);
			Vector start = new Vector(this.x >> 4, this.y >> 4);
			Vector finish = new Vector(player.getX() >> 4, player.getY() >> 4);
			
			if (start.getDistanceTo(finish) <= 1 && time % 30 == 0) {
				new ParticleSpawner((int)player.getX()+2, (int)player.getY()+2, 50, 20, level, Sprite.player_blood);
				player.setHealth(-damage);
			}
			
			if (time % 3 == 0) path = level.pathFinder(start, finish);
			
			if(path != null) {
				if (path.size() > 0) {
					Vector v = path.get(path.size() - 1).location;
					if (x-8 < v.getX() << 4) {
						xx++; //-8 offset allows the node to be in the centre of a tile
						dir = 1;
					}
					if (x-8 > v.getX() << 4) {
						xx--;
						dir = 3;
					}
					if (y < v.getY() << 4) {
						yy++;
						dir = 2;
					}
					if (y > v.getY() << 4) {
						yy--;
						dir = 0;
					}
				}
			}			
		}
		else {
			xx = 0;
			yy = 0;
		}
		
		//Random movement currently disabled until zombies are fully networked
		/*else if (time % (r.nextInt(30)+60) == 0) {
			xx = r.nextInt(3)-1;
			yy = r.nextInt(3)-1;
			if (xx != 0 && yy != 0) {
				if (r.nextInt(2) == 0) xx = 0;
				else yy = 0;
			}
		}*/
		
		if (xx != 0 || yy != 0) {
			isMoving = true;
			moveSpeed(xx, yy);
		}
		else isMoving = false;
	}
	
	public void update() {
		if (time > 5000) time =0;
		else time++;
		
		if (isMoving) chosenSprite.update();
		else chosenSprite.setFrame(0);
		
		if (health <= 0) {
			remove();
			new ParticleSpawner((int)x+2, (int)y+2, 100, 100, level, Sprite.zombie_collide);
		}
		chase();
		
		updateSprite();
	}
	
	private void updateSprite() {
		if (xx < 0) {
			chosenSprite = left;
		}
		else if (xx > 0) {
			chosenSprite = right;
		}
		
		if (yy < 0) {
			chosenSprite = up;
		}
		else if (yy > 0) {
			chosenSprite = down;
		}	
	}
	
	public void render(Renderer screen) {
		sprite = chosenSprite.getSprite();
		screen.renderEntity(x-16, y-16, this);
	}
	
	public String getUsername() {
		return username;
	}
}
