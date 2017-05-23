/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/


package gov.nasa.gsfc.drl.h2g.util;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class PNG {
	public static void fromTIFF(File tiffFile, File pngFile, int width,
			int height) throws Exception {
		BufferedImage bi = ImageIO.read(tiffFile);
		fromBufferedImage(bi, pngFile, width, height);
	}

	public static void fromBufferedImage(BufferedImage bi, File pngFile,
			int width, int height) throws Exception {
		/*
		 * if (width > 0 && height > 0) { if ((width/((double)height)) <
		 * (bi.getWidth()/((double)bi.getHeight()))) {
		 * scale(bi,width,-1,pngFile); } else { scale(bi,-1,height,pngFile); } }
		 * else { ImageIO.write(bi,"PNG",pngFile); }
		 */
		ImageIO.write(bi, "PNG", pngFile);
	}
}
