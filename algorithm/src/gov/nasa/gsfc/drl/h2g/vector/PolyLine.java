/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/
package gov.nasa.gsfc.drl.h2g.vector;

/*
 ClassName        : PolyLine
 Class Description: Represents a ShapeFile Polyline datastructure
 */

public class PolyLine {

	public int NumParts; // Number of Parts
	public int NumPoints; // Total Number of Points
	public int[] Parts; // Index to First Point in Part
	public Point[] Points; // Points for All Parts
	double[] boundingBox; // Bounding Box

	// Constructor to initialise the class instance variables
	PolyLine(int NumParts, int NumPoints, int Parts[], Point[] Points,
			double[] boundingBox) {
		this.NumParts = NumParts;
		this.NumPoints = NumPoints;
		this.Parts = Parts;
		this.Points = Points;
		this.boundingBox = boundingBox;

	}

}
