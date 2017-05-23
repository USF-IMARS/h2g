/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/


package gov.nasa.gsfc.drl.h2g.util;

import java.io.File;

public class GeoReader {
	private double minLat, maxLat;
	private double minLon, maxLon, maxLonNeg, minLonPos;
	private double[][] lats, lons;

	/****************************************************************************
	 * GeoReader.
	 ****************************************************************************/
	public GeoReader(double[][] lats, double[][] lons) throws Exception {
		this.lats = lats;
		this.lons = lons;
		calcRanges();
	}

	public GeoReader(File file, int stride) throws Exception {
		SDSReader latReader = new SDSReader(file, new String[] {
				"MODIS_Swath_Type_GEO", "Geolocation Fields", "Latitude" },
				new int[] { 0, 1 });
		SDSReader lonReader = new SDSReader(file, new String[] {
				"MODIS_Swath_Type_GEO", "Geolocation Fields", "Longitude" },
				new int[] { 0, 1 });
		int xCount = latReader.getWidth() / stride;
		int yCount = latReader.getHeight() / stride;
		lats = latReader.getDoubles(0, 0, yCount, xCount, 1, 1);
		lons = lonReader.getDoubles(0, 0, yCount, xCount, 1, 1);
		latReader.close();
		lonReader.close();
		// printfile("latitude1km.txt",lats);
		// printfile("longitude1km.txt",lons);
		calcRanges();
	}

	private void calcRanges() {
		minLat = 999.0;
		maxLat = -999.0;
		minLon = 999.0;
		maxLon = -999.0;
		minLonPos = 999.0;
		maxLonNeg = -999.0;
		for (int y = 0; y < lats.length; y++) {
			for (int x = 0; x < lats[y].length; x++) {
				double lat = lats[y][x];
				if (Util.inRange(-90.0, lat, 90.0)) {
					minLat = Util.min(minLat, lat);
					maxLat = Util.max(maxLat, lat);
				}
				double lon = lons[y][x];
				if (Util.inRange(-180.0, lon, 180.0)) {
					minLon = Util.min(minLon, lon);
					maxLon = Util.max(maxLon, lon);
				}
				if (Util.inRange(-180.0, lon, 180.0) && lons[y][x] < 0) { // Negative
					maxLonNeg = Util.max(maxLonNeg, lon);
					if (maxLonNeg == lon) {
					}
				}
				if (Util.inRange(-180.0, lon, 180.0) && lons[y][x] >= 0) { // Positive
					minLonPos = Util.min(minLonPos, lon);
					if (minLonPos == lon) {
					}
				}
			}
		}
		// if(minLonPos<112 || maxLonNeg>-121)
		// System.out.println("maxLonNeg="+maxLonNeg+" maxLonNeg(x,y)="+maxLonNegX+","+maxLonNegY+"minLonPos="+minLonPos+" minLonPos(x,y)="+minLonPosX+","+minLonPosY);
		// System.out.println(minLat+","+maxLat+":"+minLon+","+maxLon);
	}

	/****************************************************************************
	 * getMinLat.
	 ****************************************************************************/
	public double getMinLat() {
		return minLat;
	}

	/****************************************************************************
	 * getMaxLat.
	 ****************************************************************************/
	public double getMaxLat() {
		return maxLat;
	}

	/****************************************************************************
	 * getMinLon.
	 ****************************************************************************/
	public double getMinLon() {
		return minLon;
	}

	/****************************************************************************
	 * getMaxLon.
	 ****************************************************************************/
	public double getMaxLon() {
		return maxLon;
	}

	/****************************************************************************
	 * getMinLonPos.
	 ****************************************************************************/
	public double getMinLonPos() {
		return minLonPos;
	}

	/****************************************************************************
	 * getMaxLonNeg.
	 ****************************************************************************/
	public double getMaxLonNeg() {
		return maxLonNeg;
	}

	/****************************************************************************
	 * getLats.
	 ****************************************************************************/
	public double[][] getLats() {
		return lats;
	}

	/****************************************************************************
	 * getLons.
	 ****************************************************************************/
	public double[][] getLons() {
		return lons;
	}

	public void doubleGeoResolutionLowMemory(int scanht) {
		double[][] latsHighRes, lonsHighRes;
		int scanrowstart, scanrowstartHighRes, scanrowendHighRes, iHighRes, iHighResNext, jHighRes;
		int rowsLowRes = lats.length;
		int colsLowRes = lats[0].length;
		int rowsHighRes = lats.length * 2;
		int colsHighRes = lats[0].length * 2;
		System.out.println("In double GeoResolution");
		latsHighRes = new double[rowsHighRes][colsHighRes];
		System.out.println("After initializing 1st array");
		lonsHighRes = new double[rowsHighRes][colsHighRes];
		double minLat = -90.0;
		double minLon = -180.0;
		double maxLat = 90.0;
		double maxLon = 180.0;
		

		System.out.println("After initializing array");
		int nscans = rowsLowRes / scanht;
		for (int scanindex = 0; scanindex < nscans; scanindex++) {
			scanrowstart = scanindex * scanht;
			for (int iLowRes = scanrowstart; iLowRes < scanrowstart + scanht
					- 1; iLowRes++) {
				for (int jLowRes = 0; jLowRes < colsLowRes; jLowRes++) {
					iHighRes = iLowRes * 2 + 1;
					iHighResNext = iHighRes + 1;
					jHighRes = jLowRes * 2 + 1;
					if (Util.inRange(minLat, lats[iLowRes][jLowRes], maxLat)
							&& Util.inRange(minLon, lons[iLowRes][jLowRes],maxLon)
							&& Util.inRange(minLat, lats[iLowRes + 1][jLowRes],	maxLat)
							&& Util.inRange(minLon, lons[iLowRes + 1][jLowRes],maxLon)){

						latsHighRes[iHighRes][jHighRes] = (2 * lats[iLowRes][jLowRes] + lats[iLowRes + 1][jLowRes]) / 3;
						lonsHighRes[iHighRes][jHighRes] = (2 * lons[iLowRes][jLowRes] + lons[iLowRes + 1][jLowRes]) / 3;
						latsHighRes[iHighResNext][jHighRes] = (lats[iLowRes][jLowRes] + 2 * lats[iLowRes + 1][jLowRes]) / 3;
						lonsHighRes[iHighResNext][jHighRes] = (lons[iLowRes][jLowRes] + 2 * lons[iLowRes + 1][jLowRes]) / 3;

					} else {
						latsHighRes[iHighRes][jHighRes] = -999;
						lonsHighRes[iHighRes][jHighRes] = -999;
						latsHighRes[iHighResNext][jHighRes] = -999;
						lonsHighRes[iHighResNext][jHighRes] = -999;
					}
				}
			}
			// Now fill up top edge of the scan
			scanrowstartHighRes = scanrowstart * 2;
			for (jHighRes = 1; jHighRes < colsHighRes; jHighRes = jHighRes + 2) {
				if (Util.inRange(minLat,
						latsHighRes[scanrowstartHighRes + 1][jHighRes], maxLat)
						&& Util.inRange(minLon,
								lonsHighRes[scanrowstartHighRes + 1][jHighRes],
								maxLon)
						&& Util.inRange(minLat,
								latsHighRes[scanrowstartHighRes + 2][jHighRes],
								maxLat)
						&& Util.inRange(minLon,
								lonsHighRes[scanrowstartHighRes + 2][jHighRes],
								maxLon)) {

					latsHighRes[scanrowstartHighRes][jHighRes] = 2
							* latsHighRes[scanrowstartHighRes + 1][jHighRes]
							- latsHighRes[scanrowstartHighRes + 2][jHighRes];
					lonsHighRes[scanrowstartHighRes][jHighRes] = 2
							* lonsHighRes[scanrowstartHighRes + 1][jHighRes]
							- lonsHighRes[scanrowstartHighRes + 2][jHighRes];
				} else {
					latsHighRes[scanrowstartHighRes][jHighRes] = -999;
					lonsHighRes[scanrowstartHighRes][jHighRes] = -999;
				}
			}
			// Now fill up bottom edge of the scan
			scanrowendHighRes = (scanrowstart + scanht - 1) * 2 + 1;
			for (jHighRes = 1; jHighRes < colsHighRes; jHighRes = jHighRes + 2) {
				if (Util.inRange(minLat,
						latsHighRes[scanrowendHighRes - 1][jHighRes], maxLat)
						&& Util.inRange(minLon,
								lonsHighRes[scanrowendHighRes - 1][jHighRes],
								maxLon)
						&& Util.inRange(minLat,
								latsHighRes[scanrowendHighRes - 2][jHighRes],
								maxLat)
						&& Util.inRange(minLon,
								lonsHighRes[scanrowendHighRes - 2][jHighRes],
								maxLon)) {

					latsHighRes[scanrowendHighRes][jHighRes] = 2
							* latsHighRes[scanrowendHighRes - 1][jHighRes]
							- latsHighRes[scanrowendHighRes - 2][jHighRes];
					lonsHighRes[scanrowendHighRes][jHighRes] = 2
							* lonsHighRes[scanrowendHighRes - 1][jHighRes]
							- lonsHighRes[scanrowendHighRes - 2][jHighRes];
				} else {
					latsHighRes[scanrowendHighRes][jHighRes] = -999;
					lonsHighRes[scanrowendHighRes][jHighRes] = -999;
				}

			}
			// Now fill up all except left edge of scan
			for (iHighRes = scanrowstartHighRes; iHighRes <= scanrowendHighRes; iHighRes++) {
				for (jHighRes = 2; jHighRes < colsHighRes; jHighRes = jHighRes + 2) {
					if (Util.inRange(minLat,latsHighRes[iHighRes][jHighRes - 1], maxLat)
							&& Util.inRange(minLon,	lonsHighRes[iHighRes][jHighRes - 1],maxLon)
							&& Util.inRange(minLat,	latsHighRes[iHighRes][jHighRes + 1],maxLat)
							&& Util.inRange(minLon,lonsHighRes[iHighRes][jHighRes + 1],	maxLon)){
						latsHighRes[iHighRes][jHighRes] = (latsHighRes[iHighRes][jHighRes - 1] + latsHighRes[iHighRes][jHighRes + 1]) / 2;
						lonsHighRes[iHighRes][jHighRes] = (lonsHighRes[iHighRes][jHighRes - 1] + lonsHighRes[iHighRes][jHighRes + 1]) / 2;
						// if(lonsHighRes[iHighRes][jHighRes]>-1 &&
						// lonsHighRes[iHighRes][jHighRes]<1)
						// System.out.println(lonsHighRes[iHighRes][jHighRes-1]+","+lonsHighRes[iHighRes][jHighRes+1]);
					} else {
						latsHighRes[iHighRes][jHighRes] = -999;
						lonsHighRes[iHighRes][jHighRes] = -999;
					}
				}
			}

			// Now Fill up Left Edge of scan
			for (iHighRes = scanrowstartHighRes; iHighRes <= scanrowendHighRes; iHighRes++) {
				if (Util.inRange(minLat, latsHighRes[iHighRes][1], maxLat)
						&& Util.inRange(minLon,
								lonsHighRes[iHighRes][jHighRes - 1], maxLon)
						&& Util.inRange(minLat, lonsHighRes[iHighRes][1],
								maxLat)
						&& Util.inRange(minLon, lonsHighRes[iHighRes][2],
								maxLon)) {
					latsHighRes[iHighRes][0] = 2 * latsHighRes[iHighRes][1]
							- latsHighRes[iHighRes][2];
					lonsHighRes[iHighRes][0] = 2 * lonsHighRes[iHighRes][1]
							- lonsHighRes[iHighRes][2];
				} else {
					latsHighRes[iHighRes][0] = -999;
					lonsHighRes[iHighRes][0] = -999;
				}

			}

		}
		// printfile("latitude1km.txt",lats);
		// printfile("longitude1km.txt",lons);
		// printfile("latitudeHkm.txt",latsHighRes);
		// printfile("longitudeHkm.txt",lonsHighRes);
		lats = latsHighRes;
		lons = lonsHighRes;

		System.out.println("End of double GeoResolution");
		calcRanges();
	}

	public void doubleGeoResolution(int scanht) {
		double[][] latsHighRes, lonsHighRes;
		int scanrowstart, scanrowstartHighRes, scanrowendHighRes, iHighRes, iHighResNext, jHighRes;
		int rowsLowRes = lats.length;
		int colsLowRes = lats[0].length;

		int rowsHighRes = lats.length * 2;
		int colsHighRes = lats[0].length * 2;
		// System.out.println("In double GeoResolution");
		latsHighRes = new double[rowsHighRes][colsHighRes];
		// System.out.println("After initializing 1st array");
		lonsHighRes = new double[rowsHighRes][colsHighRes];
		double minLat = -90.0;
		double minLon = -180.0;
		double maxLat = 90.0;
		double maxLon = 180.0;
		for (int i = 0; i < rowsHighRes; i++)
			for (int j = 0; j < colsHighRes; j++) {
				latsHighRes[i][j] = -999;
				lonsHighRes[i][j] = -999;
			}
		// System.out.println("HighRes:"+rowsHighRes+","+colsHighRes);
		// System.out.println("After initializing array");
		int nscans = rowsLowRes / scanht;
		// System.out.println("nscans="+nscans);
		// if(rowsHighRes>=400 && colsHighRes>=1500)
		// System.out.println(">"+latsHighRes[399][1081]+","+lonsHighRes[399][1081]);
		for (int scanindex = 0; scanindex < nscans; scanindex++) {
			scanrowstart = scanindex * scanht;
			for (int iLowRes = scanrowstart; iLowRes < scanrowstart + scanht
					- 1; iLowRes++) {
				for (int jLowRes = 0; jLowRes < colsLowRes; jLowRes++) {
					// if(lats[iLowRes][jLowRes]==0.0)
					// System.out.println("lat is "+lats[iLowRes][jLowRes]+" at "+iLowRes+","+jLowRes);
					iHighRes = iLowRes * 2 + 1;
					iHighResNext = iHighRes + 1;
					jHighRes = jLowRes * 2 + 1;
					if (Util.inRange(minLat, lats[iLowRes][jLowRes], maxLat)
							&& Util.inRange(minLon, lons[iLowRes][jLowRes],
									maxLon)
							&& Util.inRange(minLat, lats[iLowRes + 1][jLowRes],
									maxLat)
							&& Util.inRange(minLon, lons[iLowRes + 1][jLowRes],
									maxLon)
							&& lons[iLowRes][jLowRes]
									* lons[iLowRes + 1][jLowRes] >= -100) {

						latsHighRes[iHighRes][jHighRes] = (2 * lats[iLowRes][jLowRes] + lats[iLowRes + 1][jLowRes]) / 3;
						lonsHighRes[iHighRes][jHighRes] = (2 * lons[iLowRes][jLowRes] + lons[iLowRes + 1][jLowRes]) / 3;
						latsHighRes[iHighResNext][jHighRes] = (lats[iLowRes][jLowRes] + 2 * lats[iLowRes + 1][jLowRes]) / 3;
						lonsHighRes[iHighResNext][jHighRes] = (lons[iLowRes][jLowRes] + 2 * lons[iLowRes + 1][jLowRes]) / 3;
						// if(latsHighRes[iHighRes][jHighRes]>-0.25 &&
						// latsHighRes[iHighRes][jHighRes]<0.25)
						// System.out.println("1ST "+latsHighRes[iHighRes][jHighRes]+":"+lats[iLowRes][jLowRes]+","+lats[iLowRes+1][jLowRes]);
						// if(latsHighRes[iHighResNext][jHighRes]>-0.25 &&
						// latsHighRes[iHighResNext][jHighRes]<0.25)
						// System.out.println("1ST "+latsHighRes[iHighResNext][jHighRes]+":"+lats[iLowRes][jLowRes]+","+lats[iLowRes+1][jLowRes]);
					} else {
						latsHighRes[iHighRes][jHighRes] = -999;
						lonsHighRes[iHighRes][jHighRes] = -999;
						latsHighRes[iHighResNext][jHighRes] = -999;
						lonsHighRes[iHighResNext][jHighRes] = -999;
					}
				}
			}
			// Now fill up top edge of the scan
			// System.out.println(">"+lonsHighRes[15][471]+","+lonsHighRes[4][467]);
			// if(rowsHighRes>=400 && colsHighRes>=1500)
			// System.out.println(">"+latsHighRes[399][1081]+","+lonsHighRes[399][1081]);
			scanrowstartHighRes = scanrowstart * 2;
			for (jHighRes = 1; jHighRes < colsHighRes; jHighRes = jHighRes + 2) {

				if (Util.inRange(minLat,
						latsHighRes[scanrowstartHighRes + 1][jHighRes], maxLat)
						&& Util.inRange(minLon,
								lonsHighRes[scanrowstartHighRes + 1][jHighRes],
								maxLon)
						&& Util.inRange(minLat,
								latsHighRes[scanrowstartHighRes + 2][jHighRes],
								maxLat)
						&& Util.inRange(minLon,
								lonsHighRes[scanrowstartHighRes + 2][jHighRes],
								maxLon)
						&& lonsHighRes[scanrowstartHighRes + 1][jHighRes]
								* lonsHighRes[scanrowstartHighRes + 2][jHighRes] >= -100) {

					latsHighRes[scanrowstartHighRes][jHighRes] = 2
							* latsHighRes[scanrowstartHighRes + 1][jHighRes]
							- latsHighRes[scanrowstartHighRes + 2][jHighRes];
					lonsHighRes[scanrowstartHighRes][jHighRes] = 2
							* lonsHighRes[scanrowstartHighRes + 1][jHighRes]
							- lonsHighRes[scanrowstartHighRes + 2][jHighRes];
					// if(latsHighRes[scanrowstartHighRes][jHighRes]>-0.25 &&
					// latsHighRes[scanrowstartHighRes][jHighRes]<0.25)
					// System.out.println("TOP:"+latsHighRes[scanrowstartHighRes][jHighRes]+":"+latsHighRes[scanrowstartHighRes+1][jHighRes]+","+latsHighRes[scanrowstartHighRes+2][jHighRes]);
				} else {
					latsHighRes[scanrowstartHighRes][jHighRes] = -999;
					lonsHighRes[scanrowstartHighRes][jHighRes] = -999;
				}
			}
			// System.out.println(">"+lonsHighRes[15][471]+","+lonsHighRes[4][467]);
			// if(rowsHighRes>=400 && colsHighRes>=1500)
			// System.out.println(">"+latsHighRes[399][1081]+","+lonsHighRes[399][1081]);
			// Now fill up bottom edge of the scan
			scanrowendHighRes = (scanrowstart + scanht - 1) * 2 + 1;
			for (jHighRes = 1; jHighRes < colsHighRes; jHighRes = jHighRes + 2) {

				if (Util.inRange(minLat,
						latsHighRes[scanrowendHighRes - 1][jHighRes], maxLat)
						&& Util.inRange(minLon,
								lonsHighRes[scanrowendHighRes - 1][jHighRes],
								maxLon)
						&& Util.inRange(minLat,
								latsHighRes[scanrowendHighRes - 2][jHighRes],
								maxLat)
						&& Util.inRange(minLon,
								lonsHighRes[scanrowendHighRes - 2][jHighRes],
								maxLon)
						&& lonsHighRes[scanrowendHighRes - 1][jHighRes]
								* lonsHighRes[scanrowendHighRes - 2][jHighRes] >= -100) {

					latsHighRes[scanrowendHighRes][jHighRes] = 2
							* latsHighRes[scanrowendHighRes - 1][jHighRes]
							- latsHighRes[scanrowendHighRes - 2][jHighRes];
					lonsHighRes[scanrowendHighRes][jHighRes] = 2
							* lonsHighRes[scanrowendHighRes - 1][jHighRes]
							- lonsHighRes[scanrowendHighRes - 2][jHighRes];
					// if(latsHighRes[scanrowendHighRes][jHighRes]>-0.25 &&
					// latsHighRes[scanrowendHighRes][jHighRes]<0.25)
					// System.out.println("BOTTOM:"+latsHighRes[scanrowendHighRes][jHighRes]+":"+latsHighRes[scanrowendHighRes-1][jHighRes]+","+latsHighRes[scanrowendHighRes-2][jHighRes]);
				} else {
					latsHighRes[scanrowendHighRes][jHighRes] = -999;
					lonsHighRes[scanrowendHighRes][jHighRes] = -999;
				}

			}
			// System.out.println(">"+lonsHighRes[15][471]+","+lonsHighRes[4][467]);
			// if(rowsHighRes>=400 && colsHighRes>=1500)
			// System.out.println(">"+latsHighRes[399][1081]+","+lonsHighRes[399][1081]);
			// Now fill up all except left edge of scan
			for (iHighRes = scanrowstartHighRes; iHighRes <= scanrowendHighRes; iHighRes++) {
				for (jHighRes = 2; jHighRes < colsHighRes; jHighRes = jHighRes + 2) {

					if (Util.inRange(minLat,
							latsHighRes[iHighRes][jHighRes - 1], maxLat)
							&& Util
									.inRange(
											minLon,
											lonsHighRes[iHighRes][jHighRes - 1],
											maxLon)
							&& Util
									.inRange(
											minLat,
											latsHighRes[iHighRes][jHighRes + 1],
											maxLat)
							&& Util
									.inRange(
											minLon,
											lonsHighRes[iHighRes][jHighRes + 1],
											maxLon)
							&& lonsHighRes[iHighRes][jHighRes - 1]
									* lonsHighRes[iHighRes][jHighRes + 1] >= -100) {
						latsHighRes[iHighRes][jHighRes] = (latsHighRes[iHighRes][jHighRes - 1] + latsHighRes[iHighRes][jHighRes + 1]) / 2;
						lonsHighRes[iHighRes][jHighRes] = (lonsHighRes[iHighRes][jHighRes - 1] + lonsHighRes[iHighRes][jHighRes + 1]) / 2;
						// if(latsHighRes[iHighRes][jHighRes]>-0.25 &&
						// latsHighRes[iHighRes][jHighRes]<0.25)
						// System.out.println("3RD:"+latsHighRes[iHighRes][jHighRes]+":"+latsHighRes[iHighRes][jHighRes-1]+","+latsHighRes[iHighRes][jHighRes+1]);
					} else {
						latsHighRes[iHighRes][jHighRes] = -999;
						lonsHighRes[iHighRes][jHighRes] = -999;
					}
				}
			}
			// System.out.println(">"+lonsHighRes[15][471]+","+lonsHighRes[4][467]);
			// if(rowsHighRes>=400 && colsHighRes>=1500)
			// System.out.println(">"+latsHighRes[399][1081]+","+lonsHighRes[399][1081]);
			// Now Fill up Left Edge of scan
			for (iHighRes = scanrowstartHighRes; iHighRes <= scanrowendHighRes; iHighRes++) {

				if (Util.inRange(minLat, latsHighRes[iHighRes][1], maxLat)
						&& Util.inRange(minLon, lonsHighRes[iHighRes][1],
								maxLon)
						&& // THIS IS A BUG
						Util.inRange(minLat, latsHighRes[iHighRes][2], maxLat)
						&& Util.inRange(minLon, lonsHighRes[iHighRes][2],
								maxLon)
						&&
						// Util.inRange(minLon,lonsHighRes[iHighRes][jHighRes-1],maxLon)
						// && // THIS IS A BUG
						// Util.inRange(minLat,lonsHighRes[iHighRes][1],maxLat)
						// &&
						Util.inRange(minLon, lonsHighRes[iHighRes][2], maxLon)
						&& /*lonsHighRes[iHighRes][1] * lonsHighRes[iHighRes][2] >=0*/ lonsHighRes[iHighRes][1] * lonsHighRes[iHighRes][2] >=-100) {
					latsHighRes[iHighRes][0] = 2 * latsHighRes[iHighRes][1]
							- latsHighRes[iHighRes][2];
					lonsHighRes[iHighRes][0] = 2 * lonsHighRes[iHighRes][1]
							- lonsHighRes[iHighRes][2];
					// if(latsHighRes[iHighRes][0]>-0.25 &&
					// latsHighRes[iHighRes][0]<0.25)
					// System.out.println("LEFT:"+latsHighRes[iHighRes][0]+":"+latsHighRes[iHighRes][1]+","+latsHighRes[iHighRes][2]);
				} else {
					latsHighRes[iHighRes][0] = -999;
					lonsHighRes[iHighRes][0] = -999;
				}

			}
			// System.out.println(">"+lonsHighRes[15][471]+","+lonsHighRes[4][467]);
			// if(rowsHighRes>=400 && colsHighRes>=1500)
			// System.out.println(">"+latsHighRes[398][1081]+","+lonsHighRes[398][1081]);
		}
		// printfile("latitude1km.txt",lats);
		// printfile("longitude1km.txt",lons);
		// printfile("latitudeHkm.txt",latsHighRes);
		// printfile("longitudeHkm.txt",lonsHighRes);
		/*
		 * System.out.println("Low Res Geo"); for (int i=0;i<rowsLowRes;i++) {
		 * for(int j=0;j<colsLowRes;j++) { if(lats[i][j]==0.0)
		 * System.out.print(lats[i][j]+","+lons[i][j]+" "); }
		 * //System.out.println(); } System.out.println("High Res Geo"); for
		 * (int i=0;i<rowsHighRes;i++) { for(int j=0;j<colsHighRes;j++) {
		 * if(latsHighRes[i][j]==0.0)
		 * System.out.print(latsHighRes[i][j]+","+lonsHighRes[i][j]+" "); }
		 * //System.out.println(); }
		 */
		lats = latsHighRes;
		lons = lonsHighRes;

		calcRanges();
		// System.out.println("End of double GeoResolution");
	}

}
