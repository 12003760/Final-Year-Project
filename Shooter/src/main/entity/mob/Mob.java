package main.entity.mob;

import java.util.Collection;
import java.util.List;

import main.entity.Entity;
import main.entity.item.Ammo;
import main.entity.projectile.Projectile;
import main.entity.projectile.ShotgunProjectile;
import main.graphics.Renderer;

public abstract class Mob extends Entity { //Entity with a sprite

	public int dir = 2;
	public boolean isMoving = false;
	protected double speed; //how many 'pixels' are moved every update, 1=60px per second
	private int slowSpeedCount = 0;
	protected int health;
	
	public void move(int xChange, int yChange) {
		//if moving diagonally
		if (xChange != 0 && yChange != 0) {
			move(xChange, 0);
			move(0, yChange);
			return;
		}
		
		if (collision(xChange, yChange) == true) {
			//isMoving = false;
		}
		else {
			x += xChange;
			y += yChange;
		}
	}
	
	//handles a mobs speed
	public void moveSpeed(int xChange, int yChange) {
		if (speed < 1) {
			if ((double)slowSpeedCount % (1.0/speed) == 0) move(xChange, yChange);
			slowSpeedCount++;
		}
		else for (int i = 0; i < speed; i++) move(xChange, yChange);
		
		if (slowSpeedCount >= 5000) slowSpeedCount = 0;
	}
	
	public int getHealth() {
		return health;
	}
	
	public abstract void update();
	
	public abstract void render(Renderer screen);
	
	//handles mob collisions with tiles and other mobs
	private boolean collision(int xChange, int yChange) {
		//if the next tile in direction of movement is solid, return true
		List<Entity> entities = level.getEntities(this, 100);
		entities.addAll((Collection<? extends Entity>) level.getPlayersInRange(this, 100));
		
		for (int c=0; c<4; c++) {
			int xTile = ((x + xChange) + c % 2 * 14 - 8) / 16;
			int yTile = ((y + yChange) + c / 2 * 14) / 16;
			if (level.getTile(xTile, yTile).solid()) {
				return true;
			}
			for (int i = 0; i < entities.size(); i++) {
				if(entities.get(i) instanceof Mob && entities.get(i).isHidden() == false) {
					if (x+xChange < entities.get(i).getX()+16 && x+xChange > entities.get(i).getX()-16 && 
							y+yChange < entities.get(i).getY()+16 && y+yChange > (entities.get(i).getY()-16)) {
						if (this != entities.get(i)) {
							return true;
						}
					}
				}
				//AMMO PICKUP
				if(this instanceof Player && entities.get(i) instanceof Ammo) {
					if (x+xChange < entities.get(i).getX()+16 && x+xChange > entities.get(i).getX()-16 && 
							y+yChange < entities.get(i).getY()+16 && y+yChange > (entities.get(i).getY()-16)) {
						Player player = (Player)this;
						player.pickUpAmmo((Ammo)entities.get(i));
						entities.get(i).remove();
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void shoot(String username, int x, int y, double direction) {
		if (this instanceof Player) {
			Projectile p = new ShotgunProjectile(username, x, y, direction);
			level.add(p);
		}
	}
	
	public void setHealth(int health) {
		this.health += health;
	}
}
