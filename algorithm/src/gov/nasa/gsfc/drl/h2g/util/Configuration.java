/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/


package gov.nasa.gsfc.drl.h2g.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;
import org.proj4.Proj4;

public class Configuration {
	
	/**Designing a singleton class ; to ensure that only one instance of the class is created**/	
	private static Configuration this_instance;
	
	
	

	/*********** Parameters Describing the RGB datasets ******/
	int REDSource; // DATASETFILE
	String[] REDName;
	int[] REDIndex;
	int REDDataType;
	double REDDoubleMultiplier;
	int REDFillvalue_min;
	int REDFillvalue_max;
	int REDRowsPerScan;
	Boolean REDishdf5;
	int REDtrimType;

	

	int GREENSource; // DATASETFILE
	String[] GREENName;
	int[] GREENIndex;
	int GREENDataType;
	double GREENDoubleMultiplier;
	int GREENFillvalue_min;
	int GREENFillvalue_max;
	int GREENRowsPerScan;
	Boolean GREENishdf5;
	int GREENtrimType;

	int BLUESource; // DATASETFILE
	String[] BLUEName;
	int[] BLUEIndex;
	int BLUEDataType;
	double BLUEDoubleMultiplier;
	int BLUEFillvalue_min;
	int BLUEFillvalue_max;
	int BLUERowsPerScan;
	Boolean BLUEishdf5;
	int BLUEtrimType;

	int[] redScale, greenScale, blueScale;
	int RGBDimToMatch;
	int fireSource;
	Boolean fireOverlay;
	

	/*******************************************************/

	

	/******** Parameters Describing the Dimension Matching Algos for RGB ***/
	int REDDimMatchAlgo; // MASK_MATCHES_SDS | MOD14_1KM_250M (****** Support
							// for all other possible types for DimMatchALgos
							// reqd)
	int GREENDimMatchAlgo;
	int BLUEDimMatchAlgo;
	/***************************************************************/

	/********** Parameters Describing the Dataset ********/
	int SDSSource; // DATASETFILE
	String[] SDSName;
	int[] SDSIndex;
	int SDSDataType; // INTEGERS | DOUBLES |SDS_IMAPP_CLOUDMASK
	double SDSDoubleMultiplier;
	int SDSFillvalue_min;
	int SDSFillvalue_max;
	int SDSRowsPerScan; // MODIS_250M | MODIS_500M | MODIS_1KM | MODIS_5KM |
						// MODIS_10KM
	Boolean SDSishdf5;
	int SDStrimType;
	int ThirdDimDepth=-1; 

	/******************************************************/

	/********** Parameters Describing the Geolocation *****/
	int GEOSource; // DATASETFILE | GEOFILE
	String[] GEOName_Lat, GEOName_Lon;
	int[] GEOIndex_Lat, GEOIndex_Lon;
	int GEODataType; // Accepts only DOUBLES Currently
	double GEOIntMultiplier;
	int GEORowsPerScan; // MODIS_250M | MODIS_500M | MODIS_1KM | MODIS_5KM |
						// MODIS_10KM | ANYOTHERVALUE
	Boolean GEOishdf5;

	/******************************************************/

	/********* Parameters Describing the Mask *************/
	Boolean MASKExists; // TRUE | FALSE
	int MASKSource; // DATASETFILE | MASKFILE
	String[] MASKName;
	int[] MASKIndex;
	int MASKDataType; // INTEGERS | DOUBLES | MASK_L2FLAGS_CHLOR_WARN_FAIL
	double MASKDoubleMultiplier;
	int MASKRowsPerScan; // MODIS_250M | MODIS_500M | MODIS_1KM | MODIS_5KM |
							// MODIS_10KM | ANYOTHERVALUE
	int[] MASKValues;
	int MASKType; // DISCRETE | CONTINUOUS
	Boolean MASKishdf5;

	/*******************************************************/

	/******** Parameters Describing the Dimension Matching Algos ***/
	int GEODimMatchAlgo; // GEO_MATCHES_SDS | MOD03_1KM_250M (****** Support for
							// all other possible types for DimMatchALgos reqd)
	int MASKDimMatchAlgo; // MASK_MATCHES_SDS | MOD14_1KM_250M (****** Support
							// for all other possible types for DimMatchALgos
							// reqd)

	/***************************************************************/

	/******** Parameters describing the processing *********/
	String scalingType;
	double[] coefficients;
	Boolean fireFlag;
	int[] fireValues;
	String colormapPath;
	int ScansPerLoop;
	Boolean isRGBinDifferentFiles;
	/*******************************************************/

	/******** Parameters describing the output projection **/
	int projectionIndex; // GEOGRAPHIC
	double resolution;
	Boolean isSubset;
	double minLat, maxLat, minLon, maxLon;
	/*******************************************************/

	/******** Parameters describing the image filling strategy **/
	boolean interpolationSpecified=Boolean.FALSE;
	String interpolationAlgo;
	int nPixels; //no of pixels to use from the neighborhood to do the averaging
	int distanceToSearch; // pixel distance to search to find valid pixels for interpolation
	/**********************************************************/
	
	/******** Parameters describing image enhancement **/
	boolean imageFilterFlag=Boolean.FALSE;
	String imageFilterAlgo;
	int filterNbd;
	boolean removeNoiseFlag=Boolean.FALSE;
	int noiseNbd;
	/***************************************************/
	
	/*** Parameters determining configuration *******/
	String configType = null, configPath = null;
	/*************/

	/******* Parameters describing legend ***********/
	Boolean hasLegend;
	Vector<Integer> markers;
	Vector<String> markerValues;
	String legend;
	/************************************************/
	
	/********* For NPP data holes********************/
	Boolean hasDataHoles=Boolean.FALSE;
	Boolean bowtieFillEnabled=Boolean.FALSE;
	int DataHoleValue=0;
	private static final int BOWTIE_UINT8 = 252;
	private static final int BOWTIE_UINT16 = 65532;
	private static final int BOWTIE_DOUBLE_100 = -99960;
	private static final int BOWTIE_DOUBLE_1000 = -999600;
	/************************************************/
	
	/********* Parameters describing any optimization *****/
	Boolean useOptimization=Boolean.FALSE;	
	private static final int USEGEODIM1 = 1;
	private static final int USEGEODIM2 = 2;
	private static final int USEGEODIM4 = 4;
	int optimizationIndex=USEGEODIM1;

	/************ possible values for xxxSource ***********/
	private static final int DATASETFILE = 0;
	private static final int GEOFILE = 1;
	private static final int MASKFILE = 2;
	private static final int FIREFILE = 3;
	private static final int REDDATASETFILE = 11;
	private static final int GREENDATASETFILE = 12;
	private static final int BLUEDATASETFILE = 13;
	/******************************************************/

	/************ possible values for xxxRowsPerScan ******/
	private static final int MODIS_250M = 40;
	private static final int MODIS_500M = 20;
	private static final int MODIS_1KM = 10;
	private static final int MODIS_5KM = 2;
	private static final int MODIS_10KM = 1;
	private static final int VIIRS_750M = 16;
	private static final int VIIRS_375M = 32;
	private static final int VIIRS_750M_GTM = 16;
	private static final int VIIRS_375M_GTM = 32;
	private static final int OMPS_NADIR = 1;
	private static final int ATMS = 1;
	private static final int CRIS = 3;
	/******************************************************/

	/************ GeoDimMatchAlgo MaskDimMatchAlgo - possible values **/
	private static final int DIM_MATCHES_SDS = 20;
	private static final int MODIS_1KM_TO_250M = 21;
	private static final int MODIS_GEO_DOWNSCALE = 22;
	private static final int MASK_UPSCALE = 23;
	private static final int MASK_CONT_DOWNSCALE = 24;
	private static final int MASK_DISC_DOWNSCALE = 25;
	private static final int MASK_UPSCALE_GTM = 26;
	private static final int MASK_CONT_DOWNSCALE_GTM = 27;
	private static final int MASK_DISC_DOWNSCALE_GTM = 28;
	private static final int UNDEFINED = -999;
	// MODIS_1KM_TO_5KM, MODIS_5KM_TO_1KM (Not for GEO), MODIS_5KM_TO_500M (Not
	// for GEO), MODIS_5KM_TO_250M (Not for GEO)
	// MODIS_1KM_TO_10KM, MODIS_10KM_TO_1KM (Not for GEO),MODIS_10KM_TO_500M
	// (Not for GEO), MODIS_10KM_TO_250M (Not for GEO)
	// MODIS_250M_TO_500M,
	// MODIS_250M_TO_1KM,MODIS_250M_TO_5KM,MODIS_250M_TO_10KM
	// MODIS_500M_TO_250M,
	// MODIS_500M_TO_1KM,MODIS_500M_TO_5KM,MODIS_500M_TO_10KM
	/********* End of GeoDimMatchALgo *****************/

	/************ xxxDataType possible values ***********/
	public static final int INTEGERS = 30;
	public static final int DOUBLES = 31;
	public static final int MASK_L2FLAGS_CHLOR_WARN_FAIL = 32;
	public static final int SDS_IMAPP_CLOUDMASK = 33;
	public static final int SDS_VCM_CLOUDMASK = 34;
	public static final int SDS_VCM_CLOUDMASK_H5 = 35;
	public static final int SDS_VCM_PHASE_H5 = 36;
	public static final int SDS_VCM_LANDWATER_H5 = 37;
    public static final int SDS_VCM_DAYNIGHT_H5 = 38;
    public static final int SDS_VCM_SNOWICE_H5 = 39;
    public static final int SDS_VCM_SUNGLINT_H5 = 40;
    public static final int VIIRS_SNOWCOVER_QUALITY=41;
    public static final int VIIRS_AEROSOL_QUALITY=42;
    public static final int VIIRS_SUM_QUALITY=43;
	/********************************************************/

	/************ MaskType possible values ***********/
	public static final int DISCRETE = 50;
	public static final int CONTINUOUS = 51;

	/********************************************************/

	/************ projectionIndex possible values ******/
	public static final int GEOGRAPHIC = 60;
	public static final int NOPROJECTION = 61;
	public static final int STEREOGRAPHIC = 62;
	public static final int TMERCATOR = 63;
	public static final int MERCATOR = 64;
	public static final int SINUSOIDAL=65;
	public static final int LAZIMUTHAL=66;
	/********* End of projectionIndex **********************/

	/********* Default image filling values -- amounts to nearest neighbor*/
	public static final int DEFAULTNPIXELS=1;
	public static final int DEFAULTDISTANCETOSEARCH=15;	
	/**********End of default image filling values ********/
	
	private static final int CHLOR_OK = 0;
	private static final int CHLOR_WARNING = 1;
	private static final int CHLOR_FAIL = 2;

    /****** Trim Type ********/
	private static final int DEFAULTTRIM=0;
	private static final int SDRTRIM=1;	
	/**** End of Trim types *******/


	
	/******************************************************/

	Vector<DataSetKey> DataSetKeys;
	Vector<String> DataSetPrimaryKeys;
	Vector<SDSDimMatchAlgo> GeoAlgoKeys, MaskDiscAlgoKeys, MaskContAlgoKeys;
	Vector<Integer> GeoAlgoID, MaskDiscAlgoID, MaskContAlgoID;
	Vector<String> ColorMapKeys;
	Vector<String> ColorMapPaths;
	Vector<String> ConfigNames, ConfigTypes, ConfigPaths;
	Proj4 testProjection;
	
    GEOTIFFTags gtifftags;
    
    int[][] ViirsTrimTable_SDR_Mod, ViirsTrimTable_SDR_Img, ViirsTrimTable_EDR_Mod, ViirsTrimTable_EDR_Img;




	private double lon_0=-999.0;




	private double lat_0=-999.0;




	private int DATADimMatchAlgo;




	private String cfgName;




	private double width_km;




	private double height_km;




	/*private Boolean redisHDF5=Boolean.FALSE;




	private Boolean greenisHDF5=Boolean.FALSE;




	private Boolean blueisHDF5=Boolean.FALSE;




	private Boolean GEOisHDF5=Boolean.FALSE;*/




	public static String H2GHOME;
	
	
	/******************************************************/
	
	public static String getH2GHOME() {
		return H2GHOME;
	}

	/**
	 * @return the testProjection
	 */
	public Proj4 getTestProjection() {
		return testProjection;
	}

	/**
	 * @param testProjection the testProjection to set
	 */
	public void setTestProjection(Proj4 testProjection) {
		this.testProjection = testProjection;
	}

	public static synchronized Configuration getInstance(String cfgType, String cfgName,String arg_projection, double arg_resolution, double arg_lat0, double arg_lon0, double toplat, double botlat, double leftlon, double rightlon, double width_km, double height_km, String H2GHOME, Boolean isRGBinDifferentFiles) throws Exception {
		
		
		if (this_instance == null)
		{
			this_instance = new Configuration(cfgType, cfgName,arg_projection,arg_resolution,arg_lat0,arg_lon0,toplat, botlat, leftlon, rightlon,width_km, height_km, H2GHOME,isRGBinDifferentFiles);
			
		}
        //System.out.println(width_km+" "+height_km);
		return this_instance;
	}
	
	public static synchronized Configuration getInstance() throws Exception {
		if (this_instance == null) {
			System.out.println("Error: Configuration was not initialized");
			System.exit(1);
		}
		return this_instance;
	}

	private Configuration(String cfgType, String cfgName, String arg_projection, double arg_resolution, double arg_lat0, double arg_lon0, double toplat, double botlat, double leftlon, double rightlon, double width_km, double height_km, String H2GHOME, Boolean isRGBinDifferentFiles) throws Exception {

		Configuration.H2GHOME=H2GHOME;
		this.cfgName=cfgName;
		setIsRGBinDifferentFiles(isRGBinDifferentFiles);
		System.out.println("Reading Configiration files ...");
		System.out.println(">> Loading DatasetKeys");
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(Util
					.getInputStream(H2GHOME+ File.separator+"configFiles" + File.separator
							+ "DataSetKeys.txt")));
			String line;
			DataSetKeys = new Vector<DataSetKey>();
			DataSetPrimaryKeys = new Vector<String>();
			while ((line = in.readLine()) != null) {
				// System.out.println("**"+line+"***");
				DataSetKey temp = new DataSetKey();
				StringTokenizer byBlanks = new StringTokenizer(line);
				temp.keyName = byBlanks.nextToken();
				DataSetPrimaryKeys.add(temp.keyName);
				temp.PLengthSDS = Integer.parseInt(byBlanks.nextToken());
				// System.out.println(temp.PLengthSDS);
				temp.PathSpecifier = new String[temp.PLengthSDS];
				for (int i = 0; i < temp.PLengthSDS; i++) {
					temp.PathSpecifier[i] = byBlanks.nextToken();
					temp.PathSpecifier[i] = temp.PathSpecifier[i].replace('$',
							' ');
				}
				temp.NIndex = Integer.parseInt(byBlanks.nextToken());
				temp.IndexSelections = new int[temp.NIndex];
				for (int i = 0; i < temp.NIndex; i++)
					temp.IndexSelections[i] = Integer.parseInt(byBlanks
							.nextToken());
				String x = byBlanks.nextToken();

				if (x.equals("INTEGERS"))
					temp.DataType = INTEGERS;
				else if (x.equals("DOUBLES"))
					temp.DataType = DOUBLES;
				else if (x.equals("MASK_L2FLAGS_CHLOR_WARN_FAIL"))
					temp.DataType = MASK_L2FLAGS_CHLOR_WARN_FAIL;
				else if (x.equals("SDS_IMAPP_CLOUDMASK"))
					temp.DataType = SDS_IMAPP_CLOUDMASK;
				else if (x.equals("SDS_VCM_CLOUDMASK"))
					temp.DataType = SDS_VCM_CLOUDMASK;
				else if (x.equals("SDS_VCM_CLOUDMASK_H5"))
					temp.DataType = SDS_VCM_CLOUDMASK_H5;
				else if (x.equals("SDS_VCM_PHASE_H5"))
					temp.DataType = SDS_VCM_PHASE_H5;
				else if (x.equals("SDS_VCM_LANDWATER_H5"))
					temp.DataType = SDS_VCM_LANDWATER_H5;
                else if (x.equals("SDS_VCM_PHASE_H5"))
					temp.DataType = SDS_VCM_PHASE_H5;
				else if (x.equals("SDS_VCM_DAYNIGHT_H5"))
					temp.DataType = SDS_VCM_DAYNIGHT_H5;
                else if (x.equals("SDS_VCM_SNOWICE_H5"))
					temp.DataType = SDS_VCM_SNOWICE_H5;
                else if (x.equals("SDS_VCM_SUNGLINT_H5"))
					temp.DataType = SDS_VCM_SUNGLINT_H5;
                else if (x.equals("VIIRS_SNOWCOVER_QUALITY"))
					temp.DataType = VIIRS_SNOWCOVER_QUALITY;
                else if (x.equals("VIIRS_AEROSOL_QUALITY"))
					temp.DataType = VIIRS_AEROSOL_QUALITY;
                else if (x.equals("VIIRS_SUM_QUALITY"))
					temp.DataType = VIIRS_SUM_QUALITY;
				


				temp.Multiplier = Double.parseDouble(byBlanks.nextToken());
				temp.FillValue_min = Integer.parseInt(byBlanks.nextToken());
				temp.FillValue_max = Integer.parseInt(byBlanks.nextToken());
				
				x = byBlanks.nextToken();
				if (x.equals("MODIS_250M"))
					temp.Resolution = MODIS_250M;
				else if (x.equals("MODIS_500M"))
					temp.Resolution = MODIS_500M;
				else if (x.equals("MODIS_1KM"))
					temp.Resolution = MODIS_1KM;
				else if (x.equals("MODIS_5KM"))
					temp.Resolution = MODIS_5KM;
				else if (x.equals("MODIS_10KM"))
					temp.Resolution = MODIS_10KM;
				else if (x.equals("VIIRS_750M"))
					temp.Resolution = VIIRS_750M;
				else if (x.equals("VIIRS_375M"))
					temp.Resolution = VIIRS_375M;
				else if (x.equals("VIIRS_750M_GTM"))
					temp.Resolution = VIIRS_750M_GTM;
				else if (x.equals("VIIRS_375M_GTM"))
					temp.Resolution = VIIRS_375M_GTM;
				else if (x.equals("OMPS_NADIR"))
					temp.Resolution = OMPS_NADIR;
				else if (x.equals("ATMS"))
					temp.Resolution = ATMS;
				else if (x.equals("CRIS"))
					temp.Resolution = CRIS;
				else {
					System.err.println("Unsupported Resolution:" + x);
					System.exit(1);
				}
				
				x = byBlanks.nextToken();
				if (x.equals("HDF4"))
					temp.ishdf5 = Boolean.FALSE;
				else if (x.equals("HDF5"))
					temp.ishdf5 = Boolean.TRUE;
				else {
					System.err.println("Unsupported Input Format:" + x);
					System.exit(1);
				}
				
				x = byBlanks.nextToken();
				if (x.equals("DEFAULT"))
					temp.trimType = DEFAULTTRIM; //Default trim equates to EDRs whose configuration files have the 'DATA_HOLE' keyword
				else if (x.equals("SDR"))
					temp.trimType = SDRTRIM;
				else {
					System.err.println("Unsupported Trim Format:" + x);
					System.exit(1);
				}
				
				// temp.displayKey();
				DataSetKeys.addElement(temp);

			}

			/*System.out.println("The size of vector are: " + DataSetKeys.size());
			for (int i = 0; i < DataSetPrimaryKeys.size(); i++) {
				DataSetKey temp2 = (DataSetKey) DataSetKeys.elementAt(i);
				System.out.println("*****" + DataSetPrimaryKeys.elementAt(i)
						+ "*****");
				temp2.displayKey();
			}*/

		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		
		/********* Reading Geo and Mask Dimension Match Algorithms ************/

		System.out.println(">> Loading DimensionMatchKeys");
		in = null;
		try {
			in = new BufferedReader(new InputStreamReader(Util
					.getInputStream(H2GHOME+ File.separator+"configFiles" + File.separator
							+ "DimensionMatchAlgoKeys.txt")));
			String line;
			GeoAlgoKeys = new Vector<SDSDimMatchAlgo>();
			MaskDiscAlgoKeys = new Vector<SDSDimMatchAlgo>();
			MaskContAlgoKeys = new Vector<SDSDimMatchAlgo>();
			GeoAlgoID = new Vector<Integer>();
			MaskDiscAlgoID = new Vector<Integer>();
			MaskContAlgoID = new Vector<Integer>();

			while ((line = in.readLine()) != null) {
				SDSDimMatchAlgo tempGeo = new SDSDimMatchAlgo();
				SDSDimMatchAlgo tempMaskDisc = new SDSDimMatchAlgo();
				SDSDimMatchAlgo tempMaskCont = new SDSDimMatchAlgo();
				int tempGeoID = UNDEFINED, tempMaskDiscID = UNDEFINED, tempMaskContID = UNDEFINED;

				StringTokenizer byBlanks = new StringTokenizer(line);
				String res = byBlanks.nextToken();

				if (res.equals("MODIS_250M")) {
					tempGeo.inputRes = MODIS_250M;
					tempMaskDisc.inputRes = MODIS_250M;
					tempMaskCont.inputRes = MODIS_250M;
				} else if (res.equals("MODIS_500M")) {
					tempGeo.inputRes = MODIS_500M;
					tempMaskDisc.inputRes = MODIS_500M;
					tempMaskCont.inputRes = MODIS_500M;

				} else if (res.equals("MODIS_1KM")) {
					tempGeo.inputRes = MODIS_1KM;
					tempMaskDisc.inputRes = MODIS_1KM;
					tempMaskCont.inputRes = MODIS_1KM;

				} else if (res.equals("MODIS_5KM")) {
					tempGeo.inputRes = MODIS_5KM;
					tempMaskDisc.inputRes = MODIS_5KM;
					tempMaskCont.inputRes = MODIS_5KM;

				} else if (res.equals("MODIS_10KM")) {
					tempGeo.inputRes = MODIS_10KM;
					tempMaskDisc.inputRes = MODIS_10KM;
					tempMaskCont.inputRes = MODIS_10KM;
					
				} else if (res.equals("VIIRS_750M")) {
					tempGeo.inputRes = VIIRS_750M;
					tempMaskDisc.inputRes = VIIRS_750M;
					tempMaskCont.inputRes = VIIRS_750M;

				} else if (res.equals("VIIRS_375M")) {
					tempGeo.inputRes = VIIRS_375M;
					tempMaskDisc.inputRes = VIIRS_375M;
					tempMaskCont.inputRes = VIIRS_375M;
					
				} else if (res.equals("VIIRS_750M_GTM")) {
					tempGeo.inputRes = VIIRS_750M_GTM;
					tempMaskDisc.inputRes = VIIRS_750M_GTM;
					tempMaskCont.inputRes = VIIRS_750M_GTM;

				} else if (res.equals("VIIRS_375M_GTM")) {
					tempGeo.inputRes = VIIRS_375M_GTM;
					tempMaskDisc.inputRes = VIIRS_375M_GTM;
					tempMaskCont.inputRes = VIIRS_375M_GTM;
				} else if (res.equals("OMPS_NADIR")) {
					tempGeo.inputRes = OMPS_NADIR;
					tempMaskDisc.inputRes = OMPS_NADIR;
					tempMaskCont.inputRes = OMPS_NADIR;
				} else if (res.equals("ATMS")) {
					tempGeo.inputRes = ATMS;
					tempMaskDisc.inputRes = ATMS;
					tempMaskCont.inputRes = ATMS;
				} else if (res.equals("CRIS")) {
					tempGeo.inputRes = CRIS;
					tempMaskDisc.inputRes = CRIS;
					tempMaskCont.inputRes = CRIS;
				} else {
					System.err.println("Unsupported Resolution:" + res);
					System.exit(1);
				}

				res = byBlanks.nextToken();

				if (res.equals("MODIS_250M")) {
					// System.out.println("inputRes 250M:"+MODIS_250M);
					tempGeo.outputRes = MODIS_250M;
					tempMaskDisc.outputRes = MODIS_250M;
					tempMaskCont.outputRes = MODIS_250M;
				} else if (res.equals("MODIS_500M")) {
					tempGeo.outputRes = MODIS_500M;
					tempMaskDisc.outputRes = MODIS_500M;
					tempMaskCont.outputRes = MODIS_500M;

				} else if (res.equals("MODIS_1KM")) {
					tempGeo.outputRes = MODIS_1KM;
					tempMaskDisc.outputRes = MODIS_1KM;
					tempMaskCont.outputRes = MODIS_1KM;

				} else if (res.equals("MODIS_5KM")) {
					tempGeo.outputRes = MODIS_5KM;
					tempMaskDisc.outputRes = MODIS_5KM;
					tempMaskCont.outputRes = MODIS_5KM;

				} else if (res.equals("MODIS_10KM")) {
					tempGeo.outputRes = MODIS_10KM;
					tempMaskDisc.outputRes = MODIS_10KM;
					tempMaskCont.outputRes = MODIS_10KM;
					
				} else if (res.equals("VIIRS_750M")) {
					tempGeo.outputRes = VIIRS_750M;
					tempMaskDisc.outputRes = VIIRS_750M;
					tempMaskCont.outputRes = VIIRS_750M;

				} else if (res.equals("VIIRS_375M")) {
					tempGeo.outputRes = VIIRS_375M;
					tempMaskDisc.outputRes = VIIRS_375M;
					tempMaskCont.outputRes = VIIRS_375M;
				} else if (res.equals("VIIRS_750M_GTM")) {
					tempGeo.outputRes = VIIRS_750M_GTM;
					tempMaskDisc.outputRes = VIIRS_750M_GTM;
					tempMaskCont.outputRes = VIIRS_750M_GTM;

				} else if (res.equals("VIIRS_375M_GTM")) {
					tempGeo.outputRes = VIIRS_375M_GTM;
					tempMaskDisc.outputRes = VIIRS_375M_GTM;
					tempMaskCont.outputRes = VIIRS_375M_GTM;
				} else if (res.equals("OMPS_NADIR")) {
					tempGeo.outputRes = OMPS_NADIR;
					tempMaskDisc.outputRes = OMPS_NADIR;
					tempMaskCont.outputRes = OMPS_NADIR;
				} else if (res.equals("ATMS")) {
					tempGeo.outputRes = ATMS;
					tempMaskDisc.outputRes = ATMS;
					tempMaskCont.outputRes = ATMS;
				} else if (res.equals("CRIS")) {
					tempGeo.outputRes = CRIS;
					tempMaskDisc.outputRes = CRIS;
					tempMaskCont.outputRes = CRIS;
				} else {
					System.err.println("Unsupported Resolution:" + res);
					System.exit(1);
				}
				res = byBlanks.nextToken();
				if (res.equals("DIM_MATCHES_SDS"))
					tempGeoID = DIM_MATCHES_SDS;
				else if (res.equals("UNDEFINED"))
					tempGeoID = UNDEFINED;
				else if (res.equals("MODIS_1KM_TO_250M")
						|| res.equals("MODIS_1KM_TO_500M"))
					tempGeoID = MODIS_1KM_TO_250M;
				else if (res.equals("MODIS_GEO_DOWNSCALE"))
					tempGeoID = MODIS_GEO_DOWNSCALE;
				else {
					System.err.println("Unsupported Geo Algorithm Key:" + res);
					System.exit(1);
				}
				res = byBlanks.nextToken();
				if (res.equals("DIM_MATCHES_SDS"))
					tempMaskContID = DIM_MATCHES_SDS;
				else if (res.equals("UNDEFINED"))
					tempMaskContID = UNDEFINED;
				else if (res.equals("MASK_UPSCALE"))
					tempMaskContID = MASK_UPSCALE;
				else if (res.equals("MASK_UPSCALE_GTM"))
					tempMaskContID = MASK_UPSCALE_GTM;
				else if (res.equals("MASK_CONT_DOWNSCALE"))
					tempMaskContID = MASK_CONT_DOWNSCALE;
				else if (res.equals("MASK_CONT_DOWNSCALE_GTM"))
					tempMaskContID = MASK_CONT_DOWNSCALE_GTM;
				else {
					System.err
							.println("Unsupported Mask Continous Algorithm Key:"
									+ res);
					System.exit(1);
				}
				res = byBlanks.nextToken();
				if (res.equals("DIM_MATCHES_SDS"))
					tempMaskDiscID = DIM_MATCHES_SDS;
				else if (res.equals("UNDEFINED"))
					tempMaskDiscID = UNDEFINED;
				else if (res.equals("MASK_UPSCALE"))
					tempMaskDiscID = MASK_UPSCALE;
				else if (res.equals("MASK_UPSCALE_GTM"))
					tempMaskContID = MASK_UPSCALE_GTM;
				else if (res.equals("MASK_DISC_DOWNSCALE"))
					tempMaskDiscID = MASK_DISC_DOWNSCALE;
				else if (res.equals("MASK_DISC_DOWNSCALE_GTM"))
					tempMaskDiscID = MASK_DISC_DOWNSCALE_GTM;
				else {
					System.err
							.println("Unsupported Mask Discrete Algorithm Key:"
									+ res);
					System.exit(1);
				}

				GeoAlgoKeys.addElement(tempGeo);
				MaskDiscAlgoKeys.addElement(tempMaskDisc);
				MaskContAlgoKeys.addElement(tempMaskCont);
				GeoAlgoID.addElement(tempGeoID);
				MaskDiscAlgoID.addElement(tempMaskDiscID);
				MaskContAlgoID.addElement(tempMaskContID);

			}
			/*
			 * System.out.println("The size of vector are: " +
			 * GeoAlgoKeys.size());
			 * 
			 * for (int i=0;i<GeoAlgoKeys.size();i++) {
			 * 
			 * SDSDimMatchAlgo temp1Geo=(SDSDimMatchAlgo)
			 * GeoAlgoKeys.elementAt(i); SDSDimMatchAlgo
			 * temp1MaskDisc=(SDSDimMatchAlgo) MaskDiscAlgoKeys.elementAt(i);
			 * SDSDimMatchAlgo temp1MaskCont=(SDSDimMatchAlgo)
			 * MaskContAlgoKeys.elementAt(i); int tempGeoID=(int)
			 * GeoAlgoID.elementAt(i); int tempMaskDiscID=(int)
			 * MaskDiscAlgoID.elementAt(i); int tempMaskContID=(int)
			 * MaskContAlgoID.elementAt(i); temp1Geo.displayKey();
			 * System.out.println(tempGeoID); temp1MaskCont.displayKey();
			 * System.out.println(tempMaskContID); temp1MaskDisc.displayKey();
			 * System.out.println(tempMaskDiscID);
			 * System.out.println("_______________________");
			 * 
			 * 
			 * }
			 */
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		
		
		System.out.println(">> Loading ColorMapKeys");
		in = null;
		try {
			in = new BufferedReader(new InputStreamReader(Util
					.getInputStream(H2GHOME+ File.separator+"configFiles" + File.separator
							+ "Colormaps.txt")));
			String line;
			ColorMapKeys = new Vector<String>();
			ColorMapPaths = new Vector<String>();

			while ((line = in.readLine()) != null) {

				StringTokenizer byBlanks = new StringTokenizer(line);
				ColorMapKeys.add(byBlanks.nextToken());
				ColorMapPaths.add(byBlanks.nextToken());

			}
			/*
			 * System.out.println("The size of ColorMap vector are: " +
			 * ColorMapKeys.size());
			 * 
			 * for (int i=0;i<ColorMapKeys.size();i++) {
			 * 
			 * System.out.println(ColorMapKeys.elementAt(i)+" "+ColorMapPaths.
			 * elementAt(i));
			 * 
			 * }
			 */

		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		
		
		in = null;
		System.out.println(">> Loading Standard ConfigNames");
		try {
			in = new BufferedReader(new InputStreamReader(Util
					.getInputStream(H2GHOME+ File.separator+"configFiles" + File.separator
							+ "configNames.txt")));
			String line;
			ConfigNames = new Vector<String>();
			ConfigTypes = new Vector<String>();
			ConfigPaths = new Vector<String>();

			while ((line = in.readLine()) != null) {
				// System.out.println("**"+line+"***");
				StringTokenizer byBlanks = new StringTokenizer(line);
				ConfigNames.add(byBlanks.nextToken());
				ConfigTypes.add(byBlanks.nextToken());
				ConfigPaths.add(byBlanks.nextToken());

			}
			/*
			 * System.out.println("The size of Config vector are: " +
			 * ConfigNames.size()); for (int i=0;i<ConfigNames.size();i++) {
			 * System
			 * .out.println(ConfigNames.elementAt(i)+" "+ConfigTypes.elementAt
			 * (i)+" "+ConfigPaths.elementAt(i));
			 * 
			 * }
			 */

		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		
		System.out.println(">> Loading ViirsTrimTable (SDR Moderate)");
		in = null;
		ViirsTrimTable_SDR_Mod=new int[VIIRS_750M][2];
		try {
			in = new BufferedReader(new InputStreamReader(Util
					.getInputStream(H2GHOME+ File.separator+"configFiles" + File.separator
							+ "ViirsTrimTable_SDR_Mod.txt")));
			String line;
			int rowno=0;
			while ((line = in.readLine()) != null) {
				// System.out.println("**"+line+"***");
				StringTokenizer byBlanks = new StringTokenizer(line);
				ViirsTrimTable_SDR_Mod[rowno][0] = Integer.parseInt(byBlanks.nextToken());
				ViirsTrimTable_SDR_Mod[rowno][1] = Integer.parseInt(byBlanks.nextToken());
				rowno++;
			}
			//for(rowno=0;rowno<VIIRS_750M;rowno++)
			//	System.out.println(ViirsTrimTable_Mod[rowno][0]+","+ViirsTrimTable_Mod[rowno][1]);
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		
		
		System.out.println(">> Loading ViirsTrimTable (SDR Imagery)");
		in = null;
		ViirsTrimTable_SDR_Img=new int[VIIRS_375M][2];
		try {
			in = new BufferedReader(new InputStreamReader(Util
					.getInputStream(H2GHOME+ File.separator+"configFiles" + File.separator
							+ "ViirsTrimTable_SDR_Img.txt")));
			String line;
			int rowno=0;
			while ((line = in.readLine()) != null) {
				// System.out.println("**"+line+"***");
				StringTokenizer byBlanks = new StringTokenizer(line);
				ViirsTrimTable_SDR_Img[rowno][0] = Integer.parseInt(byBlanks.nextToken());
				ViirsTrimTable_SDR_Img[rowno][1] = Integer.parseInt(byBlanks.nextToken());
				rowno++;
			}
			//for(rowno=0;rowno<VIIRS_375M;rowno++)
			//	System.out.println(ViirsTrimTable_Img[rowno][0]+","+ViirsTrimTable_Img[rowno][1]);
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		

		System.out.println(">> Loading ViirsTrimTable (EDR Moderate)");
		in = null;
		ViirsTrimTable_EDR_Mod=new int[VIIRS_750M][2];
		try {
			in = new BufferedReader(new InputStreamReader(Util
					.getInputStream(H2GHOME+ File.separator+"configFiles" + File.separator
							+ "ViirsTrimTable_EDR_Mod.txt")));
			String line;
			int rowno=0;
			while ((line = in.readLine()) != null) {
				// System.out.println("**"+line+"***");
				StringTokenizer byBlanks = new StringTokenizer(line);
				ViirsTrimTable_EDR_Mod[rowno][0] = Integer.parseInt(byBlanks.nextToken());
				ViirsTrimTable_EDR_Mod[rowno][1] = Integer.parseInt(byBlanks.nextToken());
				rowno++;
			}
			//for(rowno=0;rowno<VIIRS_750M;rowno++)
			//	System.out.println(ViirsTrimTable_Mod[rowno][0]+","+ViirsTrimTable_Mod[rowno][1]);
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		
		
		System.out.println(">> Loading ViirsTrimTable (EDR Imagery)");
		in = null;
		ViirsTrimTable_EDR_Img=new int[VIIRS_375M][2];
		try {
			in = new BufferedReader(new InputStreamReader(Util
					.getInputStream(H2GHOME+ File.separator+"configFiles" + File.separator
							+ "ViirsTrimTable_EDR_Img.txt")));
			String line;
			int rowno=0;
			while ((line = in.readLine()) != null) {
				// System.out.println("**"+line+"***");
				StringTokenizer byBlanks = new StringTokenizer(line);
				ViirsTrimTable_EDR_Img[rowno][0] = Integer.parseInt(byBlanks.nextToken());
				ViirsTrimTable_EDR_Img[rowno][1] = Integer.parseInt(byBlanks.nextToken());
				rowno++;
			}
			//for(rowno=0;rowno<VIIRS_375M;rowno++)
			//	System.out.println(ViirsTrimTable_Img[rowno][0]+","+ViirsTrimTable_Img[rowno][1]);
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		
		
		// System.err.println("Getting cfgtype="+cfgType+" cfgName="+cfgName);
		if (cfgType.equals("standard")) {
			System.out.println(">> Searching for standard configuration: " + cfgName);
			int index = ConfigNames.indexOf(cfgName);
			if (index < 0) {
				System.err.println("No such standard config*...exiting "
						+ cfgName);
				System.exit(1);
			}
			configType = ConfigTypes.get(index);
			configPath = ConfigPaths.get(index);
			System.out.println(">> Configuration File located:" + configPath);
		} else {
			configType = cfgType;
			configPath = cfgName;
		}

		System.out.println(">> Loading Configuration File.");
		if (configType.equals("singleband")) {
			readStandardConfig(cfgName, cfgType, configPath,arg_projection,arg_resolution,arg_lat0,arg_lon0,toplat,botlat,leftlon,rightlon, width_km, height_km);

		}
		if (configType.equals("rgb")) {
			readRGBConfig(cfgName, cfgType, configPath,arg_projection,arg_resolution,arg_lat0,arg_lon0,toplat,botlat,leftlon,rightlon, width_km, height_km);
		}
		
		
	
		
		
		
		System.out.println("Reading of Configuration Files Complete.");
	}

	private void readRGBConfig(String iType, String cfgType, String configPath, String arg_projection, double arg_resolution, double arg_lat0, double arg_lon0, double toplat, double botlat, double leftlon, double rightlon, double width_km, double height_km)
			throws Exception {
		int CONFIGFILEEXISTS = 1;
		String CONFIGFILEPATH = "";
		if (cfgType.equals("standard"))
			CONFIGFILEPATH = H2GHOME+ File.separator+"configFiles" + File.separator + configPath;
		else
			CONFIGFILEPATH = configPath;
		
		/*if (iType.equals("vtcolor") )
		{
			setRedisHDF5(Boolean.TRUE) ;
			setGreenisHDF5(Boolean.TRUE);
			setBlueisHDF5(Boolean.TRUE);
			setGEOisHDF5(Boolean.TRUE);
		}*/


		/*if (iType.equals("thermalrgb")) {
			// CONFIGFILEEXISTS=1;
			// CONFIGFILEPATH="configFiles"+File.separator+"thermalrgbconfig.txt";
			// GEODimMatchAlgo=DIM_MATCHES_SDS;
			// REDDimMatchAlgo=DIM_MATCHES_SDS;
			// GREENDimMatchAlgo=DIM_MATCHES_SDS;
			// BLUEDimMatchAlgo=DIM_MATCHES_SDS;
			// RGBDimToMatch=1;

		}*/

		if (CONFIGFILEEXISTS == 1) {
			DataSetKey temp = new DataSetKey();
			int index;
			/**** Initialize default values ****************************/
            if(getIsRGBinDifferentFiles())
            {
            	REDSource = REDDATASETFILE;
            	GREENSource = GREENDATASETFILE;
   			    BLUESource = BLUEDATASETFILE;
            }
            else
            {
			    REDSource = DATASETFILE;
			    GREENSource = DATASETFILE;
			    BLUESource = DATASETFILE;
            }
			GEOSource = GEOFILE;
			fireOverlay = Boolean.FALSE;
			isSubset = Boolean.FALSE;
			/******** End of Initialization of Default Values *********/
			
			BufferedReader in = null;
			try {
				if (cfgType.equals("standard"))
					in = new BufferedReader(new InputStreamReader(Util
							.getInputStream(CONFIGFILEPATH)));
				else
					in = new BufferedReader(new FileReader(CONFIGFILEPATH));
				// in= new BufferedReader(new FileReader(CONFIGFILEPATH));
				System.out.println("Reading File:" + CONFIGFILEPATH);
				String line;
				String keyword, value;
				while ((line = in.readLine()) != null) {

					StringTokenizer byBlanks = new StringTokenizer(line);
					keyword = byBlanks.nextToken();
					System.out.println("    Processing keyword=" + keyword);
					if (keyword.equals("REDDATASET")) {
						value = byBlanks.nextToken();
						index = DataSetPrimaryKeys.indexOf(value);
						if (index < 0) {
							System.err
									.println("Malformed Config File...exiting");
							System.exit(1);
						}
						temp = DataSetKeys.get(index);
						REDName = temp.PathSpecifier;
						REDIndex = temp.IndexSelections;
						REDDataType = temp.DataType;
						REDDoubleMultiplier = temp.Multiplier;
						REDFillvalue_min = temp.FillValue_min;
						REDFillvalue_max = temp.FillValue_max;
						REDRowsPerScan = temp.Resolution;
						setREDishdf5(temp.ishdf5);
						setREDtrimType(temp.trimType);

					}
					if (keyword.equals("GREENDATASET")) {
						value = byBlanks.nextToken();
						index = DataSetPrimaryKeys.indexOf(value);
						if (index < 0) {
							System.err
									.println("Malformed Config File...exiting");
							System.exit(1);
						}
						temp = DataSetKeys.get(index);
						GREENName = temp.PathSpecifier;
						GREENIndex = temp.IndexSelections;
						GREENDataType = temp.DataType;
						GREENDoubleMultiplier = temp.Multiplier;
						GREENFillvalue_min = temp.FillValue_min;
						GREENFillvalue_max = temp.FillValue_max;
						GREENRowsPerScan = temp.Resolution;
						setGREENishdf5(temp.ishdf5);
						setGREENtrimType(temp.trimType);

					}
					if (keyword.equals("BLUEDATASET")) {
						value = byBlanks.nextToken();
						index = DataSetPrimaryKeys.indexOf(value);
						if (index < 0) {
							System.err
									.println("Malformed Config File...exiting");
							System.exit(1);
						}
						temp = DataSetKeys.get(index);
						BLUEName = temp.PathSpecifier;
						BLUEIndex = temp.IndexSelections;
						BLUEDataType = temp.DataType;
						BLUEDoubleMultiplier = temp.Multiplier;
						BLUEFillvalue_min = temp.FillValue_min;
						BLUEFillvalue_max = temp.FillValue_max;
						BLUERowsPerScan = temp.Resolution;
						setBLUEishdf5(temp.ishdf5);
						setBLUEtrimType(temp.trimType);

					}

					if (keyword.equals("GEOLOCATION")) {
						// Get Latitude Dataset
						value = byBlanks.nextToken();
						index = DataSetPrimaryKeys.indexOf(value);
						if (index < 0) {
							System.err
									.println("Malformed Config File Undetermined Token "
											+ value + "...exiting");
							System.exit(1);
						}
						temp = DataSetKeys.get(index);
						System.out.println("geo keyname:" + temp.keyName);
						System.out.println("geo datatype:" + temp.DataType);
						GEOName_Lat = temp.PathSpecifier;
						GEOIndex_Lat = temp.IndexSelections;
						GEODataType = temp.DataType;
						GEOIntMultiplier = temp.Multiplier;
						GEORowsPerScan = temp.Resolution;
						System.out.println("Setting GeoRowsPerScan to "+GEORowsPerScan);
						// Get Longitude Dataset
						value = byBlanks.nextToken();
						index = DataSetPrimaryKeys.indexOf(value);
						if (index < 0) {
							System.err
									.println("Malformed Config File Undetermined Token "
											+ value + "...exiting");
							System.exit(1);
						}
						temp = DataSetKeys.get(index);
						GEOName_Lon = temp.PathSpecifier;
						GEOIndex_Lon = temp.IndexSelections;
						setGEOishdf5(temp.ishdf5);
					}
					if (keyword.equals("GEOSOURCE")) {
						value = byBlanks.nextToken();
						if (value.equals("DATASETFILE"))
							GEOSource = DATASETFILE;
						else if (value.equals("GEOFILE"))
							GEOSource = GEOFILE;
						else {
							System.err
									.println("Malformed Config File...GEOSOURCE can be either DATASETFILE or GEOFILE");
							System.exit(1);
						}

					}

					if (keyword.equals("FIRESOURCE")) {
						fireOverlay = Boolean.TRUE;
						value = byBlanks.nextToken();
						if (value.equals("DATASETFILE"))
							fireSource = DATASETFILE;
						else if (value.equals("GEOFILE"))
							fireSource = GEOFILE;
						else if (value.equals("MASKFILE"))
							fireSource = MASKFILE;
						else if (value.equals("FIREFILE"))
							fireSource = FIREFILE;
						else {
							System.err
									.println("Malformed Config File...FIRESOURCE can be either DATASETFILE or GEOFILE or MASKFILE or FIREFILE");
							System.exit(1);
						}
						
						

					}
					if (keyword.equals("REDSCALE")) {
						int npairs = Integer.parseInt(byBlanks.nextToken());
						redScale = new int[npairs * 2];
						for (int x = 0; x < npairs * 2; x++)
							redScale[x] = Integer
									.parseInt(byBlanks.nextToken());
						// redScale=new int[]
						// {0,1,1765,110,3530,160,7059,210,11176,240,15000,255};
						// blueScale=new int[]
						// {0,1,1765,110,3530,160,7059,210,11176,240,15000,255};
						// greenScale=new int[]
						// {0,1,1765,110,3530,160,7059,210,11176,240,15000,255};
					}
					if (keyword.equals("GREENSCALE")) {
						int npairs = Integer.parseInt(byBlanks.nextToken());
						greenScale = new int[npairs * 2];
						for (int x = 0; x < npairs * 2; x++)
							greenScale[x] = Integer.parseInt(byBlanks
									.nextToken());
						
					}
					if (keyword.equals("BLUESCALE")) {
						int npairs = Integer.parseInt(byBlanks.nextToken());
						blueScale = new int[npairs * 2];
						for (int x = 0; x < npairs * 2; x++)
							blueScale[x] = Integer.parseInt(byBlanks
									.nextToken());
						
					}

					if (keyword.equals("SCANS_PER_LOOP")) {
						ScansPerLoop = Integer.parseInt(byBlanks.nextToken());
					}
					if (keyword.equals("PROJECTION")) {
						value = byBlanks.nextToken();
						if (value.equals("GEOGRAPHIC")) {
							projectionIndex = GEOGRAPHIC;
							resolution = Double.parseDouble(byBlanks
									.nextToken());
							/*projectionIndex = STEREOGRAPHIC;
							resolution=1;
							lon_0=-76.85;
							lat_0=38.99;*/
						} else if (value.equals("NOPROJECTION")) {
							projectionIndex = NOPROJECTION;
							resolution = Double.parseDouble(byBlanks
									.nextToken());
						} else if (value.equals("STEREOGRAPHIC") || value.equals("TMERCATOR") || value.equals("LAZIMUTHAL")) {
							setProjectionIndex(value);							
							resolution = Double.parseDouble(byBlanks
									.nextToken());
							/* User selection of center of origin disabled for now*/
							/*lon_0 = Double.parseDouble(byBlanks
									.nextToken());
							lat_0 = Double.parseDouble(byBlanks
									.nextToken());*/
						} else if ( value.equals("MERCATOR") ||	value.equals("SINUSOIDAL") ) {
							
							setProjectionIndex(value);
							resolution = Double.parseDouble(byBlanks
									.nextToken());
							/* User selection of center of origin disabled for now*/
							/*lon_0 = Double.parseDouble(byBlanks
									.nextToken());		*/			
						} else {
							System.err
									.println("Malformed Config File Undetermined Token "
											+ value + "...exiting");
							System.exit(1);
						}						

					}
					
					if (keyword.equals("SUBSET")) {
						isSubset = Boolean.TRUE;
						minLat = Double.parseDouble(byBlanks.nextToken());
						maxLat = Double.parseDouble(byBlanks.nextToken());
						minLon = Double.parseDouble(byBlanks.nextToken());
						maxLon = Double.parseDouble(byBlanks.nextToken());
					}
					if (keyword.equals("IMAGEFILL")) {//for rgb this configuration will be ignored
						setInterpolationSpecified(Boolean.TRUE);
						setInterpolationAlgo(byBlanks.nextToken());
						if(getInterpolationAlgo().equals("INVDIST")){
						   setnPixels(Integer.parseInt(byBlanks.nextToken()));
						   setDistanceToSearch(Integer.parseInt(byBlanks.nextToken()));
						}
						else {
							System.err.println("Malformed Config File at IMAGEFILL ...exiting");
					        System.exit(1);
						}
					}
					if (keyword.equals("IMAGEFILTER")) {//for rgb this configuration will be ignored
						setImageFilterFlag(Boolean.TRUE);
						setImageFilterAlgo(byBlanks.nextToken());
						if(getImageFilterAlgo().equals("MEANFILTER"))
							setFilterNbd(Integer.parseInt(byBlanks.nextToken()));
						else {
							System.err.println("Malformed Config File at MEANFILTER ...exiting");
				            System.exit(1);
						}
					}
					if (keyword.equals("REMOVENOISE")) { //for rgb this configuration will be ignored
						setRemoveNoiseFlag(Boolean.TRUE);
						setNoiseNbd(Integer.parseInt(byBlanks.nextToken()));						
					}
					if (keyword.equals("DATA_HOLE")) {
						setHasDataHoles(Boolean.TRUE);
						String dataHoleValue=byBlanks.nextToken();
						if(dataHoleValue.equals("BOWTIE_UINT8"))
						{
							setBowtieFillEnabled(Boolean.TRUE);
							setDataHoleValue(BOWTIE_UINT8);
						}
						else if (dataHoleValue.equals("BOWTIE_UINT16"))
						{
							setBowtieFillEnabled(Boolean.TRUE);
							setDataHoleValue(BOWTIE_UINT16);
						}
						else if (dataHoleValue.equals("BOWTIE_DOUBLE_100"))
						{
							setBowtieFillEnabled(Boolean.TRUE);
							setDataHoleValue(BOWTIE_DOUBLE_100);
						}
						else if (dataHoleValue.equals("BOWTIE_DOUBLE_1000"))
						{
							setBowtieFillEnabled(Boolean.TRUE);
							setDataHoleValue(BOWTIE_DOUBLE_1000);
						}
						else
							setDataHoleValue(Integer.parseInt(dataHoleValue));
						    /*setDataHoleValue(Integer
								.parseInt(byBlanks.nextToken()));*/
						//System.out.println("has Data holes="+hasDataHoles+" "+getHasDataHoles()+" "+getDataHoleValue());
						
					}

				}

				// Set Up DIMMatchALgos
				int MAXRowsPerScan = Util.max(Util.max(BLUERowsPerScan,
						GREENRowsPerScan), REDRowsPerScan);

				if (MAXRowsPerScan == REDRowsPerScan)
					RGBDimToMatch = 1;
				else if (MAXRowsPerScan == GREENRowsPerScan)
					RGBDimToMatch = 2;
				else if (MAXRowsPerScan == BLUERowsPerScan)
					RGBDimToMatch = 3;
				
				/*Handle Special Cases for speeding up processing*/
				if (iType.equals("tcolor0_01") || iType.equals("tcolorfire0_01") )
				{
					RGBDimToMatch = 4;
					MAXRowsPerScan=GEORowsPerScan;
				}

				//System.out.println("RGBDimToMatch:"+RGBDimToMatch);

				SDSDimMatchAlgo thisRGB = new SDSDimMatchAlgo(REDRowsPerScan,
						MAXRowsPerScan);
				Boolean found = Boolean.FALSE;
				REDDimMatchAlgo = UNDEFINED;
				int i = -1;
				while (!found) {
					i++;
					SDSDimMatchAlgo currRGB = MaskContAlgoKeys
							.elementAt(i);
					if (currRGB.equals(thisRGB)) {
						found = Boolean.TRUE;
						REDDimMatchAlgo = MaskContAlgoID.get(i);
					}

				}
				// System.out.println("REDDimMatchALgo="+REDDimMatchAlgo);

				thisRGB = new SDSDimMatchAlgo(GREENRowsPerScan, MAXRowsPerScan);
				found = Boolean.FALSE;
				GREENDimMatchAlgo = UNDEFINED;
				i = -1;
				while (!found) {
					i++;
					SDSDimMatchAlgo currRGB = MaskContAlgoKeys
							.elementAt(i);
					if (currRGB.equals(thisRGB)) {
						found = Boolean.TRUE;
						GREENDimMatchAlgo = MaskContAlgoID.get(i);
					}

				}
				// System.out.println("GREENDimMatchALgo="+GREENDimMatchAlgo);

				thisRGB = new SDSDimMatchAlgo(BLUERowsPerScan, MAXRowsPerScan);
				found = Boolean.FALSE;
				BLUEDimMatchAlgo = UNDEFINED;
				i = -1;
				while (!found) {
					i++;
					SDSDimMatchAlgo currRGB = MaskContAlgoKeys
							.elementAt(i);
					if (currRGB.equals(thisRGB)) {
						found = Boolean.TRUE;
						BLUEDimMatchAlgo = MaskContAlgoID.get(i);
					}

				}
				// System.out.println("BLUEDimMatchALgo="+BLUEDimMatchAlgo);

				SDSDimMatchAlgo thisGeo = new SDSDimMatchAlgo(GEORowsPerScan,
						MAXRowsPerScan);
				found = Boolean.FALSE;
				GEODimMatchAlgo = UNDEFINED;
				i = -1;
				while (!found) {
					i++;
					SDSDimMatchAlgo currGeo = GeoAlgoKeys
							.elementAt(i);
					if (currGeo.equals(thisGeo)) {
						found = Boolean.TRUE;
						GEODimMatchAlgo = GeoAlgoID.get(i);
					}

				}
				//System.out.println("GEODimMatchALgo=" + GEODimMatchAlgo);
				
				
				//Check for Overriden arguments using command line arguments
				if(!arg_projection.equalsIgnoreCase("DEFAULT")){
					setProjectionIndex(arg_projection);					
				}
				if(arg_resolution!=-999.0)
					resolution=arg_resolution;
				if(arg_lat0!=-999.0)
					lat_0=arg_lat0;
				if(arg_lon0!=-999.0)
					lon_0=arg_lon0;
				//System.out.println("subsetparams: "+toplat+" "+botlat+" "+leftlon+" "+rightlon);
				if(toplat!=-999.0 && botlat!=-999.0 && leftlon!=-999.0 && rightlon!=-999.0){
					System.out.println("Command Line Subset Requested");
					isSubset = Boolean.TRUE;
					maxLat = toplat;
					minLat = botlat;
					minLon = leftlon;
					maxLon = rightlon;
				}
				if(width_km!=-999.0 && height_km!=-999.0 && arg_lat0!=-999.0 && arg_lon0!=-999.0){
					System.out.println("Command Line Subset Requested");
					isSubset = Boolean.TRUE;
					this.setWidth_km(width_km);
					this.setHeight_km(height_km);
					
				}
				/*if(isSubset && getProjection()!=GEOGRAPHIC)
				{
					System.out.println("WARNING: Subsetting is not available on selected Projection. Proceeding to project the whole granule");
					isSubset=Boolean.FALSE;
				}*/

				
			} finally {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public static int getSTEREOGRAPHIC() {
		return STEREOGRAPHIC;
	}

	public void setProjectionIndex(String projId) {
		if(projId.equalsIgnoreCase("STEREOGRAPHIC"))
			projectionIndex=STEREOGRAPHIC;
		else if (projId.equalsIgnoreCase("MERCATOR"))
			projectionIndex=MERCATOR;
		else if (projId.equalsIgnoreCase("TMERCATOR"))
			projectionIndex=TMERCATOR;
		else if (projId.equalsIgnoreCase("SINUSOIDAL"))
			projectionIndex=SINUSOIDAL;
		else if (projId.equalsIgnoreCase("LAZIMUTHAL"))
			projectionIndex=LAZIMUTHAL;
		else if (projId.equalsIgnoreCase("GEOGRAPHIC"))
			projectionIndex=GEOGRAPHIC;
		else if (projId.equalsIgnoreCase("NOPROJECTION"))
			projectionIndex=NOPROJECTION;
		else {
			System.err
					.println("Unknown projection argument "
							+ projId + "...exiting");
			System.exit(1);
		}
			
	}

	private void readStandardConfig(String iType, String cfgType, String configPath, String arg_projection, double arg_resolution, double arg_lat0, double arg_lon0, double toplat, double botlat, double leftlon, double rightlon, double width_km, double height_km)
			throws Exception {

		int CONFIGFILEEXISTS = 1;
		//Set image filling to nearest neighbor
		setnPixels(DEFAULTNPIXELS);
		setDistanceToSearch(DEFAULTDISTANCETOSEARCH);
		
		String CONFIGFILEPATH = "";
		if (cfgType.equals("standard"))
			CONFIGFILEPATH = H2GHOME+ File.separator+"configFiles" + File.separator + configPath;
		else
			CONFIGFILEPATH = configPath;

		if (CONFIGFILEEXISTS == 1) {
			DataSetKey temp = new DataSetKey();
			int index;
			/**** Initialize default values ****************************/
			MASKExists = Boolean.FALSE; // TRUE | FALSE
			fireFlag = Boolean.FALSE;
			fireValues = null;
			SDSSource = DATASETFILE; // DATASETFILE
			GEOSource = GEOFILE;
			MASKSource = MASKFILE;
			isSubset = Boolean.FALSE;
			hasLegend = Boolean.FALSE;
			fireOverlay = Boolean.FALSE;

			/******** End of Initialization of Default Values *********/
			BufferedReader in = null;
			try {
				if (cfgType.equals("standard"))
					in = new BufferedReader(new InputStreamReader(Util
							.getInputStream(CONFIGFILEPATH)));
				else
					in = new BufferedReader(new FileReader(CONFIGFILEPATH));
				System.out.println("Reading File:" + CONFIGFILEPATH);
				String line;
				String keyword, value;
				while ((line = in.readLine()) != null) {
					StringTokenizer byBlanks = new StringTokenizer(line);
					keyword = byBlanks.nextToken();
					System.out.println("    Processing keyword=" + keyword);
					if (keyword.equals("DATASET")) {
						value = byBlanks.nextToken();
						index = DataSetPrimaryKeys.indexOf(value);
						if (index < 0) {
							System.err
									.println("Malformed Config File...exiting");
							System.exit(1);
						}
						temp = DataSetKeys.get(index);
						SDSName = temp.PathSpecifier;
						SDSIndex = temp.IndexSelections;
						SDSDataType = temp.DataType;
						SDSDoubleMultiplier = temp.Multiplier;
						SDSFillvalue_min = temp.FillValue_min;
						SDSFillvalue_max = temp.FillValue_max;
						SDSRowsPerScan = temp.Resolution;
						SDSishdf5 = temp.ishdf5;
						setSDStrimType(temp.trimType);

					}

					if (keyword.equals("GEOLOCATION")) {
						// Get Latitude Dataset
						value = byBlanks.nextToken();
						index = DataSetPrimaryKeys.indexOf(value);
						if (index < 0) {
							System.err
									.println("Malformed Config File Undetermined Token "
											+ value + "...exiting");
							System.exit(1);
						}
						temp = DataSetKeys.get(index);
						// System.out.println("temp keyname:"+temp.keyName);
						// System.out.println("temp datatype:"+temp.DataType);
						GEOName_Lat = temp.PathSpecifier;
						GEOIndex_Lat = temp.IndexSelections;
						GEODataType = temp.DataType;
						GEOIntMultiplier = temp.Multiplier;
						GEORowsPerScan = temp.Resolution;
						// Get Longitude Dataset
						value = byBlanks.nextToken();
						index = DataSetPrimaryKeys.indexOf(value);
						if (index < 0) {
							System.err
									.println("Malformed Config File Undetermined Token "
											+ value + "...exiting");
							System.exit(1);
						}
						temp = DataSetKeys.get(index);
						GEOName_Lon = temp.PathSpecifier;
						GEOIndex_Lon = temp.IndexSelections;
						GEOishdf5 = temp.ishdf5;
					}
					if (keyword.equals("GEOSOURCE")) {
						value = byBlanks.nextToken();
						if (value.equals("DATASETFILE"))
							GEOSource = DATASETFILE;
						else if (value.equals("GEOFILE"))
							GEOSource = GEOFILE;
						else {
							System.err
									.println("Malformed Config File...GEOSOURCE can be either DATASETFILE or GEOFILE");
							System.exit(1);
						}

					}
					if (keyword.equals("MASK")) {
						MASKExists = Boolean.TRUE; // TRUE | FALSE
						System.out.println("Setting MASKExists to "
								+ MASKExists);
						value = byBlanks.nextToken();
						index = DataSetPrimaryKeys.indexOf(value);
						if (index < 0) {
							System.err
									.println("Malformed Config File Undetermined Token "
											+ value + "...exiting");
							System.exit(1);
						}
						temp = DataSetKeys.get(index);
						MASKName = temp.PathSpecifier;
						MASKIndex = temp.IndexSelections;
						MASKDataType = temp.DataType;
						MASKDoubleMultiplier = temp.Multiplier;
						MASKRowsPerScan = temp.Resolution;
						MASKishdf5 = temp.ishdf5;	
						value = byBlanks.nextToken();
						if (value.equals("DISCRETE"))
							MASKType = DISCRETE;
						else if (value.equals("CONTINUOUS"))
							MASKType = CONTINUOUS;
						else {
							System.err
									.println("Malformed Config File Undetermined Token "
											+ value + "...exiting");
							System.exit(1);
						}
						int no_maskvalues = Integer.parseInt(byBlanks
								.nextToken());
						System.out.println(MASKType);
						MASKValues = new int[no_maskvalues];
						System.out.println("MaskType" + MASKType + " "
								+ no_maskvalues);
						for (int i = 0; i < no_maskvalues; i++) {
							MASKValues[i] = Integer.parseInt(byBlanks
									.nextToken());
							System.out.println(MASKValues[i]);
						}

					}
					if (keyword.equals("MASKSOURCE")) {
						value = byBlanks.nextToken();
						if (value.equals("DATASETFILE"))
							MASKSource = DATASETFILE;
						else if (value.equals("GEOFILE"))
							MASKSource = GEOFILE;
						else if (value.equals("MASKFILE"))
							MASKSource = MASKFILE;
						else {
							System.err
									.println("Malformed Config File...GEOSOURCE can be either DATASETFILE or GEOFILE or MASKFILE");
							System.exit(1);
						}

					}
					if (keyword.equals("FIRESOURCE")) {
						fireOverlay = Boolean.TRUE;
						value = byBlanks.nextToken();
						if (value.equals("DATASETFILE"))
							fireSource = DATASETFILE;
						else if (value.equals("GEOFILE"))
							fireSource = GEOFILE;
						else if (value.equals("MASKFILE"))
							fireSource = MASKFILE;
						else if (value.equals("FIREFILE"))
							fireSource = FIREFILE;
						else {
							System.err
									.println("Malformed Config File...FIRESOURCE can be either DATASETFILE or GEOFILE or MASKFILE or FIREFILE");
							System.exit(1);
						}

					}
					if (keyword.equals("SCALING")) {
						value = byBlanks.nextToken();
						if (value.equals("LINEAR")) {
							scalingType = "LINEAR";
							double min = Double.parseDouble(byBlanks
									.nextToken());
							double max = Double.parseDouble(byBlanks
									.nextToken());
							double range = max - min;
							int MIN_INDEX = 1, MAX_INDEX = 255;
							int INDEX_RANGE = MAX_INDEX - MIN_INDEX;
							coefficients = new double[5];
							coefficients[0] = MIN_INDEX
									- (min * INDEX_RANGE / range);
							coefficients[1] = INDEX_RANGE / range;
							coefficients[2] = 0;
							coefficients[3] = 0;
							coefficients[4] = 0;
						} else if (value.equals("NONLINEAR")) {
							scalingType = "NONLINEAR";
							coefficients = new double[5];
							coefficients[0] = Double.parseDouble(byBlanks
									.nextToken());
							coefficients[1] = Double.parseDouble(byBlanks
									.nextToken());
							coefficients[2] = Double.parseDouble(byBlanks
									.nextToken());
							coefficients[3] = Double.parseDouble(byBlanks
									.nextToken());
							coefficients[4] = Double.parseDouble(byBlanks
									.nextToken());

						} else if (value.equals("SEGMENTED_LINEAR")) {
							scalingType = "SEGMENTED_LINEAR";
							int nsegments = Integer.parseInt(byBlanks
									.nextToken());
							coefficients = new double[nsegments * 2];
							for (int x = 0; x < nsegments * 2; x++)
								coefficients[x] = Double.parseDouble(byBlanks
										.nextToken());
						} else {
							System.err
									.println("Malformed Config File Undetermined Token "
											+ value + "...exiting");
							System.exit(1);
						}
					}
					if (keyword.equals("COLORMAP")) {
						colormapPath = byBlanks.nextToken();
					}
					if (keyword.equals("SCANS_PER_LOOP")) {
						ScansPerLoop = Integer.parseInt(byBlanks.nextToken());
					}
					if (keyword.equals("PROJECTION")) {
						value = byBlanks.nextToken();
						if (value.equals("GEOGRAPHIC")) {
							projectionIndex = GEOGRAPHIC;
							resolution = Double.parseDouble(byBlanks
									.nextToken());

						} else if (value.equals("NOPROJECTION")) {
							projectionIndex = NOPROJECTION;
							resolution = Double.parseDouble(byBlanks
									.nextToken());
						} else if (value.equals("STEREOGRAPHIC") || value.equals("TMERCATOR") || value.equals("LAZIMUTHAL")) {
							setProjectionIndex(value);
							resolution = Double.parseDouble(byBlanks
									.nextToken());
							/*lon_0 = Double.parseDouble(byBlanks
									.nextToken());
							lat_0 = Double.parseDouble(byBlanks
										.nextToken());*/
						} else if ( value.equals("MERCATOR") ||	value.equals("SINUSOIDAL") ) {
							
							setProjectionIndex(value);
							resolution = Double.parseDouble(byBlanks
									.nextToken());
							/*lon_0 = Double.parseDouble(byBlanks
									.nextToken());		*/			
						}  
						else {
							System.err
							.println("Malformed Config File Undetermined Token "
									+ value + "...exiting");
							System.exit(1);
						}


					}
					if (keyword.equals("SUBSET")) {
						isSubset = Boolean.TRUE;
						minLat = Double.parseDouble(byBlanks.nextToken());
						maxLat = Double.parseDouble(byBlanks.nextToken());
						minLon = Double.parseDouble(byBlanks.nextToken());
						maxLon = Double.parseDouble(byBlanks.nextToken());
					}
					if (keyword.equals("LEGEND")) {
						hasLegend = Boolean.TRUE;
						legend = byBlanks.nextToken();
						int nmarkers = Integer.parseInt(byBlanks.nextToken());
						markers = new Vector<Integer>();
						markerValues = new Vector<String>();
						for (int i = 0; i < nmarkers; i++) {
							markers.addElement(Integer.parseInt(byBlanks
									.nextToken()));
							markerValues.addElement(byBlanks.nextToken());
						}
					}
					if (keyword.equals("NO_INTERPOLATION")) {
						fireFlag = Boolean.TRUE;
						int nfirevalues = Integer
								.parseInt(byBlanks.nextToken());
						fireValues = new int[nfirevalues];
						for (int i = 0; i < nfirevalues; i++) {
							fireValues[i] = Integer.parseInt(byBlanks
									.nextToken());
							// System.out.println(i+" no_interp:"+fireValues[i]);
						}
					}
					if (keyword.equals("IMAGEFILL")) { 
						setInterpolationSpecified(Boolean.TRUE);
						setInterpolationAlgo(byBlanks.nextToken());
						if(getInterpolationAlgo().equals("INVDIST")){
						   setnPixels(Integer.parseInt(byBlanks.nextToken()));
						   setDistanceToSearch(Integer.parseInt(byBlanks.nextToken()));
						}
						else {
							System.err.println("Malformed Config File at IMAGEFILL ...exiting");
					        System.exit(1);
						}
					}
					if (keyword.equals("IMAGEFILTER")) { 
						setImageFilterFlag(Boolean.TRUE);
						setImageFilterAlgo(byBlanks.nextToken());
						if(getImageFilterAlgo().equals("MEANFILTER"))
							setFilterNbd(Integer.parseInt(byBlanks.nextToken()));
						else {
							System.err.println("Malformed Config File at MEANFILTER ...exiting");
				            System.exit(1);
						}
					}
					if (keyword.equals("REMOVENOISE")) { 
						setRemoveNoiseFlag(Boolean.TRUE);
						setNoiseNbd(Integer.parseInt(byBlanks.nextToken()));						
					}
					if (keyword.equals("DATA_HOLE")) {
						setHasDataHoles(Boolean.TRUE);
						String dataHoleValue=byBlanks.nextToken();
						if(dataHoleValue.equals("BOWTIE_UINT8"))
						{
							setBowtieFillEnabled(Boolean.TRUE);
							setDataHoleValue(BOWTIE_UINT8);
						}
						else if (dataHoleValue.equals("BOWTIE_UINT16"))
						{
							setBowtieFillEnabled(Boolean.TRUE);
							setDataHoleValue(BOWTIE_UINT16);
						}
						else if (dataHoleValue.equals("BOWTIE_DOUBLE_100"))
						{
							setBowtieFillEnabled(Boolean.TRUE);
							setDataHoleValue(BOWTIE_DOUBLE_100);
						}
						else if (dataHoleValue.equals("BOWTIE_DOUBLE_1000"))
						{
							setBowtieFillEnabled(Boolean.TRUE);
							setDataHoleValue(BOWTIE_DOUBLE_1000);
						}
						else
							setDataHoleValue(Integer.parseInt(dataHoleValue));
						    /*setDataHoleValue(Integer
								.parseInt(byBlanks.nextToken()));*/
						System.out.println("has Data holes="+hasDataHoles+" "+getHasDataHoles()+" "+getDataHoleValue());
						
					}
					if (keyword.equals("OPTIMIZATION")) {
						setUseOptimization(Boolean.TRUE);
						value = byBlanks.nextToken();
						if (value.equals("USEGEODIM1")) {
							setOptimizationIndex(USEGEODIM1);
						} else if (value.equals("USEGEODIM2")) {
							setOptimizationIndex(USEGEODIM2);
						} else if (value.equals("USEGEODIM4")) {
							setOptimizationIndex(USEGEODIM4);
						} 
						else {
							System.err
							.println("Malformed Config File Undetermined Token "
									+ value + "...exiting");
							System.exit(1);
						}
						
					}
					if(keyword.equals("3DDEPTH"))
						setThirdDimDepth(Integer.parseInt(byBlanks.nextToken()));
				}
				// Initialize
				// System.out.println("INSIDE Read standard Config resolution:"+resolution);
				/*for (int i = 0; i < GeoAlgoKeys.size(); i++) {

					SDSDimMatchAlgo temp1Geo = (SDSDimMatchAlgo) GeoAlgoKeys
							.elementAt(i);
					SDSDimMatchAlgo temp1MaskDisc = (SDSDimMatchAlgo) MaskDiscAlgoKeys
							.elementAt(i);
					SDSDimMatchAlgo temp1MaskCont = (SDSDimMatchAlgo) MaskContAlgoKeys
							.elementAt(i);
					int tempGeoID = (int) GeoAlgoID.elementAt(i);
					int tempMaskDiscID = (int) MaskDiscAlgoID.elementAt(i);
					int tempMaskContID = (int) MaskContAlgoID.elementAt(i);
					// temp1Geo.displayKey();
					// System.out.println(tempGeoID);
					// temp1MaskCont.displayKey();
					// System.out.println(tempMaskContID);
					// temp1MaskDisc.displayKey();
					// System.out.println(tempMaskDiscID);
					// System.out.println("_______________________");

				}*/
				SDSDimMatchAlgo thisGeo = new SDSDimMatchAlgo(GEORowsPerScan,
						SDSRowsPerScan);
				//System.out.println(GEORowsPerScan+" "+SDSRowsPerScan);
				Boolean found = Boolean.FALSE;
				GEODimMatchAlgo = UNDEFINED;
				int i = -1;
				while (!found) {
					i++;
					SDSDimMatchAlgo currGeo =  GeoAlgoKeys
							.elementAt(i);
					//GeoAlgoKeys.elementAt(i).displayKey(); System.out.println(i);//System.out.println(GeoAlgoKeys);
					//currGeo.displayKey(); System.out.println();
					if (currGeo.equals(thisGeo)) {
						found = Boolean.TRUE;
						GEODimMatchAlgo = GeoAlgoID.get(i);
					}

				}

				SDSDimMatchAlgo thisMask = new SDSDimMatchAlgo(MASKRowsPerScan,
						SDSRowsPerScan);
				found = Boolean.FALSE;
				MASKDimMatchAlgo = UNDEFINED;
				i = -1;
				if (MASKType == DISCRETE)
					while (!found) {
						System.out.println("MASK IS DISCRETE");
						i++;
						SDSDimMatchAlgo currMask = MaskDiscAlgoKeys
								.elementAt(i);
						if (currMask.equals(thisMask)) {
							found = Boolean.TRUE;
							MASKDimMatchAlgo = MaskDiscAlgoID.get(i);
						}

					}
				else if (MASKType == CONTINUOUS)
					while (!found) {
						i++;
						SDSDimMatchAlgo currMask = MaskContAlgoKeys
								.elementAt(i);
						if (currMask.equals(thisMask)) {
							found = Boolean.TRUE;
							MASKDimMatchAlgo = MaskContAlgoID.get(i);
						}

					}
				
				
				DATADimMatchAlgo=DIM_MATCHES_SDS;
				
				// System.out.println("GEODimMatchALgo="+GEODimMatchAlgo+"MASKDimMatchALgo="+MASKDimMatchAlgo);
				
				//Check for Overriden projection arguments using command line arguments
				if(!arg_projection.equalsIgnoreCase("DEFAULT")){
					setProjectionIndex(arg_projection);					
				}
				if(arg_resolution!=-999.0)
					resolution=arg_resolution;
				if(arg_lat0!=-999.0)
					lat_0=arg_lat0;
				if(arg_lon0!=-999.0)
					lon_0=arg_lon0;
				//System.out.println("subsetparams: "+arg_lat0+" "+arg_lon0+" "+toplat+" "+botlat+" "+leftlon+" "+rightlon);
				
				
				if(toplat!=-999.0 && botlat!=-999.0 && leftlon!=-999.0 && rightlon!=-999.0){
					System.out.println("Command Line Subset Requested");
					isSubset = Boolean.TRUE;
					maxLat = toplat;
					minLat = botlat;
					minLon = leftlon;
					maxLon = rightlon;
				}
				if(width_km!=-999.0 && height_km!=-999.0 && arg_lat0!=-999.0 && arg_lon0!=-999.0){
					System.out.println("Command Line Subset Requested");
					isSubset = Boolean.TRUE;
					this.setWidth_km(width_km);
					this.setHeight_km(height_km);
					
				}
				
				/*if(isSubset && getProjection()!=GEOGRAPHIC)
				{
					System.out.println("WARNING: Subsetting is not available on selected Projection. Proceeding to project the whole granule");
					isSubset=Boolean.FALSE;
				}*/


			} finally {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
			

		}

	}

	public Boolean getHasDataHoles() {
		return hasDataHoles;
	}

	public void setHasDataHoles(Boolean hasDataHoles) {
		this.hasDataHoles = hasDataHoles;
	}

	public Boolean getBowtieFillEnabled() {
		return bowtieFillEnabled;
	}

	public void setBowtieFillEnabled(Boolean bowtieFillEnabled) {
		this.bowtieFillEnabled = bowtieFillEnabled;
	}

	public int getDataHoleValue() {
		return DataHoleValue;
	}

	public void setDataHoleValue(int dataHoleValue) {
		DataHoleValue = dataHoleValue;
	}
	
	public Boolean getUseOptimization() {
		return useOptimization;
	}

	public void setUseOptimization(Boolean useOptimization) {
		this.useOptimization = useOptimization;
	}

	public int getOptimizationIndex() {
		return optimizationIndex;
	}

	public void setOptimizationIndex(int optimizationIndex) {
		this.optimizationIndex = optimizationIndex;
	}


	public double getLon_0() {
		return lon_0;
	}

	public double getLat_0() {
		return lat_0;
	}

	public int getREDSource() {
		return REDSource;
	}

	public int[] getREDIndex() {
		return REDIndex;
	}

	public String[] getREDName() {
		return REDName;
	}

	public int getREDFillvalue_min() {
		return REDFillvalue_min;
	}
	
	public int getREDFillvalue_max() {
		return REDFillvalue_max;
	}

	public int getREDDataType() {
		return REDDataType;
	}

	public double getREDDoubleMultiplier() {
		return REDDoubleMultiplier;
	}

	public int getREDRowsPerScan() {
		return REDRowsPerScan;
	}

	public int[] getredScale() {
		return redScale;
	}

	public int getGREENSource() {
		return GREENSource;
	}

	public int[] getGREENIndex() {
		return GREENIndex;
	}

	public String[] getGREENName() {
		return GREENName;
	}

	public int getGREENFillvalue_min() {
		return GREENFillvalue_min;
	}
	
	public int getGREENFillvalue_max() {
		return GREENFillvalue_max;
	}

	public int getGREENDataType() {
		return GREENDataType;
	}

	public double getGREENDoubleMultiplier() {
		return GREENDoubleMultiplier;
	}

	public int getGREENRowsPerScan() {
		return GREENRowsPerScan;
	}

	public int[] getgreenScale() {
		return greenScale;
	}

	public int getBLUESource() {
		return BLUESource;
	}

	public int[] getBLUEIndex() {
		return BLUEIndex;
	}

	public String[] getBLUEName() {
		return BLUEName;
	}

	public int getBLUEFillvalue_min() {
		return BLUEFillvalue_min;
	}
	
	public int getBLUEFillvalue_max() {
		return BLUEFillvalue_max;
	}

	public int getBLUEDataType() {
		return BLUEDataType;
	}

	public double getBLUEDoubleMultiplier() {
		return BLUEDoubleMultiplier;
	}

	public int getBLUERowsPerScan() {
		return BLUERowsPerScan;
	}

	public int[] getblueScale() {
		return blueScale;
	}

	public int getRGBDimToMatch() {
		return RGBDimToMatch;
	}

	private int getREDDimMatchAlgo() {
		return REDDimMatchAlgo;
	}

	private int getGREENDimMatchAlgo() {
		return GREENDimMatchAlgo;
	}

	private int getBLUEDimMatchAlgo() {
		return BLUEDimMatchAlgo;
	}

	public Boolean getfireOverlay() {
		return fireOverlay;
	}

	public int getfireSource() {
		return fireSource;
	}

	public int getSDSSource() {
		return SDSSource;
	}

	public int[] getSDSIndex() {
		return SDSIndex;
	}

	public String[] getSDSName() {
		return SDSName;
	}

	public int getSDSFillvalue_min() {
		return SDSFillvalue_min;
	}
	
	public int getSDSFillvalue_max() {
		return SDSFillvalue_max;
	}

	public int getSDSDataType() {
		return SDSDataType;
	}

	public double getSDSDoubleMultiplier() {
		return SDSDoubleMultiplier;
	}

	public int getSDSRowsPerScan() {
		return SDSRowsPerScan;
	}

	public int getGEOSource() {
		return GEOSource;
	}

	public int[] getGEOIndex_Lat() {
		return GEOIndex_Lat;
	}

	public int[] getGEOIndex_Lon() {
		return GEOIndex_Lon;
	}

	public String[] getGEOName_Lat() {
		return GEOName_Lat;
	}

	public String[] getGEOName_Lon() {
		return GEOName_Lon;
	}

	public int getGEODataType() {
		return GEODataType;
	}

	public int getGEORowsPerScan() {
		return GEORowsPerScan;
	}

	private int getGEODimMatchAlgo() {
		return GEODimMatchAlgo;
	}

	private double getGEOIntMultiplier() {
		return GEOIntMultiplier;
	}

	public Boolean getMASKExists() {
		return MASKExists;
	}

	public int getMASKDataType() {
		return MASKDataType;
	}

	private String[] getMASKName() {
		return MASKName;
	}

	private int getMASKSource() {
		return MASKSource;
	}

	private int[] getMASKIndex() {
		return MASKIndex;
	}

	private int getMASKDimMatchAlgo() {
		return MASKDimMatchAlgo;
	}

	public int[] getMASKValues() {
		return MASKValues;
	}

	public double getMASKDoubleMultiplier() {
		return MASKDoubleMultiplier;
	}

	public int getMASKType() {
		return MASKType;
	}

	public String getscalingType() {
		return scalingType;
	}

	public double[] getCoefficients() {
		return coefficients;
	}

	public String getColormapPath() {
		int index = ColorMapKeys.indexOf(colormapPath);
		if (index < 0) {
			System.out.println("Non Standard ColorMap being Used "
					+ colormapPath);
			return colormapPath;
		}
		String path = H2GHOME+File.separator+ColorMapPaths.get(index);
		return path;
	}

	public Boolean isStandardColorMap() {
		int index = ColorMapKeys.indexOf(colormapPath);
		if (index < 0) {
			// System.out.println("Non Standard ColorMap being Used "+colormapPath);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public int getMASKRowsPerScan() {
		return MASKRowsPerScan;
	}

	public int getScansPerLoop() {
		return ScansPerLoop;
	}

	public int getProjection() {
		return projectionIndex;
	}

	/**
	 * @return the interpolationAlgo
	 */
	public String getInterpolationAlgo() {
		return interpolationAlgo;
	}

	/**
	 * @param interpolationAlgo the interpolationAlgo to set
	 */
	public void setInterpolationAlgo(String interpolationAlgo) {
		this.interpolationAlgo = interpolationAlgo;
	}

	public int getnPixels() {
		return nPixels;
	}

	public void setnPixels(int nPixels) {
		this.nPixels = nPixels;
	}

	public int getDistanceToSearch() {
		return distanceToSearch;
	}

	public void setDistanceToSearch(int distanceToSearch) {
		this.distanceToSearch = distanceToSearch;
	}

	/**
	 * @return the interpolationSpecified
	 */
	public boolean isInterpolationSpecified() {
		return interpolationSpecified;
	}

	/**
	 * @param interpolationSpecified the interpolationSpecified to set
	 */
	public void setInterpolationSpecified(boolean interpolationSpecified) {
		this.interpolationSpecified = interpolationSpecified;
	}

	/**
	 * @return the imageFilterFlag
	 */
	public boolean isImageFilterFlag() {
		return imageFilterFlag;
	}

	/**
	 * @param imageFilterFlag the imageFilterFlag to set
	 */
	public void setImageFilterFlag(boolean imageFilterFlag) {
		this.imageFilterFlag = imageFilterFlag;
	}

	/**
	 * @return the imageFilterAlgo
	 */
	public String getImageFilterAlgo() {
		return imageFilterAlgo;
	}

	/**
	 * @param imageFilterAlgo the imageFilterAlgo to set
	 */
	public void setImageFilterAlgo(String imageFilterAlgo) {
		this.imageFilterAlgo = imageFilterAlgo;
	}

	/**
	 * @return the filterNbd
	 */
	public int getFilterNbd() {
		return filterNbd;
	}

	/**
	 * @param filterNbd the filterNbd to set
	 */
	public void setFilterNbd(int filterNbd) {
		this.filterNbd = filterNbd;
	}

	/**
	 * @return the removeNoiseFlag
	 */
	public boolean isRemoveNoiseFlag() {
		return removeNoiseFlag;
	}

	/**
	 * @param removeNoiseFlag the removeNoiseFlag to set
	 */
	public void setRemoveNoiseFlag(boolean removeNoiseFlag) {
		this.removeNoiseFlag = removeNoiseFlag;
	}

	/**
	 * @return the noiseNbd
	 */
	public int getNoiseNbd() {
		return noiseNbd;
	}

	/**
	 * @param noiseNbd the noiseNbd to set
	 */
	public void setNoiseNbd(int noiseNbd) {
		this.noiseNbd = noiseNbd;
	}

	public double getResolution() {
		return resolution;
	}

	public Boolean getisSubset() {
		return isSubset;
	}

	public double getminLat() {
		return minLat;
	}

	public double getmaxLat() {
		return maxLat;
	}

	public double getminLon() {
		return minLon;
	}

	public double getmaxLon() {
		return maxLon;
	}

	public String getconfigType() {
		return configType;
	}

	public Vector<String> getmarkerValues() {
		return markerValues;
	}

	public Vector<Integer> getmarkers() {
		return markers;
	}

	public String getlegend() {
		return legend;
	}

	public Boolean gethasLegend() {
		return hasLegend;
	}

	

	public String getCfgName() {
		return cfgName;
	}

	public Boolean getfireFlag() {
		return fireFlag;
	}

	public int[] getfireValues() {
		return fireValues;
	}

	

	public Boolean getIsRGBinDifferentFiles() {
		return isRGBinDifferentFiles;
	}

	public void setIsRGBinDifferentFiles(Boolean isRGBinDifferentFiles) {
		this.isRGBinDifferentFiles = isRGBinDifferentFiles;
	}
	
	

	/*public Boolean getRedisHDF5() {
		return redisHDF5;
	}

	public void setRedisHDF5(Boolean redisHDF5) {
		this.redisHDF5 = redisHDF5;
	}

	public Boolean getGreenisHDF5() {
		return greenisHDF5;
	}

	public void setGreenisHDF5(Boolean greenisHDF5) {
		this.greenisHDF5 = greenisHDF5;
	}

	public Boolean getBlueisHDF5() {
		return blueisHDF5;
	}

	public void setBlueisHDF5(Boolean blueisHDF5) {
		this.blueisHDF5 = blueisHDF5;
	}
	
	
	public Boolean getGEOisHDF5() {
		return GEOisHDF5;
	}

	public void setGEOisHDF5(Boolean oisHDF5) {
		GEOisHDF5 = oisHDF5;
	}*/
	
	public Boolean getREDishdf5() {
		return REDishdf5;
	}

	public void setREDishdf5(Boolean dishdf5) {
		REDishdf5 = dishdf5;
	}

	public Boolean getGREENishdf5() {
		return GREENishdf5;
	}

	public void setGREENishdf5(Boolean nishdf5) {
		GREENishdf5 = nishdf5;
	}

	public Boolean getBLUEishdf5() {
		return BLUEishdf5;
	}

	public void setBLUEishdf5(Boolean eishdf5) {
		BLUEishdf5 = eishdf5;
	}

	public Boolean getSDSishdf5() {
		return SDSishdf5;
	}

	public void setSDSishdf5(Boolean sishdf5) {
		SDSishdf5 = sishdf5;
	}

	public Boolean getGEOishdf5() {
		return GEOishdf5;
	}

	public void setGEOishdf5(Boolean oishdf5) {
		GEOishdf5 = oishdf5;
	}
	
	

	public Boolean getMASKishdf5() {
		return MASKishdf5;
	}

	public void setMASKishdf5(Boolean kishdf5) {
		MASKishdf5 = kishdf5;
	}

	/**
	 * @return the rEDtrimType
	 */
	public int getREDtrimType() {
		return REDtrimType;
	}

	/**
	 * @param rEDtrimType the rEDtrimType to set
	 */
	public void setREDtrimType(int rEDtrimType) {
		REDtrimType = rEDtrimType;
	}

	/**
	 * @return the gREENtrimType
	 */
	public int getGREENtrimType() {
		return GREENtrimType;
	}

	/**
	 * @param gREENtrimType the gREENtrimType to set
	 */
	public void setGREENtrimType(int gREENtrimType) {
		GREENtrimType = gREENtrimType;
	}

	/**
	 * @return the bLUEtrimType
	 */
	public int getBLUEtrimType() {
		return BLUEtrimType;
	}

	/**
	 * @param bLUEtrimType the bLUEtrimType to set
	 */
	public void setBLUEtrimType(int bLUEtrimType) {
		BLUEtrimType = bLUEtrimType;
	}

	/**
	 * @return the sDStrimType
	 */
	public int getSDStrimType() {
		return SDStrimType;
	}

	/**
	 * @param sDStrimType the sDStrimType to set
	 */
	public void setSDStrimType(int sDStrimType) {
		SDStrimType = sDStrimType;
	}

	public void setGEODimMatchAlgo(int dimMatchAlgo) {
		GEODimMatchAlgo = dimMatchAlgo;
	}

	public void setMASKDimMatchAlgo(int dimMatchAlgo) {
		MASKDimMatchAlgo = dimMatchAlgo;
	}

	public void setDATADimMatchAlgo(int dimMatchAlgo) {
		DATADimMatchAlgo = dimMatchAlgo;
	}

	public int[][] getSDSInts(SDSReader dataReader, int startrowSDS,
			int startcolSDS, int RowsSDS, int ColsSDS, int rowStride,
			int colStride) throws Exception {
		int[][] data = null;
		double[][] doubles = null;
		switch (getSDSDataType()) {
		case INTEGERS:
			if(getSDSishdf5())
				data = dataReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride,getThirdDimDepth());
				else
			data = dataReader.getInts(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			if(getBowtieFillEnabled())
			{
				int ScansInIteration=data.length/getSDSRowsPerScan();
				for (int scanno=0;scanno<ScansInIteration;scanno++)
				{
					int ystart=scanno*getSDSRowsPerScan();
					for(int y=0;y<getSDSRowsPerScan();y++)
						for (int x=0;x<data[0].length;x++)
						{
							if(getSDStrimType()==SDRTRIM){
								if(getSDSRowsPerScan()==VIIRS_750M)
								{
									if(x<ViirsTrimTable_SDR_Mod[y][0] || x>ViirsTrimTable_SDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(getSDSRowsPerScan()==VIIRS_375M)
								{
									if(x<ViirsTrimTable_SDR_Img[y][0] || x>ViirsTrimTable_SDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
							if(getSDStrimType()==DEFAULTTRIM) {
								if(getSDSRowsPerScan()==VIIRS_750M)
								{
									if(x<ViirsTrimTable_EDR_Mod[y][0] || x>ViirsTrimTable_EDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(getSDSRowsPerScan()==VIIRS_375M)
								{
									if(x<ViirsTrimTable_EDR_Img[y][0] || x>ViirsTrimTable_EDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
						}	
				}
			}
			break;
		case DOUBLES:
			data = new int[RowsSDS][ColsSDS];
			if(getSDSishdf5())
				doubles = dataReader.getHDF5Doubles(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride,getThirdDimDepth());
			else
				doubles = dataReader.getDoubles(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride);
			
			int ScansInIteration=doubles.length/getSDSRowsPerScan();
			for (int scanno=0;scanno<ScansInIteration;scanno++)
			{
				int ystart=scanno*getSDSRowsPerScan();
				for(int y=0;y<getSDSRowsPerScan();y++)
					for (int x=0;x<data[0].length;x++)
					{
						//data[ystart+y][x] = (int) Math.round(getSDSDoubleMultiplier()
						//		* doubles[ystart+y][x]);
						//if(doubles[ystart+y][x]==-32767) System.out.println(data[ystart+y][x]);
					    if(doubles[ystart+y][x]>=getSDSFillvalue_min() && doubles[ystart+y][x]<=getSDSFillvalue_max())
							data[ystart+y][x] = getSDSFillvalue_min();
						else							
						    data[ystart+y][x] = (int) Math.round(getSDSDoubleMultiplier()
								* doubles[ystart+y][x]);
						
						if(getBowtieFillEnabled())
						{
							if(getSDStrimType()==SDRTRIM){
								if(getSDSRowsPerScan()==VIIRS_750M)
								{
									if(x<ViirsTrimTable_SDR_Mod[y][0] || x>ViirsTrimTable_SDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(getSDSRowsPerScan()==VIIRS_375M)
								{
									if(x<ViirsTrimTable_SDR_Img[y][0] || x>ViirsTrimTable_SDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
							if(getSDStrimType()==DEFAULTTRIM) {
								if(getSDSRowsPerScan()==VIIRS_750M)
								{
									if(x<ViirsTrimTable_EDR_Mod[y][0] || x>ViirsTrimTable_EDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(getSDSRowsPerScan()==VIIRS_375M)
								{
									if(x<ViirsTrimTable_EDR_Img[y][0] || x>ViirsTrimTable_EDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
						}
					}	
			}
			
			
			/*for (int y = 0; y < doubles.length; y++) {
				for (int x = 0; x < doubles[0].length; x++) {
					if(doubles[y][x]!=getSDSFillvalue())
					  data[y][x] = (int) Math.round(getSDSDoubleMultiplier()
							* doubles[y][x]);
					else
						 data[y][x] = (int) doubles[y][x];
				}
			}*/
			break;
		case SDS_IMAPP_CLOUDMASK:
			data = dataReader.getInts(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			for (int y = 0; y < data.length; y++) {
				for (int x = 0; x < data[0].length; x++) {
					if ((data[y][x] & 0x00000001) == 0)
						data[y][x] = 0;
					else
						data[y][x] = ((data[y][x] & 0x00000006) >> 1) + 1;
				}
			}
			break;
		case SDS_VCM_CLOUDMASK:
			data = dataReader.getInts(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			for (int y = 0; y < data.length; y++) {
				for (int x = 0; x < data[0].length; x++) {
					if(data[y][x]==252)
						data[y][x]=252;
					else if ((data[y][x] & 0x00000003) == 0)
						data[y][x] = 0;
					else
						data[y][x] = ((data[y][x] & 0x0000000C) >> 2) + 1;
				}
			}
			break;
		case SDS_VCM_CLOUDMASK_H5:
			data = dataReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			ScansInIteration=data.length/getSDSRowsPerScan();
			for (int scanno=0;scanno<ScansInIteration;scanno++)
			{
				int ystart=scanno*getSDSRowsPerScan();
				for(int y=0;y<getSDSRowsPerScan();y++)
					for (int x=0;x<data[0].length;x++)
					{
						if ((data[ystart+y][x] & 0x00000003) == 0)
							data[ystart+y][x] = 0;
						else
							data[ystart+y][x] = ((data[ystart+y][x] & 0x0000000C) >> 2) + 1;
						
						if(getBowtieFillEnabled())
						{
							if(getSDStrimType()==SDRTRIM){
								if(getSDSRowsPerScan()==VIIRS_750M)
								{
									if(x<ViirsTrimTable_SDR_Mod[y][0] || x>ViirsTrimTable_SDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(getSDSRowsPerScan()==VIIRS_375M)
								{
									if(x<ViirsTrimTable_SDR_Img[y][0] || x>ViirsTrimTable_SDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
							if(getSDStrimType()==DEFAULTTRIM) {
								if(getSDSRowsPerScan()==VIIRS_750M)
								{
									if(x<ViirsTrimTable_EDR_Mod[y][0] || x>ViirsTrimTable_EDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(getSDSRowsPerScan()==VIIRS_375M)
								{
									if(x<ViirsTrimTable_EDR_Img[y][0] || x>ViirsTrimTable_EDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
						}
						
					}
			}
			/*for (int y = 0; y < data.length; y++) {
				for (int x = 0; x < data[0].length; x++) {
					if(data[y][x]==252)
						data[y][x]=252;
					else if ((data[y][x] & 0x00000003) == 0)
						data[y][x] = 0;
					else
						data[y][x] = ((data[y][x] & 0x0000000C) >> 2) + 1;
				}
			}*/
			break;
		case SDS_VCM_PHASE_H5:
			data = dataReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			ScansInIteration=data.length/getSDSRowsPerScan();
			for (int scanno=0;scanno<ScansInIteration;scanno++)
			{
				int ystart=scanno*getSDSRowsPerScan();
				for(int y=0;y<getSDSRowsPerScan();y++)
					for (int x=0;x<data[0].length;x++)
					{
						data[ystart+y][x] = (data[ystart+y][x] & 0x00000007);
						
						if(getBowtieFillEnabled())
						{
							if(getSDStrimType()==SDRTRIM){
								if(getSDSRowsPerScan()==VIIRS_750M)
								{
									if(x<ViirsTrimTable_SDR_Mod[y][0] || x>ViirsTrimTable_SDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(getSDSRowsPerScan()==VIIRS_375M)
								{
									if(x<ViirsTrimTable_SDR_Img[y][0] || x>ViirsTrimTable_SDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
							if(getSDStrimType()==DEFAULTTRIM) {
								if(getSDSRowsPerScan()==VIIRS_750M)
								{
									if(x<ViirsTrimTable_EDR_Mod[y][0] || x>ViirsTrimTable_EDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(getSDSRowsPerScan()==VIIRS_375M)
								{
									if(x<ViirsTrimTable_EDR_Img[y][0] || x>ViirsTrimTable_EDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
						}
						
					}
			}			
			break;	
			
		case SDS_VCM_LANDWATER_H5:
			data = dataReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			ScansInIteration=data.length/getSDSRowsPerScan();
			for (int scanno=0;scanno<ScansInIteration;scanno++)
			{
				int ystart=scanno*getSDSRowsPerScan();
				for(int y=0;y<getSDSRowsPerScan();y++)
					for (int x=0;x<data[0].length;x++)
					{
						data[ystart+y][x] = (data[ystart+y][x] & 0x00000007);
						
						if(getBowtieFillEnabled())
						{
							if(getSDStrimType()==SDRTRIM){
								if(getSDSRowsPerScan()==VIIRS_750M)
								{
									if(x<ViirsTrimTable_SDR_Mod[y][0] || x>ViirsTrimTable_SDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(getSDSRowsPerScan()==VIIRS_375M)
								{
									if(x<ViirsTrimTable_SDR_Img[y][0] || x>ViirsTrimTable_SDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
							if(getSDStrimType()==DEFAULTTRIM) {
								if(getSDSRowsPerScan()==VIIRS_750M)
								{
									if(x<ViirsTrimTable_EDR_Mod[y][0] || x>ViirsTrimTable_EDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(getSDSRowsPerScan()==VIIRS_375M)
								{
									if(x<ViirsTrimTable_EDR_Img[y][0] || x>ViirsTrimTable_EDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
						}
						
					}
			}			
			break;
		default:
			System.err.println(" Aborting UNKNOWN SDS DATATYPE: "
					+ getSDSDataType());
			System.exit(1);
			break;
		}
		return (data);
	}

	public int[][] getRGBInts(SDSReader dataReader, int startrowSDS,
			int startcolSDS, int RowsSDS, int ColsSDS, int rowStride,
			int colStride, int bandindicator) throws Exception {
		int[][] data = null;
		double[][] doubles = null;
		int datatypeindex = -999;
		double RGBDoubleMultiplier=1.0;
		int RGBFillValue_min=0;
		int RGBFillValue_max=0;
		int fillValueChecker;
		Boolean isHDF5=Boolean.FALSE;
		int rowsPerScan=0;
		int trimType=-1;
		switch (bandindicator) {
		case 1:
			datatypeindex = getREDDataType();
			isHDF5=getREDishdf5();
			RGBDoubleMultiplier=getREDDoubleMultiplier();
			RGBFillValue_min=getREDFillvalue_min();
			RGBFillValue_max=getREDFillvalue_max();
			rowsPerScan=getREDRowsPerScan();
			trimType=getREDtrimType();			
			break;
		case 2:
			datatypeindex = getGREENDataType();
			isHDF5=getGREENishdf5();
			RGBDoubleMultiplier=getGREENDoubleMultiplier();
			RGBFillValue_min=getGREENFillvalue_min();
			RGBFillValue_max=getGREENFillvalue_max();
			rowsPerScan=getGREENRowsPerScan();
			trimType=getGREENtrimType();
			break;
		case 3:
			datatypeindex = getBLUEDataType();
			isHDF5=getBLUEishdf5();
			RGBDoubleMultiplier=getBLUEDoubleMultiplier();
			RGBFillValue_min=getBLUEFillvalue_min();
			RGBFillValue_max=getBLUEFillvalue_max();
			rowsPerScan=getBLUERowsPerScan();
			trimType=getBLUEtrimType();
			break;
		}
		switch (datatypeindex) {

		case INTEGERS:
			if(isHDF5)
				data = dataReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride);
			else
				data = dataReader.getInts(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride);
			if(getBowtieFillEnabled())
			{
				int ScansInIteration=data.length/rowsPerScan;
				for (int scanno=0;scanno<ScansInIteration;scanno++)
				{
					int ystart=scanno*rowsPerScan;
					for(int y=0;y<rowsPerScan;y++)
						for (int x=0;x<data[0].length;x++)
						{
							if(trimType==SDRTRIM){
								if(rowsPerScan==VIIRS_750M)
								{
									if(x<ViirsTrimTable_SDR_Mod[y][0] || x>ViirsTrimTable_SDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(rowsPerScan==VIIRS_375M)
								{
									if(x<ViirsTrimTable_SDR_Img[y][0] || x>ViirsTrimTable_SDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
							if(trimType==DEFAULTTRIM){
								if(rowsPerScan==VIIRS_750M)
								{
									if(x<ViirsTrimTable_EDR_Mod[y][0] || x>ViirsTrimTable_EDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(rowsPerScan==VIIRS_375M)
								{
									if(x<ViirsTrimTable_EDR_Img[y][0] || x>ViirsTrimTable_EDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
						}	
				}
			}
			break;
		case DOUBLES:
			data = new int[RowsSDS][ColsSDS];
			if(isHDF5)
				doubles = dataReader.getHDF5Doubles(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride);
			else
				doubles = dataReader.getDoubles(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride);
			
			int ScansInIteration=doubles.length/rowsPerScan;
			for (int scanno=0;scanno<ScansInIteration;scanno++)
			{
				int ystart=scanno*rowsPerScan;
				for(int y=0;y<rowsPerScan;y++)
					for (int x=0;x<data[0].length;x++)
					{
						//data[ystart+y][x] = (int) Math.round(RGBDoubleMultiplier
						//		* doubles[ystart+y][x]);
						//fillValueChecker=(int) Math.round(doubles[ystart+y][x]*RGBDoubleMultiplier);		
						if(doubles[ystart+y][x]>=RGBFillValue_min && doubles[ystart+y][x]<=RGBFillValue_max)
						{	
							data[ystart+y][x] = RGBFillValue_min;
														
						}
						else
							data[ystart+y][x] = (int) Math.round(RGBDoubleMultiplier
									* doubles[ystart+y][x]);

						if(getBowtieFillEnabled())
						{
							if(trimType==SDRTRIM){
								if(rowsPerScan==VIIRS_750M)
								{
									if(x<ViirsTrimTable_SDR_Mod[y][0] || x>ViirsTrimTable_SDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(rowsPerScan==VIIRS_375M)
								{
									if(x<ViirsTrimTable_SDR_Img[y][0] || x>ViirsTrimTable_SDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
							if(trimType==DEFAULTTRIM){
								if(rowsPerScan==VIIRS_750M)
								{
									if(x<ViirsTrimTable_EDR_Mod[y][0] || x>ViirsTrimTable_EDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(rowsPerScan==VIIRS_375M)
								{
									if(x<ViirsTrimTable_EDR_Img[y][0] || x>ViirsTrimTable_EDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
						}
					}	
			}
			
			/*for (int y = 0; y < doubles.length; y++) {
				for (int x = 0; x < doubles[0].length; x++) {
		            fillValueChecker=(int) Math.round(doubles[y][x]*RGBDoubleMultiplier);		
					if(fillValueChecker!=RGBFillValue)
					{	
					data[y][x] = (int) Math.round(RGBDoubleMultiplier
							* doubles[y][x]);
					if (x==360 && y==7) System.out.println(RGBFillValue+":"+doubles[y][x]+"*"+RGBDoubleMultiplier+"="+data[y][x]);
					}
					else
						 //data[y][x] = (int) doubles[y][x];
						data[y][x] = RGBFillValue;
				}
			}*/
			break;
		case MASK_L2FLAGS_CHLOR_WARN_FAIL:
			data = dataReader.getInts(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			int CHL_WARN = 0x00200000;
			int CHL_FAIL = 0x00008000;
					for (int y = 0; y < data.length; y++)
				for (int x = 0; x < data[0].length; x++) {
					if ((data[y][x] & CHL_WARN) != 0)
						data[y][x] = CHLOR_WARNING;
					else if ((data[y][x] & CHL_FAIL) != 0)
						data[y][x] = CHLOR_FAIL;
					else
						data[y][x] = CHLOR_OK;
				}
			break;
		case SDS_IMAPP_CLOUDMASK:
			data = dataReader.getInts(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			for (int y = 0; y < data.length; y++) {
				for (int x = 0; x < data[0].length; x++) {
					if ((data[y][x] & 0x00000001) == 0)
						data[y][x] = 0;
					else
						data[y][x] = ((data[y][x] & 0x00000006) >> 1) + 1;
				}
			}
			break;
		case SDS_VCM_CLOUDMASK:
			data = dataReader.getInts(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			for (int y = 0; y < data.length; y++) {
				for (int x = 0; x < data[0].length; x++) {
					if(data[y][x]==252)
						data[y][x]=252;
					else if ((data[y][x] & 0x00000003) == 0)
						data[y][x] = 0;
					else
						data[y][x] = ((data[y][x] & 0x0000000C) >> 2) + 1;
				}
			}
			break;
			
		case SDS_VCM_CLOUDMASK_H5:
			data = dataReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			ScansInIteration=data.length/getSDSRowsPerScan();
			for (int scanno=0;scanno<ScansInIteration;scanno++)
			{
				int ystart=scanno*getSDSRowsPerScan();
				for(int y=0;y<getSDSRowsPerScan();y++)
					for (int x=0;x<data[0].length;x++)
					{
						if ((data[ystart+y][x] & 0x00000003) == 0)
							data[ystart+y][x] = 0;
						else
							data[ystart+y][x] = ((data[ystart+y][x] & 0x0000000C) >> 2) + 1;
						
						if(getBowtieFillEnabled())
						{
							if(trimType==SDRTRIM){
								if(rowsPerScan==VIIRS_750M)
								{
									if(x<ViirsTrimTable_SDR_Mod[y][0] || x>ViirsTrimTable_SDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(rowsPerScan==VIIRS_375M)
								{
									if(x<ViirsTrimTable_SDR_Img[y][0] || x>ViirsTrimTable_SDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
							if(trimType==DEFAULTTRIM){
								if(rowsPerScan==VIIRS_750M)
								{
									if(x<ViirsTrimTable_EDR_Mod[y][0] || x>ViirsTrimTable_EDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(rowsPerScan==VIIRS_375M)
								{
									if(x<ViirsTrimTable_EDR_Img[y][0] || x>ViirsTrimTable_EDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
						}
					}
			}
			/*for (int y = 0; y < data.length; y++) {
				for (int x = 0; x < data[0].length; x++) {
					if(data[y][x]==252)
						data[y][x]=252;
					else if ((data[y][x] & 0x00000003) == 0)
						data[y][x] = 0;
					else
						data[y][x] = ((data[y][x] & 0x0000000C) >> 2) + 1;
				}
			}*/
			break;
		case SDS_VCM_PHASE_H5:
			data = dataReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			ScansInIteration=data.length/getSDSRowsPerScan();
			for (int scanno=0;scanno<ScansInIteration;scanno++)
			{
				int ystart=scanno*getSDSRowsPerScan();
				for(int y=0;y<getSDSRowsPerScan();y++)
					for (int x=0;x<data[0].length;x++)
					{
						data[ystart+y][x] = (data[ystart+y][x] & 0x00000007);
						
						if(getBowtieFillEnabled())
						{
							if(trimType==SDRTRIM){
								if(rowsPerScan==VIIRS_750M)
								{
									if(x<ViirsTrimTable_SDR_Mod[y][0] || x>ViirsTrimTable_SDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(rowsPerScan==VIIRS_375M)
								{
									if(x<ViirsTrimTable_SDR_Img[y][0] || x>ViirsTrimTable_SDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
							if(trimType==DEFAULTTRIM){
								if(rowsPerScan==VIIRS_750M)
								{
									if(x<ViirsTrimTable_EDR_Mod[y][0] || x>ViirsTrimTable_EDR_Mod[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
								else if(rowsPerScan==VIIRS_375M)
								{
									if(x<ViirsTrimTable_EDR_Img[y][0] || x>ViirsTrimTable_EDR_Img[y][1])
										data[ystart+y][x]=getDataHoleValue();
								}
							}
						}
					}
			}			
			break;	
		default:
			System.err.println(" Aborting UNKNOWN SDS DATATYPE: "
					+ getSDSDataType());
			System.exit(1);
			break;
		}
		return (data);
	}

	public int[][] getMASKInts(SDSReader maskReader, int startrowSDS,
			int startcolSDS, int RowsSDS, int ColsSDS, int rowStride,
			int colStride) throws Exception {
		int[][] mask = null;
		double[][] doubles = null;
		switch (getMASKDataType()) {
		case INTEGERS:
			if(getMASKishdf5())
				mask = maskReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride);
			else
				mask = maskReader.getInts(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride);
			System.out.println("in get Maskints RowsSDS=" + RowsSDS
					+ " mask height=" + maskReader.getHeight());
			break;
		case DOUBLES:
			mask = new int[RowsSDS][ColsSDS];
			if(getMASKishdf5())
				doubles = maskReader.getHDF5Doubles(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride);
			else
				doubles = maskReader.getDoubles(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride);
			for (int y = 0; y < doubles.length; y++) {
				for (int x = 0; x < doubles[0].length; x++) {
					if(doubles[y][x]!=getMASKFillvalue())
					    mask[y][x] = (int) Math.round(getMASKDoubleMultiplier()
							* doubles[y][x]);
					else
						 mask[y][x] = (int) doubles[y][x];
					
				}
			}
			break;
		case MASK_L2FLAGS_CHLOR_WARN_FAIL:
			mask = maskReader.getInts(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			int CHL_WARN = 0x00200000;
			int CHL_FAIL = 0x00008000;
		
		
			for (int i = 0; i < mask.length; i++)
				for (int j = 0; j < mask[0].length; j++) {
					if ((mask[i][j] & CHL_WARN) != 0)
						mask[i][j] = CHLOR_WARNING;
					else if ((mask[i][j] & CHL_FAIL) != 0)
						mask[i][j] = CHLOR_FAIL;
					else
						mask[i][j] = CHLOR_OK;
				}
			break;
		case SDS_IMAPP_CLOUDMASK:
			mask = maskReader.getInts(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			for (int y = 0; y < mask.length; y++) {
				for (int x = 0; x < mask[0].length; x++) {
					if ((mask[y][x] & 0x00000001) == 0)
						mask[y][x] = 0;
					else
						mask[y][x] = ((mask[y][x] & 0x00000006) >> 1) + 1;
				}
			}
			break;
		case SDS_VCM_CLOUDMASK:
			mask = maskReader.getInts(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			for (int y = 0; y < mask.length; y++) {
				for (int x = 0; x < mask[0].length; x++) {
					if(mask[y][x]==252)
						mask[y][x]=252;
					else if ((mask[y][x] & 0x00000003) == 0)
						mask[y][x] = 0;
					else
						mask[y][x] = ((mask[y][x] & 0x0000000C) >> 2) + 1;
				}
			}
			break;
		case SDS_VCM_CLOUDMASK_H5:
			mask = maskReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			for (int y = 0; y < mask.length; y++) {
				for (int x = 0; x < mask[0].length; x++) {
					if(mask[y][x]==252)
						mask[y][x]=252;
					else if ((mask[y][x] & 0x00000003) == 0)
						mask[y][x] = 0;
					else
						mask[y][x] = ((mask[y][x] & 0x0000000C) >> 2) + 1;
				}
			}
			break;

		case SDS_VCM_LANDWATER_H5:
			mask = maskReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			for(int y=0;y<mask.length;y++) {
				for (int x=0;x<mask[0].length;x++) {
					mask[y][x] = (mask[y][x] & 0x00000007);						
				}
			}			
			break;
       case SDS_VCM_DAYNIGHT_H5:
			mask = maskReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			for(int y=0;y<mask.length;y++) {
				for (int x=0;x<mask[0].length;x++) {
					mask[y][x] = ((mask[y][x] & 0x00000010)>>4);						
				}
			}			
			break;
       case SDS_VCM_SNOWICE_H5:
			mask = maskReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			for(int y=0;y<mask.length;y++) {
				for (int x=0;x<mask[0].length;x++) {
					mask[y][x] = ((mask[y][x] & 0x00000020)>>5);						
				}
			}			
			break;
       case SDS_VCM_SUNGLINT_H5:
			mask = maskReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			for(int y=0;y<mask.length;y++) {
				for (int x=0;x<mask[0].length;x++) {
					mask[y][x] = ((mask[y][x] & 0x000000C0)>>6);						
				}
			}			
			break;
       case VIIRS_SNOWCOVER_QUALITY:
			mask = maskReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			for(int y=0;y<mask.length;y++) {
				for (int x=0;x<mask[0].length;x++) {
					mask[y][x] = ((mask[y][x] & 0x00000003));						
				}
			}			
			break;
			
       case VIIRS_AEROSOL_QUALITY:
			mask = maskReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			for(int y=0;y<mask.length;y++) {
				for (int x=0;x<mask[0].length;x++) {
					mask[y][x] = ((mask[y][x] & 0x00000003));						
				}
			}			
			break;	
       case VIIRS_SUM_QUALITY:
			mask = maskReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
					ColsSDS, rowStride, colStride);
			for(int y=0;y<mask.length;y++) {
				for (int x=0;x<mask[0].length;x++) {
					mask[y][x] = ((mask[y][x] & 0x0000000C) >> 2);						
				}
			}			
			break;
		default:
			System.err.println(" Aborting UNKNOWN MASK DATATYPE: "
					+ getMASKDataType());
			System.exit(1);
			break;
		}
		return (mask);
	}

	private double getMASKFillvalue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double[][] getGEODoubles(SDSReader latlonReader, int startrowSDS,
			int startcolSDS, int RowsSDS, int ColsSDS, int rowStride,
			int colStride, Boolean CrossOver, double distFrom180)
			throws Exception {

		double latlon[][] = null;
		int[][] ints = null;
		switch (getGEODataType()) {
		case DOUBLES:
			if(getGEOishdf5())
				latlon = latlonReader.getHDF5Doubles(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride);
			else
				latlon = latlonReader.getDoubles(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride);
			break;
		case INTEGERS:
			latlon = new double[RowsSDS][ColsSDS];
			if(getGEOishdf5())
				ints = latlonReader.getHDF5Ints(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride);
			else
				ints = latlonReader.getInts(startrowSDS, startcolSDS, RowsSDS,
						ColsSDS, rowStride, colStride);
			for (int y = 0; y < ints.length; y++) {
				for (int x = 0; x < ints[0].length; x++) {
					latlon[y][x] = getGEOIntMultiplier() * ints[y][x];
				}
			}
			break;
		default:
			System.err.println(" Aborting UNKNOWN GEO DATATYPE: "
					+ getGEODataType());
			System.exit(1);
			break;
		}
		
		//System.out.println(CrossOver);
		if (CrossOver && getProjection()==Configuration.GEOGRAPHIC) {
			

			double refLon = 180.0 - distFrom180;
			for (int y = 0; y < latlon.length; y++) {
				for (int x = 0; x < latlon[0].length; x++) {

					if (Util.inRange(-180.0, latlon[y][x], 180.0)) {
						if (latlon[y][x] < 0)
							latlon[y][x] = latlon[y][x] + distFrom180;
						else
							latlon[y][x] = -180.0 + (latlon[y][x] - refLon);
						if (latlon[y][x] < -180)
							latlon[y][x] = -181;
					} else {

						latlon[y][x] = -999.0;

					}

				}

			}

		}
		// else
		// System.out.println("CrossOver is False");

		return latlon;
	}
	
	public SDSReader getSDSReader(File[] iFiles) throws Exception {
		SDSReader dataReader = null;
		if(getSDSishdf5())
				dataReader = new SDSReader(iFiles[getSDSSource()],getSDSName()[0], getSDSIndex());
		else
				dataReader = new SDSReader(iFiles[getSDSSource()],getSDSName(), getSDSIndex());
		
		return (dataReader);
	}


	public SDSReader getLatReader(File[] iFiles) throws Exception {
		SDSReader latReader = null;
		//System.out.println("in getlatreader "+iFiles[getGEOSource()]+" "+getGEOSource());
		if (getGEOSource() == GEOFILE || getGEOSource() == DATASETFILE)
		{
			if(getGEOishdf5())
				latReader = new SDSReader(iFiles[getGEOSource()], getGEOName_Lat()[0],
						getGEOIndex_Lat());
			else
				latReader = new SDSReader(iFiles[getGEOSource()], getGEOName_Lat(),
						getGEOIndex_Lat());
		}
		else {
			System.err
					.println("GEOLOCATION SOURCE CAN BE EITHER GEOFILE OR DATASETFILE. Aborting ...");
			System.exit(1);
		}

		/*
		 * switch(getGeolocationSource()){ case MOD03: latReader = new
		 * SDSReader(iFiles[1],new String[]
		 * {"MODIS_Swath_Type_GEO","Geolocation Fields","Latitude"},new int[]
		 * {0,1}); break; case INPUTHDF: latReader=new SDSReader(iFiles[0],new
		 * String[] {"Latitude"},new int[] {0,1}); break; default:
		 * System.err.println("   ERROR: UNRECOGNIZED GEOLOCATION SOURCE: " +
		 * getGeolocationSource()); System.exit(1); }
		 */

		return (latReader);
	}

	public SDSReader getLonReader(File[] iFiles) throws Exception {
		SDSReader lonReader = null;
		if (getGEOSource() == GEOFILE || getGEOSource() == DATASETFILE)
		{
			if(getGEOishdf5())
				lonReader = new SDSReader(iFiles[getGEOSource()], getGEOName_Lon()[0],
						getGEOIndex_Lon());
			else
				lonReader = new SDSReader(iFiles[getGEOSource()], getGEOName_Lon(),
						getGEOIndex_Lon());
		}
		else {
			System.err
					.println("GEOLOCATION SOURCE CAN BE EITHER GEOFILE OR DATASETFILE. Aborting ...");
			System.exit(1);
		}
		/*
		 * switch(getGeolocationSource()){ case MOD03: lonReader = new
		 * SDSReader(iFiles[1],new String[]
		 * {"MODIS_Swath_Type_GEO","Geolocation Fields","Longitude"},new int[]
		 * {0,1}); break; case INPUTHDF: lonReader = new SDSReader(iFiles[0],new
		 * String[] {"Longitude"},new int[] {0,1}); break; default:
		 * System.err.println("   ERROR: UNRECOGNIZED GEOLOCATION SOURCE: " +
		 * getGeolocationSource()); System.exit(1); }
		 */

		return (lonReader);

	}

	public GeoReader matchSDS_GEODim(GeoReader geoReader, SDSReader dataReader,
			SDSReader latReader, SDSReader lonReader) throws Exception {
		int xSDS = dataReader.getWidth();
		int ySDS = dataReader.getHeight();
		int xGeo = latReader.getWidth();
		int yGeo = latReader.getHeight();
		int MODISSCAN_GEO = getGEORowsPerScan();
		//System.out.println("Matching Geolocation dimensions to Data dimensions (if required)");
		//System.out.println("   Data Dimensions: "+xSDS+","+ySDS+" ; Geolocation dimensions:"+xGeo+","+yGeo);

		switch (getGEODimMatchAlgo()) {
		case DIM_MATCHES_SDS:
			if (xSDS != xGeo || ySDS != yGeo) {
				System.err
						.println("   ERROR: DIMENSION OF GEOLOCATION DOES NOT MATCH SDS (USE CORRECT VALUE FOR GEODIMMATHCHALGO)");
				System.exit(1);
			}
			break;

		case MODIS_1KM_TO_250M:
			System.out
					.println(">>>> Upscaling Geolocation to match data resolution");
			double GeoScaleFactorX = xSDS / xGeo;
			double GeoScaleFactorY = ySDS / yGeo;
			if ((GeoScaleFactorX == 2 && GeoScaleFactorY == 2)
					|| (GeoScaleFactorX == 4 && GeoScaleFactorY == 4))
				for (int scaleindex = 1; scaleindex < GeoScaleFactorX; scaleindex = scaleindex * 2)
					geoReader.doubleGeoResolution(MODISSCAN_GEO * scaleindex);
			else {
				System.err
						.println("   ERROR: MODIS_1KM_TO_250M CAN BE USED WHEN SDS DIMENSION ARE EXACTLY 2 or 4 TIMES GEOLOCATION DIMENSION");
				System.exit(1);
			}
			break;
		case MODIS_GEO_DOWNSCALE:
			System.out.println("This GEODimMatchAlgo Not Implemented Yet"); // xGEO
																			// muslt
																			// be
																			// an
																			// integer
																			// multiple
																			// of
																			// xSDS
			System.exit(1);
			break;

		default:
			System.err.println("   ERROR: UNRECOGNIZED GEODIMMATCHALGO: "
					+ getGEODimMatchAlgo());
			System.exit(1);
		}
		//System.out.println("Matching Geolocation dimensions to Data dimensions Complete.");
		return (geoReader);
	}

	public SDSReader getMASKReader(File[] iFiles) throws Exception {
		SDSReader maskReader = null;
		if (getMASKExists()) {
			if (getMASKSource() == MASKFILE || getMASKSource() == DATASETFILE)
			{
				if(getMASKishdf5())
					maskReader = new SDSReader(iFiles[getMASKSource()],getMASKName()[0],getMASKIndex());
				else
					maskReader = new SDSReader(iFiles[getMASKSource()],
							getMASKName(), getMASKIndex());
			}
			else {
				System.err
						.println("MASK SOURCE CAN BE EITHER MASKFILE OR DATASETFILE. Aborting ...");
				System.exit(1);
			}

		} else
			System.out
					.println("   MASKEXISTS IS SET TO FALSE : RETURNING maskReader=null");

		return (maskReader);

	}
	
	public int getDATADimMatchAlgo() {
		return DATADimMatchAlgo;
	}

	public int[][] matchSDS_DataDim(int[][] data, SDSReader dataReader,
			int ySDS, int xSDS) throws Exception {

		int xData = data[0].length;// maskReader.getWidth();
		int yData = data.length;// maskReader.getHeight();
		// int MaskScaleFactor=xSDS/xMask;

		/*double MaskScaleFactorX_d;
		double MaskScaleFactorY_d;
		int MaskScaleFactorX;
		int MaskScaleFactorY;*/

		System.out.println("In matchSDS_DataDim xData=" + xData + " yData=" + yData + " xSDS="
				+ xSDS + " ySDS=" + ySDS);

		switch (getDATADimMatchAlgo()) {
		case DIM_MATCHES_SDS:
			if (xSDS != xData || ySDS != yData) {
				System.err
						.println("   ERROR: DIMENSION OF DATA DOES NOT MATCH SDS (USE CORRECT VALUE FOR DIMMATHCHALGO)");
				System.exit(1);
			}
			break;
		case MASK_CONT_DOWNSCALE:
			data = dataReader.downscaleContinuousMask(data, ySDS, xSDS);
			break;

		case MASK_DISC_DOWNSCALE:
			data = dataReader.downscaleDiscreteMask(data, ySDS, xSDS);
			break;
		case MASK_UPSCALE:
			data = dataReader.upscaleMask(data, ySDS, xSDS);
			break;
		
		default:
			System.err.println("   ERROR: UNRECOGNIZED DIMMATCHALGO: "
					+ getDATADimMatchAlgo());
			System.exit(1);
		}

		return (data);
	}

	public int[][] matchSDS_MASKDim(int[][] mask, SDSReader maskReader,
			int ySDS, int xSDS) throws Exception {

		int xMask = mask[0].length;// maskReader.getWidth();
		int yMask = mask.length;// maskReader.getHeight();
		// int MaskScaleFactor=xSDS/xMask;

		/*double MaskScaleFactorX_d;
		double MaskScaleFactorY_d;
		int MaskScaleFactorX;
		int MaskScaleFactorY;*/

		//System.out.println("xMask=" + xMask + " yMask=" + yMask + " xSDS="
		//		+ xSDS + " ySDS=" + ySDS);

		switch (getMASKDimMatchAlgo()) {
		case DIM_MATCHES_SDS:
			if (xSDS != xMask || ySDS != yMask) {
				System.err
						.println("   ERROR: DIMENSION OF MASK DOES NOT MATCH SDS (USE CORRECT VALUE FOR MASKDIMMATHCHALGO)");
				System.exit(1);
			}
			break;
		case MASK_CONT_DOWNSCALE:
			mask = maskReader.downscaleContinuousMask(mask, ySDS, xSDS);
			break;

		case MASK_DISC_DOWNSCALE:
			mask = maskReader.downscaleDiscreteMask(mask, ySDS, xSDS);
			break;
		case MASK_UPSCALE:
			mask = maskReader.upscaleMask(mask, ySDS, xSDS);
			break;
		/*
		 * case MODIS_1KM_TO_250M: double MaskScaleFactorX=xSDS/xMask; double
		 * MaskScaleFactorY=ySDS/yMask; if ( (MaskScaleFactorX==2 &&
		 * MaskScaleFactorY==2) || (MaskScaleFactorX==4 && MaskScaleFactorY==4))
		 * for (int
		 * scaleindex=1;scaleindex<MaskScaleFactorX;scaleindex=scaleindex2)
		 * mask=maskReader.doubleMASKResolution(mask,0,9,0); //Rewrite this
		 * function specially for masks
		 * 
		 * else{System.err.println(
		 * "   ERROR: MOD14_1KM_TO_250M CAN BE USED WHEN MASK DIMENSION ARE EXACTLY 2 or 4 TIMES GEOLOCATION DIMENSION"
		 * ); System.exit(1); } break;
		 */
		default:
			System.err.println("   ERROR: UNRECOGNIZED MASKDIMMATCHALGO: "
					+ getMASKDimMatchAlgo());
			System.exit(1);
		}

		return (mask);
	}

	public int[][] matchSDS_RGBDim(int[][] mask, SDSReader maskReader,
			SDSReader dataReader, int bandindicator) throws Exception {

		int xSDS = dataReader.getWidth();
		int ySDS = dataReader.getHeight();
		int xMask = maskReader.getWidth();
		int yMask = maskReader.getHeight();
		//int MaskScaleFactor = xSDS / xMask;
		System.out.println("In matchSDS_RGBDim xtoMatch="+xSDS+" ytoMatch="+ySDS+" xBand="+xMask+" yBand="+yMask);

		int algoindex = -999;
		switch (bandindicator) {
		case 1:
			algoindex = getREDDimMatchAlgo();
			break;
		case 2:
			algoindex = getGREENDimMatchAlgo();
			break;
		case 3:
			algoindex = getBLUEDimMatchAlgo();
			break;
		}
		//System.out.println("algoindex="+algoindex);
		switch (algoindex) {
		case DIM_MATCHES_SDS:
			if (xSDS != xMask || ySDS != yMask) {
				System.err
						.println("   ERROR: DIMENSION OF RGB BAND DOES NOT MATCH SDS (USE CORRECT VALUE FOR DIMMATHCHALGO)");
				System.exit(1);
			}
			break;
		case MASK_CONT_DOWNSCALE:
			 // xGEO
																			// muslt
																			// be
																			// an
																			// integer
																			// multiple
																			// of
																			// xSDS
			int stride_x=xMask/xSDS;
			int stride_y=yMask/ySDS;
			if (stride_x != stride_y || (stride_x!=2 && stride_x!=4))
			{
				System.out.println("This RGBDimMatchAlgo Not Implemented Yet");
				System.exit(1);
			}
			else
			{
				mask = maskReader.downscaleResolution(mask,0,65535,-999,stride_x);				
			}
			
			
			break;

		case MASK_UPSCALE:// System.out.println("This RGBDimMatchAlgo Not Implemented Yet properly");
							// //xGEO muslt be an integer multiple of xSDS

		case MODIS_1KM_TO_250M:
			System.out
					.println(">>>> Upscaling RGB Band to match Band with highest resolution");
			double MaskScaleFactorX = xSDS / xMask;
			double MaskScaleFactorY = ySDS / yMask;
			if ((MaskScaleFactorX == 2 && MaskScaleFactorY == 2)
					|| (MaskScaleFactorX == 4 && MaskScaleFactorY == 4))
				for (int scaleindex = 1; scaleindex < MaskScaleFactorX; scaleindex = scaleindex * 2)
					mask = maskReader.doubleResolution(mask, 0, 65535, -999); // Rewrite
																				// this
																				// function
																				// specially
																				// for
																				// masks

			else {
				System.err
						.println("   ERROR: MOD14_1KM_TO_250M CAN BE USED WHEN MASK DIMENSION ARE EXACTLY 2 or 4 TIMES GEOLOCATION DIMENSION");
				System.exit(1);
			}
			break;
		default:
			System.err.println("   ERROR: UNRECOGNIZED MASKDIMMATCHALGO: "
					+ getMASKDimMatchAlgo());
			System.exit(1);
		}

		return (mask);
	}

	public void setGeotiffTags_GEOGRAPHIC(double minLongitude,
			double maxLatitude, double lonScale, double latScale) {
		gtifftags=new GEOTIFFTags();
		gtifftags.setModelTiePointTag(new double[] { 0.0, 0.0, 0.0,
				minLongitude, maxLatitude, 0.0 });
		gtifftags.setPixelScaleTag(new double[] { lonScale,
				latScale, 0.0 });
		gtifftags.setGeogCitationGeoKey("GEOGRAPHIC|");
		gtifftags.setGTModelTypeGeoKey(2);
		gtifftags.setGTRasterTypeGeoKey(1);
		gtifftags.setGeographicTypeGeoKey(4326);
		gtifftags.setGeogGeodeticDatumGeoKey(6030);
		
	}

	public GEOTIFFTags getGtifftags() {
		return gtifftags;
	}

	public void setGeotiffTags_PROJ4(double xmin_m, double ymax_m) {
		gtifftags=new GEOTIFFTags();
		//gtifftags.setModelTiePointTag(new double[] { 0.0, 0.0, 0.0,
		//		-xmin_m*1000, -ymax_m*1000, 0.0 });
		gtifftags.setModelTiePointTag(new double[] { 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0 });
		gtifftags.setPixelScaleTag(new double[] { this.getResolution(),
				this.getResolution(), 0.0 });
		gtifftags.setGTModelTypeGeoKey(1);		
		gtifftags.setGeographicTypeGeoKey(4326);
		gtifftags.setGeogGeodeticDatumGeoKey(6326);   
	    gtifftags.setGeogLinearUnitsGeoKey(9001);
	    gtifftags.setGeogAngularUnitsGeoKey(9102);
	    gtifftags.setProjectedCSTypeGeoKey(32767);
	    gtifftags.setProjectionGeoKey(32767);	   
	    gtifftags.setProjLinearUnitsGeoKey(9001);
	    System.out.println("In setGeotifftags_proj4"+xmin_m+","+ymax_m+":"+this.getLat_0()+this.getLon_0());
		gtifftags.setProjCenterLongGeoKey(this.getLon_0());
	    gtifftags.setProjCenterLatGeoKey(this.getLat_0());
    	gtifftags.setProjNatOriginLongGeoKey(this.getLon_0());
	    gtifftags.setProjNatOriginLatGeoKey(this.getLat_0());
	    gtifftags.setProjFalseEastingGeoKey(-xmin_m*1000.0);
	    gtifftags.setProjFalseNorthingGeoKey(-ymax_m*1000.0);
	    gtifftags.setProjScaleAtNatOriginGeoKey(1.0);
	    
	    
	    switch(this.getProjection())
	    {
	    case STEREOGRAPHIC:
	    	gtifftags.setGTRasterTypeGeoKey(2);
	    	gtifftags.setGTCitationGeoKey("ST-WGS84|");
	    	gtifftags.setProjCoordTransGeoKey(14);
	    
	    	break;
	    case MERCATOR:
	    	gtifftags.setGTRasterTypeGeoKey(2);
	    	gtifftags.setGTCitationGeoKey("ME-WGS84|");
	    	gtifftags.setProjCoordTransGeoKey(7);
	    	gtifftags.setProjCenterLatGeoKey(0.0);	    	
		    gtifftags.setProjNatOriginLatGeoKey(0.0);
	    	break;
	    case TMERCATOR:
	    	gtifftags.setGTRasterTypeGeoKey(2);
	    	gtifftags.setGTCitationGeoKey("TM-WGS84|");
	    	gtifftags.setProjCoordTransGeoKey(1);
	    	
	    	break;
	    case SINUSOIDAL:
	    	gtifftags.setGTRasterTypeGeoKey(1);
	    	gtifftags.setGTCitationGeoKey("SI-WGS84|");
	    	gtifftags.setProjCoordTransGeoKey(24);	    	
	    	break;
	    case LAZIMUTHAL:
	    	gtifftags.setGTRasterTypeGeoKey(1);
	    	gtifftags.setGTCitationGeoKey("LA-WGS84|");
	    	gtifftags.setProjCoordTransGeoKey(10);
	    	break;
	    }
	   
	   
	    
		
	}

	public void setLon_0(double lon_0) {
		this.lon_0 = lon_0;
	}

	public void setLat_0(double lat_0) {
		this.lat_0 = lat_0;
	}

	public void setWidth_km(double width_km) {
		this.width_km = width_km;
	}

	public double getWidth_km() {
		return width_km;
	}

	public void setHeight_km(double height_km) {
		this.height_km = height_km;
	}

	public double getHeight_km() {
		return height_km;
	}
	
	public int getThirdDimDepth() {
		return ThirdDimDepth;
	}

	public void setThirdDimDepth(int thirdDimDepth) {
		ThirdDimDepth = thirdDimDepth;
	}
	

}
