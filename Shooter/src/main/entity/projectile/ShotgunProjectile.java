package main.entity.projectile;

import main.entity.spawner.ParticleSpawner;
import main.graphics.Renderer;
import main.graphics.Sprite;

public class ShotgunProjectile extends Projectile {

	public static final int FIRE_RATE = 30; //time between projectiles
	
	public ShotgunProjectile(String username, int x, int y, double dir) {
		super(username, x, y, dir);
		range = 150;
		speed = 6;
		damage = 15;
		
		
		xNew = Math.cos(angle);
		yNew = Math.sin(angle);
		sprite = Sprite.bullet;
	}
	
	public void update() {
		for(int i  = 0; i < speed; i++) {
			move();
			if (level.projectileCollision(username, (int)(x+xNew), (int)(y+yNew), 0, 0, 3, damage)) { //size should be 4 but works perfectly as 3?
				new ParticleSpawner((int)x+2, (int)y+2, 50, 20, level, Sprite.brick_collide);
				remove();
				break;
			}
		}
	}
	
	protected void move() {
		x += xNew;
		y += yNew;
		if(calcDistance()>range) remove();
	}
	
	private double calcDistance() {
		double distance = 0;
		distance = Math.sqrt(Math.abs((xOrigin-x)*(xOrigin-x) + (yOrigin-y)*(yOrigin-y)));
		return distance;		
	}

	public void render (Renderer screen) {
		screen.renderProjectile((int)x, (int)y, this);
	}
}
