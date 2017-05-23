/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/
package gov.nasa.gsfc.drl.h2g.util;

public class DataSetKey {
	String keyName;
	int PLengthSDS;
	String[] PathSpecifier;
	int NIndex;
	int[] IndexSelections;
	int DataType;
	double Multiplier;
	int FillValue_min;
	int FillValue_max;
	int Resolution;
	Boolean ishdf5;
	int trimType;

	public void displayKey() {
		System.out.println(keyName + " " + PLengthSDS);
		for (int i = 0; i < PLengthSDS; i++)
			System.out.print("   " + PathSpecifier[i] + " ");
		System.out.println();
		System.out.println(NIndex);
		for (int i = 0; i < NIndex; i++)
			System.out.print("   " + IndexSelections[i] + " ");
		System.out.println();
		System.out.println(DataType + " " + Multiplier + " " + FillValue_min + " " + FillValue_max + " "
				+ Resolution);

	}

}
