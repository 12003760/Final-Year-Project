package main.entity.projectile;

import main.entity.Entity;
import main.graphics.Sprite;

public abstract class Projectile extends Entity {
	
	protected final int xOrigin, yOrigin;
	protected double xNew, yNew, angle, x, y;
	protected double speed, range, distance;
	protected static Sprite sprite;
	protected int damage;
	protected String username;
	
	public Projectile(String username, int x, int y, double dir) {
		this.username = username;
		xOrigin = x;
		yOrigin = y;
		angle = dir;
		this.x = x;
		this.y = y;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	protected void move() {
	}
	
	public String getUsername() {
		return username;
	}
}
