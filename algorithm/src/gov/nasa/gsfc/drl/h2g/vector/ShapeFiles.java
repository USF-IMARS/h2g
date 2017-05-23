/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/
package gov.nasa.gsfc.drl.h2g.vector;

import java.io.RandomAccessFile;
import java.util.Vector;

/*
 ClassName        : ShapeFiles
 Class Description: Class used for reading of shapefiles; Currently handles Point,PolyLine and Polygon features only
 */

public class ShapeFiles {

	// default constructor
	public ShapeFiles() {
	}

	// method to read in shapefiles; The input parameter is the file path;
	// Method currently can read shapefiles
	// of feature types- Point, Polyline or Polygon. It returns a Vector of the
	// read in feature(Point or Polyline)
	public Vector readShapeFiles(String filePath, Boolean CrossOver,
			double distFrom180) {
		// Next Line Changed by S. Dasgupta 11/23/07
		// "Vector objects=new Vector()" Type checking for Java 1.5
		Vector<PolyLine> objects = new Vector<PolyLine>();
		int n;
		int shapeFileType;
		byte shapeFileByte[];

		try {
			// Open main file

			RandomAccessFile shapeFile = new RandomAccessFile(filePath, "r");

			// get the length of the main file;
			n = (int) shapeFile.length();
			// System.out.println("file size="+n);

			// generate a cache with the length of the main file;
			shapeFileByte = new byte[n];

			// read the main file to the buffer;
			shapeFile.read(shapeFileByte, 0, n);

			// close the shape file;
			shapeFile.close();

			// determine what shape type is represented by the shape file
			shapeFileType = this.readLEInt(shapeFileByte, 32);
			// System.out.println("ShapeFileType="+shapeFileType);

			// different reading procedure for different shapeFileType
			switch (shapeFileType) {
			/*
			 * case 1: for (int i = 0; i 28 + 100 < n; i++) { double pointX =
			 * this.readLEDouble(shapeFileByte, 100 + 12 + i 28); double pointY
			 * = this.readLEDouble(shapeFileByte, 100 + i 28 + 20); Point point
			 * = new Point(pointX, pointY); objects.add(point); } break;
			 */
			default:// Polyline or Polygon
				// System.out.println("in case 3");
				int fileheaderlength = 100;
				int recordheaderLength = 44; // recordheader+shapeType+boundingBox
				int bytepointer = fileheaderlength;
				while (bytepointer < n) {

					bytepointer = bytepointer + recordheaderLength;
					double[] boundingbox = new double[4];
					// reading in the bounding box
					boundingbox[0] = ShapeFiles.readLEDouble(shapeFileByte,
							bytepointer - 32);
					boundingbox[1] = ShapeFiles.readLEDouble(shapeFileByte,
							bytepointer - 24);
					boundingbox[2] = ShapeFiles.readLEDouble(shapeFileByte,
							bytepointer - 16);
					boundingbox[3] = ShapeFiles.readLEDouble(shapeFileByte,
							bytepointer - 8);

					// reading in number of Parts
					int numParts = this.readLEInt(shapeFileByte, bytepointer);

					// System.out.println("numParts="+numParts+"bytepointer="+bytepointer);
					int numPoints = this.readLEInt(shapeFileByte,
							bytepointer + 4);
					// System.out.println("numPoints="+numPoints+"bytepointer="+bytepointer);
					int[] Parts = new int[numParts];
					Point[] points = new Point[numPoints];
					bytepointer = bytepointer + 8;
					// System.out.println("here 3");
					for (int i = 0; i < numParts; i++) {

						Parts[i] = this.readLEInt(shapeFileByte, bytepointer);
						bytepointer = bytepointer + 4;
					}
					double refLon = 180.0 - distFrom180;
					for (int i = 0; i < numPoints; i++) {

						double pointX = ShapeFiles.readLEDouble(shapeFileByte,
								bytepointer);
						double pointY = ShapeFiles.readLEDouble(shapeFileByte,
								bytepointer + 8);
						if (CrossOver) {
							if (pointX < 0.0)
								pointX = pointX + distFrom180;
							else
								pointX = -180.0 + (pointX - refLon);
						}
						// System.out.println("reading point "+i+" "+pointX+" "+pointY+" starting at "+bytepointer);
						points[i] = new Point(pointX, pointY);
						bytepointer = bytepointer + 16;
					}

					PolyLine polyline = new PolyLine(numParts, numPoints,
							Parts, points, boundingbox);
					objects.add(polyline);
				}

			}
		} catch (Exception e) {
			System.out.println("********************" + e.toString());
		}

		return objects;
	}

	// read in integer value in LittleEndian form from buffer starting from
	// offset off
	private int readLEInt(byte[] Buffer, int off) {
		return (((Buffer[off + 3] & 0xff) << 24)
				| ((Buffer[off + 2] & 0xff) << 16)
				| ((Buffer[off + 1] & 0xff) << 8) | ((Buffer[off + 0] & 0xff)));
	}

	// read in long value in LittleEndian form from buffer starting from offset
	// off
	private static long readLELong(byte[] b, int off) {
		return (((b[off + 0] & 0xffL)) | ((b[off + 1] & 0xffL) << 8)
				| ((b[off + 2] & 0xffL) << 16) | ((b[off + 3] & 0xffL) << 24)
				| ((b[off + 4] & 0xffL) << 32) | ((b[off + 5] & 0xffL) << 40)
				| ((b[off + 6] & 0xffL) << 48) | ((b[off + 7] & 0xffL) << 56));
	}

	// read in double value in LittleEndian form from buffer starting from
	// offset off
	private static double readLEDouble(byte[] b, int off) {
		double result = Double.longBitsToDouble(readLELong(b, off));
		return result;
	}
}
