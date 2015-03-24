package main.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import main.Game;
import main.entity.mob.MultiPlayer;
import main.net.packets.Packet;
import main.net.packets.Packet.PacketTypes;
import main.net.packets.PLogin;
import main.net.packets.PDisconnect;
import main.net.packets.PMove;
import main.net.packets.PShoot;

public class GameClient extends Thread {
	
	private InetAddress ipAddress;
	private DatagramSocket socket;
	private Game game;
	
	public GameClient(Game game, String ipAddress) {
		this.game = game;
		try {
			this.socket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (java.net.UnknownHostException e) {
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
			packet = new PLogin(data);
			handleLogin((PLogin)packet, address, port);
			break;
		case DISCONNECT:
			packet = new PDisconnect(data);
			System.out.println("["+address.getHostAddress()+":"+port+"] "+((PDisconnect)packet).getUsername()+" has left the game...");
			game.level.getPlayerWithName(((PDisconnect)packet).getUsername()).remove();
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

	private void handleShoot(PShoot packet) {
		this.game.level.shootFromServer(packet.getUsername(), packet.getX(), packet.getY(), packet.getDir());
	}

	private void handleMove(PMove packet) {
		this.game.level.movePlayerFromServer(packet.getUsername(), packet.getX(), packet.getY(), packet.getDir());
	}
	
	private void handleLogin(PLogin packet, InetAddress address, int port) {
		System.out.println("["+address.getHostAddress()+":"+port+"] "+((PLogin)packet).getUsername()+" has joined the game...");
		MultiPlayer player = new MultiPlayer(packet.getX(), packet.getY(), (packet).getUsername(), game, address, port);			
		game.level.add(player);
	}

	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 13331);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
