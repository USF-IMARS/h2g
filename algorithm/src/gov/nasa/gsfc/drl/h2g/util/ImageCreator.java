/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/


package gov.nasa.gsfc.drl.h2g.util;

import gov.nasa.gsfc.drl.h2g.vector.*;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.Kernel;
import java.awt.image.ConvolveOp;
import java.awt.image.ColorModel;
import java.awt.BasicStroke;
import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator.Attribute;
import java.awt.font.TextAttribute;

import org.proj4.Proj4;
import org.proj4.ProjectionData;




public class ImageCreator {
	
	private static int check_nfires = 0;
	private GeoReader geoReader;
	private BufferedImage image, mask,imagefiltered;
	private PixelMapper mapper;
	private int width;
	private int height;
	
	/*These are geotiff tag values for Geographic Projection*/
	private static double latScale=0.0;
	private static double lonScale=0.0;
	private static double minLatitude = -999;
	private static double minLongitude = -999;
	private static double maxLatitude = -999;
	private static double maxLongitude = -999;
	private static double minLongitudePseudo=-999;
	private static double maxLongitudePseudo=-999;
	/* Values used for populating other (eg STEREOGRAPHIC) Geotiff Tags*/
	private double xMax_m;
	private double xMin_m;
	private double yMin_m;
	private double yMax_m;
	
	

	private static Boolean CrossOver=Boolean.FALSE;
	private static double distFrom180;
	
	
	private int nnDist = 1;
	private LinkedList nnOffsets = calcOffsetsNN(nnDist);
	private Random random = new Random();
	
	private static final Color NON_SWATH_COLOR = Color.BLUE;
	private static final Color HOLE_COLOR = Color.RED;
	private static final Color DATA_COLOR = Color.GREEN;
	private static final Color FIRE_COLOR = Color.ORANGE;
	private static final int NON_SWATH_RGB = NON_SWATH_COLOR.getRGB();
	private static final int HOLE_RGB = HOLE_COLOR.getRGB();
	private static final int DATA_RGB = DATA_COLOR.getRGB();
	private static final int FIRE_RGB = FIRE_COLOR.getRGB();
	private static final Color MASK_HOLE_COLOR = Color.DARK_GRAY;
	private static final int MASK_HOLE_RGB = MASK_HOLE_COLOR.getRGB();
	
	
	

		public ImageCreator(SDSReader latReader,SDSReader lonReader,SDSReader dataReader, ValueMapper valueMapper, String oType,String oFmt, String cfgtype) throws Exception {
		
		setImageProperties(latReader,lonReader,dataReader,cfgtype);
		image = createImage(oType, valueMapper, oFmt);
		imagefiltered=createImage(oType, valueMapper, oFmt);
		if(cfgtype.equals("singleband"))
		{
			byte[][] colorMap = Util.readMaskColorMap(Configuration.getH2GHOME()+File.separator+"colormaps" + File.separator
					+ "mask_colortable.txt");
			IndexColorModel model = new IndexColorModel(2, 4, colorMap[0],
					colorMap[1], colorMap[2]);
			mask = new BufferedImage(width, height,
					BufferedImage.TYPE_BYTE_INDEXED, model);

			Graphics g = mask.getGraphics();
			g.setColor(NON_SWATH_COLOR);
			g.fillRect(0, 0, width, height);
		}

	}
	
	/**
	 * This method determines the height and width of the output image depending on the projection being used. 
	 * It sets the variables width and height and appropriate geotiff tag values to the correct values.
	 * @throws Exception 
	 */
	public void setImageProperties(SDSReader latReader,SDSReader lonReader,SDSReader dataReader, String cfgtype) throws Exception {
		
		double minLat = 999.0, maxLat = -999.0, minLon = 999.0,	maxLon = -999.0;
		double[][] lats, lons;
		double[] xxyy=new double[2];
		
		int noScans;
		
		
		initproj(latReader,lonReader,dataReader);
		/*Get the singleton Instance of Configuration*/
		Configuration config = Configuration.getInstance();
		System.out.println(config.getCfgName());
		/*Initialize some variables*/
		
		noScans = latReader.getHeight() / config.getGEORowsPerScan();
		
		int GEO_SCAN_LENGTH  =  config.getGEORowsPerScan();
		int GEO_SCAN_WIDTH   =  latReader.getWidth();
		int GEO_STRIDE       =  1;
		
		if (config.getisSubset()) {
			minLat = config.getminLat();
			maxLat = config.getmaxLat();
			minLon = config.getminLon();
			maxLon = config.getmaxLon();
			System.out.println(">> Spatial Subset Requested");
			System.out.println(">>>> Bottom Latitude: " + minLat);
			System.out.println(">>>> Top    Latitude: " + maxLat);
			System.out.println(">>>> Left  Longitude: " + minLon);
			System.out.println(">>>> Right Longitude: " + maxLon);
		}
		
		/* Determine the four corners and the height/width of the output image*/
		switch (config.getProjection()){
		  case Configuration.GEOGRAPHIC:
			   
							
			if (config.getisSubset()) { /* The four corners have been specified by the user*/
				minLatitude = minLat;
				maxLatitude = maxLat;
				minLongitude = minLon;
				maxLongitude = maxLon;
				double dayLine = 180.0;
				if (minLon > 0 && maxLon < 0) {
					System.out.println(">> CrossOver at 180E/W detected. Initialising parameters for processing granule");
					CrossOver = Boolean.TRUE;
					distFrom180 = dayLine - minLon;
					maxLon = maxLon + distFrom180;
					minLon = -180.0;
					System.out.println("Utilizing pseudo projection: minLon="+ minLon + " distFrom180=" + distFrom180+ " maxLon=" + maxLon);
					setMinLongitudePseudo(minLon);
					setMaxLongitudePseudo(maxLon);
				}

			} else { /* Need to Determine the four corners from the scans */
				
				double minLat_scan = 999.0;
				double maxLat_scan = -999.0;
				double minLon_scan = 999.0;
				double maxLon_scan = -999.0;
				double minLonPos_scan = 999.0;
				double maxLonNeg_scan = -999.0;
				double minLonPos = 999.0;
				double maxLonNeg = -999.0;
      
				for (int iscan = 0; iscan < noScans; iscan++) {
					// System.out.println("Scan no:"+iscan+"<"+noScans);
					lats = config.getGEODoubles(latReader, iscan
							* GEO_SCAN_LENGTH, 0, GEO_SCAN_LENGTH, GEO_SCAN_WIDTH,
							GEO_STRIDE, GEO_STRIDE, Boolean.FALSE, -999.0);
					lons = config.getGEODoubles(lonReader, iscan
							* GEO_SCAN_LENGTH, 0, GEO_SCAN_LENGTH, GEO_SCAN_WIDTH,
							GEO_STRIDE, GEO_STRIDE, Boolean.FALSE, -999.0);
					GeoReader geoReader = new GeoReader(lats, lons);
					geoReader = config.matchSDS_GEODim(geoReader, dataReader,
							latReader, lonReader);
					minLat_scan = geoReader.getMinLat();
					maxLat_scan = geoReader.getMaxLat();
					minLon_scan = geoReader.getMinLon();
					maxLon_scan = geoReader.getMaxLon();
					minLonPos_scan = geoReader.getMinLonPos();
					maxLonNeg_scan = geoReader.getMaxLonNeg();
					if (Util.inRange(-90.0, minLat_scan, 90.0)
							&& Util.inRange(-90.0, maxLat_scan, 90.0)
							&& Util.inRange(-180.0, minLon_scan, 180.0)
							&& Util.inRange(-180.0, maxLon_scan, 180.0)) {
						minLat = Util.min(minLat_scan, minLat);
						maxLat = Util.max(maxLat_scan, maxLat);
						minLon = Util.min(minLon_scan, minLon);
						maxLon = Util.max(maxLon_scan, maxLon);
						minLonPos = Util.min(minLonPos_scan, minLonPos);
						maxLonNeg = Util.max(maxLonNeg_scan, maxLonNeg);
					}
				}
				System.out.println("minLon="+minLon+"maxLon="+maxLon+" maxLat="+maxLat+"minLat="+minLat+" minLonPos="+minLonPos+" maxLonNeg="+maxLonNeg);
				double diff_1 = maxLon - minLon;
				double dayLine = 180.0;
				if (minLon < 0 && maxLon > 0 && diff_1 > 358.0) {
					CrossOver = Boolean.TRUE;
					// System.out.println(dayLine-minLonPos);
					distFrom180 = dayLine - minLonPos;
					maxLon = maxLonNeg + distFrom180;
					minLon = -180.0;
					System.out
							.println("CrossOver Detected Utilizing pseudo projection: minLon="
									+ minLon
									+ " distFrom180="
									+ distFrom180
									+ " maxLon=" + maxLon);
					minLatitude = minLat;
					maxLatitude = maxLat;
					minLongitude = minLonPos;
					maxLongitude = maxLonNeg;
					setMinLongitudePseudo(minLon);
					setMaxLongitudePseudo(maxLon);
				} else {
					minLatitude = minLat;
					maxLatitude = maxLat;
					minLongitude = minLon;
					maxLongitude = maxLon;
				}

			}


			System.out.println("minLon=" + minLon + "maxLon=" + maxLon
					+ " maxLat=" + maxLat + "minLat=" + minLat);
			
			double latRange = maxLat - minLat;
			double lonRange = maxLon - minLon;
			width = (int) Math.round(lonRange / config.getResolution());
			height = (int) Math.round(latRange / config.getResolution());
			latScale = latRange / (height - 1);
			lonScale = lonRange / (width - 1);			
			config.setGeotiffTags_GEOGRAPHIC(minLongitude,maxLatitude,lonScale,latScale);
			break;

		  case Configuration.NOPROJECTION:
			width = dataReader.getWidth();
			height = dataReader.getHeight();
			break;
		  case Configuration.MERCATOR:
		  case Configuration.TMERCATOR:
		  case Configuration.SINUSOIDAL:
		  case Configuration.LAZIMUTHAL:
		  case Configuration.STEREOGRAPHIC:
			  
			  double resolution_km=config.getResolution()/1000.0;
			  if (config.getisSubset()) { /* The four corners have been specified by the user*/
				  /*** This section is never invoked for any projection for now. Subsetting has been forcefully disabled for this version**/	  
				 
				  //xMax_m=-9000;
				  //xMin_m= 9000;
				  //yMax_m=-9000;
				  //yMin_m= 9000;
				  
				  xMax_m= config.getWidth_km();//1000;
				  xMin_m= -config.getWidth_km();//-1000;
				  yMax_m=config.getHeight_km();//1000;
				  yMin_m= -config.getHeight_km();//-1000;
				  
				  /*int flag=0;
				  lats=new double[2][2]; lons=new double[2][2];
				  lats[0][0]=maxLat;lats[0][1]=maxLat;lats[1][0]=minLat;lats[1][1]=minLat;
				  lons[0][0]=minLon;lons[0][1]=maxLon;lons[1][0]=minLon;lons[1][1]=maxLon;
				  for(int i=0;i<2;i++)
					  for(int j=0;j<2;j++){
						  xxyy=latlon2xy_km(lons[i][j],lats[i][j]);
						  if(flag==1){
							  if(xxyy[0]<xMin_m) xMin_m=xxyy[0];
							  if(xxyy[0]>xMax_m) xMax_m=xxyy[0];
							  if(xxyy[1]<yMin_m) yMin_m=xxyy[1];
							  if(xxyy[1]>yMax_m) yMax_m=xxyy[1];
						  }
						  else
						  {
							  xMin_m=xxyy[0];
							  xMax_m=xxyy[0];
							  yMin_m=xxyy[1];
							  yMax_m=xxyy[1];
							  flag=1;
						  }
					  }*/
				 
				  
				  width=(int) Math.round((xMax_m-xMin_m)/resolution_km);
				  height=(int) Math.round((yMax_m-yMin_m)/resolution_km);
				  System.out.println(">>> xMin_m="+xMin_m+" xMax_m="+xMax_m+" yMin_m="+yMin_m+" yMax_m="+yMax_m+ "width="+width+"  height="+height);
				  minLatitude = minLat;
				  maxLatitude = maxLat;
				  minLongitude = minLon;
				  maxLongitude = maxLon;
				  distFrom180=0.0;
				  CrossOver=Boolean.FALSE;
						  
			  }
			  else{

				  xMax_m=-9000;
				  xMin_m= 9000;
				  yMax_m=-9000;
				  yMin_m= 9000;
				  int startrowGEO;
				  int flag=0;
				  double[][] xxyy2=new double[GEO_SCAN_WIDTH][2];
				  //for (int iscan = 0; iscan < noScans; iscan++) {
                  /* Checking only the first and last scan to speedup things. 
                   * Test needed to see if this assumption works in all cases, especially at poles*/
				  System.out.println(">>> Checking scans in order to determine image dimensions");
				  for (int iscan = 0; iscan < noScans; iscan++/*iscan=iscan+noScans-1*/) {
					  startrowGEO = iscan * GEO_SCAN_LENGTH;
					  //System.out.println(">>> Checking scan: "+ (iscan+1)+ " in order to determine image dimensions");
					  
					  lats = config.getGEODoubles(latReader, startrowGEO, 0, GEO_SCAN_LENGTH, GEO_SCAN_WIDTH,
							  GEO_STRIDE, GEO_STRIDE, Boolean.FALSE, -999.0);
					  lons = config.getGEODoubles(lonReader, startrowGEO, 0, GEO_SCAN_LENGTH, GEO_SCAN_WIDTH,
							  GEO_STRIDE, GEO_STRIDE, Boolean.FALSE, -999.0);
					  
					  GeoReader geoReader = new GeoReader(lats, lons);
					  geoReader = config.matchSDS_GEODim(geoReader, dataReader,
								  latReader, lonReader);
					  lats=geoReader.getLats();
					  lons=geoReader.getLons();
					  int rows = lats.length;
					  int cols = lats[0].length;
					  for (int i=0;i<rows;i++)
					  {
						  xxyy2=latlon2xy_km(lons[i],lats[i]);
						  for(int j=0;j<cols;j++)
						  {
							  
							  if( Util.inRange(-90.0, lats[i][j], 90.0) && Util.inRange(-180.0, lons[i][j], 180.0))
							  {
															  
								  if(flag==1){
									  if(xxyy2[j][0]<xMin_m) xMin_m=xxyy2[j][0];
									  if(xxyy2[j][0]>xMax_m) xMax_m=xxyy2[j][0];
									  if(xxyy2[j][1]<yMin_m) yMin_m=xxyy2[j][1];
									  if(xxyy2[j][1]>yMax_m) yMax_m=xxyy2[j][1];
								  }
								  else
								  {
									  xMin_m=xxyy2[j][0];
									  xMax_m=xxyy2[j][0];
									  yMin_m=xxyy2[j][1];
									  yMax_m=xxyy2[j][1];
									  flag=1;
								  }
							  }
						  }
					  }
					  //System.out.println("xMin_m="+xMin_m+" xMax_m="+xMax_m+" yMin_m="+yMin_m+" yMax_m="+yMax_m);  
				  }

				  System.out.println(">>> resolution in km="+resolution_km);
				  width=(int) Math.round((xMax_m-xMin_m)/resolution_km);
				  height=(int) Math.round((yMax_m-yMin_m)/resolution_km);
				  System.out.println(">>> xMin_m="+xMin_m+" xMax_m="+xMax_m+" yMin_m="+yMin_m+" yMax_m="+yMax_m+ " width="+width+"  height="+height);
				  minLatitude = -90.0;
				  maxLatitude = 90.0;
				  minLongitude = -180.0;
				  maxLongitude = 180.0;
				  distFrom180=0.0;
				  CrossOver=Boolean.FALSE;
			  }
			  config.setGeotiffTags_PROJ4(xMin_m,yMax_m);
			  break;
		}
		/*double xytest[]=latlon2xy_km(10.0,0.0);
		System.out.println("test="+xytest[0]+","+xytest[1]);
		System.exit(0);*/
	}
	
	public void initproj(SDSReader latReader, SDSReader lonReader, SDSReader dataReader) throws Exception {
		
		String srcProj,destProj;
		srcProj="+proj=latlong +datum=WGS84";
		Proj4 testProjection;
		Configuration config=Configuration.getInstance();
		System.out.println(config.getLat_0()+","+config.getLon_0());
		if(config.getLat_0()==-999.0 && config.getLon_0()==-999 && config.getProjection()!=Configuration.GEOGRAPHIC)
		{
			//Find a center of projection within the image
			
			if (config.getisSubset()) { /* The four corners have been specified by the user*/
				/*** This section is never invoked for any projection other than Geographic for now.
				   * For Geographic lat0 and lon0 parameters are not used at all ***/
				/*config.setLat_0((config.getminLat()+config.getmaxLat())/2.0);
				config.setLon_0((config.getminLon()+config.getmaxLon())/2.0);*/
				

			}
			else{
				int startrowGEO;
				double[][] lats, lons;
				int GEO_SCAN_LENGTH  =  config.getGEORowsPerScan();
				int GEO_SCAN_WIDTH   =  latReader.getWidth();
				int GEO_STRIDE       =  1;
				int noScans = latReader.getHeight() / config.getGEORowsPerScan();
				//System.out.println("Getting GeoRowsPerScan as "+config.getGEORowsPerScan()+" latreader ht="+latReader.getHeight());
				Boolean foundCtr=Boolean.FALSE;
				int midScan=Math.round(noScans/2);
				for (int iscan = midScan; iscan < noScans; iscan=iscan+1) {
					if(foundCtr) break;
					startrowGEO = iscan * GEO_SCAN_LENGTH;
					System.out.println(">>> Checking scan: "+ (iscan+1)+ " in order to determine image center");

					lats = config.getGEODoubles(latReader, startrowGEO, 0, GEO_SCAN_LENGTH, GEO_SCAN_WIDTH,
							GEO_STRIDE, GEO_STRIDE, Boolean.FALSE, -999.0);
					lons = config.getGEODoubles(lonReader, startrowGEO, 0, GEO_SCAN_LENGTH, GEO_SCAN_WIDTH,
							GEO_STRIDE, GEO_STRIDE, Boolean.FALSE, -999.0);

					GeoReader geoReader = new GeoReader(lats, lons);
					geoReader = config.matchSDS_GEODim(geoReader, dataReader,
							latReader, lonReader);
					lats=geoReader.getLats();
					lons=geoReader.getLons();
					int rows = lats.length;
					int cols = lats[0].length;
					
					int startcol=cols/2-4>0?cols/2-4:0;					
					int endcol=cols/2+4<cols?cols/2+4:cols;
					
					for (int i=0;i<rows;i++)
						for(int j=startcol;j<endcol;j++)
						{
							if (foundCtr) break;
							if(lats[i][j]!=-999 && lons[i][j]!=-999)
							{
								config.setLat_0(lats[i][j]);
								config.setLon_0(lons[i][j]);
								System.out.println("Using lat0 lon0 as:"+lats[i][j]+","+lons[i][j]);
								//System.out.println("Position of center in input product (i,j):"+iscan+","+GEO_SCAN_LENGTH+","+i+":"+j);
								foundCtr=Boolean.TRUE;
								break;
							}

						}
				}

			}


		}
			  

			
		
		switch (config.getProjection()){
		  case Configuration.GEOGRAPHIC:
			  break;
		  case Configuration.NOPROJECTION:
			  break;
		  case Configuration.STEREOGRAPHIC:
			  destProj="+proj=stere +lat_0="+config.getLat_0()+" +lon_0="+config.getLon_0()+" +datum=WGS84";
			  System.out.println("Calling Proj4 "+srcProj+" "+destProj);
			  testProjection = new Proj4(srcProj,destProj);				
		      config.setTestProjection(testProjection);
			  break;
		  case Configuration.TMERCATOR:
			  destProj="+proj=tmerc +lat_0="+config.getLat_0()+" +lon_0="+config.getLon_0()+" +datum=WGS84";
			  System.out.println("Calling Proj4 "+srcProj+" "+destProj);
			  testProjection = new Proj4(srcProj,destProj);				
		      config.setTestProjection(testProjection);
			  break;
		  case Configuration.MERCATOR:
			  destProj="+proj=merc +lat_0="+config.getLat_0()+" +lon_0="+config.getLon_0()+" +datum=WGS84";
			  System.out.println("Calling Proj4 "+srcProj+" "+destProj);
			  testProjection = new Proj4(srcProj,destProj);				
		      config.setTestProjection(testProjection);
			  break;
		  case Configuration.SINUSOIDAL:
			  destProj="+proj=sinu +lon_0="+config.getLon_0()+" +datum=WGS84";
			  System.out.println("Calling Proj4 "+srcProj+" "+destProj);
			  testProjection = new Proj4(srcProj,destProj);				
		      config.setTestProjection(testProjection);
			  break;	  
		  case Configuration.LAZIMUTHAL:
			  destProj="+proj=laea +lat_0="+config.getLat_0()+" +lon_0="+config.getLon_0()+" +datum=WGS84";
			  System.out.println("Calling Proj4 "+srcProj+" "+destProj);
			  testProjection = new Proj4(srcProj,destProj);				
		      config.setTestProjection(testProjection);
			  break;
		  
		}	
	

		return;

	}

	
	/**
	 * @return the crossOver
	 */
	public Boolean getCrossOver() {
		return CrossOver;
	}
	
	
	
	/**
	 * @return the distFrom180
	 */
	public double getDistFrom180() {
		return distFrom180;
	}







	/**
	 * @return the minLatitude
	 */
	public double getMinLatitude() {
		return minLatitude;
	}







	/**
	 * @return the minLongitude
	 */
	public double getMinLongitude() {
		return minLongitude;
	}







	/**
	 * @return the maxLatitude
	 */
	public double getMaxLatitude() {
		return maxLatitude;
	}







	/**
	 * @return the maxLongitude
	 */
	public double getMaxLongitude() {
		return maxLongitude;
	}







	/****************************************************************************
	 * createImage.
	 ****************************************************************************/
	private BufferedImage createImage(String oType, ValueMapper valueMapper,
			String oFmt) throws Exception {
		if (oType.equals("drgb")) {
			mapper = new DRGBmapper(valueMapper);
			return new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB_PRE);
		}
		if (oType.equals("argb")) {
			mapper = new ARGBmapper(valueMapper);
			if (oFmt.equals("jpeg"))
				return new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
			else
			   /*return new BufferedImage(width, height,
						BufferedImage.TYPE_INT_ARGB);*/
			   return new BufferedImage(width, height,
					  BufferedImage.TYPE_INT_RGB);

		}
		if (oType.equals("u16gs")) {
			mapper = new U16GSmapper(valueMapper);
			return new BufferedImage(width, height,
					BufferedImage.TYPE_USHORT_GRAY);
		}
		if (oType.equals("u8cm")) {
			mapper = new U8CMmapper(valueMapper);
			byte[][] colorMap = valueMapper.getColorMap8();
			IndexColorModel model = new IndexColorModel(8, 256, colorMap[0],
					colorMap[1], colorMap[2]);
			return new BufferedImage(width, height,
					BufferedImage.TYPE_BYTE_INDEXED, model);
		}
		if (oType.equals("u16cm")) {
			mapper = new U16CMmapper(valueMapper);
			byte[][] colorMap = valueMapper.getColorMap16();
			IndexColorModel model = new IndexColorModel(16, 65536, colorMap[0],
					colorMap[1], colorMap[2]/* ,valueMapper.getNoData() */);
			WritableRaster raster = Raster.createBandedRaster(
					DataBuffer.TYPE_USHORT, width, height, 1, null);
			return new BufferedImage(model, raster, false, new Hashtable());
		}
		throw new Exception("unknown output type: " + oType);
	}

	/****************************************************************************
	 * setScaler.
	 ****************************************************************************/
	public void setScalerGeoReader(ValueMapper valueMapper,
			GeoReader geoReader, String oType) throws Exception {

		this.geoReader = geoReader;
		if (oType.equals("drgb")) {
			mapper = new DRGBmapper(valueMapper);

		} else if (oType.equals("argb")) {
			mapper = new ARGBmapper(valueMapper);

		} else if (oType.equals("u16gs")) {
			mapper = new U16GSmapper(valueMapper);

		} else if (oType.equals("u8cm")) {
			mapper = new U8CMmapper(valueMapper);
			byte[][] colorMap = valueMapper.getColorMap8();
			new IndexColorModel(8, 256, colorMap[0], colorMap[1], colorMap[2]);

		} else if (oType.equals("u16cm")) {
			mapper = new U16CMmapper(valueMapper);
			byte[][] colorMap = valueMapper.getColorMap16();
			new IndexColorModel(16, 65536, colorMap[0], colorMap[1],
					colorMap[2]/* ,valueMapper.getNoData() */);
			Raster.createBandedRaster(DataBuffer.TYPE_USHORT, width, height, 1,
					null);

		} else
			throw new Exception("unknown output type: " + oType);
	}

	/****************************************************************************
	 * getGeoReader.
	 ****************************************************************************/
	public GeoReader getGeoReader() {
		return geoReader;
	}

	/****************************************************************************
	 * getImage.
	 ****************************************************************************/
	public BufferedImage getImage() {
		return image;
	}

	/****************************************************************************
	 * getLatScale.
	 ****************************************************************************/
	public double getLatScale() {
		return latScale;
	}

	/****************************************************************************
	 * getLonScale.
	 ****************************************************************************/
	public double getLonScale() {
		return lonScale;
	}

	public int getImageHeight() {
		return height;
	}

	public int getImageWidth() {
		return width;
	}

	
		
	public static double getMinLongitudePseudo() {
		return minLongitudePseudo;
	}

	public static void setMinLongitudePseudo(double minLongitudePseudo) {
		ImageCreator.minLongitudePseudo = minLongitudePseudo;
	}

	public static double getMaxLongitudePseudo() {
		return maxLongitudePseudo;
	}

	public static void setMaxLongitudePseudo(double maxLongitudePseudo) {
		ImageCreator.maxLongitudePseudo = maxLongitudePseudo;
	}

	public void fillScanBox() throws Exception {
		
		Configuration config = Configuration.getInstance();
		Boolean isSubset= config.getisSubset();
		String cfgType = config.getconfigType();
		
		double[][] lats = geoReader.getLats();
		double[][] lons = geoReader.getLons();
		
		int yCount = lats.length;
		int xCount = lats[0].length;		
		double minLat = minLatitude;
		double minLon = minLongitude;
		double maxLat = maxLatitude;
		double maxLon = maxLongitude;
		//Adjust minLon and maxLon if CrossOver is true
		if(getCrossOver() && config.getProjection()==Configuration.GEOGRAPHIC){
			minLon=getMinLongitudePseudo();
			maxLon=getMaxLongitudePseudo();
		}
		
		System.out.println("In Scanbox lat range"+minLat+","+maxLat+" lon Range (real or pseudo)="+minLon+","+maxLon);
		
		int i = 0;
		int count = xCount + (yCount - 1) + (xCount - 1) + (yCount - 1);
		int[] xxs = new int[count];
		int[] yys = new int[count];
		// System.out.println(i);
		double[] latarray_horiz = new double[xCount];
		double[] lonarray_horiz = new double[xCount];
		double[] latarray_vert = new double[yCount - 1];
		double[] lonarray_vert = new double[yCount - 1];
		


		// find a valid top edge
		int foundedge = 0;
		int latvalid, lonvalid;

		int ytop = -1;
		while (foundedge == 0 && ytop < yCount - 1) {
			// initialize the edge
			ytop++;
			// System.out.println("ytop="+ytop);
			latarray_horiz = lats[ytop];
			lonarray_horiz = lons[ytop];
			if (isSubset) {
				latvalid = checkedge_subset(latarray_horiz, lonarray_horiz,
						xCount, minLat, maxLat, minLon, maxLon, -90, 90, -180,
						180);
				lonvalid = 1;
			} else {
				latvalid = checkedge(latarray_horiz, xCount, -90, 90);
				lonvalid = checkedge(lonarray_horiz, xCount, -180, 180);
			}
			if (latvalid == 1 && lonvalid == 1)
				foundedge = 1;
		}

		// if not found edge what happens rare case
		// System.out.println("ytop*="+ytop+" foundedge="+foundedge);
		if (foundedge == 1)
			for (int x = 0, y = ytop; x < xCount; x++) {
				if (Util.inRange(minLat, lats[y][x], maxLat)
						&& Util.inRange(minLon, lons[y][x], maxLon)) {
					int[] xxyy = latlon2xy(lons[y][x],lats[y][x],Boolean.TRUE);
					xxs[i] = xxyy[0];
					yys[i] = xxyy[1];
					// System.out.println(lats[y][x]+","+lons[y][x]+"::"+xxs[i]+","+yys[i]);
					i++;					
				}
			}

		foundedge = 0;
		int xright = xCount;
		while (foundedge == 0 && xright > 0) {
			// initialize the edge
			xright--;
			// System.out.println("Checking xright="+xright);
			for (int k = 0; k < yCount - 1; k++) {
				// System.out.println("k="+k);
				latarray_vert[k] = lats[k][xright];
				lonarray_vert[k] = lons[k][xright];
			}
			// latvalid=checkedge(latarray_vert,yCount,-90,90);
			// lonvalid=checkedge(lonarray_vert,yCount,-181,180);
			if (isSubset) {
				latvalid = checkedge_subset(latarray_vert, lonarray_vert, 
						yCount - 1, minLat, maxLat, minLon, maxLon, -90, 90,
						-180, 180);
				lonvalid = 1;
			} else {
				latvalid = checkedge(latarray_vert, yCount - 1, -90, 90);
				lonvalid = checkedge(lonarray_vert, yCount - 1, -180, 180);
			}
			if (latvalid == 1 && lonvalid == 1)
				foundedge = 1;
		}
		// System.out.println("xright*="+xright+" foundedge="+foundedge);

		if (foundedge == 1)
			// for (int x = xCount-1, y = 1; y < yCount; y++) {
			for (int x = xright, y = 1; y < yCount; y++) {
				// System.out.println("Check for adding"+y+":"+lats[y][x]+","+lons[y][x]);
				if (Util.inRange(minLat, lats[y][x], maxLat)
						&& Util.inRange(minLon, lons[y][x], maxLon)) {
					int[] xxyy = latlon2xy(lons[y][x],lats[y][x],Boolean.TRUE);
					xxs[i] = xxyy[0];
					yys[i] = xxyy[1];
					// System.out.println(lats[y][x]+","+lons[y][x]+"::"+xxs[i]+","+yys[i]);
					i++;
				}
			}

		foundedge = 0;
		int ybottom = yCount;
		while (foundedge == 0 && ybottom > 0) {
			// initialize the edge
			ybottom--;
			// System.out.println("ybottom="+ybottom);
			latarray_horiz = lats[ybottom];
			lonarray_horiz = lons[ybottom];
			// latvalid=checkedge(latarray_horiz,xCount,-90,90);
			// lonvalid=checkedge(lonarray_horiz,xCount,-181,180);
			if (isSubset) {
				latvalid = checkedge_subset(latarray_horiz, lonarray_horiz, 
						xCount, minLat, maxLat, minLon, maxLon, -90, 90, -180,
						180);
				lonvalid = 1;
			} else {
				latvalid = checkedge(latarray_horiz, xCount, -90, 90);
				lonvalid = checkedge(lonarray_horiz, xCount, -180, 180);
			}
			if (latvalid == 1 && lonvalid == 1)
				foundedge = 1;
		}
		// System.out.println("ybottom*="+ybottom+" foundedge="+foundedge);

		if (foundedge == 1)
			for (int x = xCount - 2, y = ybottom; x >= 0; x--) {
				if (Util.inRange(minLat, lats[y][x], maxLat)
						&& Util.inRange(minLon, lons[y][x], maxLon)) {
					int[] xxyy = latlon2xy(lons[y][x],lats[y][x],Boolean.TRUE);
					xxs[i] = xxyy[0];
					yys[i] = xxyy[1];
					// System.out.println(lats[y][x]+","+lons[y][x]+"::"+xxs[i]+","+yys[i]);
					i++;
				}
			}

		foundedge = 0;
		int xleft = -1;
		while (foundedge == 0 && xleft < xCount - 1) {
			// initialize the edge
			xleft++;
			// System.out.println("Checking xleft="+xleft);
			// if(xleft>1080 && xleft<=1086)
			// {
			// System.out.println("\nChecking xleft="+xleft);
			for (int k = 0; k < yCount - 1; k++) {
				// System.out.println("k="+k);
				// if(k==399)
				// System.out.println("Original Lat:"+lats[k][xleft]);
				latarray_vert[k] = lats[k][xleft];
				lonarray_vert[k] = lons[k][xleft];
			}
			// System.out.println("Before checkedge:399:"+latarray_vert[399]);
			// latvalid=checkedge(latarray_vert,yCount,-90,90);
			// lonvalid=checkedge(lonarray_vert,yCount,-181,180);
			if (isSubset) {
				latvalid = checkedge_subset(latarray_vert, lonarray_vert,
						yCount - 1, minLat, maxLat, minLon, maxLon, -90, 90,
						-180, 180);
				lonvalid = 1;
			} else {
				latvalid = checkedge(latarray_vert, yCount - 1, -90, 90);
				lonvalid = checkedge(lonarray_vert, yCount - 1, -180, 180);
			}
			if (latvalid == 1 && lonvalid == 1)
				foundedge = 1;
			// }
		}
		// System.out.println("xleft*="+xleft+" foundedge="+foundedge);

		if (foundedge == 1)
			for (int x = xleft, y = yCount - 2; y >= 0; y--) {
				if (Util.inRange(minLat, lats[y][x], maxLat)
						&& Util.inRange(minLon, lons[y][x], maxLon)) {
					int[] xxyy = latlon2xy(lons[y][x],lats[y][x],Boolean.TRUE);
					xxs[i] = xxyy[0];
					yys[i] = xxyy[1];
					// System.out.println(lats[y][x]+","+lons[y][x]+"::"+xxs[i]+","+yys[i]);
					i++;
				}
			}
		
		// Split the polygon if needed.
		//wrapping happens only in x direction
		int[] xxs_poly1=new int[count];
		int[] xxs_poly2=new int[count];
		int[] yys_poly1=new int[count];
		int[] yys_poly2=new int[count];
		
		int poly1_n=1;
		int poly2_n=0;
		Boolean poly1=Boolean.TRUE;
		xxs_poly1[0]=xxs[0];
		yys_poly1[0]=yys[0];
		for (int n=0;n<i-1;n++)
		{
			if(Math.abs((double)(xxs[n]-xxs[n+1]))>0.9*width)
			{
				System.out.println("Swath wrapping detected "+xxs[n]+","+xxs[n+1]);
				if(poly1)
					poly1=Boolean.FALSE;
				else
					poly1=Boolean.TRUE;
			}
			if(poly1)
			{
				xxs_poly1[poly1_n]=xxs[n+1];
				yys_poly1[poly1_n]=yys[n+1];
				poly1_n++;
			}
			else
			{
				xxs_poly2[poly2_n]=xxs[n+1];
				yys_poly2[poly2_n]=yys[n+1];
				poly2_n++;
			}
		}

		if(cfgType.equals("singleband")) 
		{		
			Graphics g = mask.getGraphics();		
			g.setColor(HOLE_COLOR);
			if (i > 3)
				g.fillPolygon(xxs, yys, i);
			// Draw non-swath area with no-data value.
			for (int yy = 0; yy < height; yy++) {
				for (int xx = 0; xx < width; xx++) {
					if (mask.getRGB(xx, yy) == NON_SWATH_RGB) {
						image.setRGB(xx, yy, mapper.getRGB());
					}
				}
			}
		}
		else {
			Graphics g = image.getGraphics();
			g.setColor(MASK_HOLE_COLOR);//

			if (poly1_n > 3)
				g.fillPolygon(xxs_poly1, yys_poly1, poly1_n);
			if (poly2_n > 3)
				g.fillPolygon(xxs_poly2, yys_poly2, poly2_n);
			
			//if (i > 3)
			//	g.fillPolygon(xxs, yys, i);

		}
		
		

	}
	
public void fillScanBox_STEREO() throws Exception {
		
		Configuration config = Configuration.getInstance();
		Boolean isSubset= config.getisSubset();
		String cfgType = config.getconfigType();
		
		double[][] lats = geoReader.getLats();
		double[][] lons = geoReader.getLons();
		
		int yCount = lats.length;
		int xCount = lats[0].length;		
		
		
			
		int i = 0;
		int count = xCount + (yCount - 1) + (xCount - 1) + (yCount - 1);
		int[] xxs = new int[count];
		int[] yys = new int[count];
		// System.out.println(i);
		double[] latarray_horiz = new double[xCount];
		double[] lonarray_horiz = new double[xCount];
		int[][] xyarray_horiz=new int[xCount][2];
		double[] xarray_horiz=new double[xCount];
		double[] yarray_horiz=new double[xCount];
		
		double[] latarray_vert = new double[yCount - 1];
		double[] lonarray_vert = new double[yCount - 1];
		int[][] xyarray_vert=new int[yCount-1][2];
		double[] xarray_vert=new double[yCount-1];
		double[] yarray_vert=new double[yCount-1];
		
		
		


		// find a valid top edge
		int foundedge = 0;
		int latvalid, lonvalid;

		int ytop = -1;
		while (foundedge == 0 && ytop < yCount - 1) {
			// initialize the edge
			ytop++;
			// System.out.println("ytop="+ytop);
			latarray_horiz = lats[ytop];
			lonarray_horiz = lons[ytop];
			
			xyarray_horiz=latlon2xy(lonarray_horiz,latarray_horiz,Boolean.FALSE);
			for(int k=0;k<xCount;k++)
			{
			    xarray_horiz[k]=xyarray_horiz[k][0];
			    yarray_horiz[k]=xyarray_horiz[k][1];
			}
			
			latvalid = checkedge_subset(yarray_horiz,xarray_horiz,xCount,0,height,0,width,0,height,0,width);
			lonvalid=1;
			
			if (latvalid == 1 && lonvalid == 1)
				foundedge = 1;
		}

		// if not found edge what happens rare case
		// System.out.println("ytop*="+ytop+" foundedge="+foundedge);
		if (foundedge == 1)
			for (int k = 0, y = ytop; k < xCount; k++) {
				if (Util.inRange(0, xyarray_horiz[k][0], width)
						&& Util.inRange(0, xyarray_horiz[k][1], height)) {
					xxs[i] = xyarray_horiz[k][0];
					yys[i] = xyarray_horiz[k][1];
					//System.out.println("Top::"+xxs[i]+","+yys[i]);
					i++;					
				}
			}

		foundedge = 0;
		int xright = xCount;
		while (foundedge == 0 && xright > 0) {
			// initialize the edge
			xright--;
			// System.out.println("Checking xright="+xright);
			for (int k = 0; k < yCount - 1; k++) {
				// System.out.println("k="+k);
				latarray_vert[k] = lats[k][xright];
				lonarray_vert[k] = lons[k][xright];
			}
			xyarray_vert=latlon2xy(lonarray_vert,latarray_vert,Boolean.FALSE);
			for(int k=0;k<yCount-1;k++)
				{
				    xarray_vert[k]=xyarray_vert[k][0];
				    yarray_vert[k]=xyarray_vert[k][1];
				}
			latvalid = checkedge_subset(yarray_vert,xarray_vert,yCount-1,0,height,0,width,0,height,0,width);
			lonvalid = 1;
			if (latvalid == 1 && lonvalid == 1)
				foundedge = 1;
		}
		// System.out.println("xright*="+xright+" foundedge="+foundedge);

		if (foundedge == 1)
			// for (int x = xCount-1, y = 1; y < yCount; y++) {
			for (int x = xright, k = 1; k < yCount-1; k++) {
				// System.out.println("Check for adding"+y+":"+lats[y][x]+","+lons[y][x]);
				if (Util.inRange(0, xyarray_vert[k][0], width)
						&& Util.inRange(0, xyarray_vert[k][1], height)) {
					xxs[i] = xyarray_vert[k][0];
					yys[i] = xyarray_vert[k][1];
					//System.out.println("Right::"+xxs[i]+","+yys[i]);
					i++;					
				}
			}

		foundedge = 0;
		int ybottom = yCount;
		while (foundedge == 0 && ybottom > 0) {
			// initialize the edge
			ybottom--;
			// System.out.println("ybottom="+ybottom);
			latarray_horiz = lats[ybottom];
			lonarray_horiz = lons[ybottom];
			
            xyarray_horiz=latlon2xy(lonarray_horiz,latarray_horiz,Boolean.FALSE);
            for(int k=0;k<xCount;k++)
			{
			    xarray_horiz[k]=xyarray_horiz[k][0];
			    yarray_horiz[k]=xyarray_horiz[k][1];
			}
			
			latvalid = checkedge_subset(yarray_horiz,xarray_horiz,xCount,0,height,0,width,0,height,0,width);
			lonvalid = 1;
			if (latvalid == 1 && lonvalid == 1)
				foundedge = 1;
		}
		// System.out.println("ybottom*="+ybottom+" foundedge="+foundedge);

		if (foundedge == 1)
			for (int k = xCount - 1, y = ybottom; k >= 0; k--) { // was xCount-2
				if (Util.inRange(0, xyarray_horiz[k][0], width)
						&& Util.inRange(0, xyarray_horiz[k][1], height)) {
					xxs[i] = xyarray_horiz[k][0];
					yys[i] = xyarray_horiz[k][1];
					//System.out.println("Bottom::"+xxs[i]+","+yys[i]);
					i++;	
				}
			}

		foundedge = 0;
		int xleft = -1;
		while (foundedge == 0 && xleft < xCount - 1) {
			// initialize the edge
			xleft++;
			// System.out.println("Checking xleft="+xleft);
			// if(xleft>1080 && xleft<=1086)
			// {
			// System.out.println("\nChecking xleft="+xleft);
			for (int k = 0; k < yCount - 1; k++) {
				// System.out.println("k="+k);
				// if(k==399)
				// System.out.println("Original Lat:"+lats[k][xleft]);
				latarray_vert[k] = lats[k][xleft];
				lonarray_vert[k] = lons[k][xleft];
			}
			
            xyarray_vert=latlon2xy(lonarray_vert,latarray_vert,Boolean.FALSE);
            for(int k=0;k<yCount-1;k++)
			{
			    xarray_vert[k]=xyarray_vert[k][0];
			    yarray_vert[k]=xyarray_vert[k][1];
			}
			
			latvalid = checkedge_subset(yarray_vert,xarray_vert,yCount-1,0,height,0,width,0,height,0,width);
			lonvalid = 1;
			
			if (latvalid == 1 && lonvalid == 1)
				foundedge = 1;
			// }
		}
		// System.out.println("xleft*="+xleft+" foundedge="+foundedge);

		if (foundedge == 1)
			for (int x = xleft, k = yCount - 2; k >= 0; k--) {
				if (Util.inRange(0, xyarray_vert[k][0], width)
						&& Util.inRange(0, xyarray_vert[k][1], height)) {
					xxs[i] = xyarray_vert[k][0];
					yys[i] = xyarray_vert[k][1];
					//System.out.println("Left::"+xxs[i]+","+yys[i]);
					i++;					
				}
			}

		if(cfgType.equals("singleband")) 
		{		
			Graphics g = mask.getGraphics();		
			g.setColor(HOLE_COLOR);
			if (i > 3)
				g.fillPolygon(xxs, yys, i);
			// Draw non-swath area with no-data value.
			for (int yy = 0; yy < height; yy++) {
				for (int xx = 0; xx < width; xx++) {
					if (mask.getRGB(xx, yy) == NON_SWATH_RGB) {
						image.setRGB(xx, yy, mapper.getRGB());
					}
				}
			}
		}
		else {
			Graphics g = image.getGraphics();
			g.setColor(MASK_HOLE_COLOR);//

			if (i > 3)
				g.fillPolygon(xxs, yys, i);

		}
		
	

	}

	
	private double[][] latlon2xy_km(double lon[], double lat[]) throws Exception
	{
		Configuration config=Configuration.getInstance();
		double[][] xxyy = new double[lon.length][2];		
		double[][] testCoord = new double[lon.length][2];
		double[] testValues = new double[lon.length];
		
		for(int i=0;i<lon.length;i++)
		{
		   testCoord[i][0]=lon[i]; testCoord[i][1]=lat[i];testValues[i]=0;
		   xxyy[i][0]=-32767;
		   xxyy[i][1]=-32767;
		}
		ProjectionData dataTP = new ProjectionData(testCoord, testValues);
		config.getTestProjection().transform(dataTP, lon.length, 0);
		for(int i=0;i<lon.length;i++)
		{
		   xxyy[i][0]=dataTP.x[i]/1000.0; xxyy[i][1]=dataTP.y[i]/1000.0;
		}
		return xxyy;
		
	}
	
	private int[][] latlon2xy(double lon[], double lat[], boolean setToRange) throws Exception {
		int[][] xxyy_int = new int[lon.length][2];
		double[][] xxyy = new double[lon.length][2];		
		
		Configuration config=Configuration.getInstance();
		double resolution_km=config.getResolution()/1000.0;
		//System.out.println(config.getProjection());
		//System.out.println(minLongitude+" "+minLatitude+" "+latScale+" "+lonScale+":"+lon+","+lat);
		switch (config.getProjection()){
		  case Configuration.GEOGRAPHIC:
			  double minLat = minLatitude;
			  double minLon = minLongitude;
			  double maxLat = maxLatitude;
			  double maxLon = maxLongitude;
			  //Adjust minLon and maxLon if CrossOver is true
			  if(getCrossOver() && config.getProjection()==Configuration.GEOGRAPHIC){
					minLon=getMinLongitudePseudo();
					maxLon=getMaxLongitudePseudo();
			  }
			  for(int i=0;i<lon.length;i++)
			  {
				  if (Util.inRange(minLat, lat[i], maxLat)	&& Util.inRange(minLon, lon[i], maxLon)) {
					  if(setToRange){
						  xxyy_int[i][0] = Util.toRange(0, (int) Math.round((lon[i] - minLon)	/ lonScale), width - 1);
						  xxyy_int[i][1] = Util.toRange(0, (height - 1)- ((int) Math.round((lat[i] - minLat)/ latScale)), height - 1);
					  }
					  else {
						  xxyy_int[i][0] = (int) Math.round((lon[i] - minLon)	/ lonScale);
						  xxyy_int[i][1] = ((height - 1)-(int) Math.round((lat[i] - minLat)/ latScale));
					  }
				  }
				  else {
					  xxyy_int[i][0] = -99999;
					  xxyy_int[i][1] = -99999;
				  }
			  }
	          break;
		  case Configuration.MERCATOR:		
				  
		  case Configuration.TMERCATOR:
			 		  
		  case Configuration.SINUSOIDAL:
			  
		  case Configuration.LAZIMUTHAL:
			  
		  case Configuration.STEREOGRAPHIC:
			  xxyy=latlon2xy_km(lon,lat);
			  for(int i=0;i<lon.length;i++)
			  {
				  
				//System.out.println(lon[i]+","+lat[i]+","+xxyy[i][0]+","+xxyy[i][1]+" width="+width+"xMin_m= "+xMin_m);  
				  if (Util.inRange(xMin_m, xxyy[i][0], xMax_m)	&& Util.inRange(yMin_m, xxyy[i][1], yMax_m)) {
					  if(setToRange)
					  {
						  xxyy_int[i][0] = Util.toRange(0, (int) Math.round((xxyy[i][0]-xMin_m)	/ resolution_km), width - 1);
						  xxyy_int[i][1] = Util.toRange(0, (height - 1)- ((int) Math.round((xxyy[i][1]-yMin_m)/ resolution_km))
								  , height - 1);
					  }
					  else
					  {
						  xxyy_int[i][0] = (int) Math.round((xxyy[i][0]-xMin_m)	/ resolution_km);
						  xxyy_int[i][1] = ((height - 1)-(int) Math.round((xxyy[i][1]-yMin_m)/ resolution_km));
					  }
				  }
				  else {
					  xxyy_int[i][0] = -99999;
					  xxyy_int[i][1] = -99999;
				  }
 			    if(config.getProjection()==Configuration.MERCATOR)
 			    {
 			    	if(Math.abs(lat[i])==90.0) 
 			    	{
 						  xxyy_int[i][0]=-32767;xxyy_int[i][1]=-32767;
 			    	}
 			    } 	
 			   if(config.getProjection()==Configuration.TMERCATOR)
			    {
 				  if(Math.abs(lon[i]-config.getLon_0())>=90.0)
			    	{
						  xxyy_int[i][0]=-32767;xxyy_int[i][1]=-32767;
			    	}
			    } 	
			  }
	          //System.out.println(xxyy[0]+","+xxyy[1]+"::"+xxyy_int[0]+","+xxyy_int[1]);
			  
		}
		return xxyy_int;
	}
	
	private double[] latlon2xy_km(double lon, double lat) throws Exception
	{
		Configuration config=Configuration.getInstance();
		double[] xxyy = new double[]{-32767.0,-32767.0};
		double[][] testCoord = new double[1][2];
		double[] testValues = new double[1];
		testCoord[0][0]=lon; testCoord[0][1]=lat;testValues[0]=0;
		ProjectionData dataTP = new ProjectionData(testCoord, testValues);
		//config.getTestProjection().transform(dataTP, 1, 1);
		config.getTestProjection().transform(dataTP, 1, 0);
		xxyy[0]=dataTP.x[0]/1000.0; xxyy[1]=dataTP.y[0]/1000.0;
		return xxyy;
		
	}

	private int[] latlon2xy(double lon, double lat, boolean setToRange) throws Exception {
		int[] xxyy_int = new int[2];
		double[] xxyy = new double[2];		
		
		Configuration config=Configuration.getInstance();
		double resolution_km=config.getResolution()/1000.0;
		//System.out.println(config.getProjection());
		//System.out.println(minLongitude+" "+minLatitude+" "+latScale+" "+lonScale+":"+lon+","+lat);
		switch (config.getProjection()){
		  case Configuration.GEOGRAPHIC:
			  double minLat = minLatitude;
			  double minLon = minLongitude;
			  double maxLat = maxLatitude;
			  double maxLon = maxLongitude;
			  //Adjust minLon and maxLon if CrossOver is true
			  if(getCrossOver() ){
					minLon=getMinLongitudePseudo();
					maxLon=getMaxLongitudePseudo();
			  }			  	
			  if(setToRange){				  
			  xxyy_int[0] = Util.toRange(0, (int) Math.round((lon - minLon)	/ lonScale), width - 1);
	          xxyy_int[1] = Util.toRange(0, (height - 1)- ((int) Math.round((lat - minLat)/ latScale)), height - 1);
			  }
			  else {
				  xxyy_int[0] = (int) Math.round((lon - minLon)	/ lonScale);
		          xxyy_int[1] = ((height - 1)-(int) Math.round((lat - minLat)/ latScale));
			  }
	          break;
		  case Configuration.MERCATOR:
			  if(Math.abs(lat)==90.0) {
				  xxyy_int[0]=-32767;xxyy_int[1]=-32767;
				  break;
			  }
				  
		  case Configuration.TMERCATOR:
			  if(Math.abs(lon-config.getLon_0())>=90.0) {
				  xxyy_int[0]=-32767;xxyy_int[1]=-32767;
				  break;
			  }				  
		  case Configuration.SINUSOIDAL:
		  case Configuration.LAZIMUTHAL:
		  case Configuration.STEREOGRAPHIC:
			  xxyy=latlon2xy_km(lon,lat);
			  /*xxyy_int[0] = Util.toRange(0, (int) Math.round((xxyy[0]-xMin_m)	/ config.getResolution()), width - 1);
	          xxyy_int[1] = Util.toRange(0, (height - 1)- ((int) Math.round((xxyy[1]-yMin_m)/ config.getResolution()))
	        		  , height - 1);*/
			  if(setToRange)
			  {
				  if(xxyy[0]>(double)Integer.MAX_VALUE || xxyy[1]>(double)Integer.MAX_VALUE || xxyy[0]<(double)Integer.MIN_VALUE || xxyy[1]<(double)Integer.MIN_VALUE){
					  xxyy_int[0]=-32767;xxyy_int[1]=-32767;

				  }
				  else{
					  xxyy_int[0] = Util.toRange(0, (int) Math.round((xxyy[0]-xMin_m)	/ resolution_km), width - 1);
					  xxyy_int[1] = Util.toRange(0, (height - 1)- ((int) Math.round((xxyy[1]-yMin_m)/ resolution_km))
							  , height - 1);
				  }
			  }
			  else{
				  if(xxyy[0]>(double)Integer.MAX_VALUE || xxyy[1]>(double)Integer.MAX_VALUE || xxyy[0]<(double)Integer.MIN_VALUE || xxyy[1]<(double)Integer.MIN_VALUE){
					  xxyy_int[0]=-32767;xxyy_int[1]=-32767;

				  }
				  else{
					  xxyy_int[0] = (int) Math.round((xxyy[0]-xMin_m)	/ resolution_km);
					  xxyy_int[1] = ((height - 1)-(int) Math.round((xxyy[1]-yMin_m)/ resolution_km));
				  }
			  }
	          //System.out.println(xxyy[0]+","+xxyy[1]+"::"+xxyy_int[0]+","+xxyy_int[1]);
			  
		}
		return xxyy_int;
	}



	public void noproject(int[][][] values, int startscan, int SCANROWS,
			int startrow) throws Exception {
		// Project data.

		double[][] lats = geoReader.getLats();
		double[][] lons = geoReader.getLons();
		double minLat = -90.0;
		double minLon = -180.0;
		double maxLat = 90.0;
		double maxLon = 180.0;
		int[] v = new int[values.length];
		int yCountvalues = values[0].length;
		int xCountvalues = values[0][0].length;
		System.out.println("yCountvalues=" + yCountvalues + " xCountvalues="
				+ xCountvalues);
		System.out.println("minLat=" + minLat + " maxLat=" + maxLat
				+ " minLon=" + minLon + " maxLon=" + maxLon);
		// System.out.println("latyvalues="+lats.length+" latxvalues="+lats[0].length);
		for (int y = startscan, yvalues = 0; y < startscan + SCANROWS; y++, yvalues++) {
			for (int x = 0; x < xCountvalues; x++) {
				// if(lats.length==110)
				// System.out.println("y="+y+" x="+x+ "yvalues="+yvalues);

				if (Util.inRange(minLat, lats[y][x], maxLat)
						&& Util.inRange(minLon, lons[y][x], maxLon)) {
					int xx = x;
					int yy = y + startrow;
					// System.out.println("yy="+yy+" xx="+xx+
					// "yvalues="+yvalues);
					for (int c = 0; c < v.length; c++) {
						v[c] = values[c][yvalues][x];
					}

					int color = mapper.getRGB(v);
					image.setRGB(xx, yy, color);
				}

			}
		}

	}

	/****************************************************************************
	 * project.
	 ****************************************************************************/
	public void project2(int[][][] values, Boolean fireFlag, int[] fireValues,
			int startscan, int SCANROWS) throws Exception {

		// Project data.
		Configuration config=Configuration.getInstance();
		String cfgType = config.getconfigType();
		double[][] lats = geoReader.getLats();
		double[][] lons = geoReader.getLons();
		double[] latrow=new double[values[0][0].length];
		double[] lonrow=new double[values[0][0].length];
		double minLat = minLatitude;
		double minLon = minLongitude;
		double maxLat = maxLatitude;
		double maxLon = maxLongitude;
		
		//Adjust minLon and maxLon if CrossOver is true
		if(getCrossOver() && config.getProjection()==Configuration.GEOGRAPHIC){
			minLon=getMinLongitudePseudo();
			maxLon=getMaxLongitudePseudo();
		}
		
		int[] v = new int[values.length];
		//int[] xxyy = new int[2];
		int[][] xxyy = new int[values[0][0].length][2];
		int yCountvalues=values[0].length;
		int xCountvalues = values[0][0].length;
		System.out.println("Projecting...");
		// System.out.println("yCountvalues="+yCountvalues+" xCountvalues="+xCountvalues);
		// System.out.println("latyvalues="+lats.length+" latxvalues="+lats[0].length);
		for (int y = startscan, yvalues = 0; y < startscan + SCANROWS; y++, yvalues++) 
		{
			xxyy = latlon2xy(lons[y],lats[y],Boolean.TRUE);
			
			for (int x = 0; x < xCountvalues; x++) 
			{
				//System.out.println(xxyy[y][0]+","+xxyy[y][1]);
				// if(lats.length==110)
				// System.out.println("y="+y+" x="+x+ "yvalues="+yvalues);
				//if (Util.inRange(minLat, lats[y][x], maxLat)
					//	&& Util.inRange(minLon, lons[y][x], maxLon)) {
					//if (Util.inRange(minLat, lats[y][x], maxLat)
					//		&& Util.inRange(minLon, lons[y][x], maxLon)) {	
					//xxyy = latlon2xy(lons[y][x],lats[y][x],Boolean.TRUE);
					//if(xxyy[0]< 0 || xxyy[1]<0)
					if(xxyy[x][0]< 0 || xxyy[x][1]<0)
						continue;
					int xx=xxyy[x][0];
					int yy=xxyy[x][1];
					/*int xx = (int) Math.round((lons[y][x] - minLon) / lonScale);
					int yy = (height - 1)
							- ((int) Math.round((lats[y][x] - minLat)
									/ latScale));*/
					for (int c = 0; c < v.length; c++) {
						v[c] = values[c][yvalues][x];
					}
                    
					int color = mapper.getRGB(v);
					if(cfgType.equals("singleband"))
					{
						if(!config.getHasDataHoles() || v[0]!=config.getDataHoleValue()){
							if (!fireFlag || mask.getRGB(xx, yy) != FIRE_RGB) {
								image.setRGB(xx, yy, color);
								mask.setRGB(xx, yy, DATA_RGB);
							}


							if (fireFlag) {
								for (int i = 0; i < fireValues.length; i++)
									if (v[0] == fireValues[i]) {
										check_nfires++;
										mask.setRGB(xx, yy, FIRE_RGB);
										break;
									}
							}
						}
						
						
					}
					else
					{
						if(!config.getHasDataHoles() || (v[0]!=config.getDataHoleValue() && v[1]!=config.getDataHoleValue() && v[2]!=config.getDataHoleValue()))
						{
						    image.setRGB(xx, yy, color);
						}
						
					}



				//}

			}
		}

		System.out.println("Projection Complete.");

	}

	public void overlayFirePixels(Vector<Double> latloc, Vector<Double> lonloc,
			Vector<Integer> confidence) throws Exception {
		int temp_xx, temp_yy;
		int[] xxyy = new int[2];
		System.out.println("The size of fire vector are: " + latloc.size());
		
		double minLat = minLatitude;
		double minLon = minLongitude;
		double maxLat = maxLatitude;
		double maxLon = maxLongitude;
		Configuration config=Configuration.getInstance();
		//Adjust minLon and maxLon if CrossOver is true
		if(getCrossOver() && config.getProjection()==Configuration.GEOGRAPHIC){
			minLon=getMinLongitudePseudo();
			maxLon=getMaxLongitudePseudo();
		}
		
		Graphics g = image.getGraphics();
		for (int i = 0; i < latloc.size(); i++) {
			double firelat = latloc.elementAt(i);
			double firelon = lonloc.elementAt(i);
			int conf = confidence.elementAt(i);
			//System.out.println(i+"-"+conf);
			if (conf >= 0)
			{
				/*if (Util.inRange(minLatitude, firelat, maxLatitude)
						&& Util.inRange(minLongitude, firelon, maxLongitude)) {*/
				//if (Util.inRange(minLat, firelat, maxLat)
				//			&& Util.inRange(minLon, firelon, maxLon)) {
					xxyy = latlon2xy(firelon,firelat,Boolean.FALSE);
					int xx=xxyy[0];
					int yy=xxyy[1];
					if(xxyy[0]<0 || xxyy[1]<0 || xxyy[0]>=width || xxyy[1]>=height)
						continue;
					/*int xx = (int) Math.round((firelon - minLongitude)
							/ lonScale);
					int yy = (height - 1)
							- ((int) Math.round((firelat - minLatitude)
									/ latScale));*/
					
					
					if (conf <= 30)
						g.setColor(Color.YELLOW);
					else if (conf <= 80)
						g.setColor(Color.ORANGE);
					else
						g.setColor(Color.RED);
					
					temp_xx = Util.toRange(0, xx - 5, width - 1);
					temp_yy = Util.toRange(0, yy - 5, height - 1);
					g.drawOval(temp_xx, temp_yy, 10, 10);

				//}
			}
			// else
			// System.out.println("** "+firelat+" "+firelon+" "+min_Latitude+","+maxLatitude+":"+minLongitude+","+maxLongitude);
		}
	}
	

	
	
	public void rescale(int thumbHeight, int thumbWidth) {
		// BufferedImage
		// tmp=(BufferedImage)image.getScaledInstance(-1,ht,Image.SCALE_AREA_AVERAGING);

		BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		// graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);

		height = thumbImage.getHeight();
		width = thumbImage.getWidth();
		double latRange = geoReader.getMaxLat() - geoReader.getMinLat();
		double lonRange = geoReader.getMaxLon() - geoReader.getMinLon();
		latScale = latRange / (height - 1);
		lonScale = lonRange / (width - 1);
		image = thumbImage;
	}

	public void overlay(double minLon, double minLat, String filepath,
			Color color, Boolean CrossOver, double distFrom180) throws Exception {

		Graphics gimage = image.getGraphics();
		ShapeFiles shFile = new ShapeFiles();
		Configuration config=Configuration.getInstance();
		Vector states;
		//int temp_x, temp_y, temp_x2, temp_y2;
		int[] xxyy=new int[2];
		//int[] testxy=new int[2];
		
		// System.out.println("In overlay latscale="+latScale+" lonscale="+lonScale+" minLon="+minLon+" minLat="+minLat);

		/*testxy = latlon2xy(-76.85,38.99,Boolean.FALSE);
		System.out.println("Coord of proj origin="+testxy[0]+","+testxy[1]);
		testxy = latlon2xy(-166.91915893554688,65.13162994384766,Boolean.FALSE);*/

		System.out.println("overlaying "+filepath);
		states = shFile.readShapeFiles(filepath, CrossOver, distFrom180);
		// System.out.println("overlaying-2");
		int featureNumber = states.size();
		gimage.setColor(color);
		for (int ivec = 0; ivec < featureNumber; ivec++) {// for each PolyLine
			// System.out.println("at polyline no:"+i);
			PolyLine polyline = (PolyLine) states.elementAt(ivec);
			// System.out.println("Numparts="+polyline.NumParts);
			for (int jj = 0; jj < polyline.NumParts; jj++) { // for each part
				// System.out.println("at part:"+j);
				int startindex = polyline.Parts[jj];
				int stopindex = 0;
				if (jj == polyline.NumParts - 1)// last part
					stopindex = polyline.Points.length;
				else
					stopindex = polyline.Parts[jj + 1];
				for (int k = startindex; k < stopindex - 1; k++) {
					
					// System.out.println(polyline.Points[k].x+" "+polyline.Points[k].y+" "+polyline.Points[k
					// + 1].x+" "+polyline.Points[k + 1].y);
					// System.out.print("k="+k+" ");
					/*if(Util.inRange(168.354, polyline.Points[k].x, 173.386)	&& Util.inRange(-45.275, polyline.Points[k].y, -41.245))
					{
						 System.out.println("Found problem area");
						 System.out.println(polyline.Points[k].x+" "+polyline.Points[k].y+" "+polyline.Points[k+ 1].x+" "+polyline.Points[k + 1].y+"lat0/lon0"+config.getLon_0()+":"+config.getLat_0());
					}*/
					/*if(config.getProjection()!=Configuration.GEOGRAPHIC && (Math.abs(polyline.Points[k].x-config.getLon_0())>180.0 || Math.abs(polyline.Points[k+1].x-config.getLon_0())>180.0))
						continue;
					if(config.getProjection()!=Configuration.GEOGRAPHIC && (Math.abs(polyline.Points[k].y-config.getLat_0())>90.0 || Math.abs(polyline.Points[k+1].y-config.getLat_0())>90.0) )
						continue;*/
					if (!Util.inRange(-180.0, polyline.Points[k].x, 180.0)	|| !Util.inRange(-90.0, polyline.Points[k].y, 90.0) || !Util.inRange(-180.0, polyline.Points[k+1].x, 180.0)	|| !Util.inRange(-90.0, polyline.Points[k+1].y, 90.0))
						continue;
					
					xxyy = latlon2xy(polyline.Points[k].x,polyline.Points[k].y,Boolean.FALSE);
					if(xxyy[0]==-32767 && xxyy[1]==-32767)
						continue;
					//temp_x=xxyy[0];
					//temp_y=xxyy[1];
					Point P0=new Point(xxyy[0],xxyy[1]);
					
						
					xxyy = latlon2xy(polyline.Points[k+1].x,polyline.Points[k+1].y,Boolean.FALSE);
					if(xxyy[0]==-32767 && xxyy[1]==-32767)
						continue;
					//emp_x2=xxyy[0];
					//temp_y2=xxyy[1];
					Point P1=new Point(xxyy[0],xxyy[1]);
					/*if((temp_x==testxy[0] && temp_y==testxy[1]) || (temp_x2==testxy[0] && temp_y2==testxy[1]))
						System.out.println("Found faulting line :"+polyline.Points[k].x+","+polyline.Points[k].y+" :: "+polyline.Points[k+1].x+","+polyline.Points[k+1].y);*/
					/*temp_x = (int) Math.round((polyline.Points[k].x - minLon)
							/ lonScale);
					temp_y = (height - 1)
							- ((int) Math.round((polyline.Points[k].y - minLat)
									/ latScale));
					temp_x2 = (int) Math
							.round((polyline.Points[k + 1].x - minLon)
									/ lonScale);
					temp_y2 = (height - 1)
							- ((int) Math
									.round((polyline.Points[k + 1].y - minLat)
											/ latScale));*/
					//temp_x = Util.toRange(0, temp_x, width - 1);
					//temp_y = Util.toRange(0, temp_y, height - 1);
					//temp_x2 = Util.toRange(0, temp_x2, width - 1);
					//temp_y2 = Util.toRange(0, temp_y2, height - 1);
					//gimage.drawLine(temp_x, temp_y, temp_x2, temp_y2);
					//if(polyline.Points[k].x==180.0 && polyline.Points[k+1].x==180.0)
					//	  System.out.println("faulty :"+P0.x+","+P0.y+":"+P1.x+","+P1.y);
					
					
					if(Util.CohenSutherland2DClipper(P0,P1,height-1,0,0,width-1)) 
					{
						/*if(config.getProjection()==Configuration.GEOGRAPHIC)
							gimage.drawLine((int)P0.x,(int)P0.y, (int)P1.x,(int)P1.y);
						else			*/	
						//if(P0.x==0 && P1.x==width-1 || P0.x==width-1 && P1.x==0)
						//  System.out.println(polyline.Points[k].x+","+polyline.Points[k].y+":::"+polyline.Points[k+1].x+","+polyline.Points[k+1].y);
						//if(P0.x==P1.x && P0.x>=2499 && P0.x<=2501)
						//	  System.out.println(polyline.Points[k].x+","+polyline.Points[k].y+":::"+polyline.Points[k+1].x+","+polyline.Points[k+1].y+":"+P0.x+","+P0.y+":"+P1.x+","+P1.y);
						//else
													
						if(config.getProjection()!=Configuration.GEOGRAPHIC &&(Math.abs(P0.x-P1.x)==width-1 || Math.abs(P0.y-P1.y)==height-1))
						{
							System.out.println("Vector wraparound suspected- not overlaying");
							continue;
						}
						   gimage.drawLine((int)P0.x,(int)P0.y, (int)P1.x,(int)P1.y);
					}
					
					
					
				}
			}

		}
		//System.out.println("overlaying-complete");


	}

	
	/**
	 * This function overlays a Latitude/Longitude grid on the projected image and adds labels for the 
	 * Latitude, Longitude grids on the top/left edges. Note that it changes the dimensions of the final image
	 * because it add edges to allow the labels to be drawn
	 * 
	 * @param color color of the grids (they would be dashed lines)
	 * @param ImageEdgeWidth the width of the edge (in pixels) around the projected image. The edge is used to write the labels. Must be >=40. A good value is 40.0
	 * @param GridSpacing lat/lon lines will be drawn every 'GridSpacing'th degree. A good value is 10
	 * @param PlotInterval lat/lon segments will be drawn every 'PlotInterval'th degree. A good value is 0.2
	 * @param LabelFontSize Font size of the grid labels 
	 * @return void
	 * @throws Exception
	 */
	public void overlayLatLonGrid(Color color,int ImageEdgeWidth,int GridSpacing, double PlotInterval, int LabelFontSize) throws Exception {


		int xShift=ImageEdgeWidth-2;
		int yShift=ImageEdgeWidth-30;


		System.out.println("Overlaying Lat/Lon grids...");

		//A Configuration object is needed to obtain information on projections
		Configuration config=Configuration.getInstance();

		//Creating an instance of Graphics & Graphic2D for the image. Graphics2D is needed to be able to draw dashed lines
		Graphics gimage = image.getGraphics();			
		Graphics2D g2dimage = (Graphics2D) gimage.create();
		BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1}, 0);
		g2dimage.setStroke(dashed);


		//A vector to accumulate the locations of the latitude and logitude lines when they get closest to the left
		//and top edges respectively. In most cases, those points actually intersect the left and top edge. 
		//The lines will not intersect for example for a polar stereographic projection, where the latitude lines 
		// would be concentric circles
		Vector<Markers> markers=new Vector<Markers>();      
		// a place to hold the x,y locations of the projected lat,lon
		int[] xxyy=new int[2];		
		//set the color for the grid
		gimage.setColor(color);
		g2dimage.setColor(color);

		int distanceFromLeftEdge,distanceFromRightEdge,xMarkerLeft,yMarkerLeft,xMarkerRight,yMarkerRight;
		for (int lat = -90; lat <= 90.0; lat=lat+GridSpacing) {// for each latitude grid line 
			distanceFromLeftEdge=image.getWidth()-1; // setting it to the distance from the right edge
			distanceFromRightEdge=image.getWidth()-1; // setting it to the distance from the left edge
			xMarkerLeft=-1;yMarkerLeft=-1;xMarkerRight=-1; yMarkerRight=-1;
			for (double lon=-180; lon<=179.95; lon=lon+PlotInterval){ // draw a dashed line for each small segment 
				xxyy = latlon2xy(lon,lat,Boolean.FALSE);
				if(xxyy[0]==-32767 && xxyy[1]==-32767)
					continue;
				Point P0=new Point(xxyy[0],xxyy[1]);
				xxyy = latlon2xy(lon+0.2,lat,Boolean.FALSE);
				if(xxyy[0]==-32767 && xxyy[1]==-32767)
					continue;
				Point P1=new Point(xxyy[0],xxyy[1]);

				if(Util.CohenSutherland2DClipper(P0,P1,image.getHeight()-1,0,0,image.getWidth()-1)) //The line segment is clipped to fit within the image
				{
					//Checking for a potential error case when the line segment	spans the whole image width-wise or height-wise
					//The check is ok because the line-segments are expected to be very small and is not supposed to span the image
					if(config.getProjection()!=Configuration.GEOGRAPHIC &&(Math.abs(P0.x-P1.x)==image.getWidth()-1 || Math.abs(P0.y-P1.y)==image.getHeight()-1))
					{
						System.out.println("Vector wraparound suspected- not overlaying");
						continue;
					}
					//Draw the latitude segment
					g2dimage.drawLine((int)P0.x,(int)P0.y, (int)P1.x,(int)P1.y);

					//Check if either of the two ends of the segment is closer to the left edge than we have seen before
					if(P0.x<distanceFromLeftEdge){
						xMarkerLeft=(int)P0.x;
						yMarkerLeft=(int)P0.y;
						distanceFromLeftEdge=(int)P0.x;
					}
					if(P1.x<distanceFromLeftEdge){
						xMarkerLeft=(int)P1.x;
						yMarkerLeft=(int)P1.y;
						distanceFromLeftEdge=(int)P1.x;
					}
					//Check if either of the two ends of the segment is closer to the right edge than we have seen before
					if(image.getWidth()-P0.x<distanceFromRightEdge){
						xMarkerRight=(int)P0.x;
						yMarkerRight=(int)P0.y;
						distanceFromRightEdge=(int)(image.getWidth()-P0.x);
					}
					if(image.getWidth()-P1.x<distanceFromRightEdge){
						xMarkerRight=(int)P1.x;
						yMarkerRight=(int)P1.y;
						distanceFromRightEdge=(int)(image.getWidth()-P1.x);
					}
				}

			}
			if(Util.inRange(0, xMarkerLeft, image.getWidth()-1) && Util.inRange(0, yMarkerLeft, image.getHeight()-1) 
					&& Util.inRange(0, xMarkerRight, image.getWidth()-1) && Util.inRange(0, yMarkerRight, image.getHeight()-1)){ //Did we find a closest point that is within the image dimensions
				//Find out if we should use the left one or the right one
				if(xMarkerLeft==0){
					//System.out.println("Creating Marker for "+lat+" at "+xMarker+","+yMarker);
					//Add it the markers vector for later label drawing (once the edges have been created)
					markers.add(new Markers(xMarkerLeft,yMarkerLeft,lat,Boolean.TRUE));
				}else if (xMarkerRight==image.getWidth()-1){
					markers.add(new Markers(xMarkerRight,yMarkerRight,lat,Boolean.TRUE));
				}else if (yMarkerLeft==0 || yMarkerLeft==image.getHeight()-1){
					markers.add(new Markers(xMarkerLeft,yMarkerLeft,lat,Boolean.TRUE));
				}else if (yMarkerRight==0 || yMarkerRight==image.getHeight()-1){
					markers.add(new Markers(xMarkerRight,yMarkerRight,lat,Boolean.TRUE));
				}else
					markers.add(new Markers(xMarkerLeft,yMarkerLeft,lat,Boolean.TRUE));

			}
		}

		int distanceFromTop,distanceFromBottom,xMarkerTop,yMarkerTop,xMarkerBottom,yMarkerBottom;
		for (int lon = -180; lon <= 179.0; lon=lon+GridSpacing) {// for each longitude grid line
			distanceFromTop=image.getHeight()-1; // setting it to the distance from the bottom edge
			distanceFromBottom=image.getHeight()-1;
			xMarkerTop=-1;yMarkerTop=-1;xMarkerBottom=-1;yMarkerBottom=-1;
			for (double lat=-90.0; lat<=89.95; lat=lat+PlotInterval){ // draw a dashed line for each small segment
				xxyy = latlon2xy(lon,lat,Boolean.FALSE);
				if(xxyy[0]==-32767 && xxyy[1]==-32767)
					continue;
				Point P0=new Point(xxyy[0],xxyy[1]);

				xxyy = latlon2xy(lon,lat+0.2,Boolean.FALSE);
				if(xxyy[0]==-32767 && xxyy[1]==-32767)
					continue;
				Point P1=new Point(xxyy[0],xxyy[1]);

				if(Util.CohenSutherland2DClipper(P0,P1,image.getHeight()-1,0,0,image.getWidth()-1)) //The line segment is clipped to fit within the image
				{
					//Checking for a potential error case when the line segment	spans the whole image width-wise or height-wise
					//The check is ok because the line-segments are expected to be very small and is not supposed to span the image							
					if(config.getProjection()!=Configuration.GEOGRAPHIC &&(Math.abs(P0.x-P1.x)==image.getWidth()-1 || Math.abs(P0.y-P1.y)==image.getHeight()-1))
					{
						System.out.println("Vector wraparound suspected- not overlaying");
						continue;
					}
					//Draw the longitude segment
					g2dimage.drawLine((int)P0.x,(int)P0.y, (int)P1.x,(int)P1.y);
					//Check if either of the two ends of the segment is closer to the top edge than we have seen before
					if(P0.y<distanceFromTop){
						xMarkerTop=(int)P0.x;
						yMarkerTop=(int)P0.y;
						distanceFromTop=(int)P0.y;
					}
					if(P1.y<distanceFromTop){
						xMarkerTop=(int)P1.x;
						yMarkerTop=(int)P1.y;
						distanceFromTop=(int)P1.y;
					}
					if(image.getHeight()-P0.y<distanceFromBottom){
						xMarkerBottom=(int)P0.x;
						yMarkerBottom=(int)P0.y;
						distanceFromBottom=(int)(image.getHeight()-P0.y);
					}
					if(image.getHeight()-P1.y<distanceFromBottom){
						xMarkerBottom=(int)P1.x;
						yMarkerBottom=(int)P1.y;
						distanceFromBottom=(int)(image.getHeight()-P1.y);
					}  
				}


			}
			if(Util.inRange(0, xMarkerTop, image.getWidth()-1) && Util.inRange(0, yMarkerTop, image.getHeight()-1)
					&& Util.inRange(0, xMarkerBottom, image.getWidth()-1) && Util.inRange(0, yMarkerBottom, image.getHeight()-1)){//Did we find a closest point that is within the image dimensions
				//System.out.println(lon+":"+xMarkerTop+","+yMarkerTop+","+xMarkerBottom+","+yMarkerBottom);
				if(yMarkerTop==0){
					//System.out.println("Creating Marker for "+lon+" at "+xMarker+","+yMarker);
					//Add it the markers vector for later label drawing (once the edges have been created)
					markers.add(new Markers(xMarkerTop,yMarkerTop,lon,Boolean.FALSE));
				}else if (yMarkerBottom==image.getHeight()-1) {
					markers.add(new Markers(xMarkerBottom,yMarkerBottom,lon,Boolean.FALSE));
				}else if (xMarkerTop==0 || xMarkerTop==image.getWidth()-1) {
					markers.add(new Markers(xMarkerTop,yMarkerTop,lon,Boolean.FALSE));
				}else if (xMarkerBottom==0 || xMarkerBottom==image.getWidth()-1) {
					markers.add(new Markers(xMarkerBottom,yMarkerBottom,lon,Boolean.FALSE));
				}else
					markers.add(new Markers(xMarkerTop,yMarkerTop,lon,Boolean.FALSE));

			}
		}


		//Create a new image with adequate space for the edges of ImageEdgeWidth
		BufferedImage finalimg = new BufferedImage(image.getWidth()+(ImageEdgeWidth*2),image.getHeight()+(ImageEdgeWidth*2),BufferedImage.TYPE_INT_RGB);
		//Get the graphics object for the new image
		Graphics gfinalimage=finalimg.createGraphics();
		//The new image is white -- initially
		gfinalimage.setColor(Color.WHITE);
		gfinalimage.fillRect(0, 0, finalimg.getWidth(),finalimg.getHeight());
		// Now draw the projected image centered within the new bigger image (with edges)
		boolean primaryimagedrawn = finalimg.createGraphics().drawImage(image, ImageEdgeWidth, ImageEdgeWidth, null); 
		if(!primaryimagedrawn)
			System.out.println("Error in grid drawing"); 

		//Now draw the labels; the labels are black in Plan Dialog Font, size 12
		gfinalimage.setColor(Color.BLACK);
		gfinalimage.setFont(new Font(Font.DIALOG, Font.PLAIN, LabelFontSize));
		//get potential height and width of a label. This will be required for proper placement of backgrounds and avoid overlaps
		int labelwidth=gfinalimage.getFontMetrics().stringWidth("8880W"); //'880W could represent a label of maximum width'
		int labelheight=gfinalimage.getFontMetrics().getHeight();

		int nMarkers = markers.size();
		for (int ivec = 0; ivec < nMarkers; ivec++) {// for each marker
			Markers thismarker = (Markers) markers.elementAt(ivec);
			//System.out.println("*************************************************");
			//System.out.println("x="+thismarker.getX()+" y="+thismarker.getY()+" lat/lon="+thismarker.getLatlon());
			//Is it overlapping with any of the previous labels
			boolean overlap=Boolean.FALSE;
			for(int jvec=0;jvec<ivec;jvec++){
				Markers prevmarker = (Markers) markers.elementAt(jvec);
				//System.out.println("prevx="+prevmarker.getX()+" y="+prevmarker.getY()+" prev lat/lon="+prevmarker.getLatlon());
				//System.out.println("labelwidth="+labelwidth+"labelheight="+labelheight);
				if(prevmarker.isMarkerUsed() //check overlap if the label is actually used
						&& Util.inRange(thismarker.getX()-labelwidth, prevmarker.getX(),thismarker.getX()+labelwidth) 
						&& Util.inRange(thismarker.getY()-labelheight, prevmarker.getY(),thismarker.getY()+labelheight)){
					overlap=Boolean.TRUE;
					thismarker.setMarkerUsed(Boolean.FALSE); //mark this label as not drawn
					break;
				}					
			}
			if(!overlap) {//draw the label if there is no overlap with previous labels
				//When you draw the marker, note that the co-ordinates was for the original image. The new image is now within 
				// the bigger image with edges of width ImageEdgeWidth; in short there needs to be translation of +ImageWidth,+ImageWidth
				// Also there needs to be a xShift, yShift to allow ample space for writing the label
				//System.out.println("x="+thismarker.getX()+" y="+thismarker.getY()+" lat/lon="+thismarker.getLatlon());		
				if(thismarker.getX()==0) {//is this marker on the left edge
					gfinalimage.drawString(thismarker.getMarkerString().getIterator(), thismarker.getX()+ImageEdgeWidth-xShift, thismarker.getY()+ImageEdgeWidth);
				} else if(thismarker.getY()==0) {//top edge
					gfinalimage.drawString(thismarker.getMarkerString().getIterator(), thismarker.getX()+ImageEdgeWidth, thismarker.getY()+ImageEdgeWidth-yShift);				
				} else if (thismarker.getX()==image.getWidth()-1) {//right edge
					gfinalimage.drawString(thismarker.getMarkerString().getIterator(), thismarker.getX()+ImageEdgeWidth+2, thismarker.getY()+ImageEdgeWidth);
				} else if (thismarker.getY()==image.getHeight()-1) {//bottom edge
					gfinalimage.drawString(thismarker.getMarkerString().getIterator(), thismarker.getX()+ImageEdgeWidth, thismarker.getY()+ImageEdgeWidth+yShift+10);
				}else {//other lat/lon lines that do not intersect at the left or top edge
					//draw a white filled box on which to write - because if the background is black, the black text will be invisible
					int labelboxy=thismarker.getY()+ImageEdgeWidth-yShift-gfinalimage.getFontMetrics().getAscent();


					gfinalimage.setColor(Color.WHITE);
					gfinalimage.fillRect(thismarker.getX()+ImageEdgeWidth-xShift, labelboxy, labelwidth, labelheight);
					gfinalimage.setColor(Color.BLACK);
					gfinalimage.drawString(thismarker.getMarkerString().getIterator(), thismarker.getX()+ImageEdgeWidth-xShift, thismarker.getY()+ImageEdgeWidth-yShift);
				}
			}

		}
		// the bigger image (with edges) is now the new image
		image=finalimg;		
		System.out.println("Lat/Lon Grid overlaying Complete.");

	}


	
	/**
	 * This function concatenates a legend subimage at the bottom of the primary image.
	 * Do not call when you are creating non-colormapped images (for example true color images with 3 bands) 
	 * @param valueMapper The valuemapper 
	 * @param markers
	 * @param markerValues
	 * @param legend
	 * @return void
	 */
	public void concatLegend(ValueMapper valueMapper, Vector<Integer> markers,
			Vector<String> markerValues, String legend,int leght, int ImageEdgeWidth,int LabelFontSize) {
		
		//The legend cannot be less than 260 pixels (excluding the edges). That wouldn't be enough to fit the legend
		int MIN_LEGEND_WIDTH=260;
		int STARTY=5;//The y location to start drawing the legend within the legend image
		int COMPONENT_SEPARATION=20; //height separation between various elements arranged vertically
		
		mapper = new U8CMmapper(valueMapper); //get the valueMapper. It is needed to retrieve teh colormap
		byte[][] colorMap8 = valueMapper.getColorMap8(); // get the color map
		int red, green, blue;
		Color color;
		

		System.out.println("Concatenating legend...");
		//Legend width is the width of the image; the height is an argument 'leght' to this function	
		int legwt = image.getWidth(); //same as image so that it can be easily concatenated vertically
		//But it cannot be less than 260 pixels (excluding the edges). That wouldn't be enough to fit the legend
		//In that case return without drawing the legend
		if(legwt<MIN_LEGEND_WIDTH +(2*ImageEdgeWidth)) {
			System.out.println("Warning: Cannot fit legend; Image width too small");
			return; 
		}
		
		BufferedImage legendimg = new BufferedImage(legwt, leght, BufferedImage.TYPE_INT_RGB);
		
		
		Graphics g = legendimg.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, legwt, leght);
		
		int boxht = Math.round(leght / 2);
		int boxwt = (int)Math.floor((legwt-2*ImageEdgeWidth) / (double)MIN_LEGEND_WIDTH);
       		
		int index;
		int x = ImageEdgeWidth; //Start x location for legend within legend image 
		             //Start y location for legend within legend image
		
		for (int i = 0; i < colorMap8[0].length; i++,x=x+boxwt) { // for each color in the colormap
			//retrieve the color
			red = (colorMap8[0][i] & 0x000000FF);
			green = (colorMap8[1][i] & 0x000000FF);
			blue = (colorMap8[2][i] & 0x000000FF);
			color = new Color(red, green, blue);
			//set the color to draw the legend segment
			g.setColor(color);
			//draw the legend segment
			g.fillRect(x, STARTY, boxwt, boxht);
			g.setFont(new Font(Font.DIALOG, Font.PLAIN, LabelFontSize)); 
			index = markers.indexOf(i);
			if (index >= 0) {				
				g.setColor(Color.BLACK);
				g.drawString(markerValues.get(index), x, STARTY + boxht + COMPONENT_SEPARATION);
				//Draw the marker
				int markerLength=(int)(boxht/4.0);
				g.drawLine(x,STARTY+boxht-markerLength,x,STARTY+boxht); //Note that the marker will be a point if markerLength turns out to be 0
			}		

		}
		// Draw an outline to make the legend look nice
		g.drawRect(ImageEdgeWidth, STARTY, boxwt*colorMap8[0].length, boxht);
		// Draw the image label
		g.setColor(Color.BLACK);
		
		//Make the title centered
		int startx=(int)((legendimg.getWidth()- g.getFontMetrics().stringWidth(legend))/2.0);
		g.drawString(legend, startx, STARTY + boxht + g.getFontMetrics().getHeight()+COMPONENT_SEPARATION*2);
		
		//Now create the final image
		BufferedImage finalimg = new BufferedImage(image.getWidth(),image.getHeight()+leght,BufferedImage.TYPE_INT_RGB);
	    //Draw the primary image in the right spot
		boolean primaryimagedrawn = finalimg.createGraphics().drawImage(image, 0, 0, null); 
		if(!primaryimagedrawn)
			      System.out.println("Error in legend drawing"); 
        //Draw the legend image in the right spot (bottom of the primary image)
		boolean legendDrawn = finalimg.createGraphics().drawImage(legendimg, 0, image.getHeight(), null); 
		if(!legendDrawn)
		      System.out.println("Error in legend drawing");
		// The primary image is now the final image.
		image=finalimg;
		
		System.out.println("Concatenation of legend Complete.");
		

	}
	
	public void drawLegend(ValueMapper valueMapper, Vector<Integer> markers,
			Vector<String> markerValues, String legend) {
		mapper = new U8CMmapper(valueMapper);
		byte[][] colorMap8 = valueMapper.getColorMap8();
		int red, green, blue;
		Color color;

		// Legend space should be 1/20th of height and width
		int leght = (int) Math.round(height / 10.0);
		leght = Util.toRange(60, leght, 200);
		int legwt = (int) Math.round(width / 4.0);
		legwt = Util.toRange(300, legwt, 800);
		if (legwt > width || leght > height) {
			System.err.println("Image too small: Cannot draw legend");
			return;
		}
		int y = height - leght;
		int x = 0;
		Graphics g = image.getGraphics();
		g.setColor(Color.WHITE);
		// System.out.println("legend background:"+g.getColor());
		g.fillRect(x, y, legwt, leght);
		x = 5;
		y = height - leght + 5;
		int boxht = Math.round(leght / 2);
		int boxwt = Math.round(legwt / 260);

		int index;

		for (int i = 0; i < colorMap8[0].length; i++) {
			red = (colorMap8[0][i] & 0x000000FF);
			green = (colorMap8[1][i] & 0x000000FF);
			blue = (colorMap8[2][i] & 0x000000FF);
			color = new Color(red, green, blue);
			g.setColor(color);
			// System.out.println("i="+i+"legend"+x+" "+y+" cm[0]="+colorMap8[0][i]+" red="+red);
			g.fillRect(x, y, boxwt, boxht);

			index = markers.indexOf(i);
			if (index >= 0) {
				g.setColor(Color.BLACK);
				g.drawString(markerValues.get(index), x, y + boxht + 10);
			}
			x = x + boxwt;

		}
		x = 5;
		g.setColor(Color.BLACK);
		g.drawString(legend, x, y + boxht + 23);

	}
	
	private int checkedge(double[] geoarray, int len, double min, double max) {
		int corner1 = 0;
		int corner2 = 0;
		double EDGE_PCT = 0.1;
		int EDGE_DEPTH;

		// find edge start

		// System.out.println("min="+min+" max="+max);
		EDGE_DEPTH = (int) (EDGE_PCT * len);

		// System.out.println("EDGE_DEPTH="+EDGE_DEPTH+" len="+len);
		for (int i = 0; i < EDGE_DEPTH; i++) {
			// System.out.println("geoarr="+geoarray[i]);
			if (Util.inRange(min, geoarray[i], max)) {
				corner1 = 1;
				break;
			}
		}
		for (int i = len - 1; i > len - 1 - EDGE_DEPTH; i--)
			if (Util.inRange(min, geoarray[i], max)) {
				corner2 = 1;
				break;
			}
		if (corner1 == 1 && corner2 == 1)
			return (1);
		else
			return (0);
	}

	private int checkedge_subset(double[] latarray, double[] lonarray, int len,
			double latmin, double latmax, double lonmin, double lonmax,
			double validlatmin, double validlatmax, double validlonmin,
			double validlonmax) {
		int lentmp;

		// FOR LATITUDE

		// for(int i=0;i<len;i++)
		// System.out.print(lonarray[i]+" ");
		// System.out.println();

		// find edge start: it is where the switch occurs from
		// outside_swath-inside_swath
		int edgestart_lat = -999;
		if (Util.inRange(latmin, latarray[0], latmax))
			edgestart_lat = 0;
		else {
			for (int i = 0; i < len - 1; i++) {

				if (!Util.inRange(latmin, latarray[i], latmax)
						&& Util.inRange(latmin, latarray[i + 1], latmax) ) {
                    			// System.out.println("Found shift: "+latarray[i]+" "+latarray[i+1]);
					if (Util.inRange(validlatmin, latarray[i], validlatmax))
						edgestart_lat = i + 1;
					else
						edgestart_lat = i + 1;
					break;
				}
			}

		}
		// System.out.print("edgestart_lat="+edgestart_lat);
		if (edgestart_lat == -999)
			return (0);
		int edgeend_lat = -999;
		if (Util.inRange(latmin, latarray[len - 1], latmax))
			edgeend_lat = len - 1;
		else {
			for (int i = len - 1; i > 0; i--) {
				// System.out.println(len+" "+i+" "+(i-1));
				if (!Util.inRange(latmin, latarray[i], latmax)
						&& Util.inRange(latmin, latarray[i - 1], latmax)) {
					// System.out.println(len+" "+i+" "+(i-1));
					// System.out.println("Found shift: "+latarray[i]+" "+latarray[i-1]);
					if (Util.inRange(validlatmin, latarray[i], validlatmax))
						edgeend_lat = i - 1;
					else
						edgeend_lat = i - 1;
					break;
				}
			}

		}
		// System.out.print(" edgeend_lat="+edgeend_lat);
		if (edgeend_lat == -999)
			return (0);
		// System.out.println("min="+min+" max="+max);
		lentmp = edgeend_lat - edgestart_lat;
		if (lentmp <= 0)
			return (0);

		// FOR LONGITUDE

		// find edge start: it is where the switch occurs from
		// outside_swath-inside_swath
		int edgestart_lon = -999;
		if (Util.inRange(lonmin, lonarray[0], lonmax)) {
			edgestart_lon = 0;
			// System.out.println("First is inside: "+lonarray[0]);
		} else {
			for (int i = 0; i < len - 1; i++) {
				// System.out.print("<"+lonarray[i]+" "+lonarray[i+1]+">");
				if (!Util.inRange(lonmin, lonarray[i], lonmax)
						&& Util.inRange(lonmin, lonarray[i + 1], lonmax)) {
					// System.out.println();
					// System.out.println("Found shift:i= "+i+" : "+lonarray[i]+" "+lonarray[i+1]);
					if (Util.inRange(validlonmin, lonarray[i], validlonmax))
						edgestart_lon = i + 1;
					else
						edgestart_lon = i + 1;
					break;
				}
			}

		}
		// System.out.print(" edgestart_lon="+edgestart_lon);
		if (edgestart_lon == -999)
			return (0);

		int edgeend_lon = -999;
		if (Util.inRange(lonmin, lonarray[len - 1], lonmax)) {
			edgeend_lon = len - 1;
			// System.out.println("FIRST ONE setting to "+(len-1));
		} else {
			// System.out.println("NOT FIRST ONE");
			for (int i = len - 1; i > 0; i--) {
				// System.out.println(len+" "+i+" "+(i-1));
				if (!Util.inRange(lonmin, lonarray[i], lonmax)
						&& Util.inRange(lonmin, lonarray[i - 1], lonmax)) {
					// System.out.println("Found shift: "+lonarray[i]+" "+lonarray[i-1]);
					if (Util.inRange(validlonmin, lonarray[i], validlonmax))
						edgeend_lon = i - 1;
					else
						edgeend_lon = i - 1;
					break;
				}
			}

		}
		// System.out.println(" edgeend_lon="+edgeend_lon);
		if (edgeend_lon == -999)
			return (0);

		// System.out.println("min="+min+" max="+max);

		lentmp = edgeend_lon - edgestart_lon;
		if (lentmp <= 0)
			return (0);

		int edgestart = Util.max(edgestart_lat, edgestart_lon);
		int edgeend = Util.min(edgeend_lat, edgeend_lon);
		// System.out.println("edgestart="+edgestart+" edgeend="+edgeend);
		lentmp = edgeend - edgestart;
		if (lentmp <= 0)
			return (0);
		else {
			// for (int i=edgestart;i<edgeend;i++)
			// System.out.println("Edge:"+latarray[i]+" "+lonarray[i]+" |||"+lonmin+" "+lonmax);
			return (1);
		}

		/*
		 * EDGE_DEPTH=(int)(EDGE_PCTlen);
		 * 
		 * //System.out.println("EDGE_DEPTH="+EDGE_DEPTH+" len="+len); for(int
		 * i=edgestart;i<edgestart+EDGE_DEPTH;i++) {
		 * //System.out.println("geoarr="+geoarray[i]); if
		 * (Util.inRange(min,geoarray[i],max)){ corner1=1; break; } } for(int
		 * i=edgeend;i>edgeend-EDGE_DEPTH;i--) if
		 * (Util.inRange(min,geoarray[i],max)){ corner2=1; break; }
		 * if(corner1==1 && corner2==1) return(1); else return(0);
		 */
	}

	/****************************************************************************
	 * fillImage.
	 ****************************************************************************/
	/*
	 * private void fillImage (BufferedImage mask) { for (int yy = 0; yy <
	 * height; yy++) { // System.out.println("c " + yy + "/" + height); for (int
	 * xx = 0; xx < width; xx++) { if (mask.getRGB(xx,yy) == HOLE_RGB) {
	 * //image.setRGB(xx,yy,Color.WHITE.getRGB());
	 * image.setRGB(xx,yy,nearestRGB(xx,yy,mask)); } } } }
	 */
	/****************************************************************************
	 * fillImage.
	 ****************************************************************************/
	public void fillImage2() {
		for (int yy = 0; yy < height; yy++) {
			// System.out.println("c " + yy + "/" + height);
			for (int xx = 0; xx < width; xx++) {
				// if (mask.getRGB(xx,yy) == HOLE_RGB) {
				if (image.getRGB(xx, yy) == MASK_HOLE_RGB) {
					// image.setRGB(xx,yy,Color.WHITE.getRGB());
					image.setRGB(xx, yy, nearestRGB2(xx, yy));
				}
			}
		}
		// image=mask;
	}
	
	
	/**
	 * This function will remove noise. A pixel is noise if it is maximum in the nbd. Use this function only if required.
	 * Exercise caution because it may remove valid pixels.
	 * @param noisenbd defines the neighborhood size for purposes of noise removal
	 */
	public void removeNoise(int noisenbd){
		/* Noise removal code */
		Color c;
		int[] color=new int [3];
		int indexthisPixel,index;
		
		System.out.println("Applying Noise Filtering...");	
		BufferedImage imageDest=ImageCreator.deepCopy(image);
		
		for (int yy = 0; yy < image.getHeight(); yy++) {
			for (int xx = 0; xx < image.getWidth(); xx++) { // for each pixel
                //Reverse calculate the index from the color using the colormap
				c= new Color(image.getRGB(xx,yy));
				color[0]=c.getRed();
				color[1]=c.getGreen();
				color[2]=c.getBlue();
				indexthisPixel=mapper.valueMapper.getColorIndex(color);			
				//Set immediate neighborhood (-1 -> +1) both vertically and horizontally 
				int startrow=Util.toRange(0, yy-noisenbd, image.getHeight()-1);
				int endrow=Util.toRange(0, yy+noisenbd, image.getHeight()-1);
				int startcol=Util.toRange(0, xx-noisenbd, image.getWidth()-1);
				int endcol=Util.toRange(0, xx+noisenbd, image.getWidth()-1);
				
				//Initialize this pixel to noise
				boolean isNoise=Boolean.TRUE;
				//Check the immediate neighborhood to determine if this is really noise
				for(int yyy=startrow;yyy<=endrow;yyy++)
					for(int xxx=startcol;xxx<=endcol;xxx++){
						if(!(xxx==xx && yyy==yy)){ //ignore the current pixel
							//Reverse map the color to the index for the nbd pixel
							c= new Color(image.getRGB(xxx,yyy));
							color[0]=c.getRed();
							color[1]=c.getGreen();
							color[2]=c.getBlue();
							index=mapper.valueMapper.getColorIndex(color);
							//System.out.println(indexthisPixel+","+index);
							//If the nbd pixel has higher value than the pixel we are checking then it's not noise							
							if(index>=indexthisPixel)
							{							
								isNoise=Boolean.FALSE;
								break;
							}
						}
					}
				if(isNoise) //if the pixel we are checking is the maximum 
				{
					//System.out.println("Image dim:"+height+","+width+" Noise at "+xx+","+yy+":"+startrow+","+startcol);
					c= new Color(image.getRGB(startcol,startrow));

					color[0]=c.getRed();
					color[1]=c.getGreen();
					color[2]=c.getBlue();
					index=mapper.valueMapper.getColorIndex(color);
					//System.out.println("xx="+xx+" yy="+yy+" index="+index);
					imageDest.setRGB(xx, yy,mapper.valueMapper.getColor((int)Math.round(index)));
				}




			}
		}
		image=imageDest;
		System.out.println("Noise Filtering Complete.");

	}
	
	/**
	 * This function sharpens the image using the sharpening kernel
	 */
	public void sharpenImage(){
		/* Image Sharpening Code */
		float data[] = { -1.0f, -1.0f, -1.0f, -1.0f, 9.0f, -1.0f, -1.0f, -1.0f,
		        -1.0f };
	    Kernel kernel = new Kernel(3, 3, data);
	    ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP,null);
	    BufferedImage imageDest=ImageCreator.deepCopy(image);
	    convolve.filter(image, imageDest);
	    image = imageDest;
		/* End of image sharpening code */
	}
	
	/**
	 * This function convolves the image using a kernel. It uses a 5*5 equal-weight kernel.
	 * The function can be later extended by having the size of the kernel and the weights as function arguments 
	 */
	public void convolveImage(){
		Color c;
		int[] color=new int [3];
		int index;
		//Code to do kernel averaging 
		System.out.println("Kernel Convolution");
		int kernel_size=5;
		int kernel_sizesq=kernel_size*kernel_size;
		float kernel[]= new float[kernel_sizesq];//{(float)0.11,(float) 0.11,(float)0.11,(float)0.11,(float)0.11,(float)0.11,(float)0.11,(float)0.11,(float)0.11};
		for (int i=0;i<kernel_sizesq;i++)
			kernel[i]=(float)(1.0/kernel_sizesq);
		//System.out.println("I am convolving "+kernel_size+","+kernel[0]);
		ConvolveOp convolveop=new ConvolveOp(new Kernel(kernel_size,kernel_size,kernel),ConvolveOp.EDGE_NO_OP,null);

		convolveop.filter(image, imagefiltered);
		//restore fill values
		for (int row=0;row<height;row++)
			for (int col=0;col<height;col++)
			{
				c= new Color(image.getRGB(col,row));
				color[0]=c.getRed();
				color[1]=c.getGreen();
				color[2]=c.getBlue();
				index=mapper.valueMapper.getColorIndex(color);
				if(index==0)
					imagefiltered.setRGB(col, row, mapper.valueMapper.getColor((int)Math.round(index)));
			}
		image=imagefiltered;
	}

	
	/**
	 * This function applies a mean filter on the image. Only applicable for colormapped images (i.e. not rgb).
	 * @param nbd_size is the size of the window surrounding the pixel on which the mean filter is applied
	 */
	public void meanFilter(int nbd_size) {
		 
		Color c;
		int[] color=new int [3];
		int index;
		
		System.out.println("Applying Mean filtering...");		
		for (int row=0;row<image.getHeight();row++)
			for (int col=0;col<image.getWidth();col++)
			{
				//retrive the color for the current pixel
				c= new Color(image.getRGB(col,row));
				color[0]=c.getRed();
				color[1]=c.getGreen();
				color[2]=c.getBlue();
				// reverse map the color to the index using the colormap
				index=mapper.valueMapper.getColorIndex(color);
				//Ignore if this is a fill-valued pixel
				if(index==0) 
					continue;
				
				//Now initialize the 'sum' of valid indices in the nbd; Initialize no of valid indices 'nvalues'
				int sum=0, nvalues=0; 
				for(int yshift=-nbd_size; yshift <= nbd_size; yshift++)
					for(int xshift=-nbd_size; xshift <= nbd_size; xshift++) // for each pixel in the nbd
					{
						if(Util.inRange(0, col+xshift,image.getWidth()-1) && Util.inRange(0, row+yshift,image.getHeight()-1) )
						{
							// retrieve the color
							c= new Color(image.getRGB(col+xshift,row+yshift));
							color[0]=c.getRed();
							color[1]=c.getGreen();
							color[2]=c.getBlue();
							//Reverse map the color to the index using the colormap 
							index=mapper.valueMapper.getColorIndex(color);
							if(index>0) //Ignore fill-valued pixels
							{
								sum+=index; // accumulate the sum
								nvalues++; // increment the number of valid pixels
							}
						}
					}
				int avgindex=(int) Math.round(sum/nvalues); // compute the average
				image.setRGB(col, row, mapper.valueMapper.getColor((int)Math.round(avgindex))); // set the current pixel to the avg
			}			
		System.out.println("Mean Filtering Complete.");

	}
	
	
	public void fillImage3() {
		for (int yy = 0; yy < height; yy++) {
			// System.out.println("c " + yy + "/" + height);
			for (int xx = 0; xx < width; xx++) {
				if (mask.getRGB(xx, yy) == HOLE_RGB) {
					// image.setRGB(xx,yy,Color.WHITE.getRGB());
					image.setRGB(xx, yy, nearestRGB(xx, yy, mask));
				}
			}
		}		
		//image=mask;
	}
	
	
	/**
	 * Deep copies a BufferedImage objcet
	 * @param bi is the BufferedImage object to be copied
	 * @return copied BufferedImage
	 */
	static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		}
	
	
	/**
	 * Does image filling using distance weighted interpolation
	 * @param npixels is the number of neighborhood pixels to use to do the interpolation 
	 * @param distanceToSearch is the maximum nbd search area (until 'npixels' is obtained)
	 */
	public void fillImage_InvDist(int npixels, int distanceToSearch)
	{
		System.out.println("Applying Inverse Distance Interpolation...");
		for (int yy = 0; yy < height; yy++) {
			// System.out.println("c " + yy + "/" + height);
			for (int xx = 0; xx < width; xx++) {
				if (mask.getRGB(xx, yy) == HOLE_RGB) {
					// image.setRGB(xx,yy,Color.WHITE.getRGB());
					image.setRGB(xx, yy, invDistanceWeightedRGB(xx, yy, mask,image,npixels,distanceToSearch));
				}
			}
		}
		System.out.println("Inverse Distance Interpolation Complete");
		//image=mask;
	}	
	
	
	/**
	 * This function does inverse distance wighted interpolation for a particular pixel
	 * @param x is the x-coordinate of the pixel to be interpolated 
	 * @param y is the y-coordinate of the pixel to be interpolated
	 * @param mask is the mask image. It contains information about the location of the actual pixels (i.e. before any interpolation)
	 * @param image is the image to be filled
	 * @param npixels is the number of pixels to use for the interpolation
	 * @param distanceToSearch is the maximum nbd search area (until 'npixels' is obtained)
	 * @return
	 */
	private int invDistanceWeightedRGB(int x, int y, BufferedImage mask, BufferedImage image,int npixels,int distanceToSearch) {
		nnDist=1; //Initialize nbd to 1 pixel 		
		int points_collected=0; // Initialize points collected to 0
		
		int[] color=new int [3];
		double[] distances=new double[npixels];
		int[] indices=new int[npixels];		
		
		
		// Do the loop until we have 'npixels' valid pixels required for interpolation; At each loop increase the radial distance
		while (points_collected<npixels && nnDist<distanceToSearch) {
			nnOffsets = calcOffsets_range(nnDist); //obtain the offsets vector for this radial distance 'nnDist'
			Iterator it = nnOffsets.iterator(); // get the iterator
			//Loop until we have checked all the pixels that are at distance 'nndist' or we have npixels
			while (it.hasNext() && points_collected<npixels) {
				//Obtain the x,y offsets for this 
				OffsetNN offset = (OffsetNN) it.next();
				int xx = x + offset.x;
				int yy = y + offset.y;
				//System.out.println("offsets="+offset.x+","+offset.y);
				if (Util.inRange(0, xx, image.getWidth() - 1)
						&& Util.inRange(0, yy, image.getHeight() - 1)) { //if the nbd pixel is within the image
					if (mask.getRGB(xx, yy) == DATA_RGB) { // if the nbd pixel denotes an un-interpolated pixel
						// retrieve the color of the nbd pixel
						Color c= new Color(image.getRGB(xx,yy));
						color[0]=c.getRed();
						color[1]=c.getGreen();
						color[2]=c.getBlue();
						//System.out.println("color is "+color[0]+","+color[1]+","+color[2]);
						//System.out.println("points collected="+points_collected);
						int index = mapper.valueMapper.getColorIndex(color);
						//use the color to reverse map to the index value using the colormap
						if(index>0)
						{
						   indices[points_collected]=index;
						   //if(indices[points_collected]==0) System.exit(0);
						
						   //calculate the distance of the nbd pixel to the pixel-to-be-interpolated
						   distances[points_collected]=Math.sqrt(Math.pow(offset.x,2)+Math.pow(offset.y,2));
						   //increment 'points_collected'
						   points_collected++;
						   //System.out.println(offset.x+","+offset.y+","+points_collected+","+distances[points_collected-1]+","+indices[points_collected-1]);
						}
					}
				}
			}
			nnDist++; //increment the nbd distance
			//nnOffsets = calcOffsets_range(++nnDist);
			
		}
    if(points_collected==npixels) // Did we collect enough pixels for the interpolation
    {
    	//get the sum of distances
    	double sum_distances=0;
    	for (int i=0;i<npixels;i++)
    	   sum_distances+=distances[i];
    	//Now calculate the weighted index. the weights are distance-i/sum-of-distances for index-i
    	double weighted_index=0;
    	for (int i=0;i<npixels;i++)
		 weighted_index+=(distances[i]/sum_distances)*indices[i];
		
		//System.out.println("Weighted Index="+weighted_index);
		//System.exit(0);    	
    	//assign the color for the calculated weighted_index
    	return(mapper.valueMapper.getColor((int)Math.round(weighted_index)));
    }
	else // If not, assign fill value color
	   return mapper.getRGB();
	}

	/****************************************************************************
	 * nearestRGB.
	 ****************************************************************************/
	private int nearestRGB(int x, int y, BufferedImage mask) {
		//System.out.println("in nearest RGB "+nnDist);
		//nnDist=1;
		while (true) {
			Iterator it = nnOffsets.iterator();
			while (it.hasNext()) {
				OffsetNN offset = (OffsetNN) it.next();
				int xx = x + offset.x;
				int yy = y + offset.y;
				if (Util.inRange(0, xx, width - 1)
						&& Util.inRange(0, yy, height - 1)) {
					if (mask.getRGB(xx, yy) == DATA_RGB) {
						// return image.getRGB(xx,yy);
						//System.out.println("in nearest RGB "+nnDist);
						return oneOfEqualDist(offset, it, x, y, mask);
					}
				}
			}
			if (nnDist < 15) {
				nnOffsets = calcOffsetsNN(++nnDist);
			} else {
				return mapper.getRGB();
			}
		}
	}

	private int nearestRGB2(int x, int y) {
		//nnDist=1;
		while (true) {
			Iterator it = nnOffsets.iterator();
			while (it.hasNext()) {
				OffsetNN offset = (OffsetNN) it.next();
				int xx = x + offset.x;
				int yy = y + offset.y;
				if (Util.inRange(0, xx, width - 1)
						&& Util.inRange(0, yy, height - 1)) {
					if (image.getRGB(xx, yy) != MASK_HOLE_RGB
							/*&& image.getRGB(xx, yy) != Color.BLACK.getRGB()*/) {
						// return image.getRGB(xx,yy);
						return oneOfEqualDist2(offset, it, x, y);
					}
				}
			}
			if (nnDist < 15) {
				nnOffsets = calcOffsetsNN(++nnDist);
			} else {
				return mapper.getRGB();
			}
		}
	}	

	private int oneOfEqualDist(OffsetNN offset1, Iterator it, int x, int y,
			BufferedImage mask) {
		// Next Line changed on 11/23/07 from
		// "LinkedList offsets=new LinkedList()" Type checking for java 1.5
		LinkedList<OffsetNN> offsets = new LinkedList<OffsetNN>();
		offsets.add(offset1);
		while (it.hasNext()) {
			OffsetNN offset = (OffsetNN) it.next();
			if (offset.d > offset1.d) {
				break;
			}
			int xx = x + offset.x;
			int yy = y + offset.y;
			if (Util.inRange(0, xx, width - 1)
					&& Util.inRange(0, yy, height - 1)) {
				if (mask.getRGB(xx, yy) == DATA_RGB) {
					offsets.add(offset);
				}
			}
		}
		int index = random.nextInt(offsets.size());
		OffsetNN o = offsets.get(index);
		int xx = x + o.x;
		int yy = y + o.y;
		return image.getRGB(xx, yy);
	}

	private int oneOfEqualDist2(OffsetNN offset1, Iterator it, int x, int y) {
		// Next Line changed on 11/23/07 from
		// "LinkedList offsets=new LinkedList()" Type checking for java 1.5
		LinkedList<OffsetNN> offsets = new LinkedList<OffsetNN>();
		offsets.add(offset1);
		while (it.hasNext()) {
			OffsetNN offset = (OffsetNN) it.next();
			if (offset.d > offset1.d) {
				break;
			}
			int xx = x + offset.x;
			int yy = y + offset.y;
			if (Util.inRange(0, xx, width - 1)
					&& Util.inRange(0, yy, height - 1)) {
				// if (mask.getRGB(xx,yy) == DATA_RGB) {
				if (image.getRGB(xx, yy) != MASK_HOLE_RGB
						&& image.getRGB(xx, yy) != Color.BLACK.getRGB()) {
					offsets.add(offset);
				}
			}
		}
		int index = random.nextInt(offsets.size());
		OffsetNN o = offsets.get(index);
		int xx = x + o.x;
		int yy = y + o.y;
		return image.getRGB(xx, yy);
	}

	/****************************************************************************
	 * OffsetNN.
	 ****************************************************************************/
	private class OffsetNN {
		public int x;
		public int y;
		public double d;

		public OffsetNN(int x, int y, double d) {
			this.x = x;
			this.y = y;
			this.d = d;
		}
	}

	/****************************************************************************
	 * calcOffsetsNN.
	 ****************************************************************************/
	private LinkedList calcOffsetsNN(int dist) {
		// Next Line changed on 11/23/07 from
		// "LinkedList offsets=new LinkedList()" Type checking for java 1.5
		LinkedList<OffsetNN> offsets = new LinkedList<OffsetNN>();
		for (int x = -dist; x <= dist; x++) {
			for (int y = -dist; y <= dist; y++) {
				if (x != 0 || y != 0) {
					double d = Math.sqrt((x * x) + (y * y));
					if (d < dist + 1) {
						OffsetNN newOffset = new OffsetNN(x, y, d);
						for (int i = 0; i < offsets.size(); i++) {
							OffsetNN offset = offsets.get(i);
							if (newOffset.d < offset.d) {
								offsets.add(i, newOffset);
								newOffset = null;
								break;
							}
						}
						if (newOffset != null) {
							offsets.add(newOffset);
						}
					}
				}
			}
		}
		// System.out.println("dist: " + dist);
		return offsets;
	}
	
	/****************************************************************************
	 * calcOffsets_range.
	 ****************************************************************************/
	private LinkedList calcOffsets_range(int dist) {
		// Next Line changed on 11/23/07 from
		// "LinkedList offsets=new LinkedList()" Type checking for java 1.5
		LinkedList<OffsetNN> offsets = new LinkedList<OffsetNN>();
		for (int x = -dist; x <= dist; x++) {
			for (int y = -dist; y <= dist; y++) {
				if (x != 0 || y != 0) {
					double d = Math.sqrt((x * x) + (y * y));
					if (d >= dist && d < dist + 1) {
						OffsetNN newOffset = new OffsetNN(x, y, d);
						for (int i = 0; i < offsets.size(); i++) {
							OffsetNN offset = offsets.get(i);
							if (newOffset.d < offset.d) {
								offsets.add(i, newOffset);
								newOffset = null;
								break;
							}
						}
						if (newOffset != null) {
							offsets.add(newOffset);
						}
					}
				}
			}
		}
		// System.out.println("dist: " + dist);
		return offsets;
	}
	
	public void meanFilter(int[][] values) {
		int[] nbrarr=new int[9];
		int sum;
		int index;
		
		int yCountvalues=values.length;
		int xCountvalues = values[0].length;
		System.out.println("In median Filter"+yCountvalues+","+xCountvalues);
		for (int yy = 1; yy < yCountvalues-1; yy++) {
			for (int xx = 1; xx < xCountvalues-1; xx++) {
				index=0;
				sum=0;
				for(int yshift=-1; yshift <= 1; yshift++)
					for(int xshift=-1; xshift <= 1; xshift++)
					{
						nbrarr[index++]=values[yy+yshift][xx+xshift];
						sum=sum+values[yy+yshift][xx+xshift];
					}
				Arrays.sort(nbrarr);
				//values[yy][xx]=nbrarr[4];
				values[yy][xx]=(int) Math.round(sum/9.0);
			}
			
		}
	}
		
	
	

	/****************************************************************************
	 * PixelMapper.
	 ****************************************************************************/
	private abstract class PixelMapper {
		public ValueMapper valueMapper;

		public PixelMapper(ValueMapper valueMapper) {
			this.valueMapper = valueMapper;
		}

		public abstract int getRGB(int[] value);

		public int getRGB() {
			return valueMapper.getRGB();
		}
	};

	/****************************************************************************
	 * DRGBmapper. aRGB with the data (byte) in the alpha channel.
	 ****************************************************************************/
	private class DRGBmapper extends PixelMapper {
		public DRGBmapper(ValueMapper valueMapper) {
			super(valueMapper);
		}

		@Override
		public int getRGB(int[] value) {
			return (valueMapper.getRGB(value) & 0x00FFFFFF)
					| (valueMapper.getData(value) << 24);
		}
	}

	/****************************************************************************
	 * ARGBmapper.
	 ****************************************************************************/
	private class ARGBmapper extends PixelMapper {
		public ARGBmapper(ValueMapper valueMapper) {
			super(valueMapper);
		}

		@Override
		public int getRGB(int[] value) {
			return valueMapper.getRGB(value);
		}
	}

	/****************************************************************************
	 * U16GSmapper. Unsigned 16-bit gray-scale.
	 ****************************************************************************/
	private class U16GSmapper extends PixelMapper {
		public U16GSmapper(ValueMapper valueMapper) {
			super(valueMapper);
		}

		@Override
		public int getRGB(int[] value) {
			return valueMapper.getData(value);
		}
	}

	/****************************************************************************
	 * U8CMmapper. Unsigned 8-bit color map.
	 ****************************************************************************/
	private class U8CMmapper extends PixelMapper {
		public U8CMmapper(ValueMapper valueMapper) {
			super(valueMapper);
		}

		@Override
		public int getRGB(int[] value) {
			return valueMapper.getRGB(value);
		}
	}

	/****************************************************************************
	 * U16CMmapper. Unsigned 16-bit color map.
	 ****************************************************************************/
	private class U16CMmapper extends PixelMapper {
		public U16CMmapper(ValueMapper valueMapper) {
			super(valueMapper);
		}

		@Override
		public int getRGB(int[] value) {
			return valueMapper.getRGB(value);
		}
	}
}
