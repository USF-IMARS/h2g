/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/


package gov.nasa.gsfc.drl.h2g.util;

import java.awt.Color;

public class GeneralValueMapper implements ValueMapper {

	// Important values in the SDS needed for image processing
	private int fill_min;
	private int fill_max;
	// What values in mask needs to be set to fillvalues
	private int[] maskvalues;
	private int maskType;

	private byte[][] colorMap8 = new byte[3][256];

	// These are indices in the 8 bit colormap
	private static final int FILL_INDEX = 0;
	private static final int MIN_INDEX = 1;
	private static final int MAX_INDEX = 255;
	// Coefficients to transform SDS values to image index values
	// a+bx+cx^2+dx^3+e ln(x)
	private double[] coefficients;
	private String scalingType;
	private Boolean fireFlag = Boolean.FALSE;
	private int[] fireValues;

	/****************************************************************************
	 * GeneralValueMapper with Masks.
	 ****************************************************************************/
	public GeneralValueMapper(Configuration config) throws Exception {
		this.fill_min = config.getSDSFillvalue_min();
		this.fill_max = config.getSDSFillvalue_max();
		this.maskvalues = config.getMASKValues();
		this.coefficients = config.getCoefficients();
		this.fireFlag = config.getfireFlag();
		this.fireValues = config.getfireValues();
		this.maskType = config.getMASKType();
		this.scalingType = config.getscalingType();

		// colorMap8 = Util.readNormalizedColorMap(config.getColormapPath());
		// colorMap8 =
		// Util.readNormalizedColorMap("colormaps"+File.separator+"tpw_colortable.txt");
		String path = config.getColormapPath();
		if (config.isStandardColorMap())
			colorMap8 = Util.readIntegerColorMap(path);
		else
			colorMap8 = Util.readIntegerColorMap_nonStandard(path);
		
	}

	/****************************************************************************
	 * getRGB.
	 ****************************************************************************/
	public int getRGB() {
		int red = (colorMap8[0][FILL_INDEX] & 0x000000FF);
		int green = (colorMap8[1][FILL_INDEX] & 0x000000FF);
		int blue = (colorMap8[2][FILL_INDEX] & 0x000000FF);
		Color color = new Color(red, green, blue);
		return color.getRGB();
	}

	public int getRGB(int[] values) {

		int data = getData(values);
		int red = (colorMap8[0][data] & 0x000000FF);
		int green = (colorMap8[1][data] & 0x000000FF);
		int blue = (colorMap8[2][data] & 0x000000FF);
		Color color = new Color(red, green, blue);

		return color.getRGB();
	}

	/****************************************************************************
	 * getData.
	 ****************************************************************************/
	public int getData() {
		return FILL_INDEX;
	}

	public int getData(int[] values) {
		if (values[0] >= fill_min && values[0]<=fill_max) {
			return getData();
		}
		if (values.length > 1) {
			if (maskType == Configuration.DISCRETE)
				for (int i = 0; i < maskvalues.length; i++)
					if (values[1] == maskvalues[i]) {
						return getData();
					}
			if (maskType == Configuration.CONTINUOUS)
				for (int i = 0; i < maskvalues.length - 1; i = i + 2)
					if (values[1] >= maskvalues[i]
							&& values[1] <= maskvalues[i + 1]) {
						return getData();
					}

		}
		int index = 0;
		double indexdouble = 0.0;
		if (scalingType.equals("LINEAR") || scalingType.equals("NONLINEAR")) {
			indexdouble = (coefficients[0] + coefficients[1] * values[0]
					+ coefficients[2] * Math.pow(values[0], 2) + coefficients[3]
					* Math.pow(values[0], 3));
			if (coefficients[4] != 0)
				indexdouble = indexdouble + coefficients[4]
						* (Math.log(values[0]) / Math.log(10));
			index = (int) Math.round(indexdouble);
		}

		if (scalingType.equals("SEGMENTED_LINEAR")) {

			int value = values[0];
			Boolean notscaled = Boolean.TRUE;
			// System.out.println("Value="+value);
			int n = coefficients.length;
			double totalmin = coefficients[0];
			double totalmax = coefficients[n - 2];
			for (int seg = 0; seg < n - 2; seg = seg + 2) {
				double minactual = coefficients[seg];
				double maxactual = coefficients[seg + 2];
				double range = maxactual - minactual;

				int MIN_INDEX = (int) Math.round(coefficients[seg + 1]);
				int MAX_INDEX = (int) Math.round(coefficients[seg + 3]);
				int INDEX_RANGE = MAX_INDEX - MIN_INDEX;

				double intercept = MIN_INDEX
						- (minactual * INDEX_RANGE / range);
				double slope = INDEX_RANGE / range;

				if (value >= minactual && value < maxactual) {
					// System.out.println("Lies between "+minactual+" "+maxactual);
					index = (int) Math.round(intercept + slope * value);
					index = Util.toRange(0, index, 255);
					// System.out.println("Scale Value "+levels[c]);
					notscaled = Boolean.FALSE;
					break;
				}
			}
			if (notscaled) {
				if (value < totalmin)
					index = 0;
				else if (value > totalmax)
					index = 255;
				else
					index = 0;
			}

		}
		//
		// System.out.println(coefficients[0]+"+"+coefficients[1]*values[0]+"+"+coefficients[2]*Math.pow(values[0],2)+"+"+coefficients[3]*Math.pow(values[0],3)+"+"+coefficients[4]*(Math.log(values[0])/Math.log(10))+"="+index);
		return Util.toRange(MIN_INDEX, index, MAX_INDEX);
	}

	/****************************************************************************
	 * getColorMap8.
	 ****************************************************************************/
	public byte[][] getColorMap8() {
		return colorMap8;
	}

	/****************************************************************************
	 * getColorMap16.
	 ****************************************************************************/
	public byte[][] getColorMap16() {
		return null;
	}
	
	/******************************************************
	 *  Reverse mapping; Input: color triplet: Output: index
	 *******************************************************/
     public int getColorIndex(int[] rgb){
    	 for (int i=0;i<256;i++){
    		 int red = (colorMap8[0][i] & 0x000000FF);
    	 		int green = (colorMap8[1][i] & 0x000000FF);
    	 		int blue = (colorMap8[2][i] & 0x000000FF);
    		 //System.out.println("Checking "+red+","+green+","+blue);
    		 if(rgb[0]==red && rgb[1]==green && rgb[2]==blue)
    			 return(i);
    	 }
    	 return -999;
     }
     
     public int getColor(int index){
    	 int red = (colorMap8[0][index] & 0x000000FF);
 		int green = (colorMap8[1][index] & 0x000000FF);
 		int blue = (colorMap8[2][index] & 0x000000FF);
 		Color color = new Color(red, green, blue);

 		return color.getRGB();
     }
}
