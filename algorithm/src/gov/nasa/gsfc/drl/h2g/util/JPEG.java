/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/


package gov.nasa.gsfc.drl.h2g.util;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class JPEG {
	public static void fromTIFF(File tiffFile, File jpegFile, int width,
			int height) throws Exception {
		BufferedImage bi = ImageIO.read(tiffFile);
		fromBufferedImage(bi, jpegFile, width, height);
	}

	public static void fromBufferedImage(BufferedImage bi, File jpegFile,
			int width, int height) throws Exception {
		/*
		 * if (width > 0 && height > 0) { if ((width/((double)height)) <
		 * (bi.getWidth()/((double)bi.getHeight()))) {
		 * scale(bi,width,-1,jpegFile); } else { scale(bi,-1,height,jpegFile); }
		 * } else { ImageIO.write(bi,"JPEG",jpegFile); }
		 */

		ImageIO.write(bi, "JPEG", jpegFile);
	}
}
