/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/
package gov.nasa.gsfc.drl.h2g.util;

public class GEOTIFFTags {
	
	/****************GEOTIFF TAGS *************************/
	double[] ModelTiePointTag;
	double[] PixelScaleTag;
	//Only the first 6 keys suffice for geographic projection
	
	static final int GTModelTypeGeoKeyID =1024;
	int GTModelTypeGeoKey; // KeyID= 1024 Projection Coordinate System=1; Geographic(latitude longitude) system=2
	
	static final int GTRasterTypeGeoKeyID=1025;
    int GTRasterTypeGeoKey; //KeyID= 1025 RasterPixelIsArea  = 1  RasterPixelIsPoint = 2
    
    static final int GTCitationGeoKeyID=1026;
    String GTCitationGeoKey; //KeyID=1026 A string describing the projection e.g. "PS_WGS84|"
    
    static final int GeographicTypeGeoKeyID=2048;
    int GeographicTypeGeoKey; //KeyID= 2048 Use  GCS_WGS_84 =4326
    
    static final int GeogCitationGeoKeyID=2049;
    String GeogCitationGeoKey; //KeyID=2049 General citation and reference for all Geographic CS parameters.e.g. "GEOGRAPHIC|"
    
    static final int GeogGeodeticDatumGeoKeyID=2050;
    int GeogGeodeticDatumGeoKey; //KeyID=2050 Datum_WGS84 =	6326
    
    
    
    static final int GeogLinearUnitsGeoKeyID=2052;
    int GeogLinearUnitsGeoKey; //KeyID =2052 Linear_Meter =	9001
    
    static final int GeogAngularUnitsGeoKeyID=2054;
    int GeogAngularUnitsGeoKey; //KeyID=2054 Angular_Degree =	9102
    
    static final int ProjectedCSTypeGeoKeyID=3072;
    int ProjectedCSTypeGeoKey; //Key ID = 3072  user-defined=32767
    
    static final int ProjectionGeoKeyID=3074;
    int ProjectionGeoKey; //KeyID=3074 user-defined=32767
    
    static final int ProjCoordTransGeoKeyID=3075;
    int ProjCoordTransGeoKey; //Key ID = 3075 
    /*CT_TransverseMercator=1
    CT_ObliqueMercator =	3 
    CT_Mercator =	7   
    CT_LambertConfConic_2SP =	8
    CT_LambertConfConic_Helmert =	9
    CT_LambertAzimEqualArea =	10
    CT_AlbersEqualArea =	11
    CT_Stereographic =	14 
    CT_PolarStereographic =	15
    CT_Sinusoidal =	24 */
    
    static final int ProjLinearUnitsGeoKeyID=3076;
    int ProjLinearUnitsGeoKey; //Key ID = 3076 Linear_Meter =	9001
    
    static final int ProjNatOriginLongGeoKeyID=3080;
    double ProjNatOriginLongGeoKey; //Key ID=3080   Longitude of map-projection Natural origin.
    
    static final int ProjNatOriginLatGeoKeyID=3081;
    double ProjNatOriginLatGeoKey;    //Key ID=3081   Latitude of map-projection Natural origin.
    
    static final int ProjFalseEastingGeoKeyID=3082;
    double ProjFalseEastingGeoKey; //Key ID=3082    Gives the easting coordinate of the map projection Natural origin.
    
    static final int ProjFalseNorthingGeoKeyID=3083;
    double ProjFalseNorthingGeoKey;   //Key ID=3083   Gives the northing coordinate of the map projection Natural origin.  
    
       
    static final int ProjCenterLongGeoKeyID=3088;
    double ProjCenterLongGeoKey; //Key ID=3088 Longitude of Center of Projection. Note that this is not necessarily the origin of the projection.
    
    static final int ProjCenterLatGeoKeyID=3089;
    double ProjCenterLatGeoKey; //Key ID=3089 Latitude of Center of Projection. Note that this is not necessarily the origin of the projection.
    
    static final int ProjStraightVertPoleLongGeoKeyID=3095;
    double ProjStraightVertPoleLongGeoKey; //Key ID = 3095 Longitude at Straight Vertical Pole. For polar stereographic.
    
    static final int ProjScaleAtNatOriginGeoKeyID=3092;
    double ProjScaleAtNatOriginGeoKey; //Key ID=3092 Scale at Natural Origin. This is a ratio, so no units are required.
    
  
	public GEOTIFFTags() {
		
		ModelTiePointTag=new double[] { 0.0, 0.0, 0.0,0.0, 0.0, 0.0 };
		PixelScaleTag = new double[] {0.0,0.0, 0.0 };
		GTModelTypeGeoKey=2; // KeyID= 1024 Projection Coordinate System=1; Geographic(latitude longitude) system=2
		GTRasterTypeGeoKey=1; //KeyID= 1025 RasterPixelIsArea  = 1  RasterPixelIsPoint = 2	    
	    GTCitationGeoKey="PS_WGS84|"; //KeyID=1026 A string describing the projection e.g. "PS_WGS84|"
	    GeographicTypeGeoKey=4326; //KeyID= 2048 Use  GCS_WGS_84 =4326
	    GeogCitationGeoKey="GEOGRAPHIC|"; //KeyID=2049 General citation and reference for all Geographic CS parameters.e.g. "GEOGRAPHIC|"
	    GeogGeodeticDatumGeoKey=6030; //KeyID=2050 DatumE_WGS84 =	6030
	    
	    GeogLinearUnitsGeoKey=9001; //KeyID =2052 Linear_Meter =	9001
	    GeogAngularUnitsGeoKey=9102; //KeyID=2054 Angular_Degree =	9102
	    ProjectedCSTypeGeoKey=32767; //Key ID = 3072  user-defined=32767
	    ProjectionGeoKey=32767; //KeyID=3074 user-defined=32767
	    ProjCoordTransGeoKey=14; //Key ID = 3075 CT_Stereographic =	14 
	    ProjLinearUnitsGeoKey=9001; //Key ID = 3076 Linear_Meter =	9001
	    ProjNatOriginLongGeoKey=0.0; //Key ID=3080   Longitude of map-projection Natural origin.
	    ProjNatOriginLatGeoKey=0.0;    //Key ID=3081   Latitude of map-projection Natural origin.
	    ProjFalseEastingGeoKey=0.0; //Key ID=3082    Gives the easting coordinate of the map projection Natural origin.
	    ProjFalseNorthingGeoKey=0.0;   //Key ID=3083   Gives the northing coordinate of the map projection Natural origin.  
	    ProjCenterLongGeoKey=0.0; //Key ID=3088 Longitude of Center of Projection. Note that this is not necessarily the origin of the projection.
	    ProjCenterLatGeoKey=0.0; //Key ID=3089 Latitude of Center of Projection. Note that this is not necessarily the origin of the projection.
	    ProjStraightVertPoleLongGeoKey=0.0; //Key ID = 3095 Longitude at Straight Vertical Pole. For polar stereographic.
	}
	  
	public double getProjScaleAtNatOriginGeoKey() {
		return ProjScaleAtNatOriginGeoKey;
	}
	public void setProjScaleAtNatOriginGeoKey(double projScaleAtNatOriginGeoKey) {
		ProjScaleAtNatOriginGeoKey = projScaleAtNatOriginGeoKey;
	}
	public static int getProjScaleAtNatOriginGeoKeyID() {
		return ProjScaleAtNatOriginGeoKeyID;
	}
	
	public static int getGTModelTypeGeoKeyID() {
		return GTModelTypeGeoKeyID;
	}
	public static int getGTRasterTypeGeoKeyID() {
		return GTRasterTypeGeoKeyID;
	}
	public static int getGTCitationGeoKeyID() {
		return GTCitationGeoKeyID;
	}
	public static int getGeographicTypeGeoKeyID() {
		return GeographicTypeGeoKeyID;
	}
	public static int getGeogCitationGeoKeyID() {
		return GeogCitationGeoKeyID;
	}
	public static int getGeogGeodeticDatumGeoKeyID() {
		return GeogGeodeticDatumGeoKeyID;
	}
	public static int getGeogLinearUnitsGeoKeyID() {
		return GeogLinearUnitsGeoKeyID;
	}
	public static int getGeogAngularUnitsGeoKeyID() {
		return GeogAngularUnitsGeoKeyID;
	}
	public static int getProjectedCSTypeGeoKeyID() {
		return ProjectedCSTypeGeoKeyID;
	}
	public static int getProjectionGeoKeyID() {
		return ProjectionGeoKeyID;
	}
	public static int getProjCoordTransGeoKeyID() {
		return ProjCoordTransGeoKeyID;
	}
	public static int getProjLinearUnitsGeoKeyID() {
		return ProjLinearUnitsGeoKeyID;
	}
	public static int getProjNatOriginLongGeoKeyID() {
		return ProjNatOriginLongGeoKeyID;
	}
	public static int getProjNatOriginLatGeoKeyID() {
		return ProjNatOriginLatGeoKeyID;
	}
	public static int getProjFalseEastingGeoKeyID() {
		return ProjFalseEastingGeoKeyID;
	}
	public static int getProjFalseNorthingGeoKeyID() {
		return ProjFalseNorthingGeoKeyID;
	}
	public static int getProjCenterLongGeoKeyID() {
		return ProjCenterLongGeoKeyID;
	}
	public static int getProjCenterLatGeoKeyID() {
		return ProjCenterLatGeoKeyID;
	}
	public static int getProjStraightVertPoleLongGeoKeyID() {
		return ProjStraightVertPoleLongGeoKeyID;
	}
	public double[] getModelTiePointTag() {
		return ModelTiePointTag;
	}
	public void setModelTiePointTag(double[] modelTiePointTag) {
		ModelTiePointTag = modelTiePointTag;
	}
	public double[] getPixelScaleTag() {
		return PixelScaleTag;
	}
	public void setPixelScaleTag(double[] pixelScaleTag) {
		PixelScaleTag = pixelScaleTag;
	}
	public int getGTModelTypeGeoKey() {
		return GTModelTypeGeoKey;
	}
	public void setGTModelTypeGeoKey(int modelTypeGeoKey) {
		GTModelTypeGeoKey = modelTypeGeoKey;
	}
	public int getGTRasterTypeGeoKey() {
		return GTRasterTypeGeoKey;
	}
	public void setGTRasterTypeGeoKey(int rasterTypeGeoKey) {
		GTRasterTypeGeoKey = rasterTypeGeoKey;
	}
	public String getGTCitationGeoKey() {
		return GTCitationGeoKey;
	}
	public void setGTCitationGeoKey(String citationGeoKey) {
		GTCitationGeoKey = citationGeoKey;
	}
	public int getGeographicTypeGeoKey() {
		return GeographicTypeGeoKey;
	}
	public void setGeographicTypeGeoKey(int geographicTypeGeoKey) {
		GeographicTypeGeoKey = geographicTypeGeoKey;
	}
	public String getGeogCitationGeoKey() {
		return GeogCitationGeoKey;
	}
	public void setGeogCitationGeoKey(String geogCitationGeoKey) {
		GeogCitationGeoKey = geogCitationGeoKey;
	}
	public int getGeogGeodeticDatumGeoKey() {
		return GeogGeodeticDatumGeoKey;
	}
	public void setGeogGeodeticDatumGeoKey(int geogGeodeticDatumGeoKey) {
		GeogGeodeticDatumGeoKey = geogGeodeticDatumGeoKey;
	}
	public int getGeogLinearUnitsGeoKey() {
		return GeogLinearUnitsGeoKey;
	}
	public void setGeogLinearUnitsGeoKey(int geogLinearUnitsGeoKey) {
		GeogLinearUnitsGeoKey = geogLinearUnitsGeoKey;
	}
	public int getGeogAngularUnitsGeoKey() {
		return GeogAngularUnitsGeoKey;
	}
	public void setGeogAngularUnitsGeoKey(int geogAngularUnitsGeoKey) {
		GeogAngularUnitsGeoKey = geogAngularUnitsGeoKey;
	}
	public int getProjectedCSTypeGeoKey() {
		return ProjectedCSTypeGeoKey;
	}
	public void setProjectedCSTypeGeoKey(int projectedCSTypeGeoKey) {
		ProjectedCSTypeGeoKey = projectedCSTypeGeoKey;
	}
	public int getProjectionGeoKey() {
		return ProjectionGeoKey;
	}
	public void setProjectionGeoKey(int projectionGeoKey) {
		ProjectionGeoKey = projectionGeoKey;
	}
	public int getProjCoordTransGeoKey() {
		return ProjCoordTransGeoKey;
	}
	public void setProjCoordTransGeoKey(int projCoordTransGeoKey) {
		ProjCoordTransGeoKey = projCoordTransGeoKey;
	}
	public int getProjLinearUnitsGeoKey() {
		return ProjLinearUnitsGeoKey;
	}
	public void setProjLinearUnitsGeoKey(int projLinearUnitsGeoKey) {
		ProjLinearUnitsGeoKey = projLinearUnitsGeoKey;
	}
	public double getProjNatOriginLongGeoKey() {
		return ProjNatOriginLongGeoKey;
	}
	public void setProjNatOriginLongGeoKey(double projNatOriginLongGeoKey) {
		ProjNatOriginLongGeoKey = projNatOriginLongGeoKey;
	}
	public double getProjNatOriginLatGeoKey() {
		return ProjNatOriginLatGeoKey;
	}
	public void setProjNatOriginLatGeoKey(double projNatOriginLatGeoKey) {
		ProjNatOriginLatGeoKey = projNatOriginLatGeoKey;
	}
	public double getProjFalseEastingGeoKey() {
		return ProjFalseEastingGeoKey;
	}
	public void setProjFalseEastingGeoKey(double projFalseEastingGeoKey) {
		ProjFalseEastingGeoKey = projFalseEastingGeoKey;
	}
	public double getProjFalseNorthingGeoKey() {
		return ProjFalseNorthingGeoKey;
	}
	public void setProjFalseNorthingGeoKey(double projFalseNorthingGeoKey) {
		ProjFalseNorthingGeoKey = projFalseNorthingGeoKey;
	}
	public double getProjStraightVertPoleLongGeoKey() {
		return ProjStraightVertPoleLongGeoKey;
	}
	public void setProjStraightVertPoleLongGeoKey(
			double projStraightVertPoleLongGeoKey) {
		ProjStraightVertPoleLongGeoKey = projStraightVertPoleLongGeoKey;
	}
	public double getProjCenterLongGeoKey() {
		return ProjCenterLongGeoKey;
	}
	public void setProjCenterLongGeoKey(double projCenterLongGeoKey) {
		ProjCenterLongGeoKey = projCenterLongGeoKey;
	}
	public double getProjCenterLatGeoKey() {
		return ProjCenterLatGeoKey;
	}
	public void setProjCenterLatGeoKey(double projCenterLatGeoKey) {
		ProjCenterLatGeoKey = projCenterLatGeoKey;
	}

}
