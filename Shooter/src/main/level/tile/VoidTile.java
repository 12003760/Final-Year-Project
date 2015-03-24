package main.level.tile;

import main.graphics.Renderer;
import main.graphics.Sprite;

public class VoidTile extends Tile {

	public VoidTile(Sprite sprite) {
		super(sprite);
	}
	
	public void render(int x, int y, Renderer screen) {
		screen.renderTile(x<<4, y<<4, this);
	}
}
