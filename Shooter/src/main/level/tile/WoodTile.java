package main.level.tile;

import main.graphics.Renderer;
import main.graphics.Sprite;

public class WoodTile extends Tile {

	public WoodTile(Sprite sprite) {
		super(sprite);
	}
	
	public void render(int x, int y, Renderer screen) {
		screen.renderTile(x<<4, y<<4, this); //converts back to pixel precision from tile precision
	}
}
