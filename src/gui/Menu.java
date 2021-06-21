package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import main.*;

public class Menu extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	/*
	 * this subclass of jMenuBar is stored in an own class in order to separate this verry long code from the rest of the gui
	 * singleton
	 */
	
	private static final Menu instance = new Menu();
	
	//getter
	public static Menu get() {
		return instance;
	}
	
	private JMenu newGame, loadGame, history, extras;	//different menu parts
	
	//Constructor
	private Menu() {
		super();
		
		initNewGame();
		initLoadGame();
		initHistory();
		initExtras();
	}

	//menu option to start new game
	private void initNewGame() {
		newGame = new JMenu("New Game");
		add(newGame);
		
		JMenuItem hotseat = new JMenuItem("Hotseat");
		newGame.add(hotseat);
		hotseat.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Main.initGame(Game.Mode.Hotseat);
			}
		});
		
		JMenuItem white = new JMenuItem("Play As White");
		newGame.add(white);
		white.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Main.initGame(Game.Mode.WhiteUser);
			}
		});
		
		JMenuItem black = new JMenuItem("Play As Black");
		newGame.add(black);
		black.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Main.initGame(Game.Mode.BlackUser);
			}
		});
	}
	
	//menu option to load saved game
	private void initLoadGame() {
		loadGame = new JMenu("Load Game");
		add(loadGame);
		loadGame.addMenuListener(new MenuListener() {

			@Override
			public void menuCanceled(MenuEvent arg0) {}
			public void menuDeselected(MenuEvent arg0) {}

			@Override
			public void menuSelected(MenuEvent arg0) {
				registerSavedGames();
			}
		});
	}
	
	//menu option to go through the current game's history
	private void initHistory() {
		history = new JMenu("History");
		add(history);
		history.addMenuListener(new MenuListener() {

			@Override
			public void menuCanceled(MenuEvent arg0) {}
			public void menuDeselected(MenuEvent arg0) {}

			@Override
			public void menuSelected(MenuEvent arg0) {
				registerHistory();
			}
		});
	}
	
	//menu option of design editor
	private void initExtras() {
		extras = new JMenu("Extras");
		add(extras);
		
		JMenuItem designEditor = new JMenuItem("Design Editor");
		designEditor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DesignManager.openEditor();
			}
		});
		extras.add(designEditor);
	}
	
	/////////////////////////////////////////////////////////////////
	
	//registers all save files
	private void registerSavedGames() {
		loadGame.removeAll();
		for (String name : Main.getSavedNames()) {
			loadGame.add(new LoadGameItem(name));
		}
	}
	
	//lists whole history log of current game
	private void registerHistory() {
		history.removeAll();
		if (Game.hasInstance()) {
			for (int index = 0; index < Game.getHistory().getLogSize(); index++) {
				history.add(new HistoryItem(index));
			} 
		}
		history.validate();
	}
	
	///////////////////////////////////////////////////////////////////
	
	/*
	 * loads a saved game by its name
	 */
	private class LoadGameItem extends JMenuItem {
		private static final long serialVersionUID = 1L;

		//Constructor
		LoadGameItem(String name) {
			super(name);
			addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Main.loadGame(name);
				}
			});
		}
	}
	
	/*
	 * makes history jump to a given round index
	 */
	private class HistoryItem extends JMenuItem {
		private static final long serialVersionUID = 1L;

		//Constructor
		HistoryItem(int index) {
			super(Game.getHistory().getRoundName(index));
			addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Main.loadGame(Game.getName(), index);
				}
			});
		}
	}
	

}
