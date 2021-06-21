package gui;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import gui.DesignManager.DynamicColor;
import independent.NegativeFilter;
import piece.Piece;
import player.Player;

public class ImageFactory {
	
	/*
	 * the ImageFactory draws squares and pieces
	 */
	
	private static final int unit = GUI.dimension.height / 8;	//height or width of one square

	//image overlays
	public static final BufferedImage Inspected = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage Selected = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage LastMove = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage MovementPossibillity = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage AttackingPossibillity = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	
	//basic square colors (drawn over material image)
	public static final BufferedImage BrightSquare = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage DarkSquare = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	
	//piece images (draw by Squares themselves)
	public static final BufferedImage WhitePawn = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage WhiteRook = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage WhiteKnight = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage WhiteBishop = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage WhiteQueen = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage WhiteKing = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage BlackPawn = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage BlackRook = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage BlackKnight = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage BlackBishop = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage BlackQueen = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	public static final BufferedImage BlackKing = new BufferedImage(unit, unit, BufferedImage.TYPE_INT_ARGB);
	
	//drawn board
	private static final BufferedImage Board = new BufferedImage(8 * unit, 8 * unit, BufferedImage.TYPE_INT_ARGB);	//full board
	private static final BufferedImage[] square = new BufferedImage[64];											//square subimages
	
	//getter
	public static BufferedImage getBoardImage() {
		return Board;
	}
	
	//getter
	public static BufferedImage getSquareImage(int x, int y) {
		return square[x + (8 * y)];
	}
	
	//getter
	public static BufferedImage getPieceImage(Piece piece) {
		return piece != null? getPieceImage(piece.player, piece.type) : null;
	}
	
	//getter
	public static BufferedImage getPieceImage(Player player, Piece.Type type) {
		if (player == Player.White) {
			switch (type) {
			case Pawn:			return WhitePawn;
			case Rook:			return WhiteRook;
			case Knight:		return WhiteKnight;
			case Bishop:		return WhiteBishop;
			case Queen:			return WhiteQueen;
			case King:			return WhiteKing;
			}
		} else {
			switch (type) {
			case Pawn:			return BlackPawn;
			case Rook:			return BlackRook;
			case Knight:		return BlackKnight;
			case Bishop:		return BlackBishop;
			case Queen:			return BlackQueen;
			case King:			return BlackKing;
			}
		}
		return null;
	}
	
	//must be initialized before board is drawn first time
	public static void init() {
		initMonochromeImage(Selected, DynamicColor.SelectedMark);
		initMonochromeImage(LastMove, DynamicColor.LastMoveMark);
		initMonochromeImage(MovementPossibillity, DynamicColor.LegalMoveMark);
		initMonochromeImage(AttackingPossibillity, DynamicColor.AttackMark);

		initMonochromeImage(BrightSquare, DynamicColor.BrightSquare);
		initMonochromeImage(DarkSquare, DynamicColor.DarkSquare);

		for (int i = 0; i < 64; i++) square[i] = new BufferedImage(75, 75, BufferedImage.TYPE_INT_ARGB);
		
		ActionListener drawBoard = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawBoard();
			}
		};
		DynamicColor.BrightSquare.addActionListener(drawBoard);
		DynamicColor.DarkSquare.addActionListener(drawBoard);
		DesignManager.boardMaterial.addActionListener(drawBoard);
		
		drawPieces();
		drawBoard();
	}
	
	//simple color-overlays are connected with a DynamicColor here (listeners update changing colors automatically)
	private static void initMonochromeImage(BufferedImage image, DynamicColor color) {
		color.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fillWithColor(image, color);
			}
		});
		fillWithColor(image, color);
	}
	
	//redraws simple color-overlays with updated color
	private static void fillWithColor(BufferedImage image, DynamicColor color) {
		if (image != null) {
			Graphics2D g2d = (Graphics2D) image.getGraphics();
			g2d.setComposite(AlphaComposite.SrcIn);
			g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
			if (color.get() != null) {
				g2d.setColor(color.get());
				g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
			}
		}
	}
	
	//paints an image over another one
	private static void drawOverImage(BufferedImage basicImage, Image image) {
		if (basicImage != null && image != null) {
			Graphics2D g2d = (Graphics2D) basicImage.getGraphics();
			g2d.drawImage(image, 0, 0, basicImage.getWidth(), basicImage.getHeight(), null);
		}
	}
	
	//draws all pieces
	private static void drawPieces() {
		
		NegativeFilter negFilter = new NegativeFilter();
		
		for (Piece.Type t : Piece.Type.values()) {
			
			try {
				Image whiteImage = ImageIO.read(new File("image/piece/" + t.name()+".png"));
				drawOverImage(getPieceImage(Player.White, t), whiteImage);
				
				Image blackImage = negFilter.edit(whiteImage);
				drawOverImage(getPieceImage(Player.Black, t), blackImage);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//draws full board
	private static void drawBoard() {
		Graphics2D g2d = (Graphics2D) Board.getGraphics();
		
		try {
			Image material = ImageIO.read(new File(DesignManager.getMaterialPath()));
			g2d.drawImage(material, 0, 0, GUI.dimension.width, GUI.dimension.height, null);
			
			int unit = GUI.dimension.height / 8;
			int x, y;
			BufferedImage color;
			for (int i = 0; i < 64; i++) {
				x = i % 8;
				y = i / 8;
				color = (x + y) % 2 == 0? BrightSquare : DarkSquare;
				g2d.drawImage(color, x * unit, y * unit, unit, unit, null);
				
				Graphics2D gSquare = (Graphics2D) square[i].getGraphics();
				gSquare.drawImage(Board.getSubimage(x * unit, y * unit, unit, unit), 0, 0, unit, unit, null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
