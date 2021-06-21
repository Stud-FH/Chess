package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controller.VisualController;
import piece.Piece;

public class GUI extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	
	/*
	 * graphical user interface
	 * singleton
	 */
	
	private static GUI instance;	//singleton
	
	public static final Dimension dimension = new Dimension(600, 600);	//window size
	
	//initializes displaying of application
	public static void init() {
		instance = new GUI();
	}
	
	//sets perspective (VisualController) to display
	public static void setController(VisualController controller) {
		instance.gamePanel.display(controller.getBoard());
		instance.validate();
	}
	
	//notifies user of gameover, perhaps stops application
	public static void gameoverMessage(String text) {
		if (JOptionPane.showConfirmDialog(instance, 
				text, "Game Over", 
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)
		{
			instance.exit();
		}
	}
	
	//user must select any type of piece for his Pawn to become
	public static Piece.Type selectPromotion() {
		Piece.Type[] types = {Piece.Type.Queen, Piece.Type.Rook, Piece.Type.Knight, Piece.Type.Bishop};
		int index = JOptionPane.showOptionDialog(instance, "Please select your promotion.", "Pawn Promotion", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
		return types[index];
	}
	
	//game loop repetition
	private Thread thread;
	private boolean isRunning = false;
	private int FPS = 30;
	private long targetTime = 1000 / FPS;
	
	//game loop
	public void run() {
		long start, elapsed, wait;
		
		while (isRunning) {
			
			start = System.currentTimeMillis();
			
			update();
			repaint();
			
			elapsed = System.currentTimeMillis() - start;
			wait = targetTime - elapsed;
			if (wait < 5) {wait = 5;}
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	//repaint
	private void update() {
		gamePanel.repaint();
	}
	
	//start displaying
	private void start() {
		
		isRunning = true;
		thread = new Thread(this, "Game loop");
		thread.start();
	}
	
	//JPanel for user to interact with
	private GamePanel gamePanel;
	
	//Constructor
	private GUI() {
		super("Chess");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		initComponent();
		setJMenuBar(Menu.get());
		pack();
		setResizable(false);
		setVisible(true);
		setLocationRelativeTo(null);
		start();
	}
	
	//initialize gui components
	private void initComponent() {
		setLayout(new BorderLayout());
		
		gamePanel = new GamePanel();
		
		add(gamePanel, BorderLayout.CENTER);
		gamePanel.display(new EmptyPanel());
	}
	
	public void exit() {

		boolean repeat = false;
		do {
			try {
				isRunning = false;
				
				thread.join(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
				repeat = true;
			}
		} while (repeat);

		System.exit(300);
	}
	
	/*
	 * GamePanel is meant to be the center space of the application's display, so perspectives are easy to add or remove from here
	 */
	
	private class GamePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		//Constructor
		GamePanel() {
			super();
			setPreferredSize(dimension);
		}
		
		//set perspective
		void display(JPanel panel) {
			removeAll();
			add(panel);
			validate();
		}
	}
	
	/*
	 * an EmptyBoard is meant to fill the GamePanel's space with an empty chess board before the game has actually started
	 */
	private class EmptyPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		//Constructor
		EmptyPanel() {
			super();
			setPreferredSize(dimension);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			
			g2d.drawImage(ImageFactory.getBoardImage(), 0, 0, getWidth(), getHeight(), null);
			g2d.dispose();
		}
	}

}
