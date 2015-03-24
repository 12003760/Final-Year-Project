package main.entity.particle;

import java.util.Random;

import main.entity.Entity;
import main.graphics.Renderer;
import main.graphics.Sprite;

public class Particle extends Entity{
	
	private Sprite sprite;
	
	private int life;
	private int time = 0;
	
	Random r = new Random();
	
	//how much the particle moves on x and y axis
	protected double xx, yy;
	protected double xMove, yMove;
	
	public Particle(int x, int y, int life, Sprite sprite) {
		this.x = x;
		this.y = y;
		this.xx = x;
		this.yy = y;
		this.life = life + r.nextInt(20)-10;
		this.sprite = sprite;
		
		this.xMove = r.nextGaussian();
		this.yMove = r.nextGaussian();
	}
	
	public void update() {
		time++;
		if (time >= 5000) time = 0;
		if (time > life) remove();
		xMove *= 0.95;
		yMove *= 0.95;

		move(xx+xMove, (yy+yMove));
	}
	
	private void move(double x, double y) {
		//checks for x collision, and then y collision, then collision in x and y
		if (collision(x-xMove, y)) yMove *= -0.9;
		else if (collision(x, y-yMove)) xMove *= -0.9; //reduces and reverses speed in x direction
		
		this.xx += xMove;
		this.yy += yMove;
	}
	
	public boolean collision(double x, double y) {
		//if the next tile in direction of movement is solid, return true
		for (int i=0; i<4; i++) { //covers all 4 corners of the sprite
			double xTile = (x - i % 2 * 16) / 16;
			double yTile = (y - i / 2 * 16) / 16;
			
			int xTileInt = (int) Math.ceil(xTile);
			int yTileInt = (int) Math.ceil(yTile);
			
			if (i % 2 == 0) xTileInt = (int) Math.floor(xTile);			
			if (i / 2 == 0)	yTileInt = (int) Math.floor(yTile);
			
			if (level.getTile(xTileInt, yTileInt).solid()) {
				return true;
			}
		}
		return false;
	}

	public void render(Renderer screen) {
		screen.renderSprite((int)xx, (int)yy, sprite, true, 0, false, -1);
	}
}
