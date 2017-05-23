/******************************************************************************
*
*  History:
*
*  21-Nov-05, J Love, GST	Original version.
*
******************************************************************************/

package gov.nasa.gsfc.drl.thumbnails.test;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.tree.*;
import javax.imageio.*;

import ncsa.hdf.object.*;
import ncsa.hdf.object.h4.*;

import javax.media.jai.*;
import org.geotiff.image.jai.*;
import org.libtiff.jai.codec.XTIFFEncodeParam;
import org.libtiff.jai.codec.XTIFFField;
import org.geotiff.image.KeyRegistry;
import java.awt.image.renderable.*;

public class Geotiff {
  private static final int HEIGHT = 0;
  private static final int WIDTH = 1;
  private static final int DEPTH = 2;
  private static final int NO_DEPTH = -1;
  private static final int MIN = 0;
  private static final int MAX = 1;
  private static final int RED = 0;
  private static final int GREEN = 1;
  private static final int BLUE = 2;
  private static final int DEFAULT_THUMB_WIDTH = 200;
  private static final int DEFAULT_THUMB_HEIGHT = 200;
  private static final int MOD01 = 2;
  private static final int BtMOD01 = 3;
  private static final int MOD021KM = 4;
  private static final int BtMOD021KM = 5;
  private static final int MOD02HKM = 6;
  private static final int BtMOD02HKM = 7;
  private static final int MOD02QKM = 8;
  private static final int BtMOD02QKM = 9;
  private static final int MOD02OBC = 10;
  private static final int BtMOD02OBC = 11;
  private static final int MOD03 = 12;
  private static final int BtMOD03 = 13;
  private static final int MOD08 = 14;
  private static final int MOD09Q1 = 15;
  private static final int MOD10 = 16;
  private static final int BtMOD10 = 17;
  private static final int MOD10CRS = 18;
  private static final int BtMOD10CRS = 19;
  private static final int MOD13 = 20;
  private static final int BtMOD13 = 21;
  private static final int MOD13CRS = 22;
  private static final int BtMOD13CRS = 23;
  private static final int MOD14 = 24;
  private static final int BtMOD14 = 25;
  private static final int MOD14CRS = 26;
  private static final int BtMOD14CRS = 27;
  private static final int MOD29 = 28;
  private static final int BtMOD29 = 29;
  private static final int MOD29CRS = 30;
  private static final int BtMOD29CRS = 31;
  private static final int MOD35 = 32;
  private static final int BtMOD35 = 33;
  private static final int MOD35CRS = 34;
  private static final int BtMOD35CRS = 35;
  //
  private static Color[] greens = null;
  /****************************************************************************
  * ValueMapper.
  ****************************************************************************/
  private static interface ValueMapper {
    public Color getColor (int value) throws Exception;
    public int getMappedValue (int value);
  }
  /****************************************************************************
  * getDataType.
  * Always check for the CRS version first...
  ****************************************************************************/
  public static int getDataType (File hdfFile) throws Exception {
    String fileName = hdfFile.getName();
    if (fileName.matches("^M[OY]D021KM.*\\.hdf$") ||
	fileName.matches("^M[OY]D01.*M[OY]D021KM\\.hdf$")) {
      return MOD021KM;
    }
    if (fileName.matches("^BT(_)?M[OY]D021KM.*\\.hdf$") ||
	fileName.matches("^BT(_)?M[OY]D01.*M[OY]D021KM\\.hdf$")) {
      return BtMOD021KM;
    }
    if (fileName.matches("^M[OY]D02HKM.*\\.hdf$") ||
	fileName.matches("^M[OY]D01.*M[OY]D02HKM\\.hdf$")) {
      return MOD02HKM;
    }
    if (fileName.matches("^BT(_)?M[OY]D02HKM.*\\.hdf$") ||
	fileName.matches("^BT(_)?M[OY]D01.*M[OY]D02HKM\\.hdf$")) {
      return BtMOD02HKM;
    }
    if (fileName.matches("^M[OY]D02QKM.*\\.hdf$") ||
	fileName.matches("^M[OY]D01.*M[OY]D02QKM\\.hdf$")) {
      return MOD02QKM;
    }
    if (fileName.matches("^BT(_)?M[OY]D02QKM.*\\.hdf$") ||
	fileName.matches("^BT(_)?M[OY]D01.*M[OY]D02QKM\\.hdf$")) {
      return BtMOD02QKM;
    }
    if (fileName.matches("^M[OY]D02OBC.*\\.hdf$") ||
	fileName.matches("^M[OY]D01.*M[OY]D02OBC\\.hdf$")) {
      return MOD02OBC;
    }
    if (fileName.matches("^BT(_)?M[OY]D02OBC.*\\.hdf$") ||
	fileName.matches("^BT(_)?M[OY]D01.*M[OY]D02OBC\\.hdf$")) {
      return BtMOD02OBC;
    }
    if (fileName.matches("^M[OY]D01.*\\.hdf$")) {
      return MOD01;
    }
    if (fileName.matches("^BT(_)?M[OY]D01.*\\.hdf$")) {
      return BtMOD01;
    }
    if (fileName.matches("^M[OY]D03.*\\.hdf$")) {
      return MOD03;
    }
    if (fileName.matches("^BT(_)?M[OY]D03.*\\.hdf$")) {
      return BtMOD03;
    }
    if (fileName.matches("^M[OY]D10(_)?CRS.*\\.hdf$")) {
      return MOD10CRS;
    }
    if (fileName.matches("^BT(_)?M[OY]D10(_)?CRS.*\\.hdf$")) {
      return BtMOD10CRS;
    }
    if (fileName.matches("^M[OY]D10.*\\.hdf$")) {
      return MOD10;
    }
    if (fileName.matches("^BT(_)?M[OY]D10.*\\.hdf$")) {
      return BtMOD10;
    }
    if (fileName.matches("^M[OY]D13(_)?CRS.*\\.hdf$")) {
      return MOD13CRS;
    }
    if (fileName.matches("^BT(_)?M[OY]D13(_)?CRS.*\\.hdf$")) {
      return BtMOD13CRS;
    }
    if (fileName.matches("^M[OY]D13.*\\.hdf$")) {
      return MOD13;
    }
    if (fileName.matches("^BT(_)?M[OY]D13.*\\.hdf$")) {
      return BtMOD13;
    }
    if (fileName.matches("^M[OY]D14(_)?CRS.*\\.hdf$")) {
      return MOD14CRS;
    }
    if (fileName.matches("^BT(_)?M[OY]D14(_)?CRS.*\\.hdf$")) {
      return BtMOD14CRS;
    }
    if (fileName.matches("^M[OY]D14.*\\.hdf$")) {
      return MOD14;
    }
    if (fileName.matches("^BT(_)?M[OY]D14.*\\.hdf$")) {
      return BtMOD14;
    }
    if (fileName.matches("^M[OY]D29(_)?CRS.*\\.hdf$")) {
      return MOD29CRS;
    }
    if (fileName.matches("^BT(_)?M[OY]D29(_)?CRS.*\\.hdf$")) {
      return BtMOD29CRS;
    }
    if (fileName.matches("^M[OY]D29.*\\.hdf$")) {
      return MOD29;
    }
    if (fileName.matches("^BT(_)?M[OY]D29.*\\.hdf$")) {
      return BtMOD29;
    }
    if (fileName.matches("^M[OY]D35(_)?CRS.*\\.hdf$")) {
      return MOD35CRS;
    }
    if (fileName.matches("^BT(_)?M[OY]D35(_)?CRS.*\\.hdf$")) {
      return BtMOD35CRS;
    }
    if (fileName.matches("^M[OY]D35.*\\.hdf$")) {
      return MOD35;
    }
    if (fileName.matches("^BT(_)?M[OY]D35.*\\.hdf$")) {
      return BtMOD35;
    }
    if (fileName.matches("^M[OY]D09Q1\\..*\\.hdf$")) {
      return MOD09Q1;
    }
    throw new Exception("unknown type of HDF: " + fileName);
  }
  /****************************************************************************
  * createThumbnail.
  ****************************************************************************/
  public static byte[] createThumbnail (File hdfFile,
					int thumbWidth) throws Exception {
    H4File h4 = null;
    try {
      h4 = new H4File(hdfFile.getAbsolutePath());
      h4.open();
      TreeNode root = h4.getRootNode();
      byte[] bytes = null;
      switch (getDataType(hdfFile)) {
	case MOD01:
	case BtMOD01: {
	  String[] path = new String[] {"EV_1km_day"};
	  bytes = buildThumbScaled(root,path,new int[] {0,2,1},
				   new int[] {0,1,2},thumbWidth);
	  break;
	}
	case MOD021KM:
	case BtMOD021KM: {
	  String[] path = new String[] {"MODIS_SWATH_Type_L1B","Data Fields","EV_1KM_RefSB"};
	  bytes = buildThumbScaled(root,path,new int[] {1,2,0},
				   new int[] {0,0,0},thumbWidth);
	  break;
	}
	case MOD02HKM:
	case BtMOD02HKM: {
	  String[] path = new String[] {"MODIS_SWATH_Type_L1B","Data Fields","EV_500_RefSB"};
	  bytes = buildThumbScaled(root,path,new int[] {1,2,0},
				   new int[] {0,0,0},thumbWidth);
	  break;
	}
	case MOD02QKM:
	case BtMOD02QKM: {
	  String[] path = new String[] {"MODIS_SWATH_Type_L1B","Data Fields","EV_250_RefSB"};
	  bytes = buildThumbScaled(root,path,new int[] {1,2,0},
				   new int[] {0,0,0},thumbWidth);
	  break;
	}
	case MOD02OBC: {
	  bytes = buildDefaultThumb("MOD02OBC");
	  break;
	}
	case MOD03:
	case BtMOD03: {
	  bytes = buildDefaultThumb("MOD03");
	  break;
	}
	case MOD08: {
	  String[] path = new String[] {"mod08","Data Fields","Reflected_Flux_Land_And_Ocean_Mean"};
	  bytes = buildThumbScaled(root,path,new int[] {0,1},
				   new int[] {NO_DEPTH,NO_DEPTH,NO_DEPTH},
				   thumbWidth);
	  break;
	}
	case MOD09Q1: {
	  String[] path = new String[] {"MOD_Grid_250m_Surface_Reflectance","Data Fields","sur_refl_b02"};
	  bytes = buildThumbScaled(root,path,new int[] {0,1},
				   new int[] {NO_DEPTH,NO_DEPTH,NO_DEPTH},
				   thumbWidth);
	  break;
	}
	case MOD10:
	case BtMOD10: {
	  String[] path = new String[] {"MOD_Swath_Snow","Data Fields","Snow Cover"};
	  bytes = buildThumbMapped(root,path,new int[] {0,1},NO_DEPTH,
				   new SnowValueMapper(),thumbWidth);
	  break;
	}
	case MOD10CRS:
	case BtMOD10CRS: {
	  String[] path = new String[] {"MOD_Swath_Snow_5km","Data Fields","Snow Cover 5km"};
	  bytes = buildThumbMapped(root,path,new int[] {0,1},NO_DEPTH,
				   new SnowValueMapper(),thumbWidth);
	  break;
	}
	case MOD13:
	case BtMOD13: {
	  String[] path = new String[] {"NDVI"};
	  bytes = buildThumbMapped(root,path,new int[] {0,1},NO_DEPTH,
				   new NDVIvalueMapper(),thumbWidth);
	  break;
	}
	case MOD13CRS:
	case BtMOD13CRS: {
	  bytes = buildDefaultThumb("MOD13CRS");
	  break;
	}
	case MOD14:
	case BtMOD14: {
	  String[] path = new String[] {"fire mask"};
	  bytes = buildThumbMapped(root,path,new int[] {0,1},NO_DEPTH,
				   new FireValueMapper(),thumbWidth);
	  break;
	}
	case MOD14CRS:
	case BtMOD14CRS: {
	  bytes = buildDefaultThumb("MOD14CRS");
	  break;
	}
	case MOD29:
	case BtMOD29: {
	  String[] path = new String[] {"MOD_Swath_Sea_Ice","Data Fields","Sea Ice by IST"};
	  bytes = buildThumbMapped(root,path,new int[] {0,1},NO_DEPTH,
				   new IceValueMapper(),thumbWidth);
	  break;
	}
	case MOD29CRS:
	case BtMOD29CRS: {
	  String[] path = new String[] {"MOD_Swath_Sea_Ice_5km","Data Fields","Sea Ice by IST 5km"};
	  bytes = buildThumbMapped(root,path,new int[] {0,1},NO_DEPTH,
				   new IceValueMapper(),thumbWidth);
	  break;
	}
	case MOD35:
	case BtMOD35: {
	  String[] path = new String[] {"mod35","Data Fields","Cloud_Mask"};
	  bytes = buildThumbMapped(root,path,new int[] {1,2,0},0,
				   new CloudValueMapper(),thumbWidth);
	  break;
	}
	case MOD35CRS:
	case BtMOD35CRS: {
	  bytes = buildDefaultThumb("MOD35CRS");
	  break;
	}
	default:
	  throw new Exception("unsupported HDF data type: " + hdfFile.getName());
      }
      h4.close();
      return bytes;
    } catch (Exception e) {
      // e.printStackTrace();
      throw e;
    } finally {
      try {h4.close();} catch (Exception e1) {}
    }
  }
  /****************************************************************************
  * buildDefaultThumb.
  ****************************************************************************/
  private static byte[] buildDefaultThumb (String text) throws Exception {
    int thumbWidth = DEFAULT_THUMB_WIDTH;
    int thumbHeight = DEFAULT_THUMB_HEIGHT;
    BufferedImage bi = new BufferedImage(thumbWidth,thumbHeight,
					 BufferedImage.TYPE_INT_RGB);
    Graphics g = bi.getGraphics();
    g.setColor(Color.black);
    g.fillRect(0,0,thumbWidth,thumbHeight);
    g.setColor(Color.yellow);
    g.drawRect(2,2,thumbWidth-5,thumbHeight-5);
    g.setFont(new Font("Serif",Font.BOLD,30));
    g.drawString(text,10,30);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ImageIO.write((RenderedImage)bi,"jpg",out);
    byte[] bytes = out.toByteArray();
    return bytes;
  }
  /****************************************************************************
  * buildThumbMapped.
  ****************************************************************************/
  private static byte[] buildThumbMapped (TreeNode root,
					  String[] path,
					  int[] indexSelections,
					  int depthSelection,
					  ValueMapper mapper,
					  int thumbWidth) throws Exception {
    SDSReader reader = new SDSReader(root,path,indexSelections);

    if (thumbWidth > reader.getWidth()) {
      thumbWidth = reader.getWidth();
    }
    int thumbHeight = (thumbWidth * reader.getHeight()) / reader.getWidth();

    int[][] values = reader.getInts(0,0,thumbHeight,thumbWidth,
				    reader.getHeight()/thumbHeight,
				    reader.getWidth()/thumbWidth,
				    depthSelection);
    BufferedImage bi = new BufferedImage(1+thumbWidth+1,1+thumbHeight+1,
					 BufferedImage.TYPE_INT_RGB);
    Graphics g = bi.getGraphics();
    g.setColor(Color.black);
    g.fillRect(0,0,1+thumbWidth+1,1+thumbHeight+1);
    for (int y = 0; y < thumbHeight; y++) {
       for (int x = 0; x < thumbWidth; x++) {
	  Color color = mapper.getColor(values[y][x]);
	  g.setColor(color);
	  g.fillRect(1+x,1+y,1,1);
       }
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    // ImageIO.write((RenderedImage)bi,"jpg",out);
    // Geotiff instead...    
    
       KeyRegistry registry = KeyRegistry.getKeyRegistry();
       
       // In order to use GeoTIFF, you must
       // register it with JAI:
       
       GeoTIFFDescriptor.register();
       
       XTIFFEncodeParam param = new XTIFFEncodeParam();
       // System.out.println(param.getDirectory().getClass().getName());
       GeoTIFFDirectory newDir=(GeoTIFFDirectory)param.getDirectory();
       int width = bi.getWidth(null);
       int height = bi.getHeight(null);
       // Add the appropriately navigated tiepoint
       // (for the center of the image)
       
       // newDir.setTiepoints(new double[] {width/2.0,height/2.0,0,radarLon,radarLat,0});
       newDir.setTiepoints(new double[] {width/2.0,height/2.0,0,0.0,0.0,0});
       
       // And the pixel scale (which is supposed to be identical)
       // horizontally and vertically, but rounding gets in the way)

       // newDir.setPixelScale(new double [] {(east-west)/width,(north-south)/height,0});
       newDir.setPixelScale(new double [] {10.0/width,10.0/height,0});

       // This keymap stuff looks verbose and annoying, but apparently
       // that's Java for you...

       // Let's see if we can properly set up the minimal geotiff keys ...
       // RasterPixelIsArea
       newDir.addGeoKey(KeyRegistry.getCode(KeyRegistry.GEOKEY,
					 "GTRasterTypeGeoKey"),
			XTIFFField.TIFF_SHORT, 1,
			(new char[] {1}));
       // ModelTypeGeographic
       newDir.addGeoKey(KeyRegistry.getCode(KeyRegistry.GEOKEY,
					 "GTModelTypeGeoKey"),
			XTIFFField.TIFF_SHORT, 1,
			(new char[] {1}));
       // GCS_WGS_84
       newDir.addGeoKey(KeyRegistry.getCode(KeyRegistry.GEOKEY,
					 "GeographicTypeGeoKey"),
			XTIFFField.TIFF_SHORT, 1,
			(new char[] {4326}));

       // now let JAI and GeoTIFF do the rest

       // To make the Image acceptable to JAI, we have to hose it
       // through the "awtImage" operator (found at the bottom of a
       // locked filing cabinet in a disused lavatory behind a sign
       // saying "Beware of the leopard").
       // Actually, getImage returns
       // a BufferedImage, which should "just work" here.
       // If this works, we'll move the cast up into the local getImage()
 
       ParameterBlock pb = new ParameterBlock();
       pb.add(bi);
       PlanarImage im = (PlanarImage) JAI.create("awtImage",pb);
       
       // BufferedImage im = (BufferedImage)getImage();

       // Now, finally, we can TIFF-encode the image!
       // Currently Java GeoTIFF does not implement any form of compression
       // Must ask the customer about this, as the uncompressed images are
       // pretty big (especially compared to the original data files).
       // param.setCompression(XTIFFEncodeParam.COMPRESSION_GROUP4);

       JAI.create("encode",im,out,"tiff",param);
    
    
    byte[] bytes = out.toByteArray();

    return bytes;
  }
  /****************************************************************************
  * buildThumbScaled.
  ****************************************************************************/
  private static byte[] buildThumbScaled (TreeNode root,
					  String[] path,
					  int[] indexSelections,
					  int[] depthSelections,
					  int thumbWidth) throws Exception {
    int[][][] values = new int[3][][];

    SDSReader reader = new SDSReader(root,path,indexSelections);

    if (thumbWidth > reader.getWidth()) {
      thumbWidth = reader.getWidth();
    }
    int thumbHeight = (thumbWidth * reader.getHeight()) / reader.getWidth();

    for (int c = RED; c <= BLUE; c++) {
       values[c] = reader.getInts(0,0,thumbHeight,thumbWidth,
				  reader.getHeight()/thumbHeight,
				  reader.getWidth()/thumbWidth,
				  depthSelections[c]);
    }
    Integer fillValue = reader.getIntegerFillValue();
    int[] validRange = reader.getIntValidRange();
    ValueScaler scaler = new ValueScaler(values,thumbHeight,thumbWidth,
					 new Integer[] {fillValue,fillValue,fillValue},
					 new int[][] {validRange,validRange,validRange});

    BufferedImage bi = new BufferedImage(1+thumbWidth+1,1+thumbHeight+1,
					 BufferedImage.TYPE_INT_RGB);
    Graphics g = bi.getGraphics();
    g.setColor(Color.black);
    g.fillRect(0,0,1+thumbWidth+1,1+thumbHeight+1);
    for (int y = 0; y < thumbHeight; y++) {
       for (int x = 0; x < thumbWidth; x++) {
	  g.setColor(scaler.getColor(new int[] {values[RED][y][x],
						values[GREEN][y][x],
						values[BLUE][y][x]}));
	  g.fillRect(1+x,1+y,1,1);
       }
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ImageIO.write((RenderedImage)bi,"jpg",out);
    byte[] bytes = out.toByteArray();

    return bytes;
  }
  /****************************************************************************
  * NDVIvalueMapper.
  ****************************************************************************/
  private static class NDVIvalueMapper implements ValueMapper {
    private int fill = -999;
    private int min = 0;
    private int max = 10000;
    private int range = max - min;
    public Color getColor (int value) throws Exception {
      int level = toRange(0,Math.round(255*((value-min)/((float)range))),255);
      return getGreens()[level];
    }
    public int getMappedValue (int value) {
      return value;
    }
  }
  private static Color[] getGreens () {
    if (greens == null) {
      greens = new Color[256];
      for (int i = 0; i < 256; i++) {
      	 greens[i] = new Color(0,i,0);
      }
    }
    return greens;
  }
  private static int toRange (int min, int value, int max) {
    if (value < min) {
      return min;
    }
    if (value > max) {
      return max;
    }
    return value;
  }
  /****************************************************************************
  * CloudValueMapper.
  ****************************************************************************/
  private static class CloudValueMapper implements ValueMapper {
    public Color getColor (int value) throws Exception {
      if ((value & 0x00000001) == 1) {
	switch ((value & 0x00000006) >> 1) {
	  case 0:
	    return Color.red;
	  case 1:
	    return Color.orange;
	  case 2:
	    return Color.yellow;
	  case 3:
	    return Color.blue;
	  default:
	    throw new Exception("internal error");
	}
      } else {
	return Color.black;
      }
    }
    public int getMappedValue (int value) {
      if ((value & 0x00000001) == 1) {
	return (value & 0x00000006) >> 1;
      }
      return 4;
    }
  }
  /****************************************************************************
  * FireValueMapper.
  ****************************************************************************/
  private static class FireValueMapper implements ValueMapper {
    public Color getColor (int value) throws Exception {
      switch (value) {
	case 3:
	  return Color.blue;
	case 4:
	  return Color.white;
	case 5:
	  return Color.green;
	case 6:
	  return Color.cyan;
	case 7:
	  return Color.yellow;
	case 8:
	  return Color.orange;
	case 9:
	  return Color.red;
	default:
	  return Color.black;
      }
    }
    public int getMappedValue (int value) {
      return value;
    }
  }
  /****************************************************************************
  * IceValueMapper.
  ****************************************************************************/
  private static class IceValueMapper implements ValueMapper {
    public Color getColor (int value) throws Exception {
      switch (value) {
	case 25:
	  return Color.red;
	case 37:
	  return Color.blue;
	case 39:
	  return Color.blue;
	case 50:
	  return Color.white;
	case 200:
	  return Color.gray;
	default:
	  return Color.black;
      }
    }
    public int getMappedValue (int value) {
      return value;
    }
  }
  /****************************************************************************
  * SnowValueMapper.
  ****************************************************************************/
  private static class SnowValueMapper implements ValueMapper {
    public Color getColor (int value) throws Exception {
      switch (value) {
	case 25:
	  return Color.red;
	case 37:
	  return Color.blue;
	case 39:
	  return Color.blue;
	case 50:
	  return Color.white;
	case 200:
	  return Color.gray;
	default:
	  return Color.black;
      }
    }
    public int getMappedValue (int value) {
      return value;
    }
  }
  /****************************************************************************
  * ValueScaler.
  ****************************************************************************/
  private static class ValueScaler implements ValueMapper {
    private int[][] valueExtents = new int[3][];
    private int[] range = new int[3];
    public ValueScaler (int[][] values, int nRows, int nCols,
			Integer fillValue, int[] validRange) {
      valueExtents[RED] = getExtents(values,nRows,nCols,fillValue,validRange);
      range[RED] = valueExtents[RED][MAX] - valueExtents[RED][MIN];
      for (int c = GREEN; c <= BLUE; c++) {
	 valueExtents[c] = valueExtents[RED];
	 range[c] = range[RED];
      }
    }
    public ValueScaler (int[][][] values, int nRows, int nCols,
			Integer[] fillValue, int[][] validRange) {
      for (int c = RED; c <= BLUE; c++) {
	 valueExtents[c] = getExtents(values[c],nRows,nCols,
				      fillValue[c],validRange[c]);
	 range[c] = valueExtents[c][MAX] - valueExtents[c][MIN];
      }
    }
    private int[] getExtents (int[][] values, int nRows, int nCols,
			      Integer fillValue, int[] validRange) {
      int minValue = Integer.MAX_VALUE;
      int maxValue = Integer.MIN_VALUE;
      for (int row = 0; row < nRows; row++) {
	 for (int col = 0; col < nCols; col++) {
	    if (isValidValue(values[row][col],fillValue,validRange)) {
	      if (values[row][col] < minValue) {
		minValue = values[row][col];
	      }
	      if (values[row][col] > maxValue) {
		maxValue = values[row][col];
	      }
	    }
	 }
      }
      return new int[] {minValue,maxValue};
    }
    public Color getColor (int value) {
      return getColor(new int[] {value,value,value});
    }
    public Color getColor (int[] values) {
      int[] levels = new int[3];
      for (int c = RED; c <= BLUE; c++) {
         if (values[c] < valueExtents[c][MIN] ||
	     values[c] > valueExtents[c][MAX]) {
	   return Color.black;
         }
         levels[c] = (255 * (values[c] - valueExtents[c][MIN])) / range[c];
      }
      return new Color(levels[0],levels[1],levels[2]);
    }
    public int getMappedValue (int value) {
      return value;
    }
  }
  /****************************************************************************
  * isValidValue.
  ****************************************************************************/
  private static boolean isValidValue (int value, Integer fillValue, int[] validRange) {
    if (fillValue != null) {
      if (value == fillValue.intValue()) {
        return false;
      }
    }
    if (value < validRange[MIN]) {
      return false;
    }
    if (value > validRange[MAX]) {
      return false;
    }
    return true;
  }
  /****************************************************************************
  * SDSReader.
  ****************************************************************************/
  private static class SDSReader {
    private H4SDS sds;
    private Datatype sdsDatatype;
    private long[] dims;
    private long[] selectedDims;
    private int[] selectedIndex;
    private long[] startDims;
    private long[] stride;
    public SDSReader (TreeNode root,
		      String[] path,
		      int[] indexSelections) throws Exception {
      sds = getThumbHObject(root,path);
      sds.init();
      sdsDatatype = sds.getDatatype();
      dims = sds.getDims();
      if (dims.length < 2) {
	throw new Exception("less than 2 dimensions not supported");
      }
      if (dims.length > 3) {
	throw new Exception("greater than 3 dimensions not supported");
      }
      selectedDims = sds.getSelectedDims();
      selectedIndex = sds.getSelectedIndex();
      startDims = sds.getStartDims();
      stride = sds.getStride();
      selectedIndex[HEIGHT] = indexSelections[HEIGHT];
      selectedIndex[WIDTH] = indexSelections[WIDTH];
      if (indexSelections.length > 2) {
	selectedIndex[DEPTH] = indexSelections[DEPTH];
      }
    }
    public int getRank () {
      return dims.length;
    }
    public int getHeight () {
      return (int) dims[selectedIndex[HEIGHT]];
    }
    public int getWidth () {
      return (int) dims[selectedIndex[WIDTH]];
    }
    public int getDepth () {
      if (dims.length < 3) {
	return NO_DEPTH;
      }
      return (int) dims[selectedIndex[DEPTH]];
    }
    public double getDouble (int row, int col) throws Exception {
      return getDouble(row,col,NO_DEPTH);
    }
    public double getDouble (int row, int col, int depth) throws Exception {
      Object dataValues = getData(row,col,1,1,1,1,depth);
      return Array.getDouble(dataValues,0);
    }
    public double[] getDoubleRow (int row, int col, int count, int depth) throws Exception {
      Object dataValues = getData(row,col,1,count,1,1,depth);
      double[] doubles = new double[count];
      for (int i = 0; i < count; i++) {
	 doubles[i] = Array.getDouble(dataValues,i);
      }
      return doubles;
    }
    public double[] getDoubleCol (int col, int row, int count, int depth) throws Exception {
      Object dataValues = getData(row,col,count,1,1,1,depth);
      double[] doubles = new double[count];
      for (int i = 0; i < count; i++) {
	 doubles[i] = Array.getDouble(dataValues,i);
      }
      return doubles;
    }
    public int[] getIntRow (int row, int col, int count, int depth) throws Exception {
      Object dataValues = getData(row,col,1,count,1,1,depth);
      int[] ints = new int[count];
      for (int i = 0; i < count; i++) {
	 ints[i] = fixInt(Array.getInt(dataValues,i),
			  sdsDatatype.getDatatypeSize(),
			  sdsDatatype.isUnsigned());
      }
      return ints;
    }
    public int[][] getInts (int row, int col,
			    int rows, int cols,
			    int rowStride, int colStride,
			    int depth) throws Exception {
      Object dataValues = getData(row,col,rows,cols,rowStride,colStride,depth);
      int[][] ints = new int[rows][cols];
      for (int r = 0; r < rows; r++) {
	 for (int c = 0; c < cols; c++) {
	    ints[r][c] = fixInt(Array.getInt(dataValues,(r*cols)+c),
				sdsDatatype.getDatatypeSize(),
				sdsDatatype.isUnsigned());
	 }
      }
      return ints;
    }
    private Object getData (int row, int col,
			    int rows, int cols,
			    int rowStride, int colStride,
			    int depth) throws Exception {
      startDims[selectedIndex[HEIGHT]] = row;
      startDims[selectedIndex[WIDTH]] = col;
      if (depth != NO_DEPTH) {
	startDims[selectedIndex[DEPTH]] = depth;
      }
      selectedDims[selectedIndex[HEIGHT]] = rows;
      selectedDims[selectedIndex[WIDTH]] = cols;
      if (depth != NO_DEPTH) {
	selectedDims[selectedIndex[DEPTH]] = 1;
      }
      stride[selectedIndex[HEIGHT]] = rowStride;
      stride[selectedIndex[WIDTH]] = colStride;
      if (depth != NO_DEPTH) {
	stride[selectedIndex[DEPTH]] = 1;
      }
      sds.clearData();
      sds.getData();
      sds.convertFromUnsignedC();
      return sds.getData();
    }
    public Integer getIntegerFillValue () throws Exception {
      java.util.List attributes = sds.getMetadata();
      if (attributes != null) {
        Iterator it = attributes.iterator();
        while (it.hasNext()) {
          Attribute attr = (Attribute) it.next();
          if (attr.getName().equals("_FillValue")) {
	    Datatype datatype = attr.getType();
	    Object values = attr.getValue();
	    return new Integer(fixInt(Array.getInt(values,0),
				      datatype.getDatatypeSize(),
				      datatype.isUnsigned()));
          }
        }
      }
      return null;
    }
    public Double getDoubleFillValue () throws Exception {
      java.util.List attributes = sds.getMetadata();
      if (attributes != null) {
        Iterator it = attributes.iterator();
        while (it.hasNext()) {
          Attribute attr = (Attribute) it.next();
          if (attr.getName().equals("_FillValue")) {
	    Object values = attr.getValue();
	    return new Double(Array.getDouble(values,0));
          }
        }
      }
      return null;
    }
    public int[] getIntValidRange () throws Exception {
      java.util.List attributes = sds.getMetadata();
      if (attributes != null) {
        Iterator it = attributes.iterator();
        while (it.hasNext()) {
          Attribute attr = (Attribute) it.next();
          if (attr.getName().equals("valid_range")) {
	    Datatype datatype = attr.getType();
	    Object values = attr.getValue();
	    return new int[] {fixInt(Array.getInt(values,0),
				     datatype.getDatatypeSize(),
				     datatype.isUnsigned()),
			      fixInt(Array.getInt(values,1),
				     datatype.getDatatypeSize(),
				     datatype.isUnsigned())};
          }
        }
      }
      switch (sdsDatatype.getDatatypeClass()) {
        case Datatype.CLASS_INTEGER: {
	  switch (sdsDatatype.getDatatypeSize()) {
	    case 1: {
	      if (sdsDatatype.isUnsigned()) {
	        return new int[] {0,255};
	      }
	      return new int[] {-128,127};
	    }
	    case 2: {
	      if (sdsDatatype.isUnsigned()) {
	        return new int[] {0,65535};
	      }
	      return new int[] {-32768,32767};
	    }
	  }
        }
      }
      throw new Exception("unexpected datatype");
    }
    private int fixInt (int value, int size, boolean unsigned) throws Exception {
      if (unsigned) {
	if (value < 0) {
	  switch (size) {
	    case 1: {
	      value += 256;
	      break;
	    }
	    case 2: {
	      value += 65536;
	      break;
	    }
	    case 4: {
	      throw new Exception("unsigned 4-byte integer not supported");
	    }
	    default: {
	      throw new Exception("unsupported integer size: " + size);
	    }
	  }
	}
      }
      return value;
    }
    private H4SDS getThumbHObject (TreeNode root,
				   String[] path) throws Exception {
      HObject ho = getHObject(root,path);
      if (ho == null) {
        throw new Exception("thumbnail data object not found");
      }
      if (!(ho instanceof H4SDS)) {
        throw new Exception("thumbnail HObject not an H4SDS");
      }
      return (H4SDS) ho;
    }
    private HObject getHObject (TreeNode root,
				String[] path) throws Exception {
      LinkedList objectNames = new LinkedList();
      for (int i = 0; i < path.length; i++) {
         objectNames.add(path[i]);
      }
      return findHObject(root,objectNames);
    }
    private HObject findHObject (TreeNode baseNode,
			         LinkedList objectNames) throws Exception {
      String objectName = (String) objectNames.removeFirst();
      int count = baseNode.getChildCount();
      for (int i = 0; i < count; i++) {
         TreeNode node = baseNode.getChildAt(i);
         HObject ho = (HObject) ((DefaultMutableTreeNode)node).getUserObject();
         if (ho.getName().equals(objectName)) {
	   if (objectNames.size() > 0) {
	     return findHObject(node,objectNames);
	   } else {
	     return ho;
	   }
         }
      }
      return null;
    }
  }
}
