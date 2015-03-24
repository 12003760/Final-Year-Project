package main;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.entity.mob.MultiPlayer;
import main.entity.mob.Player;
import main.graphics.Font;
import main.graphics.Light;
import main.graphics.Renderer;
import main.graphics.Sprite;
import main.input.Keyboard;
import main.input.Mouse;
import main.input.WindowHandler;
import main.level.Level;
import main.net.GameClient;
import main.net.GameServer;
import main.net.packets.PLogin;

public class Game extends Canvas implements Runnable {
	
	public static Game game;
	public static int scale = 3;
	
	private static final long serialVersionUID = 1L;
	private static int width = 470;
	private static int height = width / 16 * 9;
	private static boolean runServer = false;
	private static String playerUsername, serverIp;
	private static String title = "Grave Crawlers";
	private static String version = " -Alpha 1.0";
	private static String[] resolutions = { "1280 x 720", "1600 x 900", "1920 x 1080" };
	
	public WindowHandler window;
	public Level level;
	public Player player;
	public JFrame frame;
	public Keyboard key;
	public GameClient socketClient;
	public GameServer socketServer;
	
	private boolean isRunning = false;
	private Thread thread;
	private Renderer screen;
	private Mouse mouse;
	private Font font;
	private Light playerLight;
	private Menu menu;
	private PLogin loginPacket;
	
	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); //complete image with accessible buffer
	private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData(); //allows access to image buffer data
	private BufferedImage ui = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	private int[] pixelsUi = ((DataBufferInt)ui.getRaster().getDataBuffer()).getData();
	private BufferedImage lightMap = new BufferedImage(width*3, height*3, BufferedImage.TYPE_INT_ARGB);
	
	public enum STATE {
		MENU, GAME
	};
	
	public static STATE State = STATE.MENU;
	
	public Game() {
		//Renders light at the start of the game
		playerLight = new Light(lightMap.getWidth()/2, lightMap.getHeight()/2, 150, 12);
		Graphics2D g2d = (Graphics2D) lightMap.getGraphics();
		g2d.setColor(new Color(0, 0, 0, 230));
		g2d.fillRect(0, 0, lightMap.getWidth(), lightMap.getHeight());
		g2d.setComposite(AlphaComposite.DstOut);
		playerLight.render(g2d);
		g2d.dispose();
		
		Dimension size = new Dimension(width*scale, height*scale);
		setPreferredSize(size); //scales the game up
		screen = new Renderer(width, height);
		frame = new JFrame();
		key = new Keyboard();
		window = new WindowHandler(this);
		mouse = new Mouse();
		level = Level.level;
		font = new Font();
		menu = new Menu(font);
		
		addKeyListener(key);
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
	}
	
	public static void main(String[] args) {
		JTextField username = new JTextField(10);
		JTextField ipAddress = new JTextField(10);
		JCheckBox server = new JCheckBox();
		JComboBox<?> res = new JComboBox<Object>(resolutions);
		
		JPanel options = new JPanel();
		GridLayout experimentLayout = new GridLayout(0,2);
		options.setLayout(experimentLayout);
		options.add(new JLabel("Username (unique):"));
		options.add(username);
		options.add(new JLabel("IP address of server: "));
		options.add(ipAddress);
		options.add(new JLabel("Resolution: "));
		options.add(res);
		options.add(new JLabel("Run server: "));
		options.add(server);
		
		int result = JOptionPane.showConfirmDialog(null, options, "Launch Settings", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			playerUsername = username.getText();
			serverIp = ipAddress.getText();
			runServer = server.isSelected();
			scale = res.getSelectedIndex() + 2;
			
			game = new Game();
			game.frame.setResizable(false);
			game.frame.setTitle(Game.title);
			game.frame.add(game); //adds instance of game to the frame
			game.frame.pack();
			game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			game.frame.setLocationRelativeTo(null);
			game.frame.setVisible(true);
			game.start();
		}
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void start() {
		isRunning = true;
		thread = new Thread(this, "Display");
		
		if (runServer == true) {
			socketServer = new GameServer(this);
			socketServer.start();
		}
		
		socketClient = new GameClient(this, serverIp);
		socketClient.start();
		
		thread.start();
	}
	
	public void init() {
		player = new MultiPlayer(128, 128, key, playerUsername, this, null, -1);
		loginPacket = new PLogin(player.getUsername(), player.x, player.y);
		
		if (socketServer != null) {
			socketServer.addConnection((MultiPlayer) player, loginPacket);
		}
	}
	
	//only called after the user has pressed play from the main menu
	public void addPlayer() {
		level.add(player);
		loginPacket.writeData(socketClient);
	}

	public void run() {
		long previousTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60.0; //nanoseconds in 1 60th of a second i.e 1 frame (@60FPS)
		double delta = 0;
		int frames = 0; //speed the rendering is running at
		int updates = 0; //speed the updating is running at (should be 60)
		requestFocus(); //focuses game window when run
		
		init();
		
		while (isRunning) {
			long now = System.nanoTime();
			delta += (now-previousTime) / ns;
			previousTime = now;
			while (delta >= 1) {
				update();
				updates++;
				delta--;
			}
			render();
			frames++;
			
			if (System.currentTimeMillis() - timer > 1000) { //executes once per second
				timer += 1000;
				frame.setTitle(title + version + "  |  "+updates+" ups, "+frames+" fps");
				updates = 0;
				frames = 0;
			}
		}
	}
	
	
	public void update() {
		if (State == STATE.GAME) {
			key.update();
	 		level.update();
		}
		else menu.update();
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy(); //temporary storage for rendered images
		if (bs == null) {
			createBufferStrategy(2); //triple buffering currently causes more frequent screen tearing
			return;
		}
		screen.clear();
		int xScroll = player.getX()-screen.width/2;
		int yScroll = player.getY()-screen.height/2;
		Graphics g = bs.getDrawGraphics();
		
		if (State == STATE.GAME) {
			level.render(xScroll, yScroll, screen);
			//GUI
			if (!player.isDead()) {
				font.render(player.getLoadedAmmo()+"/"+player.getUnloadedAmmo(), screen.width-50, screen.height-15, false, 0xffffffff, screen, true);
				font.render("00"+player.getRespawns(), 4, 15, false, 0xff00ECFC, screen, true);
				screen.renderSprite(5, 5, Sprite.healthBar, false, 0, true, player.getHealth());
				screen.renderSprite(5, 10, Sprite.stamBar, false, 0, true, player.getStam());
				screen.renderSprite(screen.width-70, screen.height-18, Sprite.ammoIcon, false, 0, true, -1);
			}
		}
		else if (State == STATE.MENU){
			menu.render(screen);
		}
		
		for(int i = 0; i < pixels.length; i++) { //copies the array that determines pixel information (from screen) to the pixel array that accesses the buffered image
			pixels[i] = screen.pixels[i];
		}
		
		for(int i = 0; i < pixelsUi.length; i++) {
			pixelsUi[i] = screen.pixelsUi[i];
		}
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		
		//TORCH
		Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(lightMap, calculateTorch(), null);

        g.drawImage(ui, 0, 0, getWidth(), getHeight(), null);
        
		g.dispose();
		bs.show(); //makes next available buffer visible
	}
	
	private AffineTransform calculateTorch() {
		double aTrig = Mouse.getX() - Game.getWindowWidth()/2;
		double oTrig = Mouse.getY() - Game.getWindowHeight()/2;
		double theta = Math.atan2(oTrig, aTrig);
		AffineTransform at = new AffineTransform();
		at.translate(getWidth() / 2, getHeight() / 2);
		at.rotate(theta + 2.35);
		at.scale(3, 3);
		at.translate(-lightMap.getWidth()/2, -lightMap.getHeight()/2);		
		return at;
	}

	public static int getWindowWidth() {
		return width * scale;
	}
	
	public static int getWindowHeight() {
		return height * scale;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
}
