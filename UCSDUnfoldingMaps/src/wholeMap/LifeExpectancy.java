package wholeMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

/**
 * Displays a Map with graphical representation of LifeExpectancy by Country.
 * @author Steve
 *
 */
public class LifeExpectancy extends PApplet{

	UnfoldingMap map;
	//String is countryId (the key)
	//Float is the LifeExp (the value)
	Map<String, Float> lifeExpByCountry;
	//Map<String, Float> lifeExpMap;
	List<Feature> countries;
	List<Marker> countryMarkers;
	
	public void setup()
	{
		//Setup map on screen
		size(800, 600, OPENGL);
		map = new UnfoldingMap(this, 50, 50, 700, 500
				, new Google.GoogleMapProvider());
		MapUtils.createDefaultEventDispatcher(this, map);
		
		//load expectancy info from file
		lifeExpByCountry = loadLifeExpectancyFromCSV
				("../data/LifeExpectancyWorldBank.csv");
		
		//1 Feature + 1 Marker per Country
		countries = GeoJSONReader.loadData(this
				, "../data/countries.geo.json");
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		map.addMarkers(countryMarkers);
		shadeCountries();
	}
	
	public void draw()
	{
		map.draw();
	}
	/**
	 * Loads the LifeExpectancyWorldBank info (CSV file)
	 * and returns a Map object.
	 * @param fileName
	 * @return Map<String, Float>
	 */
	private Map<String, Float> loadLifeExpectancyFromCSV(String fileName)
	{
		Map<String, Float> lifeExpMap 
			= new HashMap<String, Float>();
		
		String[] rows = loadStrings(fileName);
		int lineNum = 1;
		//println("Loading file: " + fileName);
		for(String row : rows)
		{
			//println("linNum: " + lineNum++);
			String[] columns = row.split(",");
			
			if(isFloat(columns[5]))
			{
				Float value = Float.parseFloat(columns[5]);
				lifeExpMap.put(columns[4], value);
				//println("Adding key: " + columns[4] + " and value: " + columns[5]);
			}
			else
			{
				//println(columns[5] + " is not a float");
			}
		}
		return lifeExpMap;
	}
	/**
	 * Determine if a given string is a number.
	 * Occasionally in the file there will be no data
	 */
	private boolean isFloat(String input)
	{
		boolean isNumber = false;
		try {
			Float.parseFloat(input);
			isNumber = true;
		} catch (NumberFormatException e) {
			return isNumber;
		}
		return isNumber;
	}
	/**
	 * Shade countries based on avg life expectancy
	 * Gray means no data in file for country
	 */
	private void shadeCountries()
	{
		//println("In shadeCountries");
		
		
		for(Marker marker : countryMarkers)
		{
			String countryId = marker.getId();
			//println(countryId);
			
			if(lifeExpByCountry.containsKey(countryId))
			{
				Float lifeExp = lifeExpByCountry.get(countryId);
				//map(lifeExp, lowEndAge=40, highEndAge=90, ColorCodeMin=10, ColorCodeMax=255)
				//maps the ages to color codes.
				int colorLevel = (int) map(lifeExp, 40, 90, 10, 255);
				//Use the color code you just calculated
				marker.setColor(color(255 - colorLevel, 100, colorLevel));
			}
			else //Some countries don't have info, gray is default
			{
				marker.setColor(color(150, 150, 150));
				//println("CountryId not found in lifeExpByCountry: " + countryId);

			}
		}
	}	
}
