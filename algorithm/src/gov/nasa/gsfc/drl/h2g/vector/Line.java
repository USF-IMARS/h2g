/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/
package gov.nasa.gsfc.drl.h2g.vector;


/*
 ClassName        : Line
 Class Description: Represents a Simple Straight Line segment with a start point and an end point
 */

public class Line {

	public Point s, e; // s and e denotes the end points of the line segment the
						// object represents

	// Default Constructor
	public Line() {
	}

	// Constructor which takes as input - the two end points of a line segment
	// and initialises the corresponding variables
	public Line(Point p1, Point p2) {
		s = p1;
		e = p2;
	}

	// Function to test intersection of this line segment (1) with another
	// (input)line segment (2)
	// It returns the point of intersection of the two line segments; In case
	// they do not intersect it returns null
	public Point intersect(Line line2) {

		// b1 and b2 represents the slope of line segment (1) and line segment
		// (2) respectively
		// a1 and a2 represents the y-intercept of line segment(1) and line
		// segment (2) respectively
		// xp and yp represents the x and y coordinates of the point of
		// intersection of the two line segments
		// intersection is the Point object that represents the point of
		// intersection of the two line segments
		double b2, a2, b1, a1, xp, yp;
		double xmax1 = Math.max(s.x, e.x);
		double xmin1 = Math.min(s.x, e.x);
		double xmax2 = Math.max(line2.s.x, line2.e.x);
		double xmin2 = Math.min(line2.s.x, line2.e.x);
		double ymax1 = Math.max(s.y, e.y);
		double ymin1 = Math.min(s.y, e.y);
		double ymax2 = Math.max(line2.s.y, line2.e.y);
		double ymin2 = Math.min(line2.s.y, line2.e.y);

		if (!(xmin1 > xmax2 || xmax1 < xmin2 || ymin1 > ymax2 || ymax1 < ymin2)) {

			if (s.x == e.x) { // is line segment 1 vertical?
				if (line2.s.x == line2.e.x) { // is line segment 2 vertical
					// However they may overlap
					// System.out.println("both lines are vertical");
					return null;
				} else { // only line segment 1 is vertical
					// System.out.println("first line is vertical");
					// Computing the x, y coordinates of the point of
					// intersection
					b2 = (line2.e.y - line2.e.y) / (line2.s.x - line2.s.x);
					a2 = line2.s.y - b2 * line2.s.x;
					xp = s.x;
					yp = a2 + b2 * xp;
				}
			} else {
				if (line2.s.x == line2.e.x) { // is line segment 2 vertical
					// only line segment 2 is vertical
					// System.out.println("second line is vertical");
					// Computing the x,y coordinates of the point of
					// intersection
					b1 = (e.y - s.y) / (e.x - s.x);
					a1 = s.y - b1 * s.x;
					xp = line2.s.x;
					yp = a1 + b1 * xp;
				} else { // Neither line is vertical
					// System.out.println("Neither line is vertical");
					// Computing the slopes and y intercepts of both the lines
					b1 = (e.y - s.y) / (e.x - s.x);
					b2 = (line2.e.y - line2.s.y) / (line2.e.x - line2.s.x);
					a1 = s.y - b1 * s.x;
					a2 = line2.s.y - b2 * line2.s.x;
					if (b1 == b2) { // are Lines parallel?
						// parallel lines do not intersect
						// However if they are the same line they may overlap
						return null;
					} else { // lines are not parallel; they intersect
						// Computing the x,y coordinates of the point of
						// intersection
						xp = -(a1 - a2) / (b1 - b2);
						yp = a1 + b1 * xp;
					}
				}
			}
			// Test whether intersection point falls on both lines
			if ((s.x - xp) * (xp - e.x) >= 0
					&& (line2.s.x - xp) * (xp - line2.e.x) >= 0
					&& (s.y - yp) * (yp - e.y) >= 0
					&& (line2.s.y - yp) * (yp - line2.e.y) >= 0) {
				// System.out.println("intersection point lies on both line segments");
				// returning the point of intersection
				return (new Point(xp, yp));
			}
		}

		return null;
	}

	// method to printout the Line Object
	@Override
	public String toString() {
		return ("(" + s + "," + e + ")");
	}

}
