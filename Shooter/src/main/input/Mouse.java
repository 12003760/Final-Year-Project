package main.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener{

	private static int xMouse = 0;
	private static int yMouse = 0;
	private static int buttonMouse = 0;
	
	public void mouseDragged(MouseEvent e) {	
		xMouse = e.getX();
		yMouse = e.getY();
	}

	public void mouseMoved(MouseEvent e) {
		xMouse = e.getX();
		yMouse = e.getY();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		buttonMouse = e.getButton();
	}

	public void mouseReleased(MouseEvent e) {
		buttonMouse = 0;
		//reset
	}

	public static int getX() {
		return xMouse;
	}

	public static int getY() {
		return yMouse;
	}

	public static int getButton() {
		return buttonMouse;
	}
}
