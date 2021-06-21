package independent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class ColorMixer extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/*
	 * this is a JPanel for users to generate colors by rgb codes. The rgb code is set by sliders.
	 */
	
	//modifier
	public static final int RGB = 3;	//without alpha component
	public static final int ARGB = 4;	//with alpha component
	
	private int modifier;									//rgb or argb
	private Dimension dimension;							//size of JPanel
	private ArrayList<ActionListener> actionListeners;		//listeners
	private Color editedColor;								//current state of edited color
	private int red, green, blue;							//rgb values
	private int alpha = 255;								//alpha component
	private ValueSetter redSlider, greenSlider, blueSlider;	//rgb sliders
	private ValueSetter alphaSlider;						//alpha slider
	private boolean enabled;								//input allowed?
	
	//Constructor
	public ColorMixer(Dimension dimension) {
		super();
		
		this.modifier = RGB;

		this.dimension = dimension;
		setPreferredSize(dimension);
		initComponents(modifier);
	}
	
	//constructor with modifier
	public ColorMixer(Dimension dimension, int modifier) {
		super();
		
		this.modifier = modifier;

		this.dimension = dimension;
		setPreferredSize(dimension);
		initComponents(modifier);
	}
	
	//initialize components
	private void initComponents(int modifier) {
		
		switch(modifier) {
		case RGB:		initRGB();
						break;
		case ARGB:		initRGB();
						initA();
						break;
		default:		modifier = RGB;
						initRGB();
		}
		
		validate();
	}
	
	//add rgb sliders
	private void initRGB() {
		redSlider = new ValueSetter(dimension.width - 10, 0, 255);
		redSlider.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
			
		});
		redSlider.setColor(Color.RED);
		add(redSlider);
		
		greenSlider = new ValueSetter(dimension.width - 10, 0, 255);
		greenSlider.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
			
		});
		greenSlider.setColor(Color.GREEN);
		add(greenSlider);
		
		blueSlider = new ValueSetter(dimension.width - 10, 0, 255);
		blueSlider.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
			
		});
		blueSlider.setColor(Color.BLUE);
		add(blueSlider);
	}
	
	//add alpha slider
	private void initA() {
		alphaSlider = new ValueSetter(dimension.width - 10, 0, 255);
		alphaSlider.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
			
		});
		alphaSlider.setColor(Color.GRAY);
		add(alphaSlider);
	}
	
	//update argb values from sliders
	public void update() {
		if (enabled) {
			
			switch (modifier) {
			case RGB:			red = redSlider.getValue();
								green = greenSlider.getValue();
								blue = blueSlider.getValue();
								
								editedColor = new Color(red, green, blue, alpha);
								break;
								
			case ARGB:			red = redSlider.getValue();
								green = greenSlider.getValue();
								blue = blueSlider.getValue();
								alpha = alphaSlider.getValue();
								
								editedColor = new Color(red, green, blue, alpha);
								alphaSlider.setColor(editedColor);
								break;
			}
			
			
			for (ActionListener a : actionListeners) {
				a.actionPerformed(null);
			}
		}
	}
	
	//set new color
	public void setColor(Color color) {
		red = color.getRed();
		green = color.getGreen();
		blue = color.getBlue();
		alpha = color.getAlpha();
		
		editedColor = color;
		
		switch(modifier) {
		case RGB:				redSlider.setValue(red);
								greenSlider.setValue(green);
								blueSlider.setValue(blue);
								break;
		case ARGB:				redSlider.setValue(red);
								greenSlider.setValue(green);
								blueSlider.setValue(blue);
								alphaSlider.setValue(alpha);
								
								alphaSlider.setColor(editedColor);
								break;
		}
		
	}
	
	//getter
	public Color getColor() {
		return editedColor;
	}
	
	//getter
	public int getRed() {
		return redSlider.getValue();
	}
	
	//getter
	public int getGreen() {
		return greenSlider.getValue();
	}

	//getter
	public int getBlue() {
		return blueSlider.getValue();
	}

	//getter
	public int getAlpha() {
		return alphaSlider.getValue();
	}
	//setter
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		enabled = b;
		
		switch (modifier) {
		case RGB:			redSlider.setEnabled(b);
							greenSlider.setEnabled(b);
							blueSlider.setEnabled(b);
							break;
		case ARGB:			redSlider.setEnabled(b);
							greenSlider.setEnabled(b);
							blueSlider.setEnabled(b);
							alphaSlider.setEnabled(b);
							break;
		}
		
	}

	//adds listener
	public void addActionListener(ActionListener actionListener) {
		if (actionListeners == null) {
			actionListeners = new ArrayList<ActionListener>();
		}
		actionListeners.add(actionListener);
	}

}
