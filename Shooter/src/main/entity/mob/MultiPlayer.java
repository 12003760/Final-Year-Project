package main.entity.mob;

import java.net.InetAddress;

import main.Game;
import main.input.Keyboard;

public class MultiPlayer extends Player{

	public InetAddress ipAddress;
	public int port;
	
	public MultiPlayer(int x, int y, Keyboard input, String username, Game game, InetAddress ipAddress, int port) {
		super(x, y, input, username, game, 3); //when a player initially connects, they are given 3 lives
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	public MultiPlayer(int x, int y, String username, Game game, InetAddress ipAddress, int port) {
		super(x, y, null, username, game, 3);
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public void update() {
		super.update();
	}
}
