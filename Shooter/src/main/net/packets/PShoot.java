package main.net.packets;

import main.net.GameClient;
import main.net.GameServer;

public class PShoot extends Packet {

	private String username;
	private int x, y;
	private double dir;
	
	public PShoot(byte[] data) {
		super(03);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.x = Integer.parseInt(dataArray[1]);
		this.y = Integer.parseInt(dataArray[2]);
		this.dir = Double.parseDouble(dataArray[3]);
	}
	
	public PShoot(String username, int x, int y, double dir) {
		super(03);
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
		return ("03"+this.username+","+this.x+","+this.y+","+this.dir).getBytes();
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
	
	public double getDir() {
		return dir;
	}
}
