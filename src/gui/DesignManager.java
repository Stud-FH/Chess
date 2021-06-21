package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import independent.ColorMixer;
import main.FileManager;

public class DesignManager {
	
	/*
	 * this class' responsibility is to control the gui's design settings
	 * singleton
	 */

	//////////////////// STATIC ////////////////////
	
	private static DesignManager instance;																			//singleton
	private static Properties defaultProperties;																	//default design settings
	private static FileManager<Properties> fileManager;																//saved design settings
	private static DesignEditor editor;																				//interactive design editor for user
	public static final DynamicMaterial boardMaterial = new DynamicMaterial(Material.Marble, "Board Material");		//board image overdrawn with bright and dark squares
	
	//must be initialized before gui is being created
	public static void init() {
		if (instance == null) {
			defaultProperties = new Properties();
			fileManager = new FileManager<Properties>("data/settings/design/", "set");
			instance = new DesignManager();
			editor = new DesignEditor();
			
			load("saved");
			
			ImageFactory.init();
		}
	}
	
	//load saved design settings
	public static boolean load(String name) {
		try {
			instance.setProperties(fileManager.open(name));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//save current design settings
	public static boolean save() {
		try {
			instance.properties.update();
			fileManager.save(instance.properties, "saved");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//use default design settings
	public static void reset() {
		instance.setProperties(defaultProperties);
	}
	
	//open interactive design editor
	public static void openEditor() {
		editor.setVisible(true);
	}
	
	//getter
	public static String getMaterialPath() {
		return "image/material/" + boardMaterial.get().name() + ".png";
	}
	
//////////////////// PRIVATE ////////////////////
	
	private Properties properties;	//current design settings
	
	//Constructor
	private DesignManager() {
		setProperties(defaultProperties);
	}
	
	//updates colors and material by current design settings
	private void update() {
		DynamicColor.LegalMoveMark.set(properties.legalMove);
		DynamicColor.AttackMark.set(properties.attack);
		DynamicColor.LastMoveMark.set(properties.lastMove);
		DynamicColor.SelectedMark.set(properties.selected);
		DynamicColor.BrightSquare.set(properties.brightSquare);
		DynamicColor.DarkSquare.set(properties.darkSquare);
		boardMaterial.set(properties.material);
	}
	
	//setter
	private void setProperties(Properties properties) {
		this.properties = properties;
		update();
	}
	
	
	
//////////////////// PUBLIC ////////////////////
	
	/*
	 * since Colors are unchangeable, DynamicColor saves one Color and is able to replace it by others. Observers are notified of any changes.
	 * DynamicColor is used by actively drawing classes
	 */
	
	public static enum DynamicColor {
		LegalMoveMark(DesignManager.defaultProperties.legalMove, "Legal Move"),
		AttackMark(defaultProperties.attack, "Attack"),
		LastMoveMark(defaultProperties.lastMove, "Last Move"),
		SelectedMark(defaultProperties.selected, "Selected"),
		BrightSquare(defaultProperties.brightSquare, "Bright Square"),
		DarkSquare(defaultProperties.darkSquare, "Dark Square");
		
		private final ArrayList<ActionListener> actionListeners;	//listeners
		
		private final Color defaultColor;							//first and default Color
		public final String name;									//name to display
		private Color color;										//changeable Color
		
		//Constructor
		DynamicColor(Color defaultColor, String name) {
			this.defaultColor = defaultColor;
			this.name= name;
			actionListeners = new ArrayList<ActionListener>();
			
			reset();
		}
		
		//returns currently saved color
		public Color get() {
			if (color == null) {
				return defaultColor;
			}
			return color;
		}
		
		//equal to set(null)
		public void reset() {
			set(defaultColor);
		}
		
		//setter
		public void set(Color color) {
			this.color = color;
			for (ActionListener a : actionListeners) {
				a.actionPerformed(null);
			}
		}
		
		//add listener
		public void addActionListener(ActionListener a) {
			actionListeners.add(a);
		}
	}
	

	/*
	 * same concept as DynamicColor
	 */
	
	public static class DynamicMaterial {

		private final ArrayList<ActionListener> actionListeners;	//listeners

		private final Material defaultMaterial;						//default value
		public final String name;									//name to display
		private Material material;									//changeable value
		
		//Constructor
		DynamicMaterial(Material defaultMaterial, String name) {
			this.defaultMaterial = defaultMaterial;
			this.name= name;
			actionListeners = new ArrayList<ActionListener>();
			
			reset();
		}
		
		//getter
		public Material get() {
			if (material == null) {
				return defaultMaterial;
			}
			return material;
		}
		
		//equal to set(null)
		public void reset() {
			set(defaultMaterial);
		}
		
		//setter
		public void set(Material material) {
			this.material = material;
			for (ActionListener a : actionListeners) {
				a.actionPerformed(null);
			}
		}
		
		//add listener
		public void addActionListener(ActionListener a) {
			actionListeners.add(a);
		}
	}
	
	
	/*
	 * Properties is the full collection of all design settings
	 */
	
	private static class Properties implements Serializable {
		private static final long serialVersionUID = 1661317127130920353L;
		
		private static boolean initialized;				//if default properties aren't initialized yet, they have to be
		
		Color legalMove, attack, lastMove, selected, brightSquare, darkSquare;		//colors used to draw board
		Material material;															//bard material
		
		//Constructor
		Properties() {
			if (!initialized) {
				legalMove = new Color(0, 165, 255, 80);
				attack = new Color(255, 0, 0, 80);
				lastMove = new Color(255, 225, 0, 80);
				selected = new Color(40, 175, 60, 80);
				brightSquare = new Color(255, 225, 181, 255);
				darkSquare = new Color(20, 20, 20, 255);
				
				initialized = true;
			}
		}
		
		// copies current design
		void update() {
			legalMove = DynamicColor.LegalMoveMark.get();
			attack = DynamicColor.AttackMark.get();
			lastMove = DynamicColor.LastMoveMark.get();
			selected = DynamicColor.SelectedMark.get();
			brightSquare = DynamicColor.BrightSquare.get();
			darkSquare = DynamicColor.DarkSquare.get();
			material = boardMaterial.get();
		}
	}
	
	
	/*
	 * the names of this enum's instances is used to load the preferred material image layer
	 */
	
	public enum Material {
		Marble, Wood, Glass, Concrete, Stone, Carpet;
	}
	
	
	/*
	 * JFrame for user to individualize design settings
	 */
	
	private static class DesignEditor extends JFrame {
		private static final long serialVersionUID = 1L;

		Dimension dimension;		//size of JFrame
		
		DynamicColor editedColor;	//currently edited DynamicColor
		JLabel label;				//text over ColorMixer
		ColorMixer colorizer;		//simple argb-value setter
		
		//Constructor
		DesignEditor() {
			super("Design Editor");
			dimension = new Dimension(300, 220);
			setPreferredSize(dimension);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			setLayout(new BorderLayout());
			initMenuBar();
			initComponents();
			pack();
			setResizable(false);
			setLocationRelativeTo(null);
			
			addWindowListener(new java.awt.event.WindowAdapter() {
			    @Override
			    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
			        setVisible(false);
			    }
			});
			
		}
		
		//select color to edit
		private void editColor(DynamicColor c) {
			editedColor = c;
			label.setText(editedColor.name);
			colorizer.setColor(c.get());
		}
		
		//initialize design editor components
		private void initComponents() {
			
			label = new JLabel("Select a color");
			label.setFont(new Font("Serif", Font.BOLD, 14));
			label.setPreferredSize(new Dimension(dimension.width, 20));
			label.setHorizontalAlignment(JLabel.CENTER);
			add(label, BorderLayout.NORTH);
			
			colorizer = new ColorMixer(new Dimension(dimension.width, dimension.height - 20), ColorMixer.ARGB);
			colorizer.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (editedColor != null) editedColor.set(colorizer.getColor());
				}
			});
			add(colorizer, BorderLayout.CENTER);
			colorizer.setEnabled(true);
		}
		
		//initialize design editor menu
		private void initMenuBar() {
			
			JMenuBar menubar = new JMenuBar();
			
			
			JMenu saveMenu = new JMenu("Save");
			menubar.add(saveMenu);
			
			JMenuItem save = new JMenuItem("Save");
			save.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					save();
				}
			});
			saveMenu.add(save);
			
			
			
			JMenu selectColor = new JMenu("Select Color");
			menubar.add(selectColor);
			
			for (DynamicColor c : DynamicColor.values()) {
				JMenuItem colorItem = new JMenuItem(c.name);
				colorItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						editColor(c);
					}
				});
				selectColor.add(colorItem);
			}
			
			
			
			JMenu material = new JMenu("Material");
			menubar.add(material);
			
			for (Material m : Material.values()) {
				JMenuItem materialItem = new JMenuItem(m.name());
				materialItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						boardMaterial.set(m);
					}
				});
				material.add(materialItem);
			}
			setJMenuBar(menubar);
		}
	}
	
	
	
}
