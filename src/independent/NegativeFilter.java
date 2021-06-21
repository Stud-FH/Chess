package independent;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

public class NegativeFilter extends RGBImageFilter {
	
	/*
	 * this subclass of RGBImageFilter will turn every image to its negative image by replacing each color with its complementary color
	 */
	
	//Constructor
	public NegativeFilter() {
		super();
        canFilterIndexColorModel = true;
	}
	
	//creates negative image
	public Image edit(Image original) {
		ImageProducer producer = new FilteredImageSource(original.getSource(), this);
		return Toolkit.getDefaultToolkit().createImage(producer);
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		return rgb ^ 0xFFFFFF;
	}
}
