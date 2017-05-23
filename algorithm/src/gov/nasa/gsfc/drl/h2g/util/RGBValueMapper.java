/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/

package gov.nasa.gsfc.drl.h2g.util;

import java.awt.Color;

public class RGBValueMapper implements ValueMapper {
	private int[] redScale, greenScale, blueScale;
	private int redFillValue_min, redFillValue_max, greenFillValue_min, greenFillValue_max,blueFillValue_min, blueFillValue_max;
	/*private static final int fill = -999;
	private static final int fill2 = 32767;
	private static final int fill3 = 65535;
	private static final int fill4 = -99990;*/
	private static final Color NODATA_COLOR = new Color(10,10,10);
	private static final int NODATA_RGB = NODATA_COLOR.getRGB();
	

	/****************************************************************************
	 * TrueColorScaler.
	 ****************************************************************************/
	public RGBValueMapper(int[][] reds, int[][] greens, int[][] blues,Configuration config) {
		this.redScale=config.getredScale();
		this.greenScale =config.getgreenScale();
		this.blueScale =config.getblueScale();
		this.redFillValue_min=config.getREDFillvalue_min();
		this.redFillValue_max=config.getREDFillvalue_max();
		this.greenFillValue_min=config.getGREENFillvalue_min();
		this.greenFillValue_max=config.getGREENFillvalue_max();
		this.blueFillValue_min=config.getBLUEFillvalue_min();
		this.blueFillValue_max=config.getBLUEFillvalue_max();
		
		/*this.redScale = redScale;
		this.blueScale = blueScale;
		this.greenScale = greenScale;*/
		// this.validMin = validMin;
		// this.validMax = validMax;
		/*
		 * int[] maxs = new int[] {validMax,validMax,validMax}; int[] mins = new
		 * int[] {validMin,validMin,validMin}; int[][][] values = new int[][][]
		 * {reds,greens,blues}; for (int color = 0; color < values.length;
		 * color++) { for (int row = 0; row < values[color].length; row++) { for
		 * (int col = 0; col < values[color][row].length; col++) { if
		 * (Util.inRange(validMin,values[color][row][col],validMax+5000)) { //
		 * values
		 * [color][row][col]=Util.toRange(validMin,values[color][row][col],
		 * validMax); //mins[color] =
		 * Util.min(mins[color],values[color][row][col]); //maxs[color] =
		 * Util.max(maxs[color],values[color][row][col]); }else
		 * values[color][row][col]=fill;
		 * 
		 * } } scales[color] = 255.0 / maxs[color];
		 * //System.out.println("scales["
		 * +color+"]="+scales[color]+" "+maxs[color]); }
		 */
	}

	/****************************************************************************
	 * getRGB.
	 ****************************************************************************/
	public int getRGB() {
		Color color = new Color(0, 0, 0, 0);
		return color.getRGB();
	}

	public int getRGB(int[] values) {
		
		if ((values[0] >= redFillValue_min && values[0]<=redFillValue_max) || (values[1] >= greenFillValue_min && values[1]<=greenFillValue_max) || (values[2] >= blueFillValue_min && values[2]<=blueFillValue_max)) {
			return NODATA_RGB;
		} else {
				return getEnhancedColor(
						new int[] { (values[0]), (values[1]), (values[2]) })
						.getRGB();
		}
		/*if (values[0] != fill && values[1] != fill && values[2] != fill && values[0] != fill2 && values[1] != fill2 && values[2] != fill2 && values[0] != fill3 && values[1] != fill3 && values[2] != fill3 && values[0] != fill4 && values[1] != fill4 && values[2] != fill4) {
			return getEnhancedColor(
					new int[] { (values[0]), (values[1]), (values[2]) })
					.getRGB();
		} else {
			//return Color.BLACK.getRGB();
			return NODATA_RGB;
		}*/
	}

	private Color getEnhancedColor(int[] levels) {
		int minactual, maxactual, MIN_INDEX, MAX_INDEX, INDEX_RANGE;
		double value, slope, intercept, range;
		Boolean notscaled;
		for (int c = 0; c < levels.length; c++) {
			value = levels[c];
			notscaled = Boolean.TRUE;
			// System.out.println("Value="+value);
			int n = 0;// =redScale.length;
			int[] scale = null;
			switch (c) {
			case 0:
				n = redScale.length;
				scale = redScale;
				break;
			case 1:
				n = greenScale.length;
				scale = greenScale;
				break;
			case 2:
				n = blueScale.length;
				scale = blueScale;
				break;

			}
			int totalmin = scale[0];
			int totalmax = scale[n - 2];
			for (int seg = 0; seg < n - 2; seg = seg + 2) {
				minactual = scale[seg];
				maxactual = scale[seg + 2];
				range = maxactual - minactual;

				MIN_INDEX = scale[seg + 1];
				MAX_INDEX = scale[seg + 3];
				INDEX_RANGE = MAX_INDEX - MIN_INDEX;

				intercept = MIN_INDEX - (minactual * INDEX_RANGE / range);
				slope = INDEX_RANGE / range;

				if (value >= minactual && value < maxactual) {
					// System.out.println("Lies between "+minactual+" "+maxactual);
					levels[c] = (int) Math.round(intercept + slope * value);
					levels[c] = Util.toRange(0, levels[c], 255);
					// System.out.println("Scale Value "+levels[c]);
					notscaled = Boolean.FALSE;
					break;
				}
			}
			if (notscaled) {
				if (levels[c] < totalmin)
					levels[c] = 0;
				else if (levels[c] > totalmax)
					levels[c] = 255;
				else
					levels[c] = 0;
			}
		}

		return new Color(levels[0], levels[1], levels[2]);
		// return new Color(0,0,0);
	}

	/****************************************************************************
	 * getData.
	 ****************************************************************************/
	public int getData() {
		return 0;
	}

	public int getData(int[] values) {
		return 0;
	}

	/****************************************************************************
	 * getColorMap8.
	 ****************************************************************************/
	public byte[][] getColorMap8() {
		return null;
	}

	/****************************************************************************
	 * getColorMap16.
	 ****************************************************************************/
	public byte[][] getColorMap16() {
		return null;
	}
	
	 public int getColorIndex(int[] rgb){
	  return -999;
	 }
	 public int getColor(int index){
    	 return -999;
     }
}
