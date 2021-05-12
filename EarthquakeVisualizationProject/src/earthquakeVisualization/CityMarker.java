package earthquakeVisualization;

import processing.core.PGraphics;
import processing.core.PConstants;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;

public class CityMarker extends CommonMarker {
	
	//size of city marker
	public static int TRI_SIZE = 5;
	
	public CityMarker(Location location)
	{
		super(location);
	}
	
	public CityMarker(Feature city)
	{
		super(((PointFeature)city).getLocation(), city.getProperties());
		//Cities have properties: "name" (city name), "country" (country name)
		//and "population" (population, in millions)
	}
	
	//to draw city markers
	public void drawMarker(PGraphics pg, float x, float y)
	{
		//save previous drawing style
		pg.pushStyle();
		
		//drawing our own marker for city
		pg.fill(255, 0, 255);
		pg.triangle(x, y-TRI_SIZE, x-TRI_SIZE, y+TRI_SIZE, x+TRI_SIZE, y+TRI_SIZE);
		
		//restore previous drawing style
		pg.popStyle();
	}
	
	//show title of city if this marker is selected
	//i.e. mouse is over the marker
	public void showTitle(PGraphics pg, float x, float y)
	{
		String name = getCity() + " " + getCountry() + " ";
		String pop = "Pop: " + getPopulation() + " Million";
		
		pg.pushStyle();
		
		pg.fill(255, 255, 255);
		pg.textSize(12);
		pg.rectMode(PConstants.CORNER);
		pg.rect(x, y-TRI_SIZE-39, Math.max(pg.textWidth(name), pg.textWidth(pop)) + 6, 39);
		pg.fill(0, 0, 0);
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.text(name, x+3, y-TRI_SIZE-33);
		pg.text(pop, x+3, y - TRI_SIZE -18);
		
		pg.popStyle();
	}
	
	
	//setters and getters
	private String getCity()
	{
		return getStringProperty("name");
	}
	
	private String getCountry()
	{
		return getStringProperty("country");
	}
	
	private float getPopulation()
	{
		return Float.parseFloat(getStringProperty("population"));
	}
}
