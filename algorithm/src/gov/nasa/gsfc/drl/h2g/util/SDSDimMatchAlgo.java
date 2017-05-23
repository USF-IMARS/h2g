/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/
package gov.nasa.gsfc.drl.h2g.util;

public class SDSDimMatchAlgo {
	int inputRes, outputRes;

	public SDSDimMatchAlgo() {
		inputRes = -999;
		outputRes = -999;
	}

	public SDSDimMatchAlgo(int inres, int outres) {
		inputRes = inres;
		outputRes = outres;
	}

	public void displayKey() {
		System.out.print(inputRes + " " + outputRes + " ");

	}

	public Boolean equals(SDSDimMatchAlgo x) {
		if (inputRes == x.inputRes && outputRes == x.outputRes)
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}

}
