package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import navigation.*;

public class Square extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/*
	 * this is a Position made visible and also a user input interface
	 */
	
	public final Position coord;						//link to model, position displayed
	private final BufferedImage basicImage;				//image to display
	private final ArrayList<BufferedImage> images;		//overlays
	private boolean inspected;							//mouseover boolean
	
	//Constructor
	public Square(Position coord) {
		super();
		this.coord = coord;
		
		images = new ArrayList<BufferedImage>();
		
		basicImage = getImage();
	}
	
	//getter
	BufferedImage getImage() {
		return ImageFactory.getSquareImage(coord.x, coord.y);
	}
	
	//adds overlay image
	public void drawImage(BufferedImage image) {
		if (image == null) {
			images.clear();
		} else {
			images.add(image);
		}
	}
	
	//setter
	public void setInspected(boolean b) {
		inspected = b;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.drawImage(basicImage, 0, 0, getWidth(), getHeight(), null);
		g2d.drawImage(ImageFactory.getPieceImage(coord.getPiece()), 0, 0, getWidth(), getHeight(), null);
		
		for (BufferedImage image : images) {
			g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		}
		
		if (inspected) {
			g2d.drawImage(ImageFactory.Inspected, 0, 0, getWidth(), getHeight(), null);
		}
		
		g2d.dispose();
	}
}
