/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/


package gov.nasa.gsfc.drl.h2g;

import gov.nasa.gsfc.drl.h2g.util.Configuration;
import gov.nasa.gsfc.drl.h2g.util.GEOTIFF;
import gov.nasa.gsfc.drl.h2g.util.GeneralValueMapper;
import gov.nasa.gsfc.drl.h2g.util.GeoReader;
import gov.nasa.gsfc.drl.h2g.util.ImageCreator;
import gov.nasa.gsfc.drl.h2g.util.JPEG;
import gov.nasa.gsfc.drl.h2g.util.PNG;
import gov.nasa.gsfc.drl.h2g.util.RGBValueMapper;
import gov.nasa.gsfc.drl.h2g.util.SDSReader;

import gov.nasa.gsfc.drl.h2g.util.ValueMapper;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
//import java.io.FileInputStream;
import java.io.FileReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.object.h4.H4File;



public class Create {
	//private static double min_Latitude = -999.0, max_Latitude = -999.0,
	//		min_Longitude = -999.0, max_Longitude = -999.0;
	//private static double minLat = 999.0, maxLat = -999.0, minLon = 999.0,
	//		maxLon = -999.0;
	//private static double minLonPos = 999.0, maxLonNeg = -999.0;
	private static Vector<Integer> confidence;
	private static Vector<Double> latloc, lonloc;
	

	private static Configuration config;
	private static ValueMapper mapper;
	private static Boolean CrossOver = Boolean.FALSE;
	private static double distFrom180 = -999.0;
	private static final int IMAGE_EDGE_WIDTH=40;
	private static final int LEGEND_HEIGHT=150;
	private static final int GRID_SPACING=10;
	private static final double GRID_PLOT_INTERVAL_DEGREES=0.2;
	private static final int LEGEND_FONT_SIZE=16;
	private static final int LATLONLABEL_FONT_SIZE=12;
	

	
	/****************************************************************************
	 * main.
	 ****************************************************************************/
	public static void main(String[] args) throws Exception {
		double resolution=-999.0; //use default
		String projection="DEFAULT";
		double lat0=-999.0; //use default
		double lon0=-999.0; //use default
		double width_lon=-999.0;
		double height_lat=-999.0;
		double width_km=-999.0;
		double height_km=-999.0;
		double toplat=-999.0; //default no subset
		double botlat=-999.0; //default no subset
		double leftlon=-999.0; //default no subset
		double rightlon=-999.0; //default no subset
		Boolean isRGBinDifferentFiles=Boolean.FALSE;

				
		//if (args[0].equals("newh2g")) {

			
            
			int argindex = 0;
			String H2GHOME = args[argindex++]; //H2GHOME
			String cfgType = args[argindex++]; // SINGLEBAND or RGB or STANDARD
			String cfgName = args[argindex++]; // File path or Standard ConfigName
			String oType = null;
			boolean browseEnhance=Boolean.FALSE;
			
			

			File[][] iFiles = new File[10][14];
			File oFile = null;
			int ndatasets=0,ngeolocs=0,nmasks=0,nfires=0,nreds=0,ngreens=0,nblues=0;
			
			System.out.println("Parsing command line...");
			while (argindex < args.length) {
				System.out.print("   "+args[argindex]);
				
				if (args[argindex].equals("dataset"))
				{
					iFiles[0][0] = new File(args[argindex + 1]);
					ndatasets++;
				}
				if (args[argindex].equals("geoloc"))
				{
					iFiles[0][1] = new File(args[argindex + 1]);
					ngeolocs++;
				}
				if (args[argindex].equals("mask"))
				{
					iFiles[0][2] = new File(args[argindex + 1]);
					nmasks++;
				}
				if (args[argindex].equals("fire"))
				{
					iFiles[0][3] = new File(args[argindex + 1]);
					nfires++;
				}
				
				if (args[argindex].equals("dataset2"))
				{
					iFiles[1][0] = new File(args[argindex + 1]);
					ndatasets++;
				}
				if (args[argindex].equals("geoloc2"))
				{
					iFiles[1][1] = new File(args[argindex + 1]);
					ngeolocs++;
				}
				if (args[argindex].equals("mask2"))
				{
					iFiles[1][2] = new File(args[argindex + 1]);
					nmasks++;
				}
				if (args[argindex].equals("fire2"))
				{
					iFiles[1][3] = new File(args[argindex + 1]);
					nfires++;
				}
				
				if (args[argindex].equals("dataset3"))
				{
					iFiles[2][0] = new File(args[argindex + 1]);
					ndatasets++;
				}
				if (args[argindex].equals("geoloc3"))
				{
					iFiles[2][1] = new File(args[argindex + 1]);
					ngeolocs++;
				}
				if (args[argindex].equals("mask3"))
				{
					iFiles[2][2] = new File(args[argindex + 1]);
					nmasks++;
				}
				if (args[argindex].equals("fire3"))
				{
					iFiles[2][3] = new File(args[argindex + 1]);
					nfires++;
				}
				
				if (args[argindex].equals("dataset4"))
				{
					iFiles[3][0] = new File(args[argindex + 1]);
					ndatasets++;
				}
				if (args[argindex].equals("geoloc4"))
				{
					iFiles[3][1] = new File(args[argindex + 1]);
					ngeolocs++;
				}
				if (args[argindex].equals("mask4"))
				{
					iFiles[3][2] = new File(args[argindex + 1]);
					nmasks++;
				}
				if (args[argindex].equals("fire4"))
				{
					iFiles[3][3] = new File(args[argindex + 1]);
					nfires++;
				}
				
				if (args[argindex].equals("dataset5"))
				{
					iFiles[4][0] = new File(args[argindex + 1]);
					ndatasets++;
				}
				if (args[argindex].equals("geoloc5"))
				{
					iFiles[4][1] = new File(args[argindex + 1]);
					ngeolocs++;
				}
				if (args[argindex].equals("mask5"))
				{
					iFiles[4][2] = new File(args[argindex + 1]);
					nmasks++;
				}
				if (args[argindex].equals("fire5"))
				{
					iFiles[4][3] = new File(args[argindex + 1]);
					nfires++;
				}
				
				if (args[argindex].equals("dataset6"))
				{
					iFiles[5][0] = new File(args[argindex + 1]);
					ndatasets++;
				}
				if (args[argindex].equals("geoloc6"))
				{
					iFiles[5][1] = new File(args[argindex + 1]);
					ngeolocs++;
				}
				if (args[argindex].equals("mask6"))
				{
					iFiles[5][2] = new File(args[argindex + 1]);
					nmasks++;
				}
				if (args[argindex].equals("fire6"))
				{
					iFiles[5][3] = new File(args[argindex + 1]);
					nfires++;
				}
				
				if (args[argindex].equals("dataset7"))
				{
					iFiles[6][0] = new File(args[argindex + 1]);
					ndatasets++;
				}
				if (args[argindex].equals("geoloc7"))
				{
					iFiles[6][1] = new File(args[argindex + 1]);
					ngeolocs++;
				}
				if (args[argindex].equals("mask7"))
				{
					iFiles[6][2] = new File(args[argindex + 1]);
					nmasks++;
				}
				if (args[argindex].equals("fire7"))
				{
					iFiles[6][3] = new File(args[argindex + 1]);
					nfires++;
				}
				
				if (args[argindex].equals("dataset8"))
				{
					iFiles[7][0] = new File(args[argindex + 1]);
					ndatasets++;
				}
				if (args[argindex].equals("geoloc8"))
				{
					iFiles[7][1] = new File(args[argindex + 1]);
					ngeolocs++;
				}
				if (args[argindex].equals("mask8"))
				{
					iFiles[7][2] = new File(args[argindex + 1]);
					nmasks++;
				}
				if (args[argindex].equals("fire8"))
				{
					iFiles[7][3] = new File(args[argindex + 1]);
					nfires++;
				}
				
				if (args[argindex].equals("dataset9"))
				{
					iFiles[8][0] = new File(args[argindex + 1]);
					ndatasets++;
				}
				if (args[argindex].equals("geoloc9"))
				{
					iFiles[8][1] = new File(args[argindex + 1]);
					ngeolocs++;
				}
				if (args[argindex].equals("mask9"))
				{
					iFiles[8][2] = new File(args[argindex + 1]);
					nmasks++;
				}
				if (args[argindex].equals("fire9"))
				{
					iFiles[8][3] = new File(args[argindex + 1]);
					nfires++;
				}
				
				if (args[argindex].equals("dataset10"))
				{
					iFiles[9][0] = new File(args[argindex + 1]);
					ndatasets++;
				}
				if (args[argindex].equals("geoloc10"))
				{
					iFiles[9][1] = new File(args[argindex + 1]);
					ngeolocs++;
				}
				if (args[argindex].equals("mask10"))
				{
					iFiles[9][2] = new File(args[argindex + 1]);
					nmasks++;
				}
				if (args[argindex].equals("fire10"))
				{
					iFiles[9][3] = new File(args[argindex + 1]);
					nfires++;
				}
				if (args[argindex].equals("overlaypolitical"))
					iFiles[0][4] = new File(args[argindex + 1]);
				if (args[argindex].equals("overlayroads"))
					iFiles[0][5] = new File(args[argindex + 1]);
				if (args[argindex].equals("overlayrivers"))
					iFiles[0][6] = new File(args[argindex + 1]);
				if (args[argindex].equals("overlaycanada"))
					iFiles[0][7] = new File(args[argindex + 1]);
				if (args[argindex].equals("overlaycarib"))
					iFiles[0][8] = new File(args[argindex + 1]);
				if (args[argindex].equals("overlaymexico"))
					iFiles[0][9] = new File(args[argindex + 1]);
				if (args[argindex].equals("overlayworld"))
					iFiles[0][10] = new File(args[argindex + 1]);
				if (args[argindex].equals("-browseenhance"))
					browseEnhance=Boolean.TRUE;
				
				if (args[argindex].equals("reddataset"))
				{
					iFiles[0][11] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nreds++;
				}
				if (args[argindex].equals("greendataset"))
				{
					iFiles[0][12] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					ngreens++;
				}
				if (args[argindex].equals("bluedataset"))
				{
					iFiles[0][13] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nblues++;
				}
				
				if (args[argindex].equals("reddataset2"))
				{
					iFiles[1][11] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nreds++;
				}
				if (args[argindex].equals("greendataset2"))
				{
					iFiles[1][12] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					ngreens++;
				}
				
				if (args[argindex].equals("bluedataset2"))
				{
					iFiles[1][13] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nblues++;
				}
				
				if (args[argindex].equals("reddataset3"))
				{
					iFiles[2][11] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nreds++;
				}
				if (args[argindex].equals("greendataset3"))
				{
					iFiles[2][12] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					ngreens++;
				}
				if (args[argindex].equals("bluedataset3"))
				{
					iFiles[2][13] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nblues++;
				}
				
				if (args[argindex].equals("reddataset4"))
				{
					iFiles[3][11] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nreds++;
				}
				if (args[argindex].equals("greendataset4"))
				{
					iFiles[3][12] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					ngreens++;
				}
				if (args[argindex].equals("bluedataset4"))
				{
					iFiles[3][13] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nblues++;
				}
				
				if (args[argindex].equals("reddataset5"))
				{
					iFiles[4][11] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nreds++;
				}
				if (args[argindex].equals("greendataset5"))
				{
					iFiles[4][12] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					ngreens++;
				}
				if (args[argindex].equals("bluedataset5"))
				{
					iFiles[4][13] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nblues++;
				}
				
				if (args[argindex].equals("reddataset6"))
				{
					iFiles[5][11] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nreds++;
				}
				if (args[argindex].equals("greendataset6"))
				{
					iFiles[5][12] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					ngreens++;
				}
				if (args[argindex].equals("bluedataset6"))
				{
					iFiles[5][13] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nblues++;
				}
				
				if (args[argindex].equals("reddataset7"))
				{
					iFiles[6][11] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nreds++;
				}
				if (args[argindex].equals("greendataset7"))
				{
					iFiles[6][12] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					ngreens++;
				}
				if (args[argindex].equals("bluedataset7"))
				{
					iFiles[6][13] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nblues++;
				}
				
				if (args[argindex].equals("reddataset8"))
				{
					iFiles[7][11] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nreds++;
				}
				if (args[argindex].equals("greendataset8"))
				{
					iFiles[7][12] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					ngreens++;
				}
				if (args[argindex].equals("bluedataset8"))
				{
					iFiles[7][13] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nblues++;
				}
				
				if (args[argindex].equals("reddataset9"))
				{
					iFiles[8][11] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nreds++;
				}
				if (args[argindex].equals("greendataset9"))
				{
					iFiles[8][12] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					ngreens++;
				}
				if (args[argindex].equals("bluedataset9"))
				{
					iFiles[8][13] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nblues++;
				}
				if (args[argindex].equals("reddataset10"))
				{
					iFiles[9][11] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nreds++;
				}
				if (args[argindex].equals("greendataset10"))
				{
					iFiles[9][12] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					ngreens++;
				}
				if (args[argindex].equals("bluedataset10"))
				{
					iFiles[9][13] = new File(args[argindex + 1]);
					isRGBinDifferentFiles=Boolean.TRUE;
					nblues++;
				}
				
				
				if (args[argindex].equals("output"))
					oFile = new File(args[argindex + 1]);
				if (args[argindex].equals("otype"))
					oType = args[argindex + 1];				
				if (args[argindex].equals("projection"))
					projection = args[argindex + 1];				
				if (args[argindex].equals("resolution"))
					resolution = Double.parseDouble(args[argindex + 1]);
				if (args[argindex].equals("centerlat"))
				{
					lat0 = Double.parseDouble(args[argindex + 1]);
					if (lat0==0.0) lat0=0.0001;//This is to mask a proj4 bug - returns double the distance
				}
				if (args[argindex].equals("centerlon"))
					lon0 = Double.parseDouble(args[argindex + 1]);
				if (args[argindex].equals("width_lon"))
					width_lon=Double.parseDouble(args[argindex + 1]);
					//toplat = Double.parseDouble(args[argindex + 1]);
				if (args[argindex].equals("width_km"))
					width_km = Double.parseDouble(args[argindex + 1]);
				    //botlat = Double.parseDouble(args[argindex + 1]);
				if (args[argindex].equals("height_lat"))
					height_lat = Double.parseDouble(args[argindex + 1]);
					//leftlon = Double.parseDouble(args[argindex + 1]);
				if (args[argindex].equals("height_km"))
					height_km = Double.parseDouble(args[argindex + 1]);
					//rightlon = Double.parseDouble(args[argindex + 1]);

				argindex = argindex + 2;

			}
			System.out.println();
			System.out.println("Parsing command line Complete.");
			
			if(!isRGBinDifferentFiles)
			{
				if(ngeolocs>0)
				{
					if(ndatasets!=ngeolocs)
					{
						System.out.println("Error: no of dataset inputs does not match no of geolocation inputs");
						System.exit(1);
					}
				}
				if(nmasks>0)
				{
					if(ndatasets!=nmasks)
					{
						System.out.println("Error: no of dataset inputs does not match no of mask inputs");
						System.exit(1);
					}
				}
				/*if(nfires>0)
				{
					if(ndatasets!=nfires)
					{
						System.out.println("Error: no of dataset inputs does not match no of fire location inputs");
						System.exit(1);
					}
				}*/
			}
			else
			{
				if (nreds!=ngreens || nreds!=nblues)
				{
					System.out.println("Error: no of RED GREEN AND BLUE inputs does not match");
				}
				if(nreds!=ngeolocs)
					{
						System.out.println("Error: no of dataset inputs does not match no of geolocation inputs");
						System.exit(1);
					}						
				if(nfires>0)
				{
					if(nreds!=nfires)
					{
						System.out.println("Error: no of dataset inputs does not match no of fire location inputs");
						System.exit(1);
					}
				}
			}
			
			if(lat0!=-999.0 && height_lat!=-999.0)
			{
			   toplat=lat0+(height_lat/2.0);
			   if(toplat>90.0) toplat=90.0;
			   botlat=lat0-(height_lat/2.0);
			   if(botlat<-90) botlat=-90.0;
			}
			if(lon0!=-999.0 && width_lon!=-999.0)
			{
			   leftlon=lon0-(width_lon/2.0);
			   if(leftlon<-180.0) leftlon=leftlon+360;
			   rightlon=lon0+(width_lon/2.0);
			   if(rightlon>180.0) rightlon=rightlon-360;
			}
			
			System.out.println("cfgtype=" + cfgType + " cfgName="+ cfgName + " oType=" + oType+" resoution="+resolution);			
			int nmosaicfiles=0;
			if(isRGBinDifferentFiles) 
				nmosaicfiles=nreds;
			else 
			    nmosaicfiles=ndatasets;
			int width_km_by2=(int)(width_km/2); 
			int height_km_by2=(int)(height_km/2);
			config = Configuration.getInstance(cfgType, cfgName, projection, resolution, lat0,lon0,toplat,botlat,leftlon,rightlon,width_km_by2, height_km_by2, H2GHOME,isRGBinDifferentFiles);
			//initproj();
			create(cfgName, iFiles, oType, oFile,nmosaicfiles,browseEnhance);
	  //}
		
	}

	/**********************************
	 * testproj
	 * @throws Exception 
	 ***********************************/
	/*public static void initproj() throws Exception {
		// now the input file is going to be read
		BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(
								new File(
										"/home/sdasgupta/SPA_DEV/SPA_DEV/h2g/algorithm/configFiles/test.txt"))));
		LinkedHashMap mapHeader = new LinkedHashMap();
		// read the header
		String line = null;
		for (int i = 0; i < 3; i++) {
			if ((line = br.readLine()) != null) {
				String[] header = line.split(":", 2);
				if (header.length <= 2) {
					mapHeader.put(header[0].trim(), header[1].trim());
				} else {
					System.out.println("Wrong file format");
					System.exit(0);
				}
			}
		}
		// with what you see above, the header was read

		// now I can define the number of rows of data triplets
		int rows = new Integer((String) mapHeader.get("rows")).intValue();

		double[][] testCoord = new double[rows][2];
		double[] testValues = new double[rows];
		System.out.println("Source coordinates and values:");

		// start reading the data
		for (int i = 0; i < rows; i++) {
			if ((line = br.readLine()) != null) {
				String[] values = line.split(" +");
				if (values.length == 3) {
					testCoord[i][0] = new Double(values[0].trim())
							.doubleValue();
					testCoord[i][1] = new Double(values[1].trim())
							.doubleValue();
					testValues[i] = new Double(values[2].trim()).doubleValue();
					System.out.println("x = " + testCoord[i][0] + " y = "
							+ testCoord[i][1] + " z = " + testValues[i]);
				} else {
					System.out.println("Wrong file format or empty line found");
				}
			}
		}

		// create the dataset
		// this is necessary to be able to transform
		ProjectionData dataTP = new ProjectionData(testCoord, testValues);

		// here we go with the instantiation of the proj4 object
		System.out.println(mapHeader.get("srcProj")+" "+mapHeader.get("destProj"));
		Proj4 testProjection = new Proj4((String) mapHeader.get("srcProj"),
				(String) mapHeader.get("destProj"));

		// the instantiation of the proj4 object instantiated also the
		// projection
		// objects for source and destination projection
		// therefore we can already print the projection infos:
		testProjection.printSrcProjInfo();
		System.out.println("Destination proj info");
		testProjection.printDestProjInfo();

		// and transform, passing as parameter the created dataset:
		testProjection.transform(dataTP, 1, 1);
		System.out.println("dataTP=" + dataTP.x[0] + " " + dataTP.y[0]);

		// if we need the parameters as Hashmap for a later use:
		LinkedHashMap testMap = testProjection.getSrcProjInfo();

		// and let us print them to screen to see them
		System.out.println();
		System.out.println("Proj as a Hashmap");
		System.out
				.println("******************************************************");
		System.out.println(testMap.toString());
		

	

	}*/

	/****************************************************************************
	 * create.
	 ****************************************************************************/
	public static void create(String iType, File[][] iFiles, String oType,
			File oFile,int nmosaicfiles, boolean browseEnhance) throws Exception {
		System.out.println("Output Type="+oType);
		String oFmt = oType.split("\\.")[0];
		oType = oType.split("\\.")[1];
		
		ImageCreator creator = getCreator(iType, iFiles, oType, oFmt,nmosaicfiles);
		if (oFmt.equals("geotiff")) {
			GEOTIFF.fromBufferedImage(
					new BufferedImage[] { creator.getImage() }, creator
							.getLatScale(), creator.getLonScale(),
					creator.getMinLongitude(), creator.getMaxLatitude(), oFile);
		}
		if (oFmt.equals("jpeg") || oFmt.equals("png")) {
			System.out.println("Creating browse Image...");
			/*creator.overlay(creator.getMinLongitude(), creator.getMinLatitude(), iFiles[4].toString(), new Color(
					153, 0, 255), CrossOver, distFrom180);
			creator.overlay(creator.getMinLongitude(), creator.getMinLatitude(), iFiles[7].toString(), new Color(
					153, 0, 255), CrossOver, distFrom180);
			creator.overlay(creator.getMinLongitude(), creator.getMinLatitude(), iFiles[8].toString(), new Color(
					153, 0, 255), CrossOver, distFrom180);
			creator.overlay(creator.getMinLongitude(), creator.getMinLatitude(), iFiles[9].toString(), new Color(
					153, 0, 255), CrossOver, distFrom180);
			creator.overlay(creator.getMinLongitude(), creator.getMinLatitude(), iFiles[10].toString(), new Color(
					153, 0, 255), CrossOver, distFrom180);*/
			System.out.println("Overlaying shape files...");
            creator.overlay(creator.getMinLongitude(), creator.getMinLatitude(), iFiles[0][4].toString(), new Color(
					224, 224,224), CrossOver, distFrom180);
			//creator.overlay(creator.getMinLongitude(), creator.getMinLatitude(), iFiles[0][7].toString(), new Color(
			//		255, 255,255), CrossOver, distFrom180);
			creator.overlay(creator.getMinLongitude(), creator.getMinLatitude(), iFiles[0][8].toString(), new Color(
					224, 224,224), CrossOver, distFrom180);
			creator.overlay(creator.getMinLongitude(), creator.getMinLatitude(), iFiles[0][9].toString(), new Color(
					224, 224,224), CrossOver, distFrom180);
			creator.overlay(creator.getMinLongitude(), creator.getMinLatitude(), iFiles[0][10].toString(), new Color(
					224, 224,224), CrossOver, distFrom180);
			
			System.out.println("Overlay of shape files Complete.");
			
			if(browseEnhance)
			 creator.overlayLatLonGrid( Color.RED, IMAGE_EDGE_WIDTH, GRID_SPACING,GRID_PLOT_INTERVAL_DEGREES,LATLONLABEL_FONT_SIZE);
			
			if (config.getconfigType().equals("singleband")
					&& config.gethasLegend()){
				if(browseEnhance)
					creator.concatLegend(mapper, config.getmarkers(), config
							.getmarkerValues(), config.getlegend(),LEGEND_HEIGHT,IMAGE_EDGE_WIDTH,LEGEND_FONT_SIZE);
				else 
				    creator.drawLegend(mapper, config.getmarkers(), config.getmarkerValues(), config.getlegend()); 
				
			}
			//System.out.println("Creating jpeg/png");
			if (oFmt.equals("jpeg"))
				JPEG.fromBufferedImage(creator.getImage(), oFile, creator
						.getImageWidth(), creator.getImageHeight());
			else
				PNG.fromBufferedImage(creator.getImage(), oFile, creator
						.getImageWidth(), creator.getImageHeight());
			System.out.println("Creation of browse image Complete.");
		}
		return;
		
	}

	/****************************************************************************
	 * getCreator.
	 ****************************************************************************/
	private static ImageCreator getCreator(String iType, File[][] iFiles,
			String oType, String oFmt, int nmosaicfiles) throws Exception {

		String cfgType = config.getconfigType();
		
		
		
		
		if (cfgType.equals("singleband")) {

			int STRIDE = 1, MASK_STRIDE = 1, GEO_STRIDE = 1;
			int[][] data = null;
			int[][] mask = null;
			double[][] lats, lons;
			GeoReader geoReader = null;
			
			/************************* CHANGE THIS CODE LATER ************************/
			/*Handle Special Cases for speeding up processing*/
			//if (/*config.getProjection()!=Configuration.GEOGRAPHIC && */(config.getCfgName().equals("ndvi") || config.getCfgName().equals("evi") || config.getCfgName().equals("viqualsth5d")) )
			if (config.getUseOptimization())	
			{
				//config.setDATADimMatchAlgo(24); //MASK_CONT_DOWNSCALE
				config.setDATADimMatchAlgo(25); //MASK_DISC_DOWNSCALE
				if(config.getCfgName().equals("vsnowbinh5"))
					config.setMASKDimMatchAlgo(25); //MASK_DISC_DOWNSCALE
				else
				   config.setMASKDimMatchAlgo(20); //DIM_MATCHES_SDS
				config.setGEODimMatchAlgo(20); //DIM_MATCHES_SDS
			}
			/**************************************************************************/
			
           
			// System.out.println(iFiles[config.getSDSSource()]+" "+config.getSDSName()+" "+config.getSDSIndex());
			
			/* Initializing the data SDS, geolocation SDSs, and the Mask SDSs */
			//SDSReader dataReader = new SDSReader(iFiles[config.getSDSSource()],config.getSDSName(), config.getSDSIndex());
			ImageCreator imgcreator=null;
			for(int fileset=0;fileset<nmosaicfiles;fileset++)
			{
				SDSReader dataReader = config.getSDSReader(iFiles[fileset]);
				SDSReader latReader = config.getLatReader(iFiles[fileset]);
				SDSReader lonReader = config.getLonReader(iFiles[fileset]);
				SDSReader maskReader = config.getMASKReader(iFiles[fileset]);


				/* Getting no of rows in data SDS, width of geolocation SDS,Rows-per-scan in SDS, MASK & GEO and total no of scans */
				// int xSDS = dataReader.getWidth();
				int ySDS = dataReader.getHeight();
				int xGeo = latReader.getWidth();
				int MODISSCAN_SDS  =  config.getSDSRowsPerScan();
				int MODISSCAN_GEO  =  config.getGEORowsPerScan();
				int MODISSCAN_MASK =  config.getMASKRowsPerScan();
				int noScans        =  ySDS / config.getSDSRowsPerScan();



				/* Create the valuemapper which maps pixel values to colors and the ImagecReator objects*/
				/* ValueMapper */mapper = new GeneralValueMapper(config);	
				//ImageCreator imgcreator=null;

				/************************* CHANGE THIS CODE LATER ************************/
				//if (/*config.getProjection()!=Configuration.GEOGRAPHIC && */(config.getCfgName().equals("ndvi") || config.getCfgName().equals("evi") || config.getCfgName().equals("viqualsth5d")) )
				if(fileset==0)
				{
					if (config.getUseOptimization())
						imgcreator = new ImageCreator(latReader, lonReader, latReader, mapper, oType, oFmt,cfgType);			
					else
						imgcreator = new ImageCreator(latReader, lonReader, dataReader, mapper, oType, oFmt,cfgType);
				}
				else
				{
					if (config.getUseOptimization())
						imgcreator.setImageProperties(latReader, lonReader, latReader, cfgType);			
					else
						imgcreator.setImageProperties(latReader, lonReader, dataReader, cfgType);
				}

				/**************************************************************************/

				/* Initialise the CrossOver and distfrom180 variables which are needed for 180E/W overlap regions for Geographic Projection*/
				CrossOver= imgcreator.getCrossOver();
				distFrom180=imgcreator.getDistFrom180();



				/*Prepare for image processing SCANS_PER_LOOP at a time*/
				int SCANS_PER_LOOP = config.getScansPerLoop();			
				System.out.println(">> Total no of scans: " + noScans);
				System.out.println(">> Starting to create projected image: Scans Processed Per Iteration: "	+ SCANS_PER_LOOP);			


				int startrowGEO = 0, startrowSDS = 0, startrowMASK = 0;
				int RowsRemainSDS, RowsRemainGEO, RowsRemainMASK, RowsSDS, RowsGEO, RowsMASK;

				for (int iscan = 0, n = 0; iscan < noScans; iscan = iscan+ SCANS_PER_LOOP - 1, n++) {
					System.out.println(">>> Processing scans: "	+ iscan	+ "-"+ 
							(noScans < (iscan + SCANS_PER_LOOP - 1) ? noScans: (iscan + SCANS_PER_LOOP - 1)));
					startrowSDS = iscan * MODISSCAN_SDS;
					startrowMASK = iscan * MODISSCAN_MASK;
					startrowGEO = iscan * MODISSCAN_GEO;
					// System.out.println("SCANS_PER_LOOP="+SCANS_PER_LOOP);
					// System.out.println("iscan="+iscan+" startrowSDS="+startrowSDS+" MODISSCAN_SDS="+MODISSCAN_SDS+" No of rows="+MODISSCAN_SDS*SCANS_PER_LOOP);
					// System.out.println("iscan="+iscan+" startrowMASK="+startrowMASK+" MODISSCAN_MASK="+MODISSCAN_MASK+" No of rows="+MODISSCAN_MASK*SCANS_PER_LOOP);
					// System.out.println("iscan="+iscan+" startrowGEO="+startrowGEO+" MODISSCAN_GEO="+MODISSCAN_GEO+" No of rows="+MODISSCAN_GEO*SCANS_PER_LOOP);

					RowsRemainSDS = (noScans * MODISSCAN_SDS) - (startrowSDS);
					RowsRemainGEO = (noScans * MODISSCAN_GEO) - (startrowGEO);
					RowsSDS = (MODISSCAN_SDS * SCANS_PER_LOOP < RowsRemainSDS) ? MODISSCAN_SDS* SCANS_PER_LOOP	: RowsRemainSDS;
					RowsGEO = (MODISSCAN_GEO * SCANS_PER_LOOP < RowsRemainGEO) ? MODISSCAN_GEO* SCANS_PER_LOOP	: RowsRemainGEO;
					// System.out.println("RowsRemainSDS="+RowsRemainSDS+" RowsRemainGeo="+RowsRemainGEO);
					data = config.getSDSInts(dataReader, startrowSDS, 0, RowsSDS,
							dataReader.getWidth(), STRIDE, STRIDE);

					/************************* CHANGE THIS CODE LATER ************************/
					//if (/*config.getProjection()!=Configuration.GEOGRAPHIC && */(config.getCfgName().equals("ndvi") || config.getCfgName().equals("evi")) )
					if (config.getUseOptimization())
					{
						RowsSDS=RowsSDS/config.getOptimizationIndex();
						data = config.matchSDS_DataDim(data, dataReader,RowsSDS,
								dataReader.getWidth()/config.getOptimizationIndex());
						/*RowsSDS=RowsSDS/4;
				   data = config.matchSDS_DataDim(data, dataReader,RowsSDS,
							dataReader.getWidth()/4);*/
					}
					if ((config.getCfgName().equals("viqualsth5d")) )
					{
						RowsSDS=RowsSDS/2;
						data = config.matchSDS_DataDim(data, dataReader,RowsSDS,
								dataReader.getWidth()/2);
					}
					/**************************************************************************/

					/* Get latitude longitude arrays for this scan; re-sample it to match data dimensions, results are in the geoReader object 
					 * When CrossOver is true (180E/W region) and Projection is Geographic 
					 * lons come added with a preset value to shift them within 180W - 180E */

					lats = config.getGEODoubles(latReader, startrowGEO, 0, RowsGEO,
							xGeo, GEO_STRIDE, GEO_STRIDE, Boolean.FALSE, -999.0);
					lons = config.getGEODoubles(lonReader, startrowGEO, 0, RowsGEO,
							xGeo, GEO_STRIDE, GEO_STRIDE, CrossOver, distFrom180);
					geoReader = new GeoReader(lats, lons);

					/************************* CHANGE THIS CODE LATER ************************/
					//if (/*config.getProjection()!=Configuration.GEOGRAPHIC && */(config.getCfgName().equals("ndvi") || config.getCfgName().equals("evi") || config.getCfgName().equals("viqualsth5d")) )
					if (config.getUseOptimization())
						geoReader = config.matchSDS_GEODim(geoReader, latReader,
								latReader, lonReader);
					else
						geoReader = config.matchSDS_GEODim(geoReader, dataReader,
								latReader, lonReader);
					/**************************************************************************/
					imgcreator.setScalerGeoReader(mapper, geoReader, oType);



					if (config.getProjection()==Configuration.GEOGRAPHIC)
						imgcreator.fillScanBox();
					if (config.getProjection()==Configuration.STEREOGRAPHIC)
						imgcreator.fillScanBox_STEREO();

					if (config.getMASKExists()) {

						RowsRemainMASK = (noScans * MODISSCAN_MASK)
						- (startrowMASK);
						RowsMASK = (MODISSCAN_MASK * SCANS_PER_LOOP < RowsRemainMASK) ? MODISSCAN_MASK
								* SCANS_PER_LOOP
								: RowsRemainMASK;
						System.out.println("RowsRemainMASK=" + RowsRemainMASK);
						System.out.println("RowsMASK=" + RowsMASK);

						mask = config.getMASKInts(maskReader, startrowMASK, 0,
								RowsMASK, maskReader.getWidth(), MASK_STRIDE,
								MASK_STRIDE);

						System.out.println("Going to match SDS MASK");

						/************************* CHANGE THIS CODE LATER ************************/
						//if (/*config.getProjection()!=Configuration.GEOGRAPHIC &&*/ (config.getCfgName().equals("ndvi") || config.getCfgName().equals("evi") || config.getCfgName().equals("viqualsth5d")) )
						if (config.getUseOptimization())
							mask = config.matchSDS_MASKDim(mask, maskReader, RowsGEO,
									latReader.getWidth());
						else
							mask = config.matchSDS_MASKDim(mask, maskReader, RowsSDS,
									dataReader.getWidth());					
						/**************************************************************************/
						System.out.println("Back from match SDS MASK");						
						imgcreator.project2(new int[][][] { data, mask }, config
								.getfireFlag(), config.getfireValues(), 0, RowsSDS);

					} else {
						if (config.getProjection() == Configuration.NOPROJECTION)
							imgcreator.noproject(new int[][][] { data }, 0,
									RowsSDS, startrowSDS);
						else
							imgcreator.project2(new int[][][] { data }, config
									.getfireFlag(), config.getfireValues(), 0,
									RowsSDS);
					}

				}

				if (config.getProjection() != Configuration.NOPROJECTION)
				{
					if(config.isInterpolationSpecified()){
					    if(config.getInterpolationAlgo().equals("INVDIST"))					
			              imgcreator.fillImage_InvDist(config.getnPixels(),config.getDistanceToSearch());
					}
					else
						imgcreator.fillImage3();
				}
				if (config.isRemoveNoiseFlag()){
					imgcreator.removeNoise(config.getNoiseNbd());
				}	
				if (config.isImageFilterFlag()){
					if(config.getImageFilterAlgo().equals("MEANFILTER"))
						imgcreator.meanFilter(config.getFilterNbd());
				}				
				    
				if (config.getfireOverlay()
						&& (oFmt.equals("jpeg") || oFmt.equals("png")) && iFiles[fileset][config.getfireSource()]!=null) {

					BufferedReader in = null;
					try {
						// in = new BufferedReader(new
						// InputStreamReader(Util.getInputStream(CONFIGFILEPATH)));
						in = new BufferedReader(new FileReader(iFiles[fileset][config
						                                                 .getfireSource()]));
						System.out.println(">> Reading Fireloc txt file:"
								+ iFiles[fileset][config.getfireSource()]);
						String line;

						latloc = new Vector<Double>();
						lonloc = new Vector<Double>();
						confidence = new Vector<Integer>();
						double templon;
						double refLon = 180.0 - distFrom180;
						while ((line = in.readLine()) != null) {
							// System.out.println(line);
							StringTokenizer byBlanks = new StringTokenizer(line,
							",");
							latloc.addElement(Double.parseDouble(byBlanks
									.nextToken()));
							templon = Double.parseDouble(byBlanks.nextToken());
							if (CrossOver) {
								if (templon < 0)
									templon = templon + distFrom180;
								else
									templon = -180.0 + (templon - refLon);
							}
							lonloc.addElement(templon);
							byBlanks.nextToken();
							byBlanks.nextToken();
							byBlanks.nextToken();
							confidence.addElement(Integer.parseInt(byBlanks
									.nextToken()));
						}

					} finally {
						try {
							in.close();
						} catch (Exception e) {
						}
					}
					System.out.println(">> Overlaying fires on image product");

					imgcreator.overlayFirePixels(latloc, lonloc, confidence);
				}
				if(config.getSDSishdf5())
					dataReader.h5close();
				else
					dataReader.close();
				if (config.getMASKExists())
					if(config.getMASKishdf5())
						maskReader.h5close();
					else
						maskReader.close();
				if(config.getGEOishdf5())
				{
					latReader.h5close();
					lonReader.h5close();
				}
				else
				{
					latReader.close();
					lonReader.close();
				}
			}
			return (imgcreator);

		}

		/***********************************************************/
		/** The following code creates RGB images **/
		/***********************************************************/

		if (cfgType.equals("rgb")) {
			System.out.println(">> Configuration Type: RGB");

			int STRIDE = 1;
			int GEO_STRIDE = STRIDE;

			/** INITIALISING READERS for the Red/Green/Blue Bands **/
			

			System.out
					.println(">> Initialising Readers for Red Green Blue bands");
			SDSReader redReader=null,blueReader=null,greenReader=null;
			H4File h4=null,h4red=null,h4green=null,h4blue=null;
			ImageCreator imgcreator =null;
			for(int fileset=0;fileset<nmosaicfiles;fileset++)
			{
				if(config.getIsRGBinDifferentFiles()) //For now this case only for NPP SDR H5
				{
					if(config.getREDishdf5())
					{// Assuming if one band is in hdf5 rest are all also in hdf5
					   redReader = new SDSReader(iFiles[fileset][config.getREDSource()],config.getREDName()[0],config
							.getREDIndex());									
					   blueReader = new SDSReader(iFiles[fileset][config.getBLUESource()],config.getBLUEName()[0],config
							.getBLUEIndex());
					   greenReader = new SDSReader(iFiles[fileset][config.getGREENSource()],config.getGREENName()[0],config
							.getGREENIndex());
					}
					else
					{ // Assuming if one band is in hdf4 rest are all also in hdf4
						h4red = new H4File(iFiles[fileset][config.getREDSource()].getAbsolutePath(), H4File.READ);
						System.out.println(iFiles[fileset][config.getREDSource()].getAbsolutePath());
						h4red.open();
						redReader = new SDSReader(h4red, config.getREDName(), config
								.getREDIndex());
						System.out.println(">> Initialised Readers for Red band");
						
						h4blue = new H4File(iFiles[fileset][config.getBLUESource()].getAbsolutePath(), H4File.READ);
						System.out.println(iFiles[fileset][config.getBLUESource()].getAbsolutePath());
						h4blue.open();
						blueReader = new SDSReader(h4blue, config.getBLUEName(),
								config.getBLUEIndex());
						System.out.println(">> Initialised Readers for Blue band");
						
						h4green = new H4File(iFiles[fileset][config.getGREENSource()].getAbsolutePath(), H4File.READ);
						System.out.println(iFiles[fileset][config.getGREENSource()].getAbsolutePath());
						h4green.open();
						greenReader = new SDSReader(h4green, config.getGREENName(),
								config.getGREENIndex());
						System.out.println(">> Initialised Readers for Green band");
					}

				}
				else
				{
					File file = iFiles[fileset][config.getREDSource()];
					if(config.getREDishdf5())
					{
						try {
							int H5file_id = H5.H5Fopen(file.toString(), HDF5Constants.H5F_ACC_RDONLY,HDF5Constants.H5P_DEFAULT);
							System.out.println("RGB is in single HDF5");
							redReader = new SDSReader(H5file_id,config.getREDName()[0],config.getREDIndex());									
							blueReader = new SDSReader(H5file_id,config.getBLUEName()[0],config.getBLUEIndex());
							greenReader = new SDSReader(H5file_id,config.getGREENName()[0],config.getGREENIndex());
						}
						catch (Exception e) {
							e.printStackTrace();
						}

					}
					else
					{
						h4 = new H4File(file.getAbsolutePath(), H4File.READ);
						System.out.println(file.getAbsolutePath());
						h4.open();
						redReader = new SDSReader(h4, config.getREDName(), config
								.getREDIndex());
						System.out.println(">> Initialised Readers for Red band");
						blueReader = new SDSReader(h4, config.getBLUEName(),
								config.getBLUEIndex());
						System.out.println(">> Initialised Readers for Blue band");
						greenReader = new SDSReader(h4, config.getGREENName(),
								config.getGREENIndex());
						System.out.println(">> Initialised Readers for Green band");
					}
				}
				int xRED = redReader.getWidth();
				int yRED = redReader.getHeight();			
				int xBLUE = blueReader.getWidth();
				//int yBLUE = blueReader.getHeight();			
				int xGREEN = greenReader.getWidth();
				//int yGREEN = greenReader.getHeight();
				System.out.println("xRED="+xRED+" yRED="+yRED+" xBLUE="+xBLUE+" xGREEN="+xGREEN);



				/** INITIALIZING THE GEOLOCATION SDSs Latitude & Longitude **/
				System.out.println(">> Initialising Geolocation readers");
				SDSReader latReader = config.getLatReader(iFiles[fileset]);
				SDSReader lonReader = config.getLonReader(iFiles[fileset]);
				int xGeo = latReader.getWidth();



				SDSReader reader = null;
				int RGBDimToMatch = config.getRGBDimToMatch();
				switch (RGBDimToMatch) {
				case 1:
					reader = redReader;
					break;
				case 2:
					reader = greenReader;
					break;
				case 3:
					reader = blueReader;
					break;
				case 4:
					reader = latReader;
					break;
				}


				int MODISSCAN_RED = config.getREDRowsPerScan();
				int MODISSCAN_BLUE = config.getBLUERowsPerScan();
				int MODISSCAN_GREEN = config.getGREENRowsPerScan();
				int MODISSCAN_GEO = config.getGEORowsPerScan();
				int noScans = yRED / config.getREDRowsPerScan();

				double[][] lats, lons;
				GeoReader geoReader = null;





				// PROCESSING FIRST SCAN
				int[][] reds = null;
				int[][] greens = null;
				int[][] blues = null;
				int startrowRED = 0;
				int startrowGREEN = 0;
				int startrowBLUE = 0;
				int RowsRED = MODISSCAN_RED;
				int RowsGREEN = MODISSCAN_GREEN;
				int RowsBLUE = MODISSCAN_BLUE;

				System.out.println(noScans);


				reds = config.getRGBInts(redReader, startrowRED, 0, RowsRED,
						redReader.getWidth(), STRIDE, STRIDE, 1);
				reds = config.matchSDS_RGBDim(reds, redReader, reader, 1);
				greens = config.getRGBInts(greenReader, startrowGREEN, 0,
						RowsGREEN, greenReader.getWidth(), STRIDE, STRIDE, 1);
				greens = config.matchSDS_RGBDim(greens, greenReader, reader, 2);
				blues = config.getRGBInts(blueReader, startrowBLUE, 0, RowsBLUE,
						blueReader.getWidth(), STRIDE, STRIDE, 1);
				blues = config.matchSDS_RGBDim(blues, blueReader, reader, 3);

				// TrueColorNLScaler scaler = new
				// TrueColorNLScaler(reds,greens,blues,0,10000);
				RGBValueMapper scaler = new RGBValueMapper(reds, greens, blues,config);
				if(fileset==0)
				  imgcreator = new ImageCreator(latReader, lonReader, reader, scaler, oType, oFmt,cfgType);
				else
					imgcreator.setImageProperties(latReader, lonReader, reader, cfgType);
				/*ImageCreator imgcreator = new ImageCreator(minLat, maxLat, minLon,
					maxLon, geoReader, scaler, oType, config.getResolution(),
					oFmt);*/
				/* Initialise the CrossOver and distfrom180 variables which are needed for 180E/W overlap regions for Geographic Projection*/
				CrossOver= imgcreator.getCrossOver();
				distFrom180=imgcreator.getDistFrom180();


				int SCANS_PER_LOOP = config.getScansPerLoop();
				System.out.println(">> Total no of scans: " + noScans);
				System.out
				.println(">> Starting to create projected image: Scans Processed Per Iteration: "
						+ SCANS_PER_LOOP);
				int startrowGEO = 0;
				int RowsRemainRED, RowsRemainGREEN, RowsRemainBLUE, RowsRemainGEO, RowsGEO;


				for (int iscan = 0; iscan < noScans; iscan = iscan + SCANS_PER_LOOP
				- 1) {
					System.out.println(">>> Processing scans: "
							+ iscan
							+ "-"
							+ (noScans < (iscan + SCANS_PER_LOOP - 1) ? noScans
									: (iscan + SCANS_PER_LOOP - 1)));
					startrowRED = iscan * MODISSCAN_RED;
					startrowBLUE = iscan * MODISSCAN_BLUE;
					startrowGREEN = iscan * MODISSCAN_GREEN;
					startrowGEO = iscan * MODISSCAN_GEO;
					RowsRemainRED = (noScans * MODISSCAN_RED) - (startrowRED);
					RowsRemainGREEN = (noScans * MODISSCAN_GREEN) - (startrowGREEN);
					RowsRemainBLUE = (noScans * MODISSCAN_BLUE) - (startrowBLUE);
					RowsRemainGEO = (noScans * MODISSCAN_GEO) - (startrowGEO);
					RowsRED = (MODISSCAN_RED * SCANS_PER_LOOP < RowsRemainRED) ? MODISSCAN_RED
							* SCANS_PER_LOOP
							: RowsRemainRED;
					RowsGREEN = (MODISSCAN_GREEN * SCANS_PER_LOOP < RowsRemainGREEN) ? MODISSCAN_GREEN
							* SCANS_PER_LOOP
							: RowsRemainGREEN;
					RowsBLUE = (MODISSCAN_BLUE * SCANS_PER_LOOP < RowsRemainBLUE) ? MODISSCAN_BLUE
							* SCANS_PER_LOOP
							: RowsRemainBLUE;
					RowsGEO = (MODISSCAN_GEO * SCANS_PER_LOOP < RowsRemainGEO) ? MODISSCAN_GEO
							* SCANS_PER_LOOP
							: RowsRemainGEO;

					// System.out.println("SCANS_PER_LOOP="+SCANS_PER_LOOP+"RowsRED="+RowsRED+" RowsGREEN="+RowsGREEN+" RowsBLUE="+RowsBLUE+" RowsGEO="+RowsGEO);


					reds = config.getRGBInts(redReader, startrowRED, 0, RowsRED,
							xRED, STRIDE, STRIDE, 1);
					reds = config.matchSDS_RGBDim(reds, redReader, reader, 1);
					greens = config.getRGBInts(greenReader, startrowGREEN, 0,
							RowsGREEN, xGREEN, STRIDE, STRIDE, 2);
					greens = config.matchSDS_RGBDim(greens, greenReader, reader, 2);
					blues = config.getRGBInts(blueReader, startrowBLUE, 0,
							RowsBLUE, xBLUE, STRIDE, STRIDE, 3);
					blues = config.matchSDS_RGBDim(blues, blueReader, reader, 3);

					lats = config.getGEODoubles(latReader, startrowGEO, 0, RowsGEO,
							xGeo, GEO_STRIDE, GEO_STRIDE, Boolean.FALSE, -999.0);
					System.out.println("Crossover is "+CrossOver+" Dist from 180="+distFrom180);
					lons = config.getGEODoubles(lonReader, startrowGEO, 0, RowsGEO,
							xGeo, GEO_STRIDE, GEO_STRIDE, CrossOver, distFrom180);
					geoReader = new GeoReader(lats, lons);
					geoReader = config.matchSDS_GEODim(geoReader, reader,
							latReader, lonReader);
					// System.out.println("After MAtch GeoDIM:minLat:"+geoReader.getMinLat());
					// scaler = new TrueColorNLScaler(reds,greens,blues,0,10000);
					scaler = new RGBValueMapper(reds, greens, blues, config);
					imgcreator.setScalerGeoReader(scaler, geoReader, oType);
					// if(!config.getisSubset())
					/*imgcreator.drawScanBox(config.getisSubset());
				imgcreator.project(new int[][][] { reds, greens, blues }, 0,
						reds.length);*/
					if (config.getProjection()==Configuration.GEOGRAPHIC)
						imgcreator.fillScanBox();
					if (config.getProjection()==Configuration.STEREOGRAPHIC)
						imgcreator.fillScanBox_STEREO();
					if (config.getProjection() == Configuration.NOPROJECTION)
						imgcreator.noproject(new int[][][] { reds, greens, blues }, 0,
								RowsRED, startrowRED);
					else
					imgcreator.project2(new int[][][] { reds, greens, blues }, config
							.getfireFlag(), config.getfireValues(), 0,
							reds.length);

				}
				if (config.getProjection() != Configuration.NOPROJECTION){					
				        imgcreator.fillImage2();
				}
				/**
				 * Getting firelocations from fireloc.txt file (output from
				 * mod14_SPA)
				 **/
				if (config.getfireOverlay() && iFiles[fileset][config.getfireSource()]!=null) {

					BufferedReader in = null;
					try {
						// in = new BufferedReader(new
						// InputStreamReader(Util.getInputStream(CONFIGFILEPATH)));
						in = new BufferedReader(new FileReader(iFiles[fileset][config
						                                                 .getfireSource()]));
						/*System.out.println(">> Reading Fireloc txt file:"
							+ iFiles[2]);*/
						String line;

						latloc = new Vector<Double>();
						lonloc = new Vector<Double>();
						confidence = new Vector<Integer>();
						double templon;
						double refLon = 180.0 - distFrom180;
						while ((line = in.readLine()) != null) {
							// System.out.println(line);
							StringTokenizer byBlanks = new StringTokenizer(line,
							",");
							latloc.addElement(Double.parseDouble(byBlanks
									.nextToken()));
							templon = Double.parseDouble(byBlanks.nextToken());
							if (CrossOver) {
								if (templon < 0)
									templon = templon + distFrom180;
								else
									templon = -180.0 + (templon - refLon);
							}
							lonloc.addElement(templon);
							byBlanks.nextToken();
							byBlanks.nextToken();
							byBlanks.nextToken();
							confidence.addElement(Integer.parseInt(byBlanks
									.nextToken()));
						}

					} finally {
						try {
							in.close();
						} catch (Exception e) {
						}
					}

					System.out.println(">> Overlaying fires on image product");
					imgcreator.overlayFirePixels(latloc, lonloc, confidence);
				}

				if(config.getIsRGBinDifferentFiles())
				{
					if(config.getREDishdf5())
					{
					    redReader.h5close();
					    blueReader.h5close();
					    greenReader.h5close();
					} 
					else 
					{
						h4red.close();
						h4blue.close();
						h4green.close();
					}
						
				}
				else if(config.getREDishdf5())
				{
					redReader.h5close();
				}
				else
				{						
					h4.close();
				}
				if(config.getGEOishdf5())
				{
					latReader.h5close();
					lonReader.h5close();
				}
				else
				{
					latReader.close();
					lonReader.close();
				}
			}
				return (imgcreator);
			
		}

		throw new Exception("unknown input type: " + iType);
	}
}
