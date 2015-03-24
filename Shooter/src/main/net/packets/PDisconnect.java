package main.net.packets;

import main.net.GameClient;
import main.net.GameServer;

public class PDisconnect extends Packet {

	private String username;
	
	public PDisconnect(byte[] data) {
		super(01);
		this.username = readData(data);
	}
	
	public PDisconnect(String username) {
		super(01);
		this.username = username;
	}

	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {		
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return ("01"+this.username).getBytes();
	}

	public String getUsername() {
		return username;
	}
}
