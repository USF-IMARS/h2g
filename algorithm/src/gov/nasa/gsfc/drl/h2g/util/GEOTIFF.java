/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/


package gov.nasa.gsfc.drl.h2g.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageOutputStream;

import org.w3c.dom.Node;

public class GEOTIFF {
	/****************************************************************************
	 * fromBufferedImage.
	 ****************************************************************************/
	public static void fromBufferedImage(BufferedImage[] bi, double latScale,
			double lonScale, double minLon, double maxLat, File file)
			throws Exception {
		Configuration config=Configuration.getInstance();
		GEOTIFFTags gtifftags=config.getGtifftags();
		FileImageOutputStream fios = new FileImageOutputStream(file);
		Iterator it = ImageIO.getImageWritersByFormatName("tiff");
		ImageWriter iw = (ImageWriter) it.next();
		iw.setOutput(fios);
		for (int i = 0; i < bi.length; i++) {
			System.out.println("In GeoTIFF:minLon=" + minLon + " maxLat="
					+ maxLat);
			IIOMetadata m = iw.getDefaultImageMetadata(new ImageTypeSpecifier(
					bi[i]), null);
			Node node = m.getAsTree(m.getMetadataFormatNames()[0]);
			
			/*node.getFirstChild().appendChild(
					Util.getModelPixelScaleTagNode(new double[] { lonScale,
							latScale, 0.0 }));
			node.getFirstChild().appendChild(
					Util.getModelTiePointTagNode(new double[] { 0.0, 0.0, 0.0,
							minLon, maxLat, 0.0 }));
			node.getFirstChild().appendChild(
					Util.getGeoKeyDirectoryNode(new int[] { 1, 1, 0, 5, 1024,
							0, 1, 2, 1025, 0, 1, 1, 2048, 0, 1, 4326, 2049,
							34737, 11, 0, 2050, 0, 1, 6030 }));
			node.getFirstChild().appendChild(
					Util.getGeoAsciiParamsNode("GEOGRAPHIC|"));*/
			
			switch(config.getProjection())
			{
			   case Configuration.GEOGRAPHIC:
				   node.getFirstChild().appendChild(
							Util.getModelPixelScaleTagNode(gtifftags.getPixelScaleTag()));
					node.getFirstChild().appendChild(
							Util.getModelTiePointTagNode(gtifftags.getModelTiePointTag()));
					node.getFirstChild().appendChild(
							Util.getGeoKeyDirectoryNode(new int[] { 1, 1, 0, 5, GEOTIFFTags.getGTModelTypeGeoKeyID(),
									0, 1, gtifftags.getGTModelTypeGeoKey(), GEOTIFFTags.getGTRasterTypeGeoKeyID(), 0, 1, gtifftags.getGTRasterTypeGeoKey(), GEOTIFFTags.getGeographicTypeGeoKeyID(), 0, 1, gtifftags.getGeographicTypeGeoKey(), GEOTIFFTags.getGeogCitationGeoKeyID(),
									34737, gtifftags.getGeogCitationGeoKey().length(), 0, GEOTIFFTags.getGeogGeodeticDatumGeoKeyID(), 0, 1, gtifftags.getGeogGeodeticDatumGeoKey() }));
					node.getFirstChild().appendChild(
							Util.getGeoAsciiParamsNode(gtifftags.getGeogCitationGeoKey()));
				   break;
			   case Configuration.TMERCATOR:
				   /*node.getFirstChild().appendChild(
						   Util.getModelPixelScaleTagNode(gtifftags.getPixelScaleTag()));
				   node.getFirstChild().appendChild(
						   Util.getModelTiePointTagNode(gtifftags.getModelTiePointTag()));
				   
				   node.getFirstChild().appendChild(
						   Util.getGeoKeyDirectoryNode(new int[] { 1, 1, 0, 16, 
								   GEOTIFFTags.getGTModelTypeGeoKeyID(),0, 1, gtifftags.getGTModelTypeGeoKey(), 
								   GEOTIFFTags.getGTRasterTypeGeoKeyID(), 0, 1, gtifftags.getGTRasterTypeGeoKey(), 
								   GEOTIFFTags.getGeographicTypeGeoKeyID(), 0, 1, gtifftags.getGeographicTypeGeoKey(), 
								   GEOTIFFTags.getGTCitationGeoKeyID(),34737, gtifftags.getGTCitationGeoKey().length(), 0, 
								   GEOTIFFTags.getGeogGeodeticDatumGeoKeyID(), 0, 1, gtifftags.getGeogGeodeticDatumGeoKey(),
								   GEOTIFFTags.getGeogLinearUnitsGeoKeyID(),0,1,gtifftags.getGeogLinearUnitsGeoKey(),
								   GEOTIFFTags.getGeogAngularUnitsGeoKeyID(),0,1,gtifftags.getGeogAngularUnitsGeoKey(),
								   GEOTIFFTags.getProjectedCSTypeGeoKeyID(),0,1,gtifftags.getProjectedCSTypeGeoKey(),
								   GEOTIFFTags.getProjectionGeoKeyID(),0,1,gtifftags.getProjectionGeoKey(),
								   GEOTIFFTags.getProjCoordTransGeoKeyID(),0,1,gtifftags.getProjCoordTransGeoKey(),
								   GEOTIFFTags.getProjLinearUnitsGeoKeyID(),0,1,gtifftags.getProjLinearUnitsGeoKey(),
								   GEOTIFFTags.getProjFalseNorthingGeoKeyID(),34736,1,0,
								   GEOTIFFTags.getProjFalseEastingGeoKeyID(),34736,1,1,
								   GEOTIFFTags.getProjNatOriginLatGeoKeyID(),34736,1,2,
								   GEOTIFFTags.getProjCenterLongGeoKeyID(),34736,1,3,
								   GEOTIFFTags.getProjScaleAtNatOriginGeoKeyID(),34736,1,4
								   }));
				   node.getFirstChild().appendChild(
						   Util.getGeoAsciiParamsNode(gtifftags.getGTCitationGeoKey()));
				   node.getFirstChild().appendChild(
						   Util.getGeoDoublesParamsNode(new double[]{
								   gtifftags.getProjFalseNorthingGeoKey(),
								   gtifftags.getProjFalseEastingGeoKey(),
								   gtifftags.getProjNatOriginLatGeoKey(),
								   gtifftags.getProjCenterLongGeoKey(),
								   gtifftags.getProjScaleAtNatOriginGeoKey()}
								   ));
				   break;*/
			   case Configuration.MERCATOR:
			   case Configuration.SINUSOIDAL:
			   case Configuration.LAZIMUTHAL:
			   case Configuration.STEREOGRAPHIC:
				   node.getFirstChild().appendChild(
						   Util.getModelPixelScaleTagNode(gtifftags.getPixelScaleTag()));
				   node.getFirstChild().appendChild(
						   Util.getModelTiePointTagNode(gtifftags.getModelTiePointTag()));
				   
				   node.getFirstChild().appendChild(
						   Util.getGeoKeyDirectoryNode(new int[] { 1, 1, 0, 16, 
								   GEOTIFFTags.getGTModelTypeGeoKeyID(),0, 1, gtifftags.getGTModelTypeGeoKey(), 
								   GEOTIFFTags.getGTRasterTypeGeoKeyID(), 0, 1, gtifftags.getGTRasterTypeGeoKey(), 
								   GEOTIFFTags.getGeographicTypeGeoKeyID(), 0, 1, gtifftags.getGeographicTypeGeoKey(), 
								   GEOTIFFTags.getGTCitationGeoKeyID(),34737, gtifftags.getGTCitationGeoKey().length(), 0, 
								   GEOTIFFTags.getGeogGeodeticDatumGeoKeyID(), 0, 1, gtifftags.getGeogGeodeticDatumGeoKey(),
								   GEOTIFFTags.getGeogLinearUnitsGeoKeyID(),0,1,gtifftags.getGeogLinearUnitsGeoKey(),
								   GEOTIFFTags.getGeogAngularUnitsGeoKeyID(),0,1,gtifftags.getGeogAngularUnitsGeoKey(),
								   GEOTIFFTags.getProjectedCSTypeGeoKeyID(),0,1,gtifftags.getProjectedCSTypeGeoKey(),
								   GEOTIFFTags.getProjectionGeoKeyID(),0,1,gtifftags.getProjectionGeoKey(),
								   GEOTIFFTags.getProjCoordTransGeoKeyID(),0,1,gtifftags.getProjCoordTransGeoKey(),
								   GEOTIFFTags.getProjLinearUnitsGeoKeyID(),0,1,gtifftags.getProjLinearUnitsGeoKey(),
								   GEOTIFFTags.getProjFalseNorthingGeoKeyID(),34736,1,0,
								   GEOTIFFTags.getProjFalseEastingGeoKeyID(),34736,1,1,
								   GEOTIFFTags.getProjNatOriginLongGeoKeyID(),34736,1,2,
								   GEOTIFFTags.getProjNatOriginLatGeoKeyID(),34736,1,3,
								   GEOTIFFTags.getProjScaleAtNatOriginGeoKeyID(),34736,1,4
								   //GEOTIFFTags.getProjCenterLongGeoKeyID(),34736,1,5,
								   //GEOTIFFTags.getProjCenterLatGeoKeyID(),34736,1,6
								   
								   }));
				   node.getFirstChild().appendChild(
						   Util.getGeoAsciiParamsNode(gtifftags.getGTCitationGeoKey()));
				   node.getFirstChild().appendChild(
						   Util.getGeoDoublesParamsNode(new double[]{
								   gtifftags.getProjFalseNorthingGeoKey(),
								   gtifftags.getProjFalseEastingGeoKey(),
								   gtifftags.getProjNatOriginLongGeoKey(),
								   gtifftags.getProjNatOriginLatGeoKey(),								   
								   gtifftags.getProjScaleAtNatOriginGeoKey()
								   //gtifftags.getProjCenterLongGeoKey(),
								   //gtifftags.getProjCenterLatGeoKey(),
								   }
								   ));
				   break;
			}
			
			m.setFromTree(m.getMetadataFormatNames()[0], node);
			// Next Line changed by S. Dasgupta 11/23/07 from
			// "IIOImage iioi = new IIOImage(bi[i],new LinkedList/*<BufferedImage>*/(),m);"
			// Type checking for java 1.5
			IIOImage iioi = new IIOImage(bi[i],
					new LinkedList<BufferedImage>(), m);
			iw.write(iioi);
		}
		fios.close();
	}
}
