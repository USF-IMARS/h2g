/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/
package gov.nasa.gsfc.drl.h2g.vector;


/*
 ClassName        : Point
 Class Description: Represents a 2-Dimensional point with x and y co-ordinates
 */

public class Point {
	// x and y co-ordinates
	public double x, y;

	// Empty constructor
	public Point() {
	}

	// Constructor to initialise x and y coordinates
	public Point(double pointX, double pointY) {
		x = pointX;
		y = pointY;
	}

	// method to initialise/change coordinates
	public void setCoordinates(double pointX, double pointY) {
		x = pointX;
		y = pointY;
	}

	// method to print out the Point object
	@Override
	public String toString() {
		return ("(" + x + "," + y + ")");
	}

	// method returns the euclidean distance between this Point object and the
	// input parameter Point object
	public double distance(Point inPoint) {
		// calculate the distance and return it;
		return Math.sqrt((inPoint.x - x) * (inPoint.x - x) + (inPoint.y - y)
				* (inPoint.y - y));
	}
}
