package main.level;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


public class FirstLevel extends Level {
	public FirstLevel(String path) {
		super(path);
	}
	
	protected void loadLevel(String path) {
		try {
			BufferedImage image = ImageIO.read(FirstLevel.class.getResource(path));
			int w = width = image.getWidth();
			int h = height = image.getHeight();
			tileID = new int[w*h];
			image.getRGB(0, 0, w, h, tileID, 0, w);
		} catch (IOException e) {
			System.out.println("Could not load the first level!");
		}
	}

	protected void genLevel() {
	}
}