/******************************************************************************
*
*  History:
*
*  18-Nov-05, J Love, GST	Original version.
*
******************************************************************************/

package gov.nasa.gsfc.drl.thumbnails.test;
import javax.swing.*;
import java.io.*;

public class Test1 {
  public static void main (String[] args) throws Exception {
    JFileChooser chooser = new JFileChooser();
    switch (chooser.showOpenDialog(null)) {
      case JFileChooser.APPROVE_OPTION: {
	byte[] buffer = HDF.createThumbnail(chooser.getSelectedFile(),500);
	FileOutputStream out = new FileOutputStream("thumbnail.jpg");
	out.write(buffer);
	out.close();
	break;
      }
    }
    System.exit(0);
  }
}

