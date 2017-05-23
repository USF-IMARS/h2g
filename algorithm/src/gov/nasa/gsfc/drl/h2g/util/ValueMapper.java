/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/


package gov.nasa.gsfc.drl.h2g.util;

public interface ValueMapper {
	public int getData();

	public int getData(int[] value);

	public int getRGB();

	public int getRGB(int[] value);

	public byte[][] getColorMap8();

	public byte[][] getColorMap16();
	
	public int getColorIndex(int[] rgb);
	public int getColor(int index);

}
