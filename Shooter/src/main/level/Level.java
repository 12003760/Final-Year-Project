package main.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import main.entity.Entity;
import main.entity.mob.Mob;
import main.entity.mob.Player;
import main.entity.particle.Particle;
import main.entity.projectile.Projectile;
import main.graphics.Renderer;
import main.level.tile.Tile;
import main.utilities.Vector;

public class Level {
	
	protected int width, height;
	protected int[] tileID;
	
	//lists of all entities on the level
	private List<Entity> entities = new ArrayList<Entity>();
	private List<Projectile> projectiles = new ArrayList<Projectile>();
	private List<Particle> particles = new ArrayList<Particle>();
	private List<Player> players = new ArrayList<Player>();
	
	private Comparator<Node> sortNodes = new Comparator<Node>() {
		public int compare(Node nodeA, Node nodeB) {
			if (nodeB.fCost < nodeA.fCost) return +1;
			if (nodeB.fCost > nodeA.fCost) return -1;
			return 0;
		}
	};

	public static Level level = new FirstLevel("/maps/firstlevel_map.png");
	
	//constructor for random level
	public Level(int width, int height) {
		this.width = width;
		this. height = height;
		tileID = new int[width*height];
		genLevel();
	}

	//constructor for level from memory
	public Level(String path) {
		loadLevel(path);
		genLevel();
	}
	
	protected void genLevel() {}
	
	protected void loadLevel(String path) {}
 
	public void update() {
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i).isRemoved() == true) entities.remove(i);
			else entities.get(i).update();
		}
		for (int i = 0; i < projectiles.size(); i++) {
			if (projectiles.get(i).isRemoved() == true) projectiles.remove(i);
			else projectiles.get(i).update();			
		}
		for (int i = 0; i < particles.size(); i++) {
			if (particles.get(i).isRemoved() == true) particles.remove(i);
			else particles.get(i).update();
		}
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).isRemoved() == true) players.remove(i);
			else players.get(i).update();
		}
	}
	
	public void render(int xScroll, int yScroll, Renderer screen) {
		screen.setOffset(xScroll, yScroll);
		int x0 = xScroll >> 4; //moves from pixel precision to tile precision
		int x1 = (xScroll + screen.width+16) >> 4;
		int y0 = yScroll >> 4;
		int y1 = (yScroll + screen.height+16) >> 4;
		
		for (int y=y0; y<y1; y++) {
			for (int x=x0; x<x1; x++) {
				getTile(x, y).render(x, y, screen);
			}
		}
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).render(screen);
		}
		for (int i = 0; i < projectiles.size(); i++) {
			projectiles.get(i).render(screen);
		}
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).render(screen);
		}
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).isHidden()) {
				if (players.get(i).getInput() == 1) {
					players.get(i).spawnTimeRender(screen); //only rendered for player being controlled
				}
			}
			else
				players.get(i).render(screen);
		}
	}
	
	public void add(Entity e) {
		e.init(this);
		if(e instanceof Particle) {
			particles.add((Particle)e);
		}
		else if(e instanceof Projectile) {
			projectiles.add((Projectile)e);
		}
		else if(e instanceof Player) {
			players.add((Player)e);
		}
		else entities.add(e);
	}
	
	public List<Node> pathFinder(Vector start, Vector finish) {
		List<Node> openList = new ArrayList<Node>();
		List<Node> closedList = new ArrayList<Node>();
		Node current = new Node(start, null, 0, start.getDistanceTo(finish));
		openList.add(current);
		
		while (openList.size() > 0) {
			Collections.sort(openList, sortNodes);
			current = openList.get(0);		
			if (current.location.equals(finish)) {
				List<Node> path = new ArrayList<Node>();
				while (current.parent != null) {
					path.add(current);
					current = current.parent;
				}
				openList.clear();
				closedList.clear();
				return path;
			}
			openList.remove(current);
			closedList.add(current);
			for (int i = 0; i < 9; i++) {
				if (i == 4) continue; //ignore middle tile
				
				int x = current.location.getX();
				int y = current.location.getY();
				int xDir = (i % 3) - 1; //gives location relative to middle tile
				int yDir = (i / 3) - 1;
				Tile lookingAt = getTile(x + xDir, y + yDir);
				
				if (lookingAt == null) continue;
				if (lookingAt.solid()) continue;
				
				Vector neighbourLoc = new Vector(x + xDir, y + yDir);
				double gCost = current.gCost + (current.location.getDistanceTo(neighbourLoc) /*== 1 ? 1 : 0.95*/);
				//favours diagonals over straight for more human like AI
				double hCost = neighbourLoc.getDistanceTo(finish);
				Node node = new Node(neighbourLoc, current, gCost, hCost);
				
				if (vectorInList(closedList, neighbourLoc) && gCost >= node.gCost) continue;
				if (!vectorInList(openList, neighbourLoc) || gCost < node.gCost) openList.add(node);
			}
			
		}
		closedList.clear();
		return null;
	}
	
	private boolean vectorInList(List<Node> list, Vector v) {
		for (Node n : list) {
			if (n.location.equals(v)) return true;
		}
		return false;
	}
	
	public List<Entity> getEntities(Entity e, int radius) {
		List<Entity> results = new ArrayList<Entity>();
		int eX = e.getX();
		int eY = e.getY();
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			int x = entity.getX();
			int y = entity.getY();
			
			double distance = Math.sqrt(Math.abs((x - eX)*(x - eX) + (y - eY)*(y - eY)));
			if (distance <= radius) results.add(entity);
		}
		return results;
	}
	
	public List<Player> getPlayersInRange(Entity e, int radius) {
		List<Player> results = new ArrayList<Player>();
		int eX = e.getX();
		int eY = e.getY();
		for (int i = 0; i < players.size(); i++) {
			Entity entity = players.get(i);
			int x = entity.getX();
			int y = entity.getY();
			
			double distance = Math.sqrt(Math.abs((x - eX)*(x - eX) + (y - eY)*(y - eY)));
			if (distance <= radius && !entity.isHidden()) results.add((Player)entity);
		}
		return results;
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public Player getPlayerAt(int i) {
		return players.get(i);
	}
	
	public Player getPlayerWithName(String username) {
		for(Player p : players) {
			if(p.getUsername().equals(username)) {
				return p;
			}
		}
		return null;
	}
	
	public int getMultiPlayerIndex(String username) {
		int index = 0;
		for(Player p : players) {
			if(p.getUsername().equals(username)) {
				break;
			}
			index++;
		}
		return index;
	}
	
	public void movePlayerFromServer(String username, int x, int y, int dir) {
		int index = getMultiPlayerIndex(username);
		this.players.get(index).x = x;
		this.players.get(index).y = y;
		this.players.get(index).dir = dir;
		this.players.get(index).isMoving = true;
	}
	
	public void shootFromServer(String username, int x, int y, double shootDirection) {
		int index = getMultiPlayerIndex(username);
		this.players.get(index).shoot(this.players.get(index).getUsername(), x, y, shootDirection);
	}
	
	public Tile getTile(int x, int y) {
		if (x<0 || y<0 || x>=width || y>=height) return Tile.voidTile;
		if (tileID[x+y*width] == Tile.hellrock_col) return Tile.hellrock;
		if (tileID[x+y*width] == Tile.water_col) return Tile.water;
		if (tileID[x+y*width] == Tile.stone_col) return Tile.stone;
		if (tileID[x+y*width] == Tile.wood_col) return Tile.wood;
		return Tile.voidTile;
	}
	
	public List<Projectile> getProjectiles() {
		return projectiles;
	}
	
	//handles projectile collisions
	public boolean projectileCollision(String username, int x, int y, int xOffset, int yOffset, int size, int damage) {
		//if the next tile in direction of movement is solid, return true
		for (int c=0; c<4; c++) { //covers all 4 corners of the sprite
			int xTile = (x + c % 2 * size + xOffset);
			int yTile = (y + c / 2 * size + yOffset);
			if (getTile(xTile >> 4, yTile >> 4).solid()) {
				return true;
			}
			//checks for entity collision
			for (int i = 0; i < entities.size(); i++) {
				if (entities.get(i) instanceof Mob && entities.get(i).isHidden() == false) {
					if (x < entities.get(i).getX()+8 && x > entities.get(i).getX()-8 && 
							y < entities.get(i).getY()+16 && y > (entities.get(i).getY()-16)) {
						if (entities.get(i) instanceof Mob) {
							Mob mob = (Mob)entities.get(i);
							mob.setHealth(-damage);
						}
						return true;
					}
				}
			}
			//checks for player collision
			for (int i = 0; i < players.size(); i++) {
				if (!players.get(i).getUsername().equals(username) && players.get(i).isHidden() == false) {
					if (x < players.get(i).getX()+8 && x > players.get(i).getX()-8 && 
							y < players.get(i).getY()+16 && y > (players.get(i).getY()-16)) {
						if (players.get(i) instanceof Mob) {
							Mob mob = (Mob)players.get(i);
							mob.setHealth(-damage);
						}
						return true;
					}
				}
			}
		}
		return false;
	}
}
