package main.graphics;


public class Sprite {
	
	public final int SIZE;
	private int x, y;
	private int width, height;
	public int[] pixels;
	protected SpriteSheet sheet;
	
	//GUI:
	public static Sprite playerListPanel = new Sprite(50, 270, 0xff333130);
	public static Sprite mainMenu = new Sprite(720, 720, 0xFF1F1F1F);
	public static Sprite healthBar = new Sprite (100, 2, 0xFFFC0054);
	public static Sprite stamBar = new Sprite (100, 2, 0xFFFCCE00);
	public static Sprite ammoIcon = new Sprite (16, 2, 15, SpriteSheet.tiles);
	
	//ITEMS:
	public static Sprite ammo = new Sprite(16, 1, 15, SpriteSheet.tiles);
	
	//PROJECTILES:
	public static Sprite bullet = new Sprite(16, 0, 15, SpriteSheet.tiles);

	//PARTICLES:
	public static Sprite brick_collide = new Sprite(1, 0xff000000);
	public static Sprite zombie_collide = new Sprite(1, 0xff38FFBC);
	public static Sprite weapon_shoot = new Sprite(1, 0xffE6C700);
	public static Sprite player_blood = new Sprite(1, 0xffEB3B00);
	
	//LEVEL:
	public static Sprite hellrock = new Sprite(16, 0, 0, SpriteSheet.tiles);
	public static Sprite water = new Sprite(16, 1, 0, SpriteSheet.tiles);
	public static Sprite stone = new Sprite(16, 2, 0, SpriteSheet.tiles);
	public static Sprite wood = new Sprite(16, 3, 0, SpriteSheet.tiles);
	public static Sprite voidSprite = new Sprite(16, 0xff000000);
	
	//PLAYER:
	public static Sprite player_down_shoot = new Sprite(32, 0, 3, SpriteSheet.player);
	public static Sprite player_up_shoot = new Sprite(32, 1, 3, SpriteSheet.player);
	public static Sprite player_right_shoot = new Sprite(32, 2, 3, SpriteSheet.player);
	public static Sprite player_left_shoot = new Sprite(32, 3, 3, SpriteSheet.player);
	
	//MOBS:
	public static Sprite dummy = new Sprite(32, 0, 6, SpriteSheet.tiles);
	
	//Only used for animated sprites
	protected Sprite(int width, int height, SpriteSheet sheet) {
		SIZE = 0;
		this.width = width;
		this.height = height;
		this.sheet = sheet;
	}
	
	public Sprite(int size, int x, int y, SpriteSheet sheet) {
		SIZE = size;
		this.width = size;
		this.height = size;
		pixels = new int[SIZE*SIZE]; //size of sprite
		this.x = x*size; //converts sprite location on sheet to pixels
		this.y = y*size;
		this.sheet = sheet;
		loadSprite();
	}
	
	public Sprite(int width, int height, int colour) {
		SIZE = -1;
		this.width = width;
		this.height = height;
		pixels = new int[width*height];
		setColour(colour);
	}
	
	//void sprite constructor
	public Sprite(int size, int colour) {
		SIZE = size;
		this.width = size;
		this.height = size;
		pixels = new int[SIZE*SIZE];
		setColour(colour);
	}
	
	public Sprite(int[] spritePixels, int width, int height) {
		SIZE = width;
		this.width = width;
		this.height = height;
		this.pixels = new int[spritePixels.length];
		//copies the pixels instead of referencing them
		for (int i = 0; i < pixels.length; i++) {
			this.pixels[i] = spritePixels[i];
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	private void setColour(int colour) {
		for (int i=0; i<width*height; i++) {
			pixels[i] = colour;
		}
	}

	private void loadSprite() {
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				//sets the sprite pixels equal to appropriate pixels from sprite sheet
				pixels[x+y*SIZE] = sheet.pixels[(x+this.x) + (y+this.y) * sheet.SIZE];
				//sheet.SIZE potentially causes rendering problems with sprites that may not be square
			}
		}
	}

	public static Sprite[] split(SpriteSheet sheet) {
		int amount = (sheet.getWidth() * sheet.getHeight()) / (sheet.WIDTH * sheet.HEIGHT);
		Sprite[] sprites = new Sprite[amount];
		int i = 0;
		
		int[] pixels = new int[sheet.WIDTH * sheet.HEIGHT];
		
		for (int yPos = 0; yPos < sheet.getHeight() / sheet.HEIGHT; yPos++) {
			for (int xPos = 0; xPos < sheet.getWidth() / sheet.WIDTH; xPos++) {
				for (int y = 0; y < sheet.HEIGHT; y++) {
					for (int x = 0; x < sheet.WIDTH; x++) {
						int xOffset = x + xPos * sheet.WIDTH;
						int yOffset = y + yPos * sheet.HEIGHT;
						pixels[x+y*sheet.WIDTH] = sheet.getPixels()[xOffset + yOffset * sheet.getWidth()];
					}
				}
				 Sprite sprite = new Sprite(pixels, sheet.WIDTH, sheet.HEIGHT);
				 sprites[i++] = sprite;
			}
		}
		return sprites;
	}

}
