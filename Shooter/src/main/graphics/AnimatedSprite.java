package main.graphics;

public class AnimatedSprite extends Sprite {

	private int frame = 0;
	private int rate = 5;
	private int time = 0;
	private int animationSize = 0;
	private Sprite sprite;
	
	public AnimatedSprite(int width, int height, SpriteSheet sheet, int animationSize) {
		super(width, height, sheet);
		this.animationSize = animationSize;
		sprite = sheet.getSprites()[0];
		if (animationSize > sheet.getSprites().length)
			System.err.println("Animation size is too long!");
	}
	
	public void update() {
		time++;
		if(time%rate == 0) {
			if (frame >= animationSize - 1) frame = 0;
			else frame++;
			sprite = sheet.getSprites()[frame];
		}
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public void setFrameRate (int frames) {
		rate=  frames;
	}

	public void setFrame(int i) {
		sprite = sheet.getSprites()[i];
	}
}
