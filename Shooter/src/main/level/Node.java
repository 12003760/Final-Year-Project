package main.level;

import main.utilities.Vector;

public class Node {

	public Vector location;
	public Node parent;
	public double fCost, gCost, hCost;
	
	public Node(Vector location, Node parent, double gCost, double hCost) {
		this.location = location;
		this.parent = parent;
		this.gCost = gCost;
		this.hCost = hCost;
		this.fCost = this.gCost + this.hCost;
	}
}
