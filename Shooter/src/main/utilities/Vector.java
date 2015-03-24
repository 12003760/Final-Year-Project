package main.utilities;

public class Vector {
	
	private int x, y;
	
	public Vector() {
		this(0, 0);
	}
	
	public Vector(int x, int y) {
		this.setX(x);
		this.setY(y);
	}
	
	public Vector(Vector v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	public Vector add(Vector v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}
	
	public void sub(Vector v) {
		this.x -= v.x;
		this.y -= v.y;
	}
	
	public double getDistanceTo(Vector b) {
		double dx = this.x - b.x;
		double dy = this.y - b.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Vector)) return false;
		Vector v = (Vector) o;
		if (v.getX() == this.getX() && v.getY() == this.getY()) return true;
		return false;
	}
}
