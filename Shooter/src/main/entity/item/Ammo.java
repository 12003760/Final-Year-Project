package main.entity.item;

import main.graphics.Renderer;
import main.graphics.Sprite;

public class Ammo extends Item {
	private int amount;

	public Ammo(int x, int y) {
		super(x << 4, y << 4);
		sprite = Sprite.ammo;
		amount = 10;
	}
	
	public Ammo(int x, int y, int amount) {
		super(x << 4, y << 4);
		sprite = Sprite.ammo;
		this.amount = amount;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void render(Renderer screen) {
		screen.renderEntity(x, y, this);
	}
}
