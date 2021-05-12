package earthquakeVisualization;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import processing.core.*;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;

import parsing.ParseFeed;

public class EarthquakeCityMap extends PApplet {
	
	//earthquake feed
	private String earthquakeURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	//files containing city and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	//the map
	private UnfoldingMap map;
	private AbstractMapProvider provider;
	
	//Markers for each city
	private List<Marker> cityMarkers;
	
	//Markers for each country
	private List<Marker> countryMarkers;
	
	//Markers for each earthquake
	private List<Marker> quakeMarkers;
	
	//for event handling
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	public void setup()
	{
		//initializing canvas
		size(1000, 650, OPENGL);
		
		//setting up map
		provider = new Microsoft.HybridProvider();
		map = new UnfoldingMap(this, 250, 50, 700, 550, provider);
		
		MapUtils.createDefaultEventDispatcher(this, map);
		map.setZoomRange(1.5f,  10);
		map.zoomToLevel(2);
		
		//load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//load city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities)
		{
			cityMarkers.add(new CityMarker(city));
		}
		
		//load earthquake data
		List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakeURL);
		quakeMarkers = new ArrayList<Marker>();
		for(PointFeature feature : earthquakes)
		{
			//check if land quake
			if(isLand(feature))
			{
				quakeMarkers.add(new LandQuakeMarker(feature));
			}
			//ocean quake
			else
			{
				quakeMarkers.add(new OceanQuakeMarker(feature));
			}
		}
		
		/*
		 * to print the countries with number of earthquakes in them
		 * uncomment the line below
		 * */
		//printQuakes();
		/*
		 * to sort earthquakes with respect to their magnitudes in decreasing order
		 * and printing them in console the number of earthquakes which are displayed is the
		 * parameter here so it will display top 6
		 * */
		//sortAndPrint(6);
		
		//add markers to map
		//country markers are not added to map they are used for their geometric properties
		map.addMarkers(cityMarkers);
		map.addMarkers(quakeMarkers);
		
		//to set a desired font style
		PFont myFont = createFont("Gabriola", 20);
		textFont(myFont);
	}
	
	public void draw()
	{
		background(0);
		map.draw();
		addKey();
	}
	
	//to draw the key
	private void addKey()
	{
		//save previous drawing style
		pushStyle();
		
		fill(255, 250, 240);
		textSize(18);
		textAlign(LEFT, CENTER);
		
		int originX = 50;
		int originY = 50;
		
		int width = 150;
		int height = 350;
		
		int lineSpace = 30;
		
		rect(originX, originY, width, height);
		int xbase = originX + 30;
		int ybase = originY + 30;
		
		fill(0);
		text("Earthquake Key", xbase, ybase);
		ybase += lineSpace;
		
		fill(255, 0, 255);
		triangle(xbase, ybase,
				xbase-CityMarker.TRI_SIZE, ybase+ 2*CityMarker.TRI_SIZE,
				xbase+CityMarker.TRI_SIZE, ybase+ 2*CityMarker.TRI_SIZE);
		
		fill(0);
		textAlign(LEFT, CENTER);
		text("City Marker", xbase+15, ybase);
		ybase += lineSpace;
		
		fill(255, 255, 255);
		ellipseMode(CORNER);
		ellipse(xbase-6, ybase, 12, 12);
		
		fill(0);
		text("Land Quake", xbase+15, ybase);
		ybase += lineSpace;
		
		fill(255, 255, 255);
		rect(xbase-6, ybase, 12 ,12);
		
		fill(0);
		text("Ocean Quake", xbase+15, ybase);
		ybase += lineSpace;
		
		text("Size ~ Magnitude", xbase, ybase);
		ybase += lineSpace;
		
		fill(255, 255, 0);
		ellipseMode(CORNER);
		ellipse(xbase-6, ybase, 12, 12);
		
		fill(0);
		text("Shallow", xbase+15, ybase);
		ybase += lineSpace;
		
		fill(0, 0, 255);
		ellipseMode(CORNER);
		ellipse(xbase-6, ybase, 12, 12);
		
		fill(0);
		text("Intermediate", xbase+15, ybase);
		ybase += lineSpace;
		
		fill(255, 0, 0);
		ellipseMode(CORNER);
		ellipse(xbase-6, ybase, 12, 12);
		
		fill(0);
		text("Deep", xbase+15, ybase);
		ybase += lineSpace;
		
		fill(255);
		ellipseMode(CORNER);
		ellipse(xbase-6, ybase, 12, 12);
		
		strokeWeight(2);
		stroke(0);
		line(xbase-6, ybase, xbase+6, ybase+12);
		line(xbase-6, ybase+12, xbase+6, ybase);
		
		fill(0);
		text("Past Day", xbase+15, ybase);
		
		//restore previous drawing style
		popStyle();
	}
	
	//check if quake occurred on land
	private boolean isLand(PointFeature earthquake)
	{
		for(Marker country : countryMarkers)
		{
			if(isInCountry(earthquake, country))
			{
				return true;
			}
		}
		return false;
	}
	
	//to check if a quake occurred in a country
	//this will also add country property to the properties of earthquake
	//where the earthquake occurred
	private boolean isInCountry(PointFeature earthquake, Marker country)
	{
		//getting location earthquake
		Location checkLoc = earthquake.getLocation();
		
		//some countries are represented by MultiMarker
		//looping over the SimplePolygonMarkers which make up the MultiMarker
		if(country.getClass() == MultiMarker.class)
		{
			//looping over markers making the MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers())
			{
				//checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc))
				{
					earthquake.addProperty("country", country.getProperty("name"));
					//return if found inside one
					return true;
				}
			}
		}
		//check if inside a country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc))
		{
			earthquake.addProperty("country", country.getProperty("name"));
			return true;
		}
		return false;
	}
	
	//prints the country with number of earthquakes in it
	private void printQuakes()
	{
		int  totalWaterQuakes = quakeMarkers.size();
		for(Marker country : countryMarkers)
		{
			String countryName = country.getStringProperty("name");
			int numOfQuakes = 0;
			for(Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if(eqMarker.isOnLand())
				{
					if(countryName.equals(eqMarker.getProperty("country").toString()))
					{
						numOfQuakes++;
					}
				}
			}
			if(numOfQuakes > 0)
			{
				totalWaterQuakes -= numOfQuakes;
				System.out.println(countryName + " : " + numOfQuakes);
			}
		}
		System.out.println("OCEAN QUAKES : " + totalWaterQuakes);
	}
	
	//sort earthquakes with respect to their magnitude in descending order
	//and print the top numToPrint earthquakes
	private void sortAndPrint(int numToPrint)
	{
		List<EarthquakeMarker> quakes = new ArrayList<EarthquakeMarker>();
		for(Marker marker : quakeMarkers)
		{
			quakes.add((EarthquakeMarker)marker);
		}
		Collections.sort(quakes);
		Object[] arr = quakes.toArray();
		if(numToPrint > arr.length)
		{
			for(Object obj : arr)
			{
				System.out.println(obj);
			}
		}
		else
		{
			for(int i=0; i<numToPrint; i++)
			{
				System.out.println(arr[i]);
			}
		}
	}
	
	//automatically called when mouse is moved
	public void mouseMoved()
	{
		//clear  the selection
		if(lastSelected != null)
		{
			lastSelected.setSelected(false);
			lastSelected = null;
		}
		selectMarkerIfHover(cityMarkers);
		selectMarkerIfHover(quakeMarkers);
	}
	
	//check if there is marker selected
	private void selectMarkerIfHover(List<Marker> markers)
	{
		if(lastSelected != null)
		{
			return;
		}
		for(Marker m : markers)
		{
			CommonMarker marker = (CommonMarker)m;
			if(marker.isInside(map, mouseX, mouseY))
			{
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	/* The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	public void mouseClicked()
	{
		if(lastClicked != null)
		{
			unhideMarkers();
			lastClicked.setClicked(false);
			lastClicked = null;
		}
		else
		{
			checkCitiesForClick();
			if(lastClicked == null)
			{
				checkEarthquakesForClick();
			}
		}
	}
	
	//to check if a city marker was clicked on
	//and respond appropriately
	private void checkCitiesForClick()
	{
		if(lastClicked != null)
		{
			return;
		}
		//loop over all city markers to see if one of them is clicked
		for(Marker marker : cityMarkers)
		{
			if(!marker.isHidden() && marker.isInside(map, mouseX, mouseY))
			{
				lastClicked = (CommonMarker)marker;
				
				//hide all other city markers
				for(Marker mhide : cityMarkers)
				{
					if(mhide != lastClicked)
					{
						mhide.setHidden(true);
					}
				}
				
				//hide those earthquake markers which does not affect the city
				for(Marker mhide : quakeMarkers)
				{
					EarthquakeMarker quakeMarker = (EarthquakeMarker)mhide;
					if(quakeMarker.getDistanceTo(marker.getLocation()) > quakeMarker.threatCircle())
					{
						quakeMarker.setHidden(true);
					}
				}
				return;
			}
		}
	}
	
	//check if an earthquake marker was clicked on
	private void checkEarthquakesForClick()
	{
		if(lastClicked != null)
		{
			return;
		}
		//loop over all earthquake markers to see if one of them was clicked
		for(Marker m : quakeMarkers)
		{
			EarthquakeMarker marker = (EarthquakeMarker)m;
			if(!marker.isHidden() && marker.isInside(map, mouseX, mouseY))
			{
				lastClicked = marker;
				
				//hide all other earthquake markers
				for(Marker mhide : quakeMarkers)
				{
					if(mhide != lastClicked)
					{
						mhide.setHidden(true);
					}
				}
				
				//hide cities which are not affected by this earthquake
				for(Marker mhide : cityMarkers)
				{
					if(mhide.getDistanceTo(marker.getLocation()) > marker.threatCircle())
					{
						mhide.setHidden(true);
					}
				}
				return;
			}
		}
	}
	
	//unhide all markers
	private void unhideMarkers()
	{
		for(Marker city : cityMarkers)
		{
			city.setHidden(false);
		}
		
		for(Marker quake : quakeMarkers)
		{
			quake.setHidden(false);
		}
	}
}
