package independent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextField;


public class ValueSetter extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/*
	 * this is a slider with additional value input field, with minimum and maximum value
	 */
	
	//static height
	public static final int HEIGHT = 30;
	
	//value
	private final int min, max;
	private int value;
	
	private ArrayList<ActionListener> actionListeners;		//listeners
	private Slider slider;									//value slider
	private JTextField textField;							//manual value input
	private boolean enabled;								//input allowed?
	
	//Constructor
	public ValueSetter(int size, int min, int max) {
		super();
		setPreferredSize(new Dimension(size, HEIGHT));
		
		this.min = min;
		this.max = max;
		
		actionListeners = new ArrayList<ActionListener>();
		
		slider = new Slider(size - 60, min, max);
		
		textField = new JTextField("0", (int) (Math.log10(size)+1));
		textField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {}

			@Override
			public void focusLost(FocusEvent arg0) {
				setValue(getTextFieldInput());
			}
		});
		
		add(slider);
		add(textField);
	}

	//update value
	public void update() {
		slider.setValue(value);
		textField.setText(Integer.toString(value));
		
		if (enabled) {
			for (ActionListener a : actionListeners) {
				//TODO add argument new ActionEvent(Object source, int id, String command
				a.actionPerformed(null);
			}
		}
	}
	
	//getter
	public int getValue() {
		return value;
	}
	
	//setter
	public void setValue(int value) {
		if (value < min) {
			value = min;
		} else if(value > max) {
			value = max;
		} else {
			this.value = value;
		}
		slider.setValue(value);
		textField.setText(Integer.toString(value));
	}
	
	//getter
	private int getTextFieldInput() {
		try {
			return Integer.parseInt(textField.getText());
		} catch (NumberFormatException e) {

			return value;
		}
	}
	
	//setter
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		enabled = b;
		slider.setEnabled(b);
	}
	
	//getter
	public boolean isEnabled() {
		return enabled;
	}
	
	//setter
	public void setColor(Color color) {
		slider.backgroundColor = color;
	}
	
	//add listener
	public void addActionListener(ActionListener actionListener) {
		
		actionListeners.add(actionListener);
	}
	
	/*
	 * an interactive JPanel constituting a slider
	 */
	private class Slider extends JPanel implements MouseListener, MouseMotionListener{
		private static final long serialVersionUID = 1L;
		
		private int size, selection, value;		//length of slider, pixel clicked and value
		private final int min, max;				//minimum and maximum
		private Graphics2D g2d;					//Graphics2D is needed to paint
		private Color backgroundColor;			//background color
		private boolean enabled;				//input allowed?
		
		//Constructor
		public Slider(int size, int min, int max) {
			super();
			setPreferredSize(new Dimension(size, ValueSetter.HEIGHT / 2));
			
			this.size = size;
			this.min = min;
			this.max = max;
			
			backgroundColor = Color.GRAY;
			
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
		//setter
		public void setValue(int value) {
			if (value >= min && value <= max) {
				this.value = value;
			}
			selection = ((value - min) * size) / (max - min);
		}

		//calculate value
		private void update() {
			value = ((selection * (max - min)) / size) + min;
			setValue(value);
			update();
			
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			g2d = (Graphics2D) g;
			
			g2d.setColor(backgroundColor);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			
			g2d.setColor(Color.WHITE);
			g2d.fillRect(selection - 1, 0, 3, getHeight());

			g2d.setColor(Color.BLACK);
			g2d.drawRect(0, 0, getWidth(), getHeight());
			
			g2d.dispose();
		}
		
		//get pixel clicked
		private void adaptMousePosition(MouseEvent e) {
			if (e.getX() < 0) {
				selection = 0;
			} else if (e.getX() > size) {
				selection = size;
			} else {
				selection = e.getX();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (enabled) {
				adaptMousePosition(e);
				update();
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if (enabled) {
				adaptMousePosition(e);
				update();
			}
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (enabled) {
				adaptMousePosition(e);
				update();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (enabled) {
				adaptMousePosition(e);
				update();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (isEnabled()) {
				setEnabled(true);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setEnabled(false);
		}

		@Override
		public void mouseMoved(MouseEvent e) {}
		
		//setter
		public void setEnabled(boolean b) {
			super.setEnabled(b);
			enabled = b;
			
		}
		
	}

}
