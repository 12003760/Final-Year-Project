package main.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import main.Game;
import main.entity.mob.MultiPlayer;
import main.net.packets.Packet;
import main.net.packets.Packet.PacketTypes;
import main.net.packets.PLogin;
import main.net.packets.PDisconnect;
import main.net.packets.PMove;
import main.net.packets.PShoot;

public class GameServer extends Thread {
	
	private DatagramSocket socket;
	private Game game;
	private List<MultiPlayer> connectedPlayers = new ArrayList<MultiPlayer>();
	
	public GameServer(Game game) {
		this.game = game;
		try {
			this.socket = new DatagramSocket(13331);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while(true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}
	
	private void parsePacket(byte[] data, InetAddress address, int port) {
		String msg = new String(data).trim();
		PacketTypes type = Packet.lookupPacket(msg.substring(0, 2));
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			if (connectedPlayers.size() < 2) {
				packet = new PLogin(data);
				System.out.println("["+address.getHostAddress()+":"+port+"] "+((PLogin)packet).getUsername()+" has connected...");
				MultiPlayer player = new MultiPlayer(400, 100, ((PLogin)packet).getUsername(), game, address, port);	
				this.addConnection(player, (PLogin)packet);
			}
			break;
		case DISCONNECT:
			packet = new PDisconnect(data);
			System.out.println("["+address.getHostAddress()+":"+port+"] "+((PDisconnect)packet).getUsername()+" has disconnected...");
			this.removeConnection((PDisconnect)packet);
			break;
		case MOVE:
			packet = new PMove(data);
			this.handleMove((PMove)packet);
			break;
		case SHOOT:
			packet = new PShoot(data);
			this.handleShoot((PShoot)packet);
			break;
		}
	}
	
	private void handleMove(PMove packet) {
		if(getMultiPlayer(packet.getUsername()) != null) {
			int index = getMultiPlayerIndex(packet.getUsername());
			this.connectedPlayers.get(index).x = packet.getX();
			this.connectedPlayers.get(index).y = packet.getY();
			this.connectedPlayers.get(index).dir = packet.getDir();
			
			packet.writeData(this);
		}
	}
	
	private void handleShoot(PShoot packet) {
		if(getMultiPlayer(packet.getUsername()) != null) {			
			packet.writeData(this);
		}
	}

	public void removeConnection(PDisconnect packet) {
		this.connectedPlayers.remove(getMultiPlayerIndex(packet.getUsername()));
		packet.writeData(this);
	}
	
	public MultiPlayer getMultiPlayer(String username) {
		for (MultiPlayer p : this.connectedPlayers) {
			if (p.getUsername().equals(username)) {
				return p;
			}
		}
		return null;
	}
	
	public int getMultiPlayerIndex(String username) {
		int i = 0;
		for (MultiPlayer p : this.connectedPlayers) {
			if (p.getUsername().equals(username)) {
				break;
			}
			i++;
		}
		return i;
	}

	public void addConnection(MultiPlayer player, PLogin packet) {
		boolean alreadyConnected = false;
		for(MultiPlayer p : this.connectedPlayers) {
			if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
				if (p.ipAddress == null) {
					p.ipAddress = player.ipAddress;
				}
				if (p.port == -1) {
					p.port = player.port;
				}
				alreadyConnected = true;
			} else {
				//tells the currently connected players there is a new player
				sendData(packet.getData(), p.ipAddress, p.port);
				
				//tells the currently connected players that the new player exists
				packet = new PLogin(p.getUsername(), p.x, p.y);
				sendData(packet.getData(), player.ipAddress, player.port);
			}
		}
		if (alreadyConnected == false) {
			this.connectedPlayers.add(player);
		}
	}

	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDataToAllClients(byte[] data) {
		for (MultiPlayer p : connectedPlayers) {
			sendData(data, p.ipAddress, p.port);
		}
	}
}
