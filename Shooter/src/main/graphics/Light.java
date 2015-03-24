package main.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Light {

	private BufferedImage image;
	@SuppressWarnings("unused")
	private int x, y, radius, brightness;
	
	public Light(int x, int y, int radius, int brightness) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.brightness = brightness;
		
		image = new BufferedImage(radius*3, radius*3, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		
		int step = 3;
		int numSteps = radius / step;
		g2d.setColor(new Color(0, 0, 0, brightness));
		for (int i = 0; i < numSteps; i++) {
			g2d.fillOval(radius-i*step, radius-i*step, i * step * 1, i * step * 1);
		}
	}
	
	public void update() {
	}
	
	public void render(Graphics2D g2d) {
		g2d.drawImage(image, x - radius, y - radius, null);
	}
}
