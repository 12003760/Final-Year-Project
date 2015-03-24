package main.input;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import main.Game;
import main.net.packets.PDisconnect;

public class WindowHandler implements WindowListener {
	
	private final Game game;
	
	public WindowHandler(Game game) {
		this.game = game;
		this.game.frame.addWindowListener(this);
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		PDisconnect packet = new PDisconnect(this.game.player.getUsername());
		packet.writeData(this.game.socketClient);
	}

	public void windowDeactivated(WindowEvent e) {	
	}

	public void windowDeiconified(WindowEvent e) {	
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

}
