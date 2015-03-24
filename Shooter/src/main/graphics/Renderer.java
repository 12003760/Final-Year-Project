package main.graphics;

import main.entity.Entity;
import main.entity.projectile.Projectile;
import main.level.tile.Tile;

public class Renderer {
	public int width, height;
	public int[] pixels;
	public int[] pixelsUi;
	public int xOffset, yOffset;

	//width and height of game
	public Renderer(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width*height];
		pixelsUi = new int[width*height];
	}
	
	public void clear() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0;
		}
		for (int i = 0; i < pixelsUi.length; i++) {
			pixelsUi[i] = 0x00FFFFFF;
		}
	}
	
	public void renderSprite(int xPos, int yPos, Sprite sprite, boolean isFixed, int imageColour, boolean ui, int xCut) {
		if (isFixed) {
			xPos -= xOffset;
			yPos -= yOffset;
		}
		for (int y = 0; y<sprite.getHeight(); y++) {
			int yAbsolute = y + yPos;
			for (int x = 0; x<sprite.getWidth(); x++) {
				if (xCut != -1 && x > xCut) break;
				int xAbsolute = x + xPos;
				
				if(xAbsolute < 0 || xAbsolute >= width || yAbsolute < 0 || yAbsolute >= height) continue;
				int colour = sprite.pixels[x+y*sprite.getWidth()];
				if (colour != 0xffff00ff && colour != 0xffff32f3) {
					if (ui) {
						if (imageColour == 0) pixelsUi[xAbsolute+yAbsolute*width] = colour;
						else pixelsUi[xAbsolute+yAbsolute*width] = imageColour;
					}
					else {
						if (imageColour == 0) pixels[xAbsolute+yAbsolute*width] = colour;
						else pixels[xAbsolute+yAbsolute*width] = imageColour;
					}
				}
			}
		}
	}
	
	public void renderTile(int xPos, int yPos, Tile tile) {
		xPos -= xOffset;
		yPos -= yOffset;
		
		for (int y = 0; y<tile.sprite.SIZE; y++) {
			int yAbsolute = y + yPos;
			for (int x = 0; x<tile.sprite.SIZE; x++) {
				int xAbsolute = x + xPos;
				//if a tile goes off the screen, don't render it
				if (xAbsolute < -tile.sprite.SIZE || xAbsolute >= width || yAbsolute < 0 || yAbsolute >= height) break;
				//xAbsolute < -tile.sprite.SIZE allows for additional room for rendering so flickering is not seen
				if (xAbsolute < 0) xAbsolute = 0;
				pixels[xAbsolute+yAbsolute*width] = tile.sprite.pixels[x+y*tile.sprite.SIZE];
			}
		}
	}
	
	public void renderProjectile(int xPos, int yPos, Projectile p) {
		xPos -= xOffset;
		yPos -= yOffset;
		
		for (int y = 0; y<p.getSprite().SIZE; y++) {
			int yAbsolute = y + yPos;
			for (int x = 0; x<p.getSprite().SIZE; x++) {
				int xAbsolute = x + xPos;
				if (xAbsolute < -p.getSprite().SIZE || xAbsolute >= width || yAbsolute < 0 || yAbsolute >= height) break;
				if (xAbsolute < 0) xAbsolute = 0;
				int colour = p.getSprite().pixels[x+y*p.getSprite().SIZE];
				if (colour != 0xffff32f3)
					pixels[xAbsolute+yAbsolute*width] = colour;
			}
		}
	}
	
	public void renderEntity(int xPos, int yPos, Entity e) {
		xPos -= xOffset;
		yPos -= yOffset;
		
		for (int y = 0; y<e.getSprite().SIZE; y++) {
			int yAbsolute = y + yPos;
			for (int x = 0; x<e.getSprite().SIZE; x++) {
				int xAbsolute = x + xPos;
				//int xCopy = x; //originally used for x flip
				if (xAbsolute < -e.getSprite().SIZE || xAbsolute >= width || yAbsolute < 0 || yAbsolute >= height) break;
				if (xAbsolute < 0) xAbsolute = 0;
				int colour = e.getSprite().pixels[x + y*e.getSprite().SIZE];
				if(colour != 0xffff32f3)
					pixels[xAbsolute+yAbsolute*width] = colour;
			}
		}
	}
	
	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}
