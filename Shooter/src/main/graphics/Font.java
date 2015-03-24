package main.graphics;

public class Font {
	
	private static SpriteSheet font = new SpriteSheet("/fonts/arial.png", 8);
	private static Sprite[] chars = Sprite.split(font);
	
	private static String alphaNum = "ABCDEFGHIJKLM"
									+ "NOPQRSTUVWXYZ"
									+ "abcdefghijklm"
									+ "nopqrstuvwxyz"
									+ "1234567890./:";
	
	public Font() {
	}
	
	public void render(String text, int x, int y, boolean isFixed, int colour, Renderer screen, Boolean ui) { //output = ui or image
		for (int i = 0; i < text.length(); i++) {
			int index = alphaNum.indexOf(text.charAt(i));
			
			if (text.charAt(i) == ' ');
			else {			
				screen.renderSprite(x, y, chars[index], isFixed, colour, ui, -1);
			}
			
			x += 8;
		}
		
	}
}
