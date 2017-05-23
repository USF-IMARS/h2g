package gov.nasa.gsfc.drl.h2g.util;

import java.text.AttributedString;
import java.awt.font.TextAttribute;

public class Markers {
	private int x;
	private int y;
	private int latlon;
	private boolean isLat;
	private boolean markerUsed;
	public Markers(int xMarker, int yMarker, int latorlon, boolean isLat ) {
		x=xMarker;
		y=yMarker;
		latlon=latorlon;
		this.isLat=isLat;
		this.markerUsed=Boolean.TRUE;
		
	}
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
	/**
	 * @return the latlon
	 */
	public int getLatlon() {
		return latlon;
	}
	/**
	 * @param latlon the latlon to set
	 */
	public void setLatlon(int latlon) {
		this.latlon = latlon;
	}
	
	public AttributedString getMarkerString() {
		AttributedString markerstring;
		int superscriptindex=-1;
		
		if(Math.abs(latlon)<10)
			 superscriptindex=1;
		 else if (Math.abs(latlon)<100)
			 superscriptindex=2;
		 else if (Math.abs(latlon)<1000)
			 superscriptindex=3;
		 else
			 return null;
		
		
		if(isLat){
			if(latlon<0){
				 markerstring = new AttributedString(Integer.toString(Math.abs(latlon))+"0S");			 
				 
			}else{
				 markerstring = new AttributedString(Integer.toString(latlon)+"0N");
			}
		}else{
			if(latlon<0){
				markerstring = new AttributedString(Integer.toString(Math.abs(latlon))+"0W");
			}else{
				markerstring = new AttributedString(Integer.toString(latlon)+"0E");
			}
		}
		markerstring.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, superscriptindex, superscriptindex+1);
			
		
		return markerstring;
	}
	/**
	 * @return the markerUsed
	 */
	public boolean isMarkerUsed() {
		return markerUsed;
	}
	/**
	 * @param markerUsed the markerUsed to set
	 */
	public void setMarkerUsed(boolean markerUsed) {
		this.markerUsed = markerUsed;
	}
	

}
