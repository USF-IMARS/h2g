/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/


package gov.nasa.gsfc.drl.h2g.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.StringTokenizer;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import gov.nasa.gsfc.drl.h2g.vector.*;

public class Util {
	/****************************************************************************
	 * readNormalizedColorMap.
	 ****************************************************************************/
	public static byte[][] readNormalizedColorMap(String path) throws Exception {
		byte[][] colorMap = new byte[3][256];
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(Util
					.getInputStream(path)));
			for (int value = 0; value < 256; value++) {
				String line = in.readLine();
				StringTokenizer byBlanks = new StringTokenizer(line, " ");
				colorMap[0][value] = (byte) (255 * Double.parseDouble(byBlanks
						.nextToken()));
				colorMap[1][value] = (byte) (255 * Double.parseDouble(byBlanks
						.nextToken()));
				colorMap[2][value] = (byte) (255 * Double.parseDouble(byBlanks
						.nextToken()));
				System.out.println(value + " "
						+ unsignedByteToInt(colorMap[0][value]) + " "
						+ unsignedByteToInt(colorMap[1][value]) + " "
						+ unsignedByteToInt(colorMap[2][value]));
			}
			in.close();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		return colorMap;
	}

	public static byte[][] readMaskColorMap(String path) throws Exception {
		byte[][] colorMap = new byte[3][4];
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(Util
					.getInputStream(path)));
			in.readLine();
			for (int value = 0; value < 4; value++) {
				String line = in.readLine();
				StringTokenizer byBlanks = new StringTokenizer(line, " ");
				byBlanks.nextToken();
				colorMap[0][value] = (byte) Integer.parseInt(byBlanks
						.nextToken());
				colorMap[1][value] = (byte) Integer.parseInt(byBlanks
						.nextToken());
				colorMap[2][value] = (byte) Integer.parseInt(byBlanks
						.nextToken());
			}
			in.close();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		return colorMap;
	}

	/****************************************************************************
	 * readIntegerColorMap.
	 ****************************************************************************/
	public static byte[][] readIntegerColorMap(String path) throws Exception {
		byte[][] colorMap = new byte[3][256];
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(Util
					.getInputStream(path)));
			// in= new BufferedReader(new FileReader(path));
			in.readLine();
			for (int value = 0; value < 256; value++) {
				String line = in.readLine();
				StringTokenizer byBlanks = new StringTokenizer(line, " ");
				byBlanks.nextToken();
				colorMap[0][value] = (byte) Integer.parseInt(byBlanks
						.nextToken());
				colorMap[1][value] = (byte) Integer.parseInt(byBlanks
						.nextToken());
				colorMap[2][value] = (byte) Integer.parseInt(byBlanks
						.nextToken());
			}
			in.close();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		return colorMap;
	}

	public static byte[][] readIntegerColorMap_nonStandard(String path)
			throws Exception {
		byte[][] colorMap = new byte[3][256];
		BufferedReader in = null;
		try {
			// in = new BufferedReader(new
			// InputStreamReader(Util.getInputStream(path)));
			in = new BufferedReader(new FileReader(path));
			in.readLine();
			for (int value = 0; value < 256; value++) {
				String line = in.readLine();
				StringTokenizer byBlanks = new StringTokenizer(line, " ");
				byBlanks.nextToken();
				colorMap[0][value] = (byte) Integer.parseInt(byBlanks
						.nextToken());
				colorMap[1][value] = (byte) Integer.parseInt(byBlanks
						.nextToken());
				colorMap[2][value] = (byte) Integer.parseInt(byBlanks
						.nextToken());
			}
			in.close();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		return colorMap;
	}

	/****************************************************************************
	 * getModelPixelScaleTagNode.
	 ****************************************************************************/
	public static Node getModelPixelScaleTagNode(double[] values) {
		IIOMetadataNode n = new IIOMetadataNode("TIFFField");
		n.setAttribute("number", "33550");
		n.setAttribute("name", "ModelPixelScaleTag");
		IIOMetadataNode n1 = new IIOMetadataNode("TIFFDoubles");
		n.appendChild(n1);
		for (int i = 0; i < values.length; i++) {
			IIOMetadataNode n2 = new IIOMetadataNode("TIFFDouble");
			n2.setAttribute("value", Double.toString(values[i]));
			n1.appendChild(n2);
		}
		return n;
	}

	/****************************************************************************
	 * getModelTiePointTagNode.
	 ****************************************************************************/
	public static Node getModelTiePointTagNode(double[] values) {
		IIOMetadataNode n = new IIOMetadataNode("TIFFField");
		n.setAttribute("number", "33922");
		n.setAttribute("name", "ModelTiePointTag");
		IIOMetadataNode n1 = new IIOMetadataNode("TIFFDoubles");
		n.appendChild(n1);
		for (int i = 0; i < values.length; i++) {
			IIOMetadataNode n2 = new IIOMetadataNode("TIFFDouble");
			n2.setAttribute("value", Double.toString(values[i]));
			n1.appendChild(n2);
		}
		return n;
	}

	/****************************************************************************
	 * getGeoKeyDirectoryNode.
	 ****************************************************************************/
	public static Node getGeoKeyDirectoryNode(int[] values) {
		IIOMetadataNode n = new IIOMetadataNode("TIFFField");
		n.setAttribute("number", "34735");
		n.setAttribute("name", "GeoKeyDirectory");
		IIOMetadataNode n1 = new IIOMetadataNode("TIFFShorts");
		n.appendChild(n1);
		System.out.println("No of Keys="+values.length);
		for (int i = 0; i < values.length; i++) {
			IIOMetadataNode n2 = new IIOMetadataNode("TIFFShort");
			//System.out.println("Putting "+Integer.toString(values[i]));
			n2.setAttribute("value", Integer.toString(values[i]));
			n1.appendChild(n2);
		}
		return n;
	}

	/****************************************************************************
	 * getGeoAsciiParamsNode.
	 ****************************************************************************/
	public static Node getGeoAsciiParamsNode(String value) {
		IIOMetadataNode n = new IIOMetadataNode("TIFFField");
		n.setAttribute("number", "34737");
		n.setAttribute("name", "GeoAsciiParams");
		IIOMetadataNode n1 = new IIOMetadataNode("TIFFAsciis");
		n.appendChild(n1);
		IIOMetadataNode n2 = new IIOMetadataNode("TIFFAscii");
		n2.setAttribute("value", value);
		n1.appendChild(n2);
		return n;
	}
	
	public static Node getGeoDoublesParamsNode(double values[]) {
		IIOMetadataNode n = new IIOMetadataNode("TIFFField");
		n.setAttribute("number", "34736");
		n.setAttribute("name", "GeoDoubleParams");
		IIOMetadataNode n1 = new IIOMetadataNode("TIFFDoubles");
		n.appendChild(n1);
		for (int i = 0; i < values.length; i++) {
			IIOMetadataNode n2 = new IIOMetadataNode("TIFFDouble");
			n2.setAttribute("value", Double.toString(values[i]));
			n1.appendChild(n2);
		}		
		return n;		
	}

	/****************************************************************************
	 * getColorMapNode.
	 ****************************************************************************/
	public static Node getColorMapNode(int[] map) {
		IIOMetadataNode n = new IIOMetadataNode("TIFFField");
		n.setAttribute("number", "320");
		n.setAttribute("name", "Colormap");
		IIOMetadataNode n1 = new IIOMetadataNode("TIFFShorts");
		n.appendChild(n1);
		for (int i = 0; i < map.length; i++) {
			IIOMetadataNode n2 = new IIOMetadataNode("TIFFShort");
			n2.setAttribute("value", Integer.toString(map[i]));
			n1.appendChild(n2);
		}
		return n;
	}

	/****************************************************************************
	 * setPhotometric.
	 ****************************************************************************/
	public static void setPhotometric(Node node, String value,
			String description) {
		Node n = findNode(node, "262");
		scanNode(n.getChildNodes().item(0).getChildNodes().item(0), 0);
		Element e = (Element) n.getChildNodes().item(0).getChildNodes().item(0);
		NamedNodeMap map = e.getAttributes();
		Attr a = (Attr) map.getNamedItem("value");
		a.setValue(value);
		a = (Attr) map.getNamedItem("description");
		a.setValue(description);
		scanNode(n.getChildNodes().item(0).getChildNodes().item(0), 0);
	}

	/****************************************************************************
	 * findNode.
	 ****************************************************************************/
	public static Node findNode(Node node, String number) {
		while (node != null) {
			switch (node.getNodeType()) {
			case Node.ELEMENT_NODE: {
				Node n = findElement((Element) node, number);
				if (n != null) {
					return n;
				}
				break;
			}
				/*
				 * default: { System.out.println(getIndent(level) +
				 * "unknown node type: " + node.getNodeType()); break; }
				 */
			}
			Node n = findNode(node.getFirstChild(), number);
			if (n != null) {
				return n;
			}
			node = node.getNextSibling();
		}
		return null;
	}

	private static Element findElement(Element e, String number) {
		NamedNodeMap map = e.getAttributes();
		// System.out.println(getIndent(level) + "elementTagName: " +
		// e.getTagName() + " nAttrs: " + map.getLength() + " nElems: " +
		// list.getLength());
		for (int i = 0; i < map.getLength(); i++) {
			Node n = map.item(i);
			switch (n.getNodeType()) {
			case Node.ATTRIBUTE_NODE: {
				Attr a = (Attr) n;
				// System.out.println(getIndent(level+1) + "attrName: " +
				// a.getName() + " attrValue: " + a.getValue());
				if (a.getName().equals("number") && a.getValue().equals(number)) {
					return e;
				}
				break;
			}
				/*
				 * default: { System.out.println(getIndent(level) +
				 * "unknown node type: " + n.getNodeType()); break; }
				 */
			}
		}
		/*
		 * NodeList list = e.getElementsByTagName("*"); for (int i = 0; i <
		 * list.getLength(); i++) { Node n = list.item(i);
		 * System.out.println(getIndent(level) + "elemName: " +
		 * n.getNodeName()); }
		 */
		return null;
	}

	/****************************************************************************
	 * scanMetadata.
	 ****************************************************************************/
	public static void scanMetadata(IIOMetadata m) {
		String[] names = m.getMetadataFormatNames();
		for (int i = 0; i < names.length; i++) {
			System.out.println("***" + names[i] + "***");
			Node node = m.getAsTree(m.getMetadataFormatNames()[i]);
			scanNode(node.getFirstChild(), 0);
			System.out.println("");
		}
	}

	/****************************************************************************
	 * scanNode.
	 ****************************************************************************/
	public static void scanNode(Node node, int level) {
		while (node != null) {
			// System.out.println(getIndent(level) + "nodeName: " +
			// node.getNodeName() + " nodeType: " + node.getNodeType());
			switch (node.getNodeType()) {
			case Node.ELEMENT_NODE: {
				scanElement((Element) node, level);
				break;
			}
			default: {
				System.out.println(getIndent(level) + "unknown node type: "
						+ node.getNodeType());
				break;
			}
			}
			scanNode(node.getFirstChild(), level + 1);
			node = node.getNextSibling();
		}
	}

	private static void scanElement(Element e, int level) {
		NamedNodeMap map = e.getAttributes();
		NodeList list = e.getElementsByTagName("*");
		System.out.println(getIndent(level) + "elementTagName: "
				+ e.getTagName() + " nAttrs: " + map.getLength() + " nElems: "
				+ list.getLength());
		for (int i = 0; i < map.getLength(); i++) {
			Node n = map.item(i);
			switch (n.getNodeType()) {
			case Node.ATTRIBUTE_NODE: {
				Attr a = (Attr) n;
				System.out.println(getIndent(level + 1) + "attrName: "
						+ a.getName() + " attrValue: " + a.getValue());
				break;
			}
			default: {
				System.out.println(getIndent(level) + "unknown node type: "
						+ n.getNodeType());
				break;
			}
			}
		}
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			System.out.println(getIndent(level) + "elemName: "
					+ n.getNodeName());
		}
	}

	private static String getIndent(int level) {
		StringBuffer indent = new StringBuffer();
		for (int i = 0; i < level; i++) {
			indent.append("  ");
		}
		return indent.toString();
	}

	/****************************************************************************
	 * toRange.
	 ****************************************************************************/
	public static int toRange(int min, int value, int max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

	public static double toRange(double min, double value, double max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

	/****************************************************************************
	 * inRange.
	 ****************************************************************************/
	public static boolean inRange(int min, int value, int max) {
		return (min <= value && value <= max);
	}

	public static boolean inRange(double min, double value, double max) {
		return (min <= value && value <= max);
	}

	/****************************************************************************
	 * min.
	 ****************************************************************************/
	public static int min(int a, int b) {
		return (a < b ? a : b);
	}

	public static double min(double a, double b) {
		return (a < b ? a : b);
	}

	public static double min(double a, double b, double c, double d) {
		return min(min(a, b), min(c, d));
	}

	/****************************************************************************
	 * max.
	 ****************************************************************************/
	public static int max(int a, int b) {
		return (a > b ? a : b);
	}

	public static double max(double a, double b) {
		return (a > b ? a : b);
	}

	public static double max(double a, double b, double c, double d) {
		return max(max(a, b), max(c, d));
	}

	public static int mode(int[] a) {
		int maxValue = -999;
		int maxCount = 0;

		for (int i = 0; i < a.length; i++) {
			int count = 0;
			for (int j = 0; j < a.length; j++) {
				if (a[j] == a[i])
					++count;
			}
			if (count > maxCount) {
				maxCount = count;
				maxValue = a[i];
			}
		}

		return maxValue;
	}

	public static int avg(int[] a) {

		double sum = 0;

		for (int i = 0; i < a.length; i++)
			sum = sum + a[i];

		return (int) Math.round(sum / a.length);
	}

	/****************************************************************************
	 * getInputStream.
	 ****************************************************************************/
	public static InputStream getInputStream(String path) throws Exception {
		try {
			Object o = new Object();
			Class c = o.getClass();
			URL url = c.getResource("/" + path);
			return url.openStream();
		} catch (Exception e) {

		}
		return new FileInputStream(path);
	}

	private static int unsignedByteToInt(byte b) {
		return b & 0xFF;
	}
	
	/*Cohen Sutherland algorithm : adapted from http://shamimkhaliq.50megs.com/Java/lineclipper.htm*/
	
	private static int outCodes(Point P,int yTop,int yBottom,int xLeft,int xRight)
	{
		int Code = 0; 

		if(P.y > yTop) Code += 1; /* code for above */
		else if(P.y < yBottom) Code += 2; /* code for below */

		if(P.x > xRight) Code += 4; /* code for right */
		else if(P.x < xLeft) Code += 8; /* code for left */

		return Code;
	}

	private static boolean rejectCheck(int outCode1, int outCode2)
	{
		if ((outCode1 & outCode2) != 0 ) return true;
		return(false); 
	} 


	private static boolean acceptCheck(int outCode1, int outCode2)
	{
		if ( (outCode1 == 0) && (outCode2 == 0) ) return(true);
		return(false); 
	}


	public static boolean CohenSutherland2DClipper(Point P0,Point P1,int yTop,int yBottom,int xLeft,int xRight)
	{
		int outCode0,outCode1; 
		while(true)
		{
			outCode0 = outCodes(P0,yTop,yBottom,xLeft,xRight);
			outCode1 = outCodes(P1,yTop,yBottom,xLeft,xRight);
			
			if( rejectCheck(outCode0,outCode1) ) return(false);
			//System.out.println("I am here");
			if( acceptCheck(outCode0,outCode1) ) return(true);
			if(outCode0 == 0)
			{
				double tempCoord; int tempCode;
				tempCoord = P0.x; P0.x= P1.x; P1.x = tempCoord;
				tempCoord = P0.y; P0.y= P1.y; P1.y = tempCoord;
				tempCode = outCode0; outCode0 = outCode1; outCode1 = tempCode;
			} 
			if( (outCode0 & 1) != 0 ) 
			{ 
				P0.x += (P1.x - P0.x)*(yTop - P0.y)/(P1.y - P0.y);
				P0.y = yTop;
			}
			else
				if( (outCode0 & 2) != 0 ) 
				{ 
					P0.x += (P1.x - P0.x)*(yBottom - P0.y)/(P1.y - P0.y);
					P0.y = yBottom;
				}
				else
					if( (outCode0 & 4) != 0 ) 
					{ 
						P0.y += (P1.y - P0.y)*(xRight - P0.x)/(P1.x - P0.x);
						P0.x = xRight;
					}
					else
						if( (outCode0 & 8) != 0 ) 
						{ 
							P0.y += (P1.y - P0.y)*(xLeft - P0.x)/(P1.x - P0.x);
							P0.x = xLeft;
						}
		} 
	} 
 

	

}
