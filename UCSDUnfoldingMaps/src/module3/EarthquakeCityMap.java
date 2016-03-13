/**
 * Week 2 Module three lab assignement
 * @author Steven Kenyon
 * 
 */
package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;
import processing.core.PShape;
//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.AbstractMarker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Steven Kenyon
 * Date: February 7, 2016
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;
	

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	// The Key
	PShape body;
	
	//Colors
	private int blueColor = color(0, 0, 255);
	private int yellowColor = color(255, 255, 0);
	private int redColor = color(204, 0, 0);
	private int whiteColor = color(255, 255, 255);
	private int blackColor = color(0, 0, 0);
	
	//Size markers
	private int minorSize = 5;
	private int lightSize = 9;
	private int moderateSize = 12;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	

	
	public void setup() {
		size(950, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    // These print statements show you (1) all of the relevant properties 
	    // in the features, and (2) how to get one property and use it
	    if (earthquakes.size() > 0) {
	    	PointFeature f = earthquakes.get(0);
	    	System.out.println(f.getProperties());
	    	Object magObj = f.getProperty("magnitude");
	    	float mag = Float.parseFloat(magObj.toString());
	    	// PointFeatures also have a getLocation method
	    	
	    }
	    
	    // Here is an example of how to use Processing's color method to generate 
	    // an int that represents the color yellow.  
	    //int yellow = color(255, 255, 0);
	    
	    //TODO: Add code here as appropriate
	    //System.out.println("earthquakes.size:" + earthquakes.size());
	    //Add the points to the map
	    for(int e = 0; e < earthquakes.size(); e++)
	    {
	    	PointFeature pf = earthquakes.get(e);
	    	SimplePointMarker spm = createMarker(pf);
	    	map.addMarker(spm);
	    }
	}
		
	// A suggested helper method that takes in an earthquake feature and 
	// returns a SimplePointMarker for that earthquake
	// TODO: Implement this method and call it from setUp, if it helps
	private SimplePointMarker createMarker(PointFeature feature)
	{
		// finish implementing and use this method, if it helps.
		SimplePointMarker output = new SimplePointMarker(feature.getLocation());
		
    	Object magObj = feature.getProperty("magnitude");
    	float mag = Float.parseFloat(magObj.toString());
    	
    	//Minor earthquakes (less than magnitude 4.0) will have blue markers and be small.
    	if(mag < THRESHOLD_LIGHT)
    	{
			output.setColor(blueColor);//blue
			output.setStrokeColor(blueColor);//blue
			output.setRadius(minorSize);
    	}
    	//Light earthquakes (between 4.0-4.9) will have yellow markers and be medium.
    	else if((mag >= THRESHOLD_LIGHT) 
    			&& (mag < THRESHOLD_MODERATE))
    	{
    		output.setColor(yellowColor);//yellow
    		output.setStrokeColor(yellowColor);//yellow
    		output.setRadius(lightSize);
    	}
    	//Moderate and higher earthquakes (5.0 and over) will have red markers and be largest.
    	else
    	{
    		output.setColor(redColor);//red
    		output.setStrokeColor(redColor);//red
    		output.setRadius(moderateSize);
    	}    	
		return output;
	}

	//Draw the GUI
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}


	// helper method to draw key in GUI
	// TODO: Implement this method to draw the key
	private void addKey() 
	{	
		// Remember you can use Processing's graphics methods here
		
		//Header text
		textSize(15);
		fill(whiteColor);
		text("Earthquake Key:", 50, 60);
		
		//Key info
		textSize(14);
		fill(redColor);
		ellipse(50, 95, moderateSize, moderateSize);
		fill(whiteColor);
		text("5+ Mag", 70, 100);		

		fill(yellowColor);
		ellipse(50, 135, lightSize, lightSize);
		fill(whiteColor);
		text("4+ Mag", 70, 140);

		fill(blueColor);
		ellipse(50, 175, minorSize, minorSize);
		fill(whiteColor);
		text("Below 4 Mag", 70, 180);
		
	}
}
