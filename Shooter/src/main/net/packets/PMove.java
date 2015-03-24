package main.net.packets;

import main.net.GameClient;
import main.net.GameServer;

public class PMove extends Packet {

	private String username;
	private int x, y;
	private int dir;
	
	public PMove(byte[] data) {
		super(02);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.x = Integer.parseInt(dataArray[1]);
		this.y = Integer.parseInt(dataArray[2]);
		this.dir = Integer.parseInt(dataArray[3]);
	}
	
	public PMove(String username, int x, int y, int dir) {
		super(02);
		this.username = username;
		this.x = x;
		this.y = y;
		this.dir = dir;
	}

	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {		
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return ("02"+this.username+","+this.x+","+this.y+","+this.dir).getBytes();
	}

	public String getUsername() {
		return username;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getDir() {
		return dir;
	}
}
