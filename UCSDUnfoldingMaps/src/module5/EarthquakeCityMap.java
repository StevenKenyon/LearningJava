package module5;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import module4.CityMarker;
import parsing.ParseFeed;
import processing.core.PApplet;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Steven Kenyon
 * Date: April 15, 2016
 * 
 * Your map is about to become interactive! You are going to add functionality to your 
 * map so that additional information is displayed when the user hovers over or clicks 
 * on any marker with her/ his mouse. When she hovers over a city marker, your map will 
 * display a box with the city’s name, country, and population. When she hovers over an 
 * earthquake marker, your map will display the title of the earthquake (including its 
 * magnitude and region). Clicking on a marker gives even more information: A click on 
 * a city marker will lead to only that city and earthquakes which affect it being 
 * displayed on the map. Clicking once again on that marker will bring the rest of the 
 * map’s markers back. Similarly, after clicking on an earthquake marker, only cities 
 * potentially affected by that earthquake will be displayed. You’ll use event-driven 
 * programming to make this happen.
 * 
 * To accomplish this you will need to override two methods, mouseClicked() and 
 * mouseMoved(). In both of the these methods you will need to make use of the 
 * isInside() method of the SimplePointMarker class, as well as the PApplet fields, 
 * mouseX and mouseY.
 * */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setup and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private UnfoldingMap map;
	
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;
	
	// NEW IN MODULE 5
	private CommonMarker lastSelected; //selected means hovered over
	private CommonMarker lastClicked;
	//private CommonMarker theSelectedMarker;
	
	public void setup() {
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new module5.CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }

	    // could be used for debugging
	    printQuakes();
	 		
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	}  // End setup
	
	
	public void draw() {
		background(0);
		map.draw();
		addKey();
		
	}
	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
	}
	
	// If there is a marker under the cursor, and lastSelected is null 
	// set the lastSelected to be the first marker found under the cursor
	// Make sure you do not select two markers.
	// 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Done: Implement this method
			//Rotate through all markers checking to see if the mouse is within
			// the marker.
			for(int i = 0; i < markers.size(); i++)
			{
				if (lastSelected == null) 
				{
					Marker theMarker = markers.get(i);
					//If so mark it as selected using the setSelected() method
					if(theMarker.isInside(map, (float)mouseX, (float)mouseY))
					{
						theMarker.setSelected(true);
						lastSelected = (CommonMarker) theMarker;
					}
					else
					{
						theMarker.setSelected(false);
					}
				}
			}		
	}
	
	// method for testing - Draws a small white square on the map.
	private void drawSquareForTesting()
	{
		fill(255, 255, 255);
		rect(100, 100, 25, 25);		
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
		// TODO: Implement this method
		// Hint: You probably want a helper method or two to keep this code
		// from getting too long/disorganized
		// If (lastClicked == null) set the marker as clicked and copy to lastClicked
		// 		hide markers outside threat circle
		if(lastClicked == null)// TODO: mark this not null and null with intelligence.
		{
			//was it a marker that was clicked? or just ocean/bare land
			if(markerWasClicked(quakeMarkers) | markerWasClicked(cityMarkers))
			{
				System.out.println("Clicked a marker");
				
				// get specific marker that was clicked
				Marker m = getMarkerThatWasClicked(quakeMarkers);
				if(m == null) m = getMarkerThatWasClicked(cityMarkers);
				
				// hide non-threatening markers
				boolean hiddenFine = hideNonthreatingMarkers(m);
				// marker.getDistanceTo(otherMarker) gives distance.  combine with threatCircle() to choose
				if(!hiddenFine) System.out.println("Error trying to hide markers!!!??");
				lastClicked = (CommonMarker) m;
			}
		}
		else // if (lastClicked != null) unhide all markers and deselect chosen marker and make lastClicked = null
		{
			System.out.println("Clicked a marker");
			unhideMarkers();
			lastClicked = null;			
		}		
	}
	/**
	 * Hides all the markers that are not within the threat Circle of the specified marker.
	 * @param clickedMarker
	 * @return
	 */
	private boolean hideNonthreatingMarkers(Marker clickedMarker)
	{
		boolean allHiddenFine = false;
		//if a city marker rotate through all quakes to see if the city is 
		//within any threat circle.
		if(clickedMarker instanceof module5.CityMarker)
		{
			//hide quakes that don't affect this city
			for(int i = 0; i < quakeMarkers.size(); i++)
			{
				EarthquakeMarker aQuake = (EarthquakeMarker) quakeMarkers.get(i);
				if(isOutsideThreatCircle(aQuake, clickedMarker))
				{
					aQuake.setHidden(true);
				}				
			}
			//hide other cities
			boolean citiesHiddenFine = hideOtherCities((module5.CityMarker) clickedMarker);
			if(!citiesHiddenFine) System.out.println("=== Issue Hiding Cities ===");
		}
		else if(clickedMarker instanceof LandQuakeMarker 
				|| clickedMarker instanceof OceanQuakeMarker)
		{
			System.out.println("This is a LandQuake or OceanQuake Marker");
			//Hide any quake that is not the selected quake
			for(int i = 0; i < quakeMarkers.size(); i++)
			{
				Marker aQuake = quakeMarkers.get(i);
				if(aQuake.getLocation() != clickedMarker.getLocation())
				{
					aQuake.setHidden(true);
				}				
			}
			//Hide any city outside the threat circle
			for(int i = 0; i < cityMarkers.size(); i++)
			{
				Marker aCity = cityMarkers.get(i);
				if(isOutsideThreatCircle((EarthquakeMarker) clickedMarker, aCity))
				{
					aCity.setHidden(true);
				}		
			}
		}
		//if a quake marker compare to all other markers to see if any are within 
		// any threat circle.
		
		allHiddenFine = true;
		return allHiddenFine;		
	}
	/**
	 * check to see if two markers are within the "threat circle"
	 * @param em
	 * @param m
	 * @return
	 */
	private boolean isOutsideThreatCircle(EarthquakeMarker em, Marker m)
	{
		boolean isOutside = false;
		double distTween = em.getDistanceTo(m.getLocation());
		if(em.threatCircle() < distTween)//City is not within a quake's threat Circle
		{
			isOutside = true;
		}		
		return isOutside;		
	}
	
	/**
	 * Hide all cities except the passed "keeper" city.
	 * @param keeper
	 * @return
	 */
	private boolean hideOtherCities(Marker keeper)
	{
		boolean allGood = false;
		for(int i = 0; i < cityMarkers.size(); i++)
		{
			module5.CityMarker aCity = (module5.CityMarker)cityMarkers.get(i);
			if(keeper.getLocation() != aCity.getLocation())
			{
				aCity.setHidden(true);
			}
		}
		allGood = true;
		return allGood;
	}
	
	// Check to see if the mouse is hovering over a marker RIGHT NOW.
	// Probably should use the MouseClicked(MouseEvent e) constructor but that's not
	// what the teachers are encouraging.
	private boolean markerWasClicked(List<Marker> markers)
	{		
		boolean markerFound = false;
		for(int i = 0; i < markers.size(); i++)
		{
			if(!markerFound)
			{
				Marker theMarker = markers.get(i);
				if(theMarker.isInside(map, (float)mouseX, (float)mouseY))
				{
					markerFound = true;
				}
			}
		}		
		return markerFound;
	}
	
	/**
	 * Returns a marker based on where the mouse is currently hovering.
	 * Returns a null value if none found.
	 * @param markers
	 * @return
	 */
	private Marker getMarkerThatWasClicked(List<Marker> markers)
	{
		boolean markerFound = false;
		Marker returnMarker = null;
		for(int i = 0; i < markers.size(); i++)
		{
			if(!markerFound)
			{
				Marker aMarker = markers.get(i);
				if(aMarker.isInside(map, (float)mouseX, (float)mouseY))
				{
					returnMarker = aMarker;
					markerFound = true;
				}
			}
		}
		return returnMarker;
	}
	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	
	// helper method to draw key in GUI
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 150, 250);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", xbase+25, ybase+25);
		
		fill(150, 30, 30);
		int tri_xbase = xbase + 35;
		int tri_ybase = ybase + 50;
		triangle(tri_xbase, tri_ybase-CityMarker.TRI_SIZE, tri_xbase-CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE, tri_xbase+CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE);

		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("City Marker", tri_xbase + 15, tri_ybase);
		
		text("Land Quake", xbase+50, ybase+70);
		text("Ocean Quake", xbase+50, ybase+90);
		text("Size ~ Magnitude", xbase+25, ybase+110);
		
		fill(255, 255, 255);
		ellipse(xbase+35, 
				ybase+70, 
				10, 
				10);
		rect(xbase+35-5, ybase+90-5, 10, 10);
		
		fill(color(255, 255, 0));
		ellipse(xbase+35, ybase+140, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase+35, ybase+160, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase+35, ybase+180, 12, 12);
		
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase+50, ybase+140);
		text("Intermediate", xbase+50, ybase+160);
		text("Deep", xbase+50, ybase+180);

		text("Past hour", xbase+50, ybase+200);
		
		fill(255, 255, 255);
		int centerx = xbase+35;
		int centery = ybase+200;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx-8, centery-8, centerx+8, centery+8);
		line(centerx-8, centery+8, centerx+8, centery-8);
			
	}

	
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.	
	private boolean isLand(PointFeature earthquake) {
		
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	// prints countries with number of earthquakes
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
	}
	
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}
}
