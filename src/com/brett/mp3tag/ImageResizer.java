package com.brett.mp3tag;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageResizer {

	public boolean isResizeNecessary(Dimension imgSize, Dimension boundary) {

		int original_width = imgSize.width;
		int original_height = imgSize.height;
		int bound_width = boundary.width;
		int bound_height = boundary.height;

		if (original_width > bound_width) {
			return true;
		}
		if (original_height > bound_height) {
			return true;
		}
		return false;
	}

	public Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

		int original_width = imgSize.width;
		int original_height = imgSize.height;
		int bound_width = boundary.width;
		int bound_height = boundary.height;
		int new_width = original_width;
		int new_height = original_height;

		// first check if we need to scale width
		if (original_width > bound_width) {
			//scale width to fit
			new_width = bound_width;
			//scale height to maintain aspect ratio
			new_height = (new_width * original_height) / original_width;
		}

		// then check if we need to scale even with the new height
		if (new_height > bound_height) {
			//scale height to fit instead
			new_height = bound_height;
			//scale width to maintain aspect ratio
			new_width = (new_height * original_width) / original_height;
		}

		return new Dimension(new_width, new_height);
	}

	public BufferedImage resizeImage(BufferedImage originalImage, Dimension boundary)
	{
		Dimension newSize = getScaledDimension(new Dimension(originalImage.getWidth(), originalImage.getHeight()), boundary);
		BufferedImage resizedImage = new BufferedImage(newSize.width, newSize.height,  originalImage.getType());
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newSize.width, newSize.height, null);
		g.dispose();

		return resizedImage;
	}
}