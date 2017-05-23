package gov.nasa.gsfc.drl.thumbnails.test;
import javax.media.jai.*;
import javax.swing.*;

public class Test2jai {
  public static void main (String[] args) {
    JFileChooser chooser = new JFileChooser();
    switch (chooser.showOpenDialog(null)) {
      case JFileChooser.APPROVE_OPTION: {
	RenderedOp op = JAI.create("fileload",chooser.getSelectedFile().getAbsolutePath());
	break;
      }
    }
    System.exit(0);
  }
}
