package main.level.tile;

import main.graphics.Renderer;
import main.graphics.Sprite;

public class Tile {
	
	 public int x, y;
	 public Sprite sprite;
	 
	 public static Tile hellrock = new HellrockTile(Sprite.hellrock);
	 public static Tile water = new WaterTile(Sprite.water);
	 public static Tile stone = new StoneTile(Sprite.stone);
	 public static Tile wood = new WoodTile(Sprite.wood);
	 public static Tile voidTile = new VoidTile(Sprite.voidSprite);
	 
	 public static final int hellrock_col = 0xffff0000;
	 public static final int water_col = 0xff006cff;
	 public static final int stone_col = 0xff4c4c4c;
	 public static final int wood_col = 0xfffff726;
	 
	 public Tile(Sprite sprite) {
		 this.sprite = sprite;
	 }
	 
	 public void render(int x, int y, Renderer screen) {}
	 
	 public boolean solid() {
		 return false;
	 }
}
