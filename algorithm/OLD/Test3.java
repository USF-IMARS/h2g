package gov.nasa.gsfc.drl.thumbnails.test;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.imageio.metadata.*;
import javax.swing.*;
import java.util.*;

public class Test3 {
  public static void main (String[] args) throws Exception {
    JFileChooser chooser = new JFileChooser();
    switch (chooser.showOpenDialog(null)) {
      case JFileChooser.APPROVE_OPTION: {
      	ImageInputStream iis = ImageIO.createImageInputStream(chooser.getSelectedFile());
      	Iterator it = ImageIO.getImageReaders(iis);
      	ImageReader ir = (ImageReader) it.next();
      	ir.setInput(iis);
      	IIOMetadata m = ir.getImageMetadata(0);
      	GeoTiffIIOMetadataAdapter a = new GeoTiffIIOMetadataAdapter(m);
      	System.out.println(a.getGeoKeyDirectoryVersion());
      	System.out.println(a.getGeoKeyRevision());
      	System.out.println(a.getGeoKeyMinorRevision());
      	int numGeoKeys = a.getNumGeoKeys();
      	System.out.println(numGeoKeys);
      	for (int i = 0; i < numGeoKeys; i++) {

      	}
      	double[] scales = a.getModelPixelScales();
      	System.out.println("scales: " + scales[0] + " " + scales[1] + " " + scales[2]);
      	double[] tiepoints = a.getModelTiePoints();
      	System.out.println("tiepoints: " + tiepoints[0] + " " + tiepoints[1] + " " + tiepoints[2] + " " + tiepoints[3] + " " + tiepoints[4] + " " + tiepoints[5]);
      	// double[] trans = a.getModelTransformation();
        // System.out.println("transformation: " + trans[0]);
	break;
      }
    }
    System.exit(0);
  }
}
