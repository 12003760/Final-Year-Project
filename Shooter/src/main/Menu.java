package main;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import main.graphics.Font;
import main.graphics.Renderer;
import main.graphics.Sprite;
import main.input.Mouse;

public class Menu implements ActionListener{
	
	private Font font;
	private JTextField username;
	
	private int playColour = 0xFFFFFFFF;
	private int quitColour = 0xFFFFFFFF;
	
	//used only for coordinates
	private Rectangle playButton = new Rectangle(108 * Game.scale, 140 * Game.scale, 33 * Game.scale, 10 * Game.scale);
	private Rectangle quitButton = new Rectangle(108 * Game.scale, 153 * Game.scale, 33 * Game.scale, 10 * Game.scale);

	public Menu(Font font) {
		this.font = font;
		username = new JTextField(10);
		username.addActionListener(this);

	}
	
	public void update() {
		if (Mouse.getX() > playButton.x && Mouse.getY() > playButton.y
				&& Mouse.getX() <= playButton.x + playButton.getWidth()
				&& Mouse.getY() <= playButton.y + playButton.getHeight()) {
			
			playColour = 0xFF940000;
			
			if (Mouse.getButton() == 1) {
				Game.State = Game.STATE.GAME;
				Game.game.addPlayer();
			}
		} else playColour = 0xFFFFFFFF;
		
		if (Mouse.getX() > quitButton.x && Mouse.getY() > quitButton.y
				&& Mouse.getX() <= quitButton.x + quitButton.getWidth()
				&& Mouse.getY() <= quitButton.y + quitButton.getHeight()) {
			
			quitColour = 0xFF940000;
			
			if (Mouse.getButton() == 1) {
				System.exit(0);
			}
		} else quitColour = 0xFFFFFFFF;
	}
	
	public void render(Renderer screen) {	
		screen.renderSprite(0, 0, Sprite.mainMenu, false, 0, false, -1);
		
		font.render("GRAVE CRAWLERS", 100, 120, false, 0xFF940000, screen, false);
		font.render("play", 110, 140, false, playColour, screen, false);
		font.render("quit", 110, 155, false, quitColour, screen, false);
	}

	public void actionPerformed(ActionEvent arg0) {
	}
	
}
