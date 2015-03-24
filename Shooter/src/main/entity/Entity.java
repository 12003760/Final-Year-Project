package main.entity;

import java.util.Random;

import main.graphics.Renderer;
import main.graphics.Sprite;
import main.level.Level;

public class Entity {
	
	public int x; //keeps track of where the entity is located
	public int y;
	private boolean removed = false;
	protected Level level;
	protected Sprite sprite;
	protected final Random r = new Random();
	protected boolean hidden = false;
	
	public void update() {
	}
	
	public void render (Renderer screen) {
	}
	
	public void hide() {
		hidden = true;
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public void remove() {
		removed = true;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void init(Level level) {
		this.level = level;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
}
