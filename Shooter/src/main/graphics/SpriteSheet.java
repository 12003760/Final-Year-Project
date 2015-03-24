package main.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {

	private String path;
	public final int SIZE, WIDTH, HEIGHT;
	private int sheetWidth, sheetHeight;
	public int[] pixels;
	
	public static SpriteSheet tiles = new SpriteSheet("/textures/tile_sprites.png", 256);
	
	public static SpriteSheet player = new SpriteSheet ("/textures/player_sprites.png", 128, 128);
	public static SpriteSheet player_down = new SpriteSheet(player, 0, 0, 1, 3, 32);
	public static SpriteSheet player_up = new SpriteSheet(player, 1, 0, 1, 3, 32);
	public static SpriteSheet player_right = new SpriteSheet(player, 2, 0, 1, 3, 32);
	public static SpriteSheet player_left = new SpriteSheet(player, 3, 0, 1, 3, 32);
	
	public static SpriteSheet zombie = new SpriteSheet ("/textures/zombie_sprites.png", 128, 96);
	public static SpriteSheet zombie_down = new SpriteSheet(zombie, 0, 0, 1, 3, 32);
	public static SpriteSheet zombie_up = new SpriteSheet(zombie, 1, 0, 1, 3, 32);
	public static SpriteSheet zombie_right = new SpriteSheet(zombie, 2, 0, 1, 3, 32);
	public static SpriteSheet zombie_left = new SpriteSheet(zombie, 3, 0, 1, 3, 32);
	
	private Sprite[] sprites;
	
	public SpriteSheet(String path, int size) {
		this.path = path;
		SIZE = size;
		WIDTH = size;
		HEIGHT = size;
		pixels = new int[size*size];
		loadSheet();
	}
	
	public SpriteSheet(String path, int w, int h) {
		this.path = path;
		SIZE = w;
		WIDTH = w;
		HEIGHT = h;
		pixels = new int[w * h];
		loadSheet();
	}
	
	public SpriteSheet(SpriteSheet sheet, int x, int y, int w, int h, int spriteSize) {
		int xx = x * spriteSize;
		int yy = y * spriteSize;
		int width = w * spriteSize;
		int height = h * spriteSize;
		
		WIDTH = width;
		HEIGHT = height;
		SIZE = 0; //not used
		
		pixels = new int[width * height];
		
		for (int y0 = 0; y0 < height; y0++) {
			int yPos = yy + y0;
			for (int x0 = 0; x0 < width; x0++) {
				int xPos = xx + x0;
				pixels[x0+y0*width] = sheet.pixels[xPos + yPos * sheet.WIDTH];
			}
		}
		
		int frame = 0;
		sprites = new Sprite[w * h];
		
		for (int ya = 0; ya < h; ya++) {
			for (int xa = 0; xa < w; xa++) {
				int[] spritePixels = new int[spriteSize * spriteSize];
				for (int y0 = 0; y0 < spriteSize; y0++) {
					for (int x0 = 0; x0 < spriteSize; x0++) {
						spritePixels[x0 + y0 * spriteSize] = pixels[(x0 + xa * spriteSize) + (y0 + ya * spriteSize) * WIDTH];
					}
				}
				Sprite sprite = new Sprite(spritePixels, spriteSize, spriteSize);
				sprites[frame++] = sprite;
			}
		}
	}
	
	public Sprite[] getSprites() {
		return sprites;
	}
	
	public int getWidth() {
		return sheetWidth;
	}
	
	public int getHeight() {
		return sheetHeight;
	}
	
	public int[] getPixels() {
		return pixels;
	}
	
	private void loadSheet() {
		try {
			BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path));
			sheetWidth = image.getWidth();
			sheetHeight = image.getHeight();
			//System.out.println(sheetWidth +", "+ sheetHeight);
			pixels = new int[sheetWidth * sheetHeight];
			image.getRGB(0, 0, sheetWidth, sheetHeight, pixels, 0, sheetWidth);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
